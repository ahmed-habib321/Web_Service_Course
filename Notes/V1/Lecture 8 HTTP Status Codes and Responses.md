## 1. Why HTTP status codes matter

HTTP status codes are the **primary way APIs communicate outcomes** to clients.

They are:

* **Machine-readable** (clients can react programmatically)
* **Standardized** (understood by all HTTP clients)
* **More reliable than custom messages**

💡 A client should be able to understand what happened **without parsing text**.

---

## 2. Status codes and CRUD operations

Each CRUD operation typically maps to specific HTTP status codes.

### Successful responses

#### `200 OK` – Successful read

Used when data is returned in the response body.

```http
GET /users/10
```

Response:

```http
200 OK
```

```json
{
  "id": 10,
  "name": "Ahmed"
}
```

---

#### `201 Created` – Resource created

Used after a successful **POST** that creates a new resource.

Best practices:

* Return the created resource (optional)
* Include a `Location` header pointing to the new resource

```http
POST /users
```

Response:

```http
201 Created
Location: /users/15
```

---

#### `204 No Content` – Success with no body

Used when:

* A resource is deleted
* A resource is updated but no response body is needed

```http
DELETE /users/10
```

Response:

```http
204 No Content
```

💡 This tells the client the operation succeeded, but there’s nothing to display.

---

## 3. Client error responses

Client errors mean **the request was invalid**.

#### `400 Bad Request`

Used when:

* Invalid input
* Missing required fields
* Validation errors

```http
POST /users
```

Response:

```http
400 Bad Request
```

```json
{
  "error": "Email is required"
}
```

---

#### `404 Not Found`

Used when:

* The requested resource does not exist

```http
GET /users/999`
```

Response:

```http
404 Not Found
```

💡 Do **not** return `200 OK` with an error message — that breaks REST conventions.

---

## 4. Server error responses

#### `500 Internal Server Error`

Used when:

* Something goes wrong on the server
* The client is **not** at fault

```http
500 Internal Server Error
```

Best practice:

* Log full error details internally
* Return a generic message to the client

---

## **5. Advanced HTTP status codes**

These are used for more precise error handling beyond the basic 400/404/500 set.

| Status Code                  | Meaning                                                  | When to use                              | Example                                               |
| ---------------------------- | -------------------------------------------------------- | ---------------------------------------- | ----------------------------------------------------- |
| **401 Unauthorized**         | Client is **not authenticated**                          | No or invalid authentication credentials | Accessing `/users/10` without a valid token           |
| **403 Forbidden**            | Client is **authenticated but not allowed**              | Authenticated user lacks permissions     | A regular user tries to delete another user's account |
| **409 Conflict**             | Request **conflicts with current server state**          | Duplicate entries, version conflicts     | Creating a user with an email that already exists     |
| **422 Unprocessable Entity** | Request **syntax is correct, but semantic errors exist** | Validation errors                        | Submitting a negative age for a user                  |

### ✅ Usage in RESTful APIs:

* **401** → “Who are you?” (Authentication missing/invalid)
* **403** → “You are not allowed to do this” (Authorization problem)
* **409** → “This conflicts with existing data”
* **422** → “Your input is invalid”

---

### **Example in Spring Boot**

```java
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody User user) {
    if (!authService.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    if (!authService.hasRole("ADMIN")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    if (userService.existsByEmail(user.getEmail())) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            new ApiError(409, "Email already exists")
        );
    }
    if (user.getAge() < 0) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
            new ApiError(422, "Age cannot be negative")
        );
    }
    User saved = userService.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
}
```

---


## 6. Consistency is more important than perfection

A well-designed API:

* Uses status codes **consistently**
* Uses the same error structure everywhere
* Avoids ambiguous responses

Example of a consistent error response:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid email format",
  "path": "/users"
}
```

---

## 7. Java / Spring Boot mapping (example)

```java
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody User user) {
    User saved = userService.save(user);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", "/users/" + saved.getId())
            .body(saved);
}
```

---

## 8. Common mistakes to avoid

❌ Always returning `200 OK`
❌ Putting error details only in the response body
❌ Using `500` for client-side validation errors
❌ Inconsistent error formats

## 9. Standard error response design

A consistent error response structure improves:

* Machine readability
* Debugging
* Client handling of errors

### Recommended fields

| Field                  | Description                              |
| ---------------------- | ---------------------------------------- |
| `status`               | HTTP status code                         |
| `error`                | Short description, e.g., “Bad Request”   |
| `message`              | Detailed human-readable error message    |
| `path`                 | URI of the request that caused the error |
| `timestamp` (optional) | When the error occurred                  |

### Example JSON response

```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Age cannot be negative",
  "path": "/users",
  "timestamp": "2026-01-08T23:45:00Z"
}
```

---

### **Spring Boot global error handling example**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(UserNotFoundException ex, HttpServletRequest request) {
        ApiError error = new ApiError(404, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EmailConflictException.class)
    public ResponseEntity<ApiError> handleConflict(EmailConflictException ex, HttpServletRequest request) {
        ApiError error = new ApiError(409, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
```

Here, `ApiError` is a simple POJO:

```java
public class ApiError {
    private int status;
    private String message;
    private String path;
    private String error;

    public ApiError(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.error = HttpStatus.valueOf(status).getReasonPhrase();
    }

    // getters and setters
}
```