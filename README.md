# Spring Batch 3 Part Series

**Using Spring 6.2.3 & Spring Batch 5.2.2 (Java 17+)**

This project demonstrates three common Spring Batch patterns using XML configuration and an embedded HSQLDB database.

## Prerequisites

- Java 17 or higher
- Maven 3.8+

## Getting Started

```bash
# Clone the repository
git clone <repository-url>
cd springbatch3part

# Build the project
mvn clean compile

# Run all tests (executes all three batch jobs)
mvn test
```

## Project Structure

```
src/main/java/com/batch/
├── AppJobExecutionListener.java   # Job lifecycle listener with MDC and duration
├── BatchMetricsLogger.java        # Micrometer metrics recording and summary
├── ItemFailureLoggerListener.java # Error handling listener
├── StepMetricsListener.java       # Step metrics with timing and commit counts
├── simpletask/
│   ├── HelloTask.java             # Prints a greeting message
│   └── TimeTask.java              # Prints the current time
├── todb/
│   ├── Ledger.java                # Domain record (Java record)
│   ├── LedgerMapper.java          # Maps flat file fields to Ledger
│   └── LedgerProcessor.java       # Validates and normalizes Ledger records
└── fromdb/
    └── LedgerRowMapper.java       # Maps DB rows to Ledger

src/test/java/com/batch/
├── AppTest.java                   # Ledger record unit tests
├── simpletask/
│   └── SimpleTaskletTestCase.java # Runs the simple tasklet job
├── todb/
│   ├── LedgerMapperTest.java      # Unit tests for CSV field mapping
│   └── ToDBBatchTestCase.java     # Integration test: flat-file-to-DB job
└── fromdb/
    ├── LedgerRowMapperTest.java   # Unit tests for DB row mapping
    └── FromDBBatchTestCase.java   # Integration test: DB-to-flat-file job
```

## The Three Batch Jobs

### Part I - Simple Tasklet

A two-step job that prints a greeting message followed by the current time.

```bash
mvn test -Dtest=SimpleTaskletTestCase
```

### Part II - Flat File to Database

Reads ledger records from a CSV file (`src/main/resources/ledger.txt`), validates and normalizes them through an `ItemProcessor`, and writes them to an HSQLDB table using `JdbcBatchItemWriter`. Includes skip/retry fault tolerance policies.

```bash
mvn test -Dtest=ToDBBatchTestCase
```

### Part III - Database to Flat File

Reads ledger records from the database and writes them to a CSV file (`target/ledgers-output.txt`).

```bash
mvn test -Dtest=FromDBBatchTestCase
```

## Observability

All three batch jobs are instrumented with listeners that provide:

- **MDC Context** — Every log line includes `job` and `step` names for correlation (e.g., `[job=simpleJob step=step1]`)
- **Job Duration** — Start time, end time, and total duration logged at job completion; failures logged at ERROR level with exception details
- **Step Metrics** — Read, processed, written, skipped, filtered, commit, and rollback counts with step duration
- **Micrometer Metrics** — Job timers (`batch.job.duration`), counters (`batch.job.count`), and step-level gauges/counters recorded via `SimpleMeterRegistry` with a summary logged after each job
- **Error Logging** — Read and write errors captured by `ItemFailureLoggerListener`
- **Persistent Logs** — Rolling file appender writes to `logs/batch.log` with 30-day retention

## Technology Stack

| Component        | Version       |
|------------------|---------------|
| Java             | 17+           |
| Spring Framework | 6.2.3         |
| Spring Batch     | 5.2.2         |
| Micrometer       | 1.14.4        |
| JUnit            | 5.11.4        |
| HSQLDB           | 2.7.4         |
| SLF4J + Logback  | 2.0.16 / 1.5.16 |

## Modernization Journey

This project was originally written in 2013 using Java 1.5, Spring 3.1, and Spring Batch 2.1. It has been fully modernized to current standards. Here is a summary of every change made during the upgrade.

### Platform Upgrade

| Component        | Before (2013)     | After             |
|------------------|-------------------|--------------------|
| Java             | 1.5               | 17                 |
| Spring Framework | 3.1               | 6.2.3              |
| Spring Batch     | 2.1               | 5.2.2              |
| JUnit            | 3.x               | 5.11.4 (Jupiter)   |
| HSQLDB           | 1.8               | 2.7.4              |
| Logging          | commons-logging    | SLF4J 2.0 + Logback 1.5 |
| Build            | Maven (Java 1.5)  | Maven (Java 17)    |

### Java Language Modernization

- **Java record** — Replaced mutable `Ledger` JavaBean (getters/setters/constructor) with an immutable `record`
- **`java.time`** — Replaced `java.util.Date` and `Calendar` with `LocalDate` and `LocalDateTime` throughout
- **`BigDecimal`** — Replaced `double` with `BigDecimal` for all monetary fields (`depositAmount`, `paymentAmount`)
- **`var`** — Used local variable type inference where it improves readability
- **Parameterized logging** — Replaced string concatenation (`"Job: " + id`) with SLF4J placeholders (`"Job: {}", id`)
- **Removed `throws Exception`** — Cleaned up unnecessary checked exception declarations on tasklet methods
- **Meaningful parameter names** — Renamed `arg0, arg1` to `contribution, chunkContext` in tasklet methods
- **Extracted helper method** — Deduplicated dollar-parsing code in `LedgerMapper` into `parseDollarAmount()`
- **Wildcard cleanup** — Changed `Chunk<? extends Object>` to `Chunk<?>` in listener signatures

### Spring Batch Framework Improvements

- **Eliminated custom DAO layer** — Removed `LedgerDAO`, `LedgerDAOImpl`, and `LedgerWriter` (3 files). Replaced with Spring Batch's built-in `JdbcBatchItemWriter` with `BeanPropertyItemSqlParameterSourceProvider`, reducing boilerplate
- **Added `ItemProcessor`** — New `LedgerProcessor` validates records (filters negative amounts) and normalizes data (uppercases `paymentType`), implementing the full Reader-Processor-Writer pipeline
- **Fault tolerance** — Added skip policy for `FlatFileParseException` (skip-limit=10) and retry policy for `DeadlockLoserDataAccessException` (retry-limit=3) to the chunk configuration
- **`@SpringBatchTest`** — Migrated all integration tests to use `JobLauncherTestUtils` with proper job execution assertions
- **Shared base context** — Extracted duplicated XML infrastructure (DataSource, TransactionManager, JdbcTemplate, JobRepository, JobLauncher) into `base-batch-context.xml`, imported by all three job contexts
- **`DECIMAL` column type** — Changed the `ledger` table DDL from `double` to `DECIMAL(12,2)` for monetary columns

### Test Suite Expansion

The original project had 1 trivial test (`assertTrue(true)`). The modernized project has **14 tests**:

- `AppTest` — 4 unit tests for `Ledger` record construction and `BigDecimal` field precision
- `LedgerMapperTest` — 4 unit tests for CSV field mapping and dollar amount parsing
- `LedgerRowMapperTest` — 3 unit tests for database row mapping with mock `ResultSet`
- `SimpleTaskletTestCase` — Integration test verifying the two-step tasklet job completes
- `ToDBBatchTestCase` — Integration test verifying flat-file-to-DB job with item count assertions
- `FromDBBatchTestCase` — Integration test verifying DB-to-flat-file job with output verification

### Observability

- **MDC context propagation** — Job name and step name added to every log line via SLF4J MDC
- **Job duration and error reporting** — Enhanced `AppJobExecutionListener` with timing, exit descriptions, and ERROR-level failure logging with exception details
- **Step metrics** — Enhanced `StepMetricsListener` with step duration, commit count, and rollback count
- **Micrometer integration** — Added `BatchMetricsLogger` with `SimpleMeterRegistry` recording job timers, counters, and step-level gauges
- **Structured log pattern** — Updated Logback to include MDC fields: `[job=%X{jobName} step=%X{stepName}]`
- **Persistent logging** — Added rolling file appender (`logs/batch.log`) with 30-day retention
- **Full listener coverage** — All three jobs now have job-level and step-level listeners (FromDB job previously had none)

### Test Isolation Fixes

- **`DROP TABLE IF EXISTS`** — Added `schema-drop-ddl.sql` to prevent "object already exists" errors when `@SpringBatchTest` reuses Spring contexts with in-memory HSQLDB
- **`@BeforeEach` cleanup** — Added table cleanup in tests to prevent row count mismatches from shared in-memory databases

## Original Blog Series (from 2013)

- Part I - Simple Tasklet: http://blogs.justenougharchitecture.com/?p=124
- Part II - Flat File To Database: http://blogs.justenougharchitecture.com/?p=122
- Part III - From Database to Flat File: http://blogs.justenougharchitecture.com/?p=118
