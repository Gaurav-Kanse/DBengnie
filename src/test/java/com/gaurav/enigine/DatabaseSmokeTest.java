package com.gaurav.enigine;

import com.gaurav.enigine.context.DatabaseContext;
import com.gaurav.enigine.context.DatabaseContextConfigurator;
import com.gaurav.enigine.context.EngineConfig;
import com.gaurav.enigine.operation.CollectionOperation;
import com.gaurav.enigine.operation.query.Operation;
import com.gaurav.enigine.operation.query.Query;
import com.gaurav.enigine.scheme.ModelToCollectionConverter;
import com.gaurav.enigine.scheme.Scheme;
import com.gaurav.enigine.scheme.annotation.Collection;
import com.gaurav.enigine.scheme.annotation.Field;
import com.gaurav.enigine.scheme.annotation.Index;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * Small end-to-end exercise of DBengnie's public API.
 * Run this class with the project's dependencies and annotation processors on the classpath.
 */
public final class DatabaseSmokeTest {
    @Data
    @NoArgsConstructor
    @Collection(id = 1, name = "customers")
    public static class Customer {
        @Field(id = 1)
        @Index(primary = true)
        private Integer id;

        @Field(id = 2, maxLength = 40)
        @Index
        private String email;

        @Field(id = 3, maxLength = 40)
        @Index
        private String city;

        Customer(int id, String email, String city) {
            this.id = id;
            this.email = email;
            this.city = city;
        }
    }

    private Path databasePath;

    private DatabaseContextConfigurator configurator() {
        return new DatabaseContextConfigurator() {
            @Override
            public EngineConfig engineConfig() {
                return EngineConfig.builder()
                        .clusterKeyType(EngineConfig.ClusterKeyType.INT)
                        .baseDBPath(databasePath.toString())
                        .build();
            }

            @Override
            public Scheme scheme() {
                Scheme.Collection collection = new ModelToCollectionConverter(Customer.class).toCollection();
                return Scheme.builder()
                        .version(1)
                        .dbName("smoke-test")
                        .collections(Collections.singletonList(collection))
                        .build();
            }
        };
    }

    private void exercise() throws Exception {
        DatabaseContext firstContext = configurator().databaseContext();
        try {
            CollectionOperation customers = firstContext.getOperation("customers");
            customers.insert().execute(new Customer(1, "ada@example.test", "London"));
            customers.insert().execute(new Customer(2, "grace@example.test", "New York"));
            customers.insert().execute(new Customer(3, "linus@example.test", "Helsinki"));

            Query emailLookup = new Query("email", Operation.EQ, "grace@example.test");
            require(customers.select().query(emailLookup).count() == 1, "indexed lookup should find one customer");
            Customer found = customers.select().query(emailLookup).asList(Customer.class).get(0);
            require(found.getCity().equals("New York"), "lookup should deserialize the stored model");

            long changed = customers.update().query(emailLookup).execute(Customer.class, customer -> customer.setCity("Boston"));
            require(changed == 1, "update should affect one customer");
            require(customers.select().query(new Query("city", Operation.EQ, "Boston")).count() == 1,
                    "updated indexed field should be queryable");

            long removed = customers.delete().query(new Query("email", Operation.EQ, "linus@example.test")).execute();
            require(removed == 1, "delete should remove one customer");
            require(customers.select().count() == 2, "two records should remain before closing");
        } finally {
            firstContext.shutdownGracefully();
        }

        // Reopening the same database directory checks that records survive a clean close.
        DatabaseContext reopenedContext = configurator().databaseContext();
        try {
            CollectionOperation customers = reopenedContext.getOperation("customers");
            long persistedCount = java.util.concurrent.CompletableFuture
                    .supplyAsync(() -> customers.select().count())
                    .get(5, TimeUnit.SECONDS);
            require(persistedCount == 2, "records should persist after reopening; found " + persistedCount);
            Customer persisted = customers.select()
                    .query(new Query("email", Operation.EQ, "grace@example.test"))
                    .asList(Customer.class)
                    .get(0);
            require(persisted.getCity().equals("Boston"), "updated value should persist after reopening");
            require(!customers.select().query(new Query("email", Operation.EQ, "linus@example.test")).exists(),
                    "deleted record should remain absent after reopening");
        } finally {
            reopenedContext.shutdownGracefully();
        }
    }

    @Test
    void persistsAndSupportsCollectionOperations() throws Exception {
        Path path = Files.createTempDirectory("dbengnie-smoke-test-");
        try {
            DatabaseSmokeTest smokeTest = new DatabaseSmokeTest();
            smokeTest.databasePath = path;
            smokeTest.exercise();
        } finally {
            try (var paths = Files.walk(path)) {
                paths.sorted(Comparator.reverseOrder()).forEach(DatabaseSmokeTest::deleteQuietly);
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void main(String[] args) throws Exception {
        Path path = Files.createTempDirectory("dbengnie-smoke-");
        try {
            DatabaseSmokeTest smokeTest = new DatabaseSmokeTest();
            smokeTest.databasePath = path;
            smokeTest.exercise();
            System.out.println("DBengnie smoke test passed: insert, indexed select, update, delete, and disk reopen.");
        } finally {
            try (var paths = Files.walk(path)) {
                paths.sorted(Comparator.reverseOrder()).forEach(DatabaseSmokeTest::deleteQuietly);
            }
        }
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not clean up smoke-test database at " + path, e);
        }
    }
}
