# DBengnie

DBengnie is an embedded database engine written in Java. It stores model objects in disk-backed collections and provides indexed collection operations through a Java API. It is designed to run inside a Java application; it is not a standalone database server and does not provide a SQL parser or JDBC driver.

> **Project status:** The repository includes a working end-to-end smoke test for insert, indexed lookup, update, delete, and reopening a database. This is an evolving project, so review the current feature set and limitations before relying on it for production data.

## Features

- Disk-backed record storage organized into pages.
- Model-to-collection schema mapping with Java annotations.
- Primary, unique, and secondary index metadata and index-backed queries.
- Collection insert, select, update, and delete operations.
- Query conditions, including equality and composition/order support in the query API.
- Configurable B+ tree and storage, cache, file handling, and page settings through `EngineConfig`.
- Clean shutdown and persistence across a clean close and reopen.

DBengnie is an embedded storage engine. It does not currently expose SQL, a network protocol, JDBC, or a migration framework. The persistence smoke test checks clean shutdown/reopen; it should not be interpreted as proof of crash recovery or production-grade durability guarantees.

## Requirements

- JDK 21 or newer
- Maven 3.9 or newer

## Build and run tests

Clone the repository, then from its root run:

```sh
mvn test
```

This compiles the project and runs the automated tests. To run the end-to-end smoke example as a standalone Java `main` method:

```sh
mvn test-compile \
  -Dexec.mainClass=com.gaurav.enigine.DatabaseSmokeTest \
  -Dexec.classpathScope=test \
  exec:java
```

The smoke test uses a temporary database directory and removes it afterward.

## Quick start

The example below follows the API used by the repository's smoke test. Define a model and annotate its collection, fields, and indexes:

```java
import com.gaurav.enigine.scheme.annotation.Collection;
import com.gaurav.enigine.scheme.annotation.Field;
import com.gaurav.enigine.scheme.annotation.Index;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Collection(id = 1, name = "customers")
public class Customer {
    @Field(id = 1)
    @Index(primary = true)
    private Integer id;

    @Field(id = 2, maxLength = 120)
    @Index(unique = true)
    private String email;

    @Field(id = 3, maxLength = 80)
    private String city;

    public Customer(Integer id, String email, String city) {
        this.id = id;
        this.email = email;
        this.city = city;
    }
}
```

Create a database context with a schema and a persistent directory. Then use collection operations:

```java
import com.gaurav.enigine.context.DatabaseContext;
import com.gaurav.enigine.context.DatabaseContextConfigurator;
import com.gaurav.enigine.context.EngineConfig;
import com.gaurav.enigine.operation.CollectionOperation;
import com.gaurav.enigine.operation.query.Operation;
import com.gaurav.enigine.operation.query.Query;
import com.gaurav.enigine.scheme.ModelToCollectionConverter;
import com.gaurav.enigine.scheme.Scheme;

import java.util.Collections;

public class Example {
    public static void main(String[] args) {
        String databaseDirectory = "./data/customer-db";

        DatabaseContextConfigurator configurator = new DatabaseContextConfigurator() {
            @Override
            public EngineConfig engineConfig() {
                return EngineConfig.builder()
                        .baseDBPath(databaseDirectory)
                        .clusterKeyType(EngineConfig.ClusterKeyType.INT)
                        .build();
            }

            @Override
            public Scheme scheme() {
                Scheme.Collection customers =
                        new ModelToCollectionConverter(Customer.class).toCollection();
                return Scheme.builder()
                        .dbName("customer-db")
                        .version(1)
                        .collections(Collections.singletonList(customers))
                        .build();
            }
        };

        DatabaseContext context = configurator.databaseContext();
        try {
            CollectionOperation customers = context.getOperation("customers");

            customers.insert().execute(new Customer(1, "ada@example.com", "London"));

            Query byEmail = new Query("email", Operation.EQ, "ada@example.com");
            Customer found = customers.select()
                    .query(byEmail)
                    .asList(Customer.class)
                    .get(0);
            System.out.println(found.getCity());

            customers.update()
                    .query(byEmail)
                    .execute(Customer.class, customer -> customer.setCity("Oxford"));

            customers.delete()
                    .query(new Query("email", Operation.EQ, "ada@example.com"))
                    .execute();
        } finally {
            // Close cleanly so pending resources are released before reopening the database.
            context.shutdownGracefully();
        }
    }
}
```

The `Customer` class above requires Lombok, which is already configured in this repository's Maven build. When consuming DBengnie from another project, add Lombok as a compile-time/annotation-processing dependency if you use Lombok annotations, or replace the Lombok annotations with ordinary Java accessors and a no-argument constructor.

## Configuration

`EngineConfig.builder()` provides defaults and allows settings to be overridden. Common settings include:

- `baseDBPath`: directory used for database files. Set this to a stable, writable path; using a temporary directory loses the database when that directory is removed.
- `clusterKeyType`: type used for cluster keys (`INT`, `UINT`, `LONG`, or `ULONG`). Choose a type compatible with the primary/cluster key in your collection schema.
- `indexCache` and `indexCacheSize`: index cache controls.
- `dbPageSize` and `dbPageBufferSize`: record page and buffer sizing.
- `indexStorageManagerStrategy`: `ORGANIZED`, `COMPACT`, or `PAGE_BUFFER`.
- `indexIOSessionStrategy`: index I/O strategy (`IMMEDIATE`, `MEMORY_SNAPSHOT`, or `RECOVERABLE_DISK_SNAPSHOT`). Check the implementation and tests before relying on a strategy's recovery guarantees.
- `fileHandlerStrategy`, `fileHandlerPoolMaxFiles`, and `fileHandlerPoolThreads`: file handler resource management.

Configuration options and defaults are defined in [`EngineConfig`](src/main/java/com/gaurav/enigine/context/EngineConfig.java).

## Schema and indexes

Use `@Collection` on a model class and `@Field` on persisted fields. Field IDs should be stable and unique within a collection because they identify fields in the stored schema. Use `@Index` to declare index metadata:

- `@Index(primary = true)` marks a primary index.
- `@Index(unique = true)` requests uniqueness for an indexed field.
- `@Index` declares a regular index.
- Optional index flags include `autoIncrement` and `lowCardinality`.

The model's collection and field names are used by `getOperation(...)` and query conditions. Keep schema IDs and names stable when reopening an existing database. Schema evolution and compatibility behavior should be checked against the current `SchemeManager` implementation before changing a schema used by existing data.

## Query and collection operations

Obtain a collection API with `context.getOperation("collectionName")`. The operation API exposes:

- `insert().execute(model)` to insert a model.
- `select()` to read records and inspect results with methods such as `count()`, `exists()`, `query(...)`, and `asList(Model.class)`.
- `update().query(condition).execute(Model.class, updater)` to modify matching records.
- `delete().query(condition).execute()` to delete matching records.

Queries are built with `Query(fieldName, Operation, value)`. For example:

```java
Query byCity = new Query("city", Operation.EQ, "London");
long matches = customers.select().query(byCity).count();
```

The query API also defines condition composition and ordering types. Consult `com.gaurav.enigine.operation.query` for the supported operators and composition rules.

## Project layout

```text
src/main/java/com/gaurav/enigine/   Engine implementation
src/test/                           Automated tests
src/test/java/.../DatabaseSmokeTest.java  End-to-end runnable smoke test
pom.xml                             Maven build and dependency configuration
```

## Contributing

1. Create a branch for your change.
2. Keep schema and storage behavior covered by tests.
3. Run `mvn test` before submitting a pull request.
4. Describe behavioral changes and any persistence or compatibility impact.

## License

No license file is currently included. Until a license is added, all rights are reserved by the copyright holder; please do not assume the project is open source or that reuse, modification, or redistribution is permitted. Add a `LICENSE` file before publishing the repository as an openly licensed project.
