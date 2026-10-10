# DBengnie

DBengnie is a Java embedded database engine with disk-backed records, indexes, and collection operations.

## Requirements

- Java 21 or newer
- Maven 3.9 or newer

## Build and test

```sh
mvn test
```

## Run the database smoke test

The smoke test inserts and queries records, updates and deletes records, closes the database, then reopens it and verifies persistence.

```sh
mvn test-compile -Dexec.mainClass=com.gaurav.enigine.DatabaseSmokeTest \
    -Dexec.classpathScope=test exec:java
```

The same exercise also runs as part of `mvn test`.
