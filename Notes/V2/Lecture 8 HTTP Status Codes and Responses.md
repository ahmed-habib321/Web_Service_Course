# HTTP Status Codes and API Responses — Complete Guide

## Why Status Codes Are Essential for REST APIs

HTTP status codes are the **universal language** REST APIs use to communicate outcomes. They're not optional decorations—they're fundamental to how clients understand what happened with their requests.

### The Power of Status Codes

**Machine-readable communication:**
Status codes allow client applications to make decisions programmatically without parsing human-readable text. A mobile app can instantly know "this failed because the user isn't authenticated" just from seeing `401`.

**Universal standardization:**
Every HTTP client—browsers, mobile apps, server-to-server communication—understands these codes the same way. You're speaking a language everyone knows.

**Reliability over custom messages:**
Rather than inventing your own error system, status codes provide a tried-and-tested framework that integrates seamlessly with HTTP infrastructure like caching, load balancers, and monitoring tools.

**The golden rule:** A client should understand the outcome category (success, client error, server error) just from the status code, without needing to read any response body text.

---

## Status Codes Mapped to CRUD Operations

Different CRUD operations naturally produce different status codes depending on what happened.

### Success Responses

#### `200 OK` — Standard Success with Data

Use this when an operation succeeds and you're returning data in the response body.

**Most common with GET requests:**

```http
GET /users/10
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 10,
  "name": "Ahmed",
  "email": "ahmed@example.com"
}
```

**When to use:**
- Successfully retrieved data (GET)
- Successfully updated and returning the updated resource (PUT)
- Any successful operation where you're sending data back

---

#### `201 Created` — New Resource Successfully Created

Use this specifically when a POST request successfully creates a new resource.

**Example request:**
```http
POST /users
Content-Type: application/json

{
  "name": "Sara",
  "email": "sara@example.com"
}
```

**Best practice response:**
```http
HTTP/1.1 201 Created
Location: /users/15
Content-Type: application/json

{
  "id": 15,
  "name": "Sara",
  "email": "sara@example.com"
}
```

**Key elements:**
- **Status 201** tells the client something new was created
- **Location header** provides the URI of the newly created resource
- **Response body** optionally includes the created resource (saves the client a follow-up GET request)

**Why this matters:** The `Location` header allows clients to immediately know where to find the new resource without parsing the response body.

---

#### `204 No Content` — Success Without Response Data

Use this when an operation succeeds but there's no meaningful data to return.

**Common scenarios:**

**Deletion:**
```http
DELETE /users/10
```

**Response:**
```http
HTTP/1.1 204 No Content
```

**Update without returning the resource:**
```http
PUT /users/10
```

**Response:**
```http
HTTP/1.1 204 No Content
```

**What it communicates:** "Your request worked perfectly, but there's nothing to show you." This is efficient—no bandwidth wasted on empty JSON objects.

---

## Client Error Responses (4xx Series)

These status codes indicate **the client made a mistake**. The request was invalid, forbidden, or targeted something that doesn't exist.

### `400 Bad Request` — Invalid Input

Use when the request itself is malformed or contains invalid data.

**Common causes:**
- Missing required fields
- Invalid data formats
- Validation failures
- Malformed JSON

**Example:**
```http
POST /users
Content-Type: application/json

{
  "name": "Ahmed"
  // Missing required "email" field
}
```

**Response:**
```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "Email field is required",
  "path": "/users"
}
```

**Key principle:** Use 400 when the client needs to fix something about their request format or content.

---

### `404 Not Found` — Resource Doesn't Exist

Use when the client requests a resource that doesn't exist.

**Example:**
```http
GET /users/999
```

**Response:**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "User with ID 999 does not exist",
  "path": "/users/999"
}
```

**Critical mistake to avoid:**
```http
HTTP/1.1 200 OK

{
  "error": "User not found"
}
```

This is **wrong**! Don't return `200 OK` when something isn't found. The status code is the primary signal—use it correctly.

---

## Server Error Responses (5xx Series)

These indicate **the server encountered a problem**, not the client.

### `500 Internal Server Error` — Something Broke

Use when an unexpected error occurs on the server side that isn't the client's fault.

**Common causes:**
- Database connection failures
- Unhandled exceptions
- Configuration errors
- Third-party service failures

**Example response:**
```http
HTTP/1.1 500 Internal Server Error
Content-Type: application/json

{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later.",
  "path": "/users"
}
```

**Best practices:**
- **Log detailed error information** server-side for debugging
- **Return generic messages** to clients to avoid exposing internal implementation details
- **Don't expose stack traces** or database errors to clients (security risk)

---

## Advanced Status Codes for Precise Communication

Beyond the basic codes, these provide more nuanced error reporting.

### Complete Reference Table

| Status Code | Name | Meaning | When to Use | Example Scenario |
|-------------|------|---------|-------------|------------------|
| **401 Unauthorized** | Authentication Required | Client hasn't provided valid credentials | Missing token, expired token, invalid credentials | User tries to access `/users/10` without logging in |
| **403 Forbidden** | Insufficient Permissions | Client is authenticated but lacks authorization | Valid user trying restricted action | Regular user attempts to delete admin account |
| **409 Conflict** | Resource Conflict | Request conflicts with current state | Duplicate resources, version conflicts | Creating user with email that already exists |
| **422 Unprocessable Entity** | Semantic Validation Error | Syntax correct but data semantically invalid | Business rule violations | Age is -5, date is in future when it shouldn't be |

---

### Understanding the Distinction: 401 vs 403

These are commonly confused, but they have distinct meanings:

**401 Unauthorized = "Who are you?"**
- The server doesn't know who's making the request
- Authentication is missing or invalid
- Client needs to provide valid credentials

**Example:**
```http
GET /users/10
```

```http
HTTP/1.1 401 Unauthorized
WWW-Authenticate: Bearer

{
  "status": 401,
  "error": "Unauthorized",
  "message": "Valid authentication token required"
}
```

---

**403 Forbidden = "I know who you are, but you can't do that"**
- The server knows who you are (authenticated)
- But you don't have permission for this action
- No amount of re-authenticating will help

**Example:**
```http
DELETE /users/50
Authorization: Bearer <valid_token_for_regular_user>
```

```http
HTTP/1.1 403 Forbidden

{
  "status": 403,
  "error": "Forbidden",
  "message": "Only administrators can delete users"
}
```

---

### 400 vs 422: Syntax vs Semantics

**400 Bad Request:**
- Request is malformed or syntactically invalid
- JSON parsing errors
- Missing required fields
- Wrong data types

**422 Unprocessable Entity:**
- Request syntax is valid
- But the content violates business rules or semantic constraints
- The server understood the request but can't process it

**Example of 400:**
```json
// Invalid JSON syntax
{
  "name": "Ahmed"
  "age": 25  // Missing comma
}
```

**Example of 422:**
```json
// Valid JSON, but semantically wrong
{
  "name": "Ahmed",
  "age": -25  // Negative age violates business rules
}
```

---

### 409 Conflict: State Conflicts

Use `409` when the request is valid but conflicts with the current state of the resource.

**Common scenarios:**
- Duplicate unique identifiers (emails, usernames)
- Version conflicts (optimistic locking)
- Concurrent modification issues

**Example:**
```http
POST /users
Content-Type: application/json

{
  "email": "ahmed@example.com",
  "name": "Ahmed"
}
```

```http
HTTP/1.1 409 Conflict

{
  "status": 409,
  "error": "Conflict",
  "message": "A user with email ahmed@example.com already exists"
}
```

---

## Spring Boot Implementation Example

Here's how these status codes map to actual code:

```java
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody User user) {
    
    // 401: Not authenticated
    if (!authService.isAuthenticated()) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .build();
    }
    
    // 403: Authenticated but not authorized
    if (!authService.hasRole("ADMIN")) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .build();
    }
    
    // 409: Email already exists (conflict)
    if (userService.existsByEmail(user.getEmail())) {
        ApiError error = new ApiError(
            409, 
            "Email already exists", 
            "/users"
        );
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
    }
    
    // 422: Semantic validation error
    if (user.getAge() < 0) {
        ApiError error = new ApiError(
            422, 
            "Age cannot be negative", 
            "/users"
        );
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(error);
    }
    
    // 201: Successfully created
    User saved = userService.save(user);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header("Location", "/users/" + saved.getId())
        .body(saved);
}
```

---

## Consistency Over Perfection

The most important principle: **be consistent** across your entire API.

### What Consistency Looks Like

**Same error structure everywhere:**
Every error response should follow the same JSON structure. Don't use different formats for different endpoints.

**Predictable status code usage:**
If you use `409` for duplicate emails in `/users`, use it for duplicate products in `/products` too.

**Standard patterns:**
- Creation always returns `201`
- Deletion always returns `204`
- Not found always returns `404`

### Example Consistent Error Response

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid email format",
  "path": "/users",
  "timestamp": "2026-01-13T14:30:00Z"
}
```

Use this exact structure for all errors—just change the values.

---

## Standard Error Response Design

A well-designed error response structure makes your API easier to use and debug.

### Recommended Fields

| Field | Purpose | Example |
|-------|---------|---------|
| `status` | HTTP status code (numeric) | `404` |
| `error` | Short standard description | `"Not Found"` |
| `message` | Detailed, human-readable explanation | `"User with ID 999 does not exist"` |
| `path` | URI that caused the error | `"/users/999"` |
| `timestamp` | When the error occurred (optional) | `"2026-01-13T14:30:00Z"` |

### Complete Error Response Example

```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Age cannot be negative",
  "path": "/users",
  "timestamp": "2026-01-13T14:30:00Z"
}
```

**Why these fields matter:**
- **status:** Clients can programmatically handle errors by code
- **error:** Standard HTTP reason phrase for clarity
- **message:** Helps developers debug issues
- **path:** Useful for logging and tracking which endpoint failed
- **timestamp:** Essential for correlating errors with server logs

---

## Global Error Handling in Spring Boot

Rather than handling errors in every controller method, use global exception handling:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            UserNotFoundException ex, 
            HttpServletRequest request) {
        
        ApiError error = new ApiError(
            404, 
            ex.getMessage(), 
            request.getRequestURI()
        );
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
    }

    @ExceptionHandler(EmailConflictException.class)
    public ResponseEntity<ApiError> handleConflict(
            EmailConflictException ex, 
            HttpServletRequest request) {
        
        ApiError error = new ApiError(
            409, 
            ex.getMessage(), 
            request.getRequestURI()
        );
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex, 
            HttpServletRequest request) {
        
        // Log the full error internally
        logger.error("Unexpected error", ex);
        
        // Return generic message to client
        ApiError error = new ApiError(
            500, 
            "An unexpected error occurred", 
            request.getRequestURI()
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }
}
```

### The ApiError Class

```java
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String path;
    private String timestamp;

    public ApiError(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.error = HttpStatus.valueOf(status).getReasonPhrase();
        this.timestamp = Instant.now().toString();
    }

    // Getters and setters
}
```

**Benefits of this approach:**
- Centralized error handling logic
- Consistent error responses across all endpoints
- Easier to maintain and update
- Automatic HTTP status code management

---

## Common Mistakes to Avoid

### ❌ Always Returning 200 OK

**Wrong:**
```http
HTTP/1.1 200 OK

{
  "success": false,
  "error": "User not found"
}
```

**Right:**
```http
HTTP/1.1 404 Not Found

{
  "status": 404,
  "message": "User not found"
}
```

**Why it matters:** HTTP infrastructure, caching, and monitoring tools rely on status codes. If everything returns 200, nothing works properly.

---

### ❌ Hiding Errors in Response Body Only

**Wrong:**
```http
HTTP/1.1 200 OK

{
  "errorCode": 404,
  "errorMessage": "Not found"
}
```

The status code says "success" but the body says "error." This confuses clients and breaks HTTP standards.

---

### ❌ Using 500 for Client Mistakes

**Wrong:**
```http
HTTP/1.1 500 Internal Server Error

{
  "message": "Email is required"
}
```

**Right:**
```http
HTTP/1.1 400 Bad Request

{
  "message": "Email is required"
}
```

**Rule:** Use 5xx codes only for server-side problems. Client validation errors should use 4xx codes.

---

### ❌ Inconsistent Error Formats

**Wrong:** Different formats across endpoints
```json
// Endpoint 1
{"error": "Not found"}

// Endpoint 2
{"status": 404, "message": "Resource missing", "path": "/users"}
```

**Right:** Same structure everywhere
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "[specific details]",
  "path": "[endpoint]"
}
```

---

## Complete Example: User Creation Endpoint

Here's everything put together:

```java
@PostMapping("/users")
public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
    
    try {
        // Business logic
        User created = userService.create(user);
        
        // Success: 201 Created with Location header
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", "/users/" + created.getId())
            .body(created);
            
    } catch (EmailAlreadyExistsException e) {
        // 409 Conflict
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ApiError(409, e.getMessage(), "/users"));
            
    } catch (InvalidAgeException e) {
        // 422 Unprocessable Entity
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ApiError(422, e.getMessage(), "/users"));
    }
}
```

---

## Key Takeaways

**1. Status codes are the primary communication mechanism**
Don't bury outcomes in response body text—use the right status code.

**2. Use the right code for the right situation**
- 2xx for success
- 4xx for client errors
- 5xx for server errors

**3. Be specific when it helps**
Use 201 instead of 200 for creation, 204 for deletions, 401 vs 403 for auth issues.

**4. Consistency matters more than perfection**
Pick a standard error structure and stick to it throughout your API.

**5. Leverage global exception handling**
Don't repeat error response logic in every controller—centralize it with `@RestControllerAdvice`.

**6. Think like a client**
Every status code and error message should help developers quickly understand what happened and what to do about it.

By properly using HTTP status codes, you create APIs that communicate clearly, work well with HTTP infrastructure, and provide excellent developer experience.