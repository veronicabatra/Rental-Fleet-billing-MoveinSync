# Fleet Billing & Fair Cost Split

A Spring Boot based Fleet Billing System for calculating accurate and explainable monthly vehicle billing.

## Tech Stack

- Java 23
- Spring Boot 4.1.1
- Spring Data JPA
- Hibernate
- Maven
- MySQL 8
- HTML
- Bootstrap
- JavaScript

## Features

- Per-KM billing
- Per-trip billing
- Fixed monthly billing
- Tiered KM slabs
- Mid-month rate changes
- Dead-running KM
- Night charges
- Waiting charges
- Toll charges
- Fair fixed-fee allocation
- Exact paisa-level reconciliation
- Fraud detection
- Duplicate billing prevention
- Explainable trip-wise billing

## How to Run

### 1. Requirements

Install:

- Java 23
- MySQL 8
- Git

Maven does not need to be installed separately because Maven Wrapper is included.

Check Java:

    java -version

Check Maven Wrapper:

    .\mvnw.cmd -version

### 2. Create MySQL Database

Open MySQL Workbench and run:

    CREATE DATABASE fleet_billing;

### 3. Configure Database

Open:

    src/main/resources/application.properties

Set your MySQL credentials:

    spring.application.name=fleet-billing

    spring.datasource.url=jdbc:mysql://localhost:3306/fleet_billing
    spring.datasource.username=root
    spring.datasource.password=YOUR_PASSWORD

    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true

Replace `YOUR_PASSWORD` with your MySQL password.

### 4. Start Backend

Open terminal in the project root and run:

    .\mvnw.cmd spring-boot:run

Backend will start at:

    http://localhost:8080

### 5. Start Frontend

Open the `frontend` folder and run `index.html` using VS Code Live Server.

The frontend communicates with the Spring Boot backend at:

    http://localhost:8080

## Project Structure

    fleet-billing/
    ├── src/
    │   └── main/
    │       ├── java/com/moveinsync/fleetbilling/
    │       │   ├── controller/
    │       │   ├── dto/
    │       │   ├── entity/
    │       │   ├── repository/
    │       │   └── service/
    │       └── resources/
    │           └── application.properties
    ├── frontend/
    │   ├── index.html
    │   └── app.js
    ├── pom.xml
    ├── mvnw
    ├── mvnw.cmd
    └── README.md

## Billing Models

### PER_KM

Billing is based on:

    Billable KM = Duty KM + Dead-running KM

Both flat and tiered pricing are supported.

Example:

    0–100 KM    → ₹50/KM
    101–200 KM  → ₹45/KM
    200+ KM     → ₹40/KM

For 500 KM:

    100 × 50 = ₹5,000
    100 × 45 = ₹4,500
    300 × 40 = ₹12,000

    Total = ₹21,500

### PER_TRIP

A fixed amount is charged for every trip.

Example:

    ₹500/trip × 10 trips = ₹5,000

### FIXED_MONTHLY

A fixed monthly amount is distributed across trips according to their duty KM contribution.

    Trip Allocation =
    Monthly Fee × (Trip Duty KM / Total Duty KM)

The final allocations are reconciled at paisa level so that:

    Sum of Trip Allocations = Exact Monthly Fee

## Mid-Month Rate Changes

The system supports different pricing periods within the same billing month.

Example:

    1 Sep – 15 Sep  → ₹20/KM
    16 Sep – 30 Sep → ₹25/KM

Each trip is billed according to the pricing period applicable on its trip date.

Pricing periods are validated to ensure there are no gaps or overlaps.

## Additional Charges

The system supports:

- Night charges
- Waiting charges
- Toll charges

Final trip amount:

    Total =
    Base Charge
    + Fixed Fee Allocation
    + Night Charge
    + Waiting Charge
    + Toll

## Fraud Detection

The system performs basic billing data validation.

Examples:

- Missing ride record
- Invalid distance
- Impossible distance
- Duplicate billed trip

Fraud information is stored with the billing result.

## Idempotent Billing

Billing is identified using:

    Vehicle + Billing Month

If billing for the same vehicle and month is submitted again, the existing billing run is returned instead of creating a duplicate billing run.

## Money Handling

All monetary calculations use Java `BigDecimal` instead of floating-point types.

Explicit rounding is used for monetary calculations to avoid precision problems.

## Assumptions

1. Billing is generated vehicle-wise for a particular month.
2. Every trip belongs to a vehicle.
3. Dead-running KM is included in distance-based billing.
4. Fixed monthly fees are allocated proportionally using duty KM.
5. Missing ride records are treated as a data-quality/fraud warning.
6. Invalid or impossible distances are rejected or flagged.
7. Mid-month pricing periods must cover the billing month without gaps or overlaps.
8. Contract validity is determined using the trip date.
9. Monetary values are handled using `BigDecimal`.
10. Billing for the same vehicle and month is idempotent.

## Design Choices & Trade-offs

### Layered Architecture

The application follows:

    Controller
        ↓
    Service
        ↓
    Repository
        ↓
    Database

Business logic is kept in the service layer instead of controllers.

This improves maintainability and testability, although it results in more classes.

### BigDecimal for Money

`BigDecimal` is used instead of `double` for financial calculations.

This provides accurate monetary calculations at the cost of slightly more verbose code.

### Progressive Tiered Pricing

Tiered pricing is calculated progressively across applicable slabs instead of applying one rate to the entire distance.

This makes the billing calculation deterministic and matches slab-based pricing requirements.

### Fixed Fee Allocation

Fixed monthly fees are allocated proportionally across trips based on duty KM.

This provides a fair and explainable trip-level breakdown, but requires additional rounding and reconciliation logic.

### Simple Frontend

The frontend uses HTML, Bootstrap and JavaScript instead of a large frontend framework.

This keeps the assignment lightweight and focuses development effort on the billing engine and backend logic.

### Idempotency

A vehicle-month combination is treated as a unique billing run.

This prevents accidental duplicate billing. A production system could additionally provide a controlled re-billing or reversal workflow.

## Testing Scenarios

The following scenarios should be tested:

1. Per-KM billing
2. Per-trip billing
3. Tiered slab calculation
4. Fixed monthly fee allocation
5. Dead-running KM
6. Night charges
7. Waiting charges
8. Toll charges
9. Mid-month rate changes
10. Duplicate billing
11. Invalid distance
12. Missing ride record
13. Exact paisa reconciliation

## Future Improvements

- Authentication and authorization
- Role-based access control
- Vendor portal
- Invoice/PDF generation
- Asynchronous billing
- Redis caching
- Message queues
- Advanced fraud detection
- Audit logging
- Monitoring and alerting
- CI/CD deployment
- Billing reversal and re-billing workflow

## Conclusion

The Fleet Billing system focuses on accurate, deterministic and explainable monthly billing.

The main objectives are:

    Correct Pricing
    +
    Exact Money Calculation
    +
    Fair Fixed-Fee Allocation
    +
    Mid-Month Pricing Support
    +
    Fraud Detection
    +
    Idempotent Billing
