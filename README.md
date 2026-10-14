# JSON Dataset API

A Spring Boot REST API that stores JSON records in a PostgreSQL database and supports dynamic grouping and sorting of dataset records.

## Live Deployment

Base URL: https://json-dataset-api-2.onrender.com

## Tech Stack

- Java 21
- Spring Boot
- Maven
- PostgreSQL
- Spring Data JPA
- REST API

## API Endpoints

### 1. Insert a Record

**Method:** `POST`

**Endpoint:** `/api/dataset/{datasetName}/record`

Example request:

```json
{
  "name": "Arun",
  "city": "Vellore",
  "age": 25
}
```

Example response: `201 Created`

### 2. Retrieve Dataset Records

**Method:** `GET`

**Endpoint:** `/api/dataset/{datasetName}/query`

Returns all records belonging to the specified dataset.

### 3. Group Records

**Method:** `GET`

**Example:** `/api/dataset/test/query?groupBy=city`

Groups records according to the selected JSON field.

### 4. Sort Records

**Method:** `GET`

Ascending order:

`/api/dataset/test/query?sortBy=age&order=asc`

Descending order:

`/api/dataset/test/query?sortBy=age&order=desc`

Supported order values: `asc` and `desc`.

## Error Handling

- `400 Bad Request` for malformed JSON and invalid sorting order.
- `201 Created` when a record is successfully inserted.

## Local Setup

1. Install Java 21 and PostgreSQL.
2. Clone this repository.
3. Configure the database connection using environment variables.
4. Run the application using the Maven wrapper.

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Database Configuration

Configure these environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Never commit database passwords or other secrets to the repository.