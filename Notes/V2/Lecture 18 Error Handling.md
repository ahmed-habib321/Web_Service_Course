# Robust Error Handling in REST APIs

## Why Exceptions Matter in RESTful Design

Many developers fall into the trap of returning `null` values or generic error messages when things go wrong. This approach creates ambiguity and forces clients to guess what happened:

```java
// Problematic approach
public Message getMessage(long id) {
    return messages.get(id);  // Returns null if not found
}
```

A client receiving `null` can't distinguish between "resource doesn't exist," "database connection failed," or "you don't have permission." This is fundamentally un-RESTful.

The solution is **explicit, typed exceptions**:

```java
public Message getMessage(long id) {
    Message msg = messages.get(id);
    if (msg == null) {
        throw new DataNotFoundException("Message with id " + id + " not found");
    }
    return msg;
}
```

Custom exceptions make your API predictable, eliminate ambiguity, and enable clients to implement proper error handling strategies.

---

## The Container Error Page Problem

By default, Java application containers respond to exceptions with HTML error pages—the kind you'd see in a browser during development. These pages might be helpful for humans debugging issues, but they're useless to REST API clients expecting JSON or XML.

Imagine a mobile app making a request and receiving back an HTML page with a stack trace. The app can't parse it, can't extract meaningful information, and ultimately crashes or displays cryptic errors to users.

REST APIs must return **machine-readable error responses** that clients can process programmatically. This is where exception mappers come into play.

---

## Exception Mappers: Translating Errors to HTTP

JAX-RS provides the `ExceptionMapper<T>` interface, which intercepts exceptions and converts them into proper HTTP responses:

```java
@Provider
public class DataNotFoundMapper implements ExceptionMapper<DataNotFoundException> {
    @Override
    public Response toResponse(DataNotFoundException ex) {
        return Response.status(Response.Status.NOT_FOUND)
                       .entity(new ErrorMessage(
                           ex.getMessage(), 
                           404, 
                           "http://docs.example.com/errors/404"
                       ))
                       .build();
    }
}
```

The `@Provider` annotation registers this mapper with the JAX-RS runtime. Now, whenever `DataNotFoundException` is thrown anywhere in your application, this mapper automatically intercepts it and generates a structured 404 response.

---

## Structured Error Payloads

Instead of plain text error messages, return structured objects that clients can parse and act upon:

```json
{
  "error": "Message with id 10 not found",
  "code": 404,
  "documentation": "http://docs.example.com/errors/404"
}
```

This payload provides:
- **error**: A human-readable description
- **code**: The HTTP status code for quick filtering
- **documentation**: A link to detailed troubleshooting information

You'd implement this with a simple ErrorMessage class:

```java
public class ErrorMessage {
    private String error;
    private int code;
    private String documentation;
    
    // Constructor, getters, setters
}
```

This standardized format helps clients diagnose issues, log meaningful information, and provide better user experiences.

---

## Exception Mapper Hierarchy

When multiple exception mappers exist, JAX-RS follows a specificity rule: **more specific mappers take precedence over generic ones**.

Consider this scenario:
- You have a `DataNotFoundMapper` that handles `DataNotFoundException`
- You also have a `GenericExceptionMapper` that handles `Throwable`

When a `DataNotFoundException` is thrown, the specific mapper handles it. If any other unexpected exception occurs, the generic mapper catches it as a safety net.

This hierarchy ensures predictable behavior while preventing any exception from escaping unmapped.

---

## Built-in JAX-RS Exceptions

JAX-RS includes several built-in exception classes that automatically map to appropriate HTTP status codes:

```java
throw new NotFoundException("Message not found");        // 404
throw new BadRequestException("Invalid format");         // 400
throw new ForbiddenException("Access denied");           // 403
throw new InternalServerErrorException("Server error");  // 500
```

These exceptions inherit from `WebApplicationException` and work out of the box without custom mappers. However, you can still override them with your own mappers if you need custom error payloads.

For quick prototyping or simple cases, these built-in exceptions save time and follow JAX-RS conventions.

---

## The Generic Exception Mapper Safety Net

No matter how carefully you code, unexpected exceptions will occur. A generic exception mapper acts as a last line of defense:

```java
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable ex) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(new ErrorMessage(
                           "An unexpected error occurred", 
                           500, 
                           null
                       ))
                       .build();
    }
}
```

This mapper catches anything that slips through your specific handlers—database connection failures, null pointer exceptions, or library errors. Instead of exposing stack traces or crashing, your API returns a clean 500 error.

**Important**: During development, you might want to temporarily disable this generic mapper to see raw exceptions and debug more easily. Re-enable it for production to maintain security and professionalism.

---

## Best Practices for Error Handling

**Separate Concerns Between Layers**

Your service layer should focus on business logic and throw meaningful exceptions. Let the resource layer and exception mappers handle the HTTP translation. This separation keeps code clean and testable.

**Use Semantically Correct Status Codes**

Don't return 500 for everything. Use 404 when resources don't exist, 400 for malformed requests, 401 for authentication failures, 403 for authorization issues, and 500 only for genuine server problems.

**Provide Actionable Error Information**

Include enough detail for clients to understand and potentially fix the problem. "Invalid request" is less helpful than "The 'email' field must be a valid email address."

**Implement a Mapper Hierarchy**

Start with specific mappers for your custom exceptions, add mappers for common scenarios (validation errors, authentication failures), and finish with a generic mapper for the unexpected.

**Leverage Built-in Exceptions When Appropriate**

If a built-in JAX-RS exception fits your needs and you don't require custom payloads, use it. This reduces code and follows established conventions.

---

## Complete Error Handling Flow

Here's how all the pieces work together in practice:

```java
@Path("/messages")
public class MessageResource {
    
    @GET
    @Path("/{id}")
    public Message getMessage(@PathParam("id") long id) {
        // Service throws DataNotFoundException if not found
        return service.getMessage(id);
    }
}
```

```java
@Provider
public class DataNotFoundMapper implements ExceptionMapper<DataNotFoundException> {
    @Override
    public Response toResponse(DataNotFoundException ex) {
        ErrorMessage error = new ErrorMessage(
            ex.getMessage(),
            404,
            "http://docs.example.com/errors/404"
        );
        return Response.status(Response.Status.NOT_FOUND)
                       .entity(error)
                       .build();
    }
}
```

When a client requests `/messages/999` and that message doesn't exist:

1. The service throws `DataNotFoundException`
2. The exception mapper intercepts it
3. The mapper constructs a structured error response
4. The client receives:

```json
{
  "error": "Message with id 999 not found",
  "code": 404,
  "documentation": "http://docs.example.com/errors/404"
}
```

The client can parse this JSON, check the status code, display an appropriate message, and potentially follow the documentation link for more information.

---

## Summary

Effective error handling transforms a fragile API into a robust, professional service. By using custom exceptions, you make error conditions explicit and predictable. Exception mappers ensure your API always returns machine-readable responses instead of HTML error pages. Structured error payloads give clients the information they need to handle failures gracefully. A well-designed mapper hierarchy catches both expected and unexpected errors, while built-in JAX-RS exceptions reduce boilerplate. Together, these techniques create APIs that fail gracefully and provide excellent developer experiences even when things go wrong.