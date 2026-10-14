# JSON Dataset API

A RESTful API built using Java, Spring Boot, Spring Data JPA, and PostgreSQL to store, retrieve, group, and sort dynamic JSON records.

## Features

- Store JSON objects under named datasets.
- Retrieve all records belonging to a dataset.
- Group records dynamically using a JSON field.
- Sort records by a specified field in ascending or descending order.
- Persist records in PostgreSQL.
- Return appropriate HTTP status codes for successful requests and invalid input.

## Technology Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Postman

## Prerequisites

Install the following:

- JDK 21
- PostgreSQL 16 or a compatible version
- Apache Maven 3.10 or a compatible version

## Database Setup

Create a PostgreSQL database named `json_dataset_db`.

The application uses the following configuration:

- Host: `localhost`
- Port: `5432`
- Database: `json_dataset_db`
- Username: `postgres`
- Password: supplied through the `DB_PASSWORD` environment variable

Hibernate automatically creates or updates the required database table using the configured schema-management setting.

## Environment Configuration

The application expects the PostgreSQL password through an environment variable.

### Windows PowerShell

Open PowerShell and run:

```powershell
$env:DB_PASSWORD = Read-Host "Enter your PostgreSQL password"
```

Enter your local PostgreSQL password when prompted.

Keep using the same PowerShell terminal for subsequent Maven commands. If you open another terminal, set the environment variable again.

## Running the Application

Navigate to the project directory:

```powershell
cd C:\Users\IT\Downloads\json-dataset-api
```

Start the application using Maven:

```powershell
& "$env:USERPROFILE\Downloads\maven-manual\apache-maven-3.10.0\bin\mvn.cmd" spring-boot:run
```

If Maven is configured in your Windows PATH, you can instead run:

```powershell
mvn spring-boot:run
```

The application starts at:

`http://localhost:8080`

## API Documentation

All endpoints use the base path `/api/dataset`.

### 1. Create a Record

**Method:** `POST`

**Endpoint:**

`/api/dataset/{datasetName}/record`

**Example URL:**

`http://localhost:8080/api/dataset/employees/record`

**Request body:**

```json
{
  "name": "Arun",
  "age": 25,
  "department": "IT",
  "salary": 30000
}
```

**Expected status:** `201 Created`

The response includes the generated record ID, dataset name, and submitted JSON record.

### 2. Retrieve Records

**Method:** `GET`

**Endpoint:**

`/api/dataset/{datasetName}/query`

**Example URL:**

`http://localhost:8080/api/dataset/employees/query`

Returns all records stored in the specified dataset as a JSON array.

If the dataset contains no records, the API returns `200 OK` with an empty array.

### 3. Group Records

**Method:** `GET`

**Endpoint:**

`/api/dataset/{datasetName}/query?groupBy={field}`

**Example URL:**

`http://localhost:8080/api/dataset/employees/query?groupBy=department`

Groups records according to the specified JSON field.

Example response:

```json
{
  "IT": [
    {
      "name": "Arun",
      "age": 25,
      "department": "IT",
      "salary": 30000
    }
  ]
}
```

Records with missing or null grouping fields are grouped under the key `"null"`.

### 4. Sort Records

**Method:** `GET`

Sort records in ascending order:

`http://localhost:8080/api/dataset/employees/query?sortBy=age&order=asc`

Sort records in descending order:

`http://localhost:8080/api/dataset/employees/query?sortBy=age&order=desc`

The `sortBy` parameter specifies the JSON field to sort by. The `order` parameter accepts `asc` or `desc`.

Numeric values are compared numerically. Other values are compared as case-insensitive text.

### Error Handling

- Invalid sorting order returns `400 Bad Request`.
- Malformed JSON request bodies return `400 Bad Request`.
- Empty dataset results return `200 OK` with an empty JSON array.
- Successful record creation returns `201 Created`.
- Successful record retrieval and queries return `200 OK`.

## Running Automated Tests

Set the `DB_PASSWORD` environment variable as described above.

Run:

```powershell
& "$env:USERPROFILE\Downloads\maven-manual\apache-maven-3.10.0\bin\mvn.cmd" clean test
```

If Maven is configured in your PATH, you can use `mvn clean test`.

The tests cover record creation, retrieval, grouping, sorting, and invalid sort-order handling. They connect to the configured database, so use a development database when running them.

## Project Structure

```text
json-dataset-api/
├── src/
│   ├── main/
│   │   ├── java/com/freightfox/dataset/
│   │   │   ├── JsonDatasetApiApplication.java
│   │   │   ├── controller/
│   │   │   │   └── DatasetRecordController.java
│   │   │   ├── entity/
│   │   │   │   └── DatasetRecord.java
│   │   │   ├── repository/
│   │   │   │   └── DatasetRecordRepository.java
│   │   │   └── service/
│   │   │       └── DatasetRecordService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/freightfox/dataset/
│           └── JsonDatasetApiApplicationTests.java
├── pom.xml
└── README.md
```

## Testing with Postman

The API can be tested in Postman by selecting the appropriate HTTP method, entering the endpoint URL, and providing a JSON request body for POST requests.

Manually verified scenarios include:

- Creating employee records.
- Retrieving saved records.
- Grouping records by department.
- Sorting records in ascending and descending order.
- Handling an invalid sorting order.
- Retrieving an empty dataset.

## Author

Developed as a Java Spring Boot REST API project.
