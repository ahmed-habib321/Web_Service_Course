## Richardson Maturity Model

The **Richardson Maturity Model** evaluates how “RESTful” an API is. It has **four levels**:

| Level | Description                 | Characteristics                                                                              |
| ----- | --------------------------- | -------------------------------------------------------------------------------------------- |
| 0     | POX (Plain Old XML/JSON)    | Single endpoint, ignores HTTP methods; e.g., `/api` with POST payload controlling everything |
| 1     | Resource-based URIs         | Distinct URLs for resources; e.g., `/messages`, `/profiles`                                  |
| 2     | HTTP methods & status codes | Uses proper GET/POST/PUT/DELETE and standard status codes (200, 201, 404, 500)               |
| 3     | HATEOAS                     | Adds hypermedia links in responses to guide clients dynamically                              |

💡 Purpose: assess and **improve RESTfulness**, but not mandatory to implement all levels.
