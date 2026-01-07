## 1. Why parameter handling matters

REST APIs get data from many places, not just the request body.

Data can come from:

* The URL path
* Query string
* Headers
* Cookies
* Forms
* Request metadata

JAX-RS provides **dedicated annotations** for each source, keeping code clean and expressive.

---

## 2. `@PathParam` – URL path variables

Used to capture **dynamic segments** of the URI.

```java
@GET
@Path("/{id}")
public Message getMessage(@PathParam("id") long id) {
    return service.get(id);
}
```

Request:

```
GET /messages/10
```

💡 Best for identifying **specific resources**.

---

## 3. `@QueryParam` – query string parameters

Used for:

* Filtering
* Sorting
* Pagination
* Optional criteria

```java
@GET
public List<Message> getMessages(
    @QueryParam("year") int year,
    @QueryParam("offset") int offset,
    @QueryParam("limit") int limit
) {
}
```

Request:

```
/messages?year=2023&offset=0&limit=10
```

💡 Query parameters are optional by nature.

---

## 4. `@MatrixParam` – semicolon parameters

Matrix parameters appear **inside path segments**.

```http
/messages;year=2023;author=ahmed
```

```java
@GET
public List<Message> getByMatrix(
    @MatrixParam("year") int year,
    @MatrixParam("author") String author
) {
}
```

⚠️ Rarely used in modern APIs but part of JAX-RS spec.

---

## 5. `@HeaderParam` – HTTP headers

Used to read:

* Custom headers
* Tokens
* Versioning info

```java
@GET
public Response getWithHeader(
    @HeaderParam("X-Request-ID") String requestId
) {
}
```

Useful for:

* Tracing
* Correlation IDs
* Custom metadata

---

## 6. `@CookieParam` – cookies

Reads cookie values from the request.

```java
@GET
public Response getFromCookie(
    @CookieParam("sessionId") String sessionId
) {
}
```

Common use:

* Session handling
* Tracking

---

## 7. `@FormParam` – form data

Used for HTML form submissions with:

```
Content-Type: application/x-www-form-urlencoded
```

```java
@POST
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public Response submitForm(
    @FormParam("username") String username,
    @FormParam("password") String password
) {
}
```

💡 Mostly used in legacy or hybrid applications.

---

## 8. `@Context` – request context injection

Injects **runtime request information**.

Common injected objects:

* `UriInfo`
* `HttpHeaders`
* `SecurityContext`
* `Request`

```java
@GET
public Response getContext(@Context UriInfo uriInfo) {
    return Response.ok(uriInfo.getPath()).build();
}
```

This allows dynamic behavior based on request metadata.

---

## 9. `@BeanParam` – parameter aggregation

Combines multiple parameters into a **single reusable bean**.

```java
public class MessageFilter {

    @QueryParam("year")
    private int year;

    @QueryParam("author")
    private String author;

    @QueryParam("offset")
    private int offset;

    @QueryParam("limit")
    private int limit;
}
```

```java
@GET
public List<Message> getFiltered(@BeanParam MessageFilter filter) {
    return service.getFiltered(filter);
}
```

Benefits:

* Cleaner method signatures
* Reusable filter logic
* Easier maintenance

---

## 10. Handling multiple filter combinations

JAX-RS does **not** auto-apply filtering logic.

Inside the method or service layer:

* Check which parameters are present
* Apply conditional logic

```java
if (year > 0) {
    // filter by year
}
```

💡 Business logic belongs in the **service layer**, not the resource.

---

## 11. Best practices summary

* Use `@PathParam` for **resource identity**
* Use `@QueryParam` for **optional filters**
* Use `@BeanParam` to reduce complexity
* Avoid overusing matrix params
* Keep logic out of resource classes

---

## 12. `@DefaultValue` – providing defaults for optional parameters

When using `@QueryParam`, `@PathParam`, or other parameter annotations, sometimes the client may **omit the parameter**.
`@DefaultValue` lets you specify a fallback value.

```java
@GET
public List<Message> getMessages(
    @QueryParam("year") @DefaultValue("2023") int year,
    @QueryParam("limit") @DefaultValue("10") int limit
) {
    return service.getMessages(year, limit);
}
```

Behavior:

* `/messages?year=2024` → year = 2024, limit = 10
* `/messages` → year = 2023, limit = 10 (defaults applied)

💡 Ensures consistent API behavior even when clients omit optional filters.

---

## 13. Custom parameter converters

Sometimes you want to **convert complex query/path/header parameters into Java objects** automatically.

### Step 1: Implement `ParamConverter`

```java
import javax.ws.rs.ext.ParamConverter;

public class RoleConverter implements ParamConverter<Role> {
    @Override
    public Role fromString(String value) {
        return Role.valueOf(value.toUpperCase());
    }
    @Override
    public String toString(Role role) {
        return role.name();
    }
}
```

### Step 2: Register it with `ParamConverterProvider`

```java
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;

@Provider
public class RoleConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if(rawType.equals(Role.class)) {
            return (ParamConverter<T>) new RoleConverter();
        }
        return null;
    }
}
```

### Step 3: Use in resource method

```java
@GET
@Path("/users")
public List<User> getUsers(@QueryParam("role") Role role) {
    return service.getByRole(role);
}
```

✅ Benefits:

* Centralized conversion logic
* Supports enums, dates, complex objects
* Reduces boilerplate in resource methods

---

## 14. Security-related parameters

JAX-RS provides **access to authentication, authorization, and security context** via annotations and `@Context`.

### 3.1 Using `@Context SecurityContext`

```java
@GET
@Path("/profile")
public Response getProfile(@Context SecurityContext securityContext) {
    String username = securityContext.getUserPrincipal().getName();
    boolean isAdmin = securityContext.isUserInRole("ADMIN");
    return Response.ok(service.getProfile(username)).build();
}
```

### 3.2 Reading authentication tokens from headers

```java
@GET
@Path("/secure")
public Response secureEndpoint(@HeaderParam("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    return Response.ok(service.validateToken(token)).build();
}
```

### 3.3 Combining with custom annotations

* You can create **custom security annotations** (e.g., `@RolesAllowed("ADMIN")`)
* Ensures certain endpoints are accessible only to authorized users