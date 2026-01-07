## 1. Why explicit exceptions matter

Returning `null` or raw error messages is **not RESTful**.

* Use **custom exceptions** like `DataNotFoundException` to indicate specific errors.
* Benefits:

  * Makes the API **predictable**
  * Avoids ambiguous responses
  * Simplifies client handling

```java
public Message getMessage(long id) {
    Message msg = messages.get(id);
    if (msg == null) {
        throw new DataNotFoundException("Message with id " + id + " not found");
    }
    return msg;
}
```

---

## 2. Avoid default container error pages

* Java containers return **HTML error pages** by default.
* REST APIs should return **machine-readable JSON or XML**, not HTML.
* This ensures **clients can handle errors programmatically**.

---

## 3. `ExceptionMapper` – mapping exceptions to responses

JAX-RS provides `ExceptionMapper<T>`:

```java
@Provider
public class DataNotFoundMapper implements ExceptionMapper<DataNotFoundException> {
    @Override
    public Response toResponse(DataNotFoundException ex) {
        return Response.status(Response.Status.NOT_FOUND)
                       .entity(new ErrorMessage(ex.getMessage(), 404, "http://docs.example.com/errors/404"))
                       .build();
    }
}
```

* `@Provider` → registers the mapper automatically
* Converts exceptions → meaningful HTTP responses

---

## 4. Custom payloads

Include structured information for the client:

```json
{
  "error": "Data not found",
  "code": 404,
  "documentation": "http://docs.example.com/errors/404"
}
```

* Helps clients **diagnose and handle errors**
* Standardizes API error responses

---

## 5. Hierarchical exception handling

* **Specific mappers** take precedence over **generic mappers**
* Example:

  * `DataNotFoundMapper` handles 404 errors
  * `GenericExceptionMapper` handles all uncaught exceptions

---

## 6. Built-in exception support

* `WebApplicationException` and subclasses (`NotFoundException`, `BadRequestException`) automatically map to HTTP responses
* You can still override with **custom mappers** if needed

```java
throw new NotFoundException("Message not found");
```

Maps automatically to:

```http
404 Not Found
```

---

## 7. Generic exception mapper

Catches **uncaught exceptions**:

```java
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable ex) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(new ErrorMessage("Unexpected error occurred", 500, null))
                       .build();
    }
}
```

* Improves **robustness**
* Prevents leaking stack traces to clients

💡 For testing, you can **disable generic mappers** to see raw exceptions.

---

## 8. Best practices

1. **Separate business logic from error handling**

   * Service layer throws exceptions
   * Resource layer relies on mappers to convert exceptions to responses

2. **Use meaningful HTTP status codes**:

   * 404 → resource missing
   * 400 → bad request
   * 500 → server error

3. **Provide structured payloads**

   * Message, code, documentation

4. **Hierarchy of mappers**

   * Specific → generic
   * Ensures predictable behavior

5. **Leverage built-in exceptions**

   * Saves code and follows JAX-RS conventions

---

## 9. Example: Full flow

```java
@Path("/messages")
public class MessageResource {

    @GET
    @Path("/{id}")
    public Message getMessage(@PathParam("id") long id) {
        return service.getMessage(id); // throws DataNotFoundException if not found
    }
}

@Provider
public class DataNotFoundMapper implements ExceptionMapper<DataNotFoundException> {
    public Response toResponse(DataNotFoundException ex) {
        return Response.status(Response.Status.NOT_FOUND)
                       .entity(new ErrorMessage(ex.getMessage(), 404, "http://docs.example.com/errors/404"))
                       .build();
    }
}
```

Client sees:

```json
{
  "error": "Message with id 10 not found",
  "code": 404,
  "documentation": "http://docs.example.com/errors/404"
}
```