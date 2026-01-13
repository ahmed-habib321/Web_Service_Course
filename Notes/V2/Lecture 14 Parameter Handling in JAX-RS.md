# JAX-RS Parameter Handling: A Complete Guide

## Understanding Parameter Sources in REST APIs

REST APIs don't just receive data through request bodies. Information flows into your endpoints from multiple sources—URL paths, query strings, headers, cookies, and more. JAX-RS provides specialized annotations to extract data from each of these sources, making your code both cleaner and more maintainable.

---

## Capturing Dynamic URL Segments with `@PathParam`

When you need to identify a specific resource, `@PathParam` extracts values from the URL path itself. This is your go-to choice for resource identifiers.

```java
@GET
@Path("/{id}")
public Message getMessage(@PathParam("id") long id) {
    return service.get(id);
}
```

A request to `GET /messages/10` automatically binds `10` to the `id` parameter. This pattern works perfectly for operations on specific resources—retrieving a user by ID, fetching a particular order, or accessing an individual post.

---

## Filtering and Options with `@QueryParam`

Query parameters excel at providing optional filtering, sorting, and pagination capabilities. Unlike path parameters, these are inherently optional and don't affect the core resource being accessed.

```java
@GET
public List<Message> getMessages(
    @QueryParam("year") int year,
    @QueryParam("offset") int offset,
    @QueryParam("limit") int limit
) {
    return service.getMessages(year, offset, limit);
}
```

A request like `/messages?year=2023&offset=0&limit=10` gives clients flexibility in how they retrieve data without changing the fundamental endpoint structure.

---

## The Uncommon `@MatrixParam`

Matrix parameters use semicolons within path segments: `/messages;year=2023;author=ahmed`. While part of the JAX-RS specification, they're rarely seen in modern API design.

```java
@GET
public List<Message> getByMatrix(
    @MatrixParam("year") int year,
    @MatrixParam("author") String author
) {
    return service.getFiltered(year, author);
}
```

Most developers prefer query parameters for their familiarity and broader tooling support.

---

## Accessing Request Metadata with `@HeaderParam`

Headers carry important metadata—authentication tokens, request identifiers, API versions, and custom tracking information.

```java
@GET
public Response getWithHeader(
    @HeaderParam("X-Request-ID") String requestId
) {
    return service.processWithTracking(requestId);
}
```

This approach is particularly valuable for cross-cutting concerns like distributed tracing, where correlation IDs need to flow through your entire system.

---

## Session Management with `@CookieParam`

Cookies traditionally handle session state and user tracking. JAX-RS makes reading cookie values straightforward.

```java
@GET
public Response getFromCookie(
    @CookieParam("sessionId") String sessionId
) {
    return service.getUserSession(sessionId);
}
```

While modern APIs often use token-based authentication, cookies remain relevant for browser-based applications and certain authentication flows.

---

## Processing Form Submissions with `@FormParam`

When handling HTML form submissions with `Content-Type: application/x-www-form-urlencoded`, `@FormParam` extracts individual field values.

```java
@POST
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public Response submitForm(
    @FormParam("username") String username,
    @FormParam("password") String password
) {
    return service.authenticate(username, password);
}
```

This pattern appears most often in legacy systems or hybrid applications that blend traditional web forms with REST APIs.

---

## Runtime Information via `@Context`

The `@Context` annotation injects runtime request information, giving you access to objects that describe the current request environment.

```java
@GET
public Response getContext(@Context UriInfo uriInfo) {
    String currentPath = uriInfo.getPath();
    return Response.ok(currentPath).build();
}
```

Commonly injected types include `UriInfo` (URI details), `HttpHeaders` (all headers), `SecurityContext` (authentication info), and `Request` (HTTP request metadata). This enables dynamic behavior based on the request context.

---

## Simplifying Complex Parameters with `@BeanParam`

As endpoints grow more complex, method signatures can become unwieldy with many individual parameters. `@BeanParam` aggregates related parameters into a reusable bean.

First, define your parameter bean:

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

Then use it in your endpoint:

```java
@GET
public List<Message> getFiltered(@BeanParam MessageFilter filter) {
    return service.getFiltered(filter);
}
```

This approach yields cleaner method signatures, promotes reusability across multiple endpoints, and simplifies maintenance when filtering logic needs to change.

---

## Implementing Filtering Logic

JAX-RS annotations extract parameters but don't apply filtering logic automatically. You'll need to implement conditional logic in your service layer.

```java
public List<Message> getFiltered(MessageFilter filter) {
    List<Message> results = getAllMessages();
    
    if (filter.getYear() > 0) {
        results = filterByYear(results, filter.getYear());
    }
    
    if (filter.getAuthor() != null) {
        results = filterByAuthor(results, filter.getAuthor());
    }
    
    return paginate(results, filter.getOffset(), filter.getLimit());
}
```

Keep your resource classes thin—they should handle HTTP concerns while business logic lives in the service layer.

---

## Providing Default Values

When clients omit optional parameters, `@DefaultValue` provides sensible fallbacks.

```java
@GET
public List<Message> getMessages(
    @QueryParam("year") @DefaultValue("2023") int year,
    @QueryParam("limit") @DefaultValue("10") int limit
) {
    return service.getMessages(year, limit);
}
```

Now `/messages?year=2024` uses year 2024 and limit 10, while `/messages` applies both defaults. This ensures predictable API behavior even when clients don't specify every parameter.

---

## Custom Parameter Conversion

Sometimes you need to convert string parameters into complex Java objects automatically. JAX-RS supports custom converters for this purpose.

**Step 1:** Create the converter:

```java
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

**Step 2:** Register it with a provider:

```java
@Provider
public class RoleConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(
        Class<T> rawType, 
        Type genericType, 
        Annotation[] annotations
    ) {
        if (rawType.equals(Role.class)) {
            return (ParamConverter<T>) new RoleConverter();
        }
        return null;
    }
}
```

**Step 3:** Use it naturally in your endpoints:

```java
@GET
@Path("/users")
public List<User> getUsers(@QueryParam("role") Role role) {
    return service.getByRole(role);
}
```

This centralizes conversion logic, eliminates boilerplate, and works seamlessly with enums, dates, and custom types.

---

## Working with Security Parameters

JAX-RS provides elegant ways to access authentication and authorization information.

### Accessing Security Context

```java
@GET
@Path("/profile")
public Response getProfile(@Context SecurityContext securityContext) {
    String username = securityContext.getUserPrincipal().getName();
    boolean isAdmin = securityContext.isUserInRole("ADMIN");
    return Response.ok(service.getProfile(username, isAdmin)).build();
}
```

### Extracting Authentication Tokens

```java
@GET
@Path("/secure")
public Response secureEndpoint(
    @HeaderParam("Authorization") String authHeader
) {
    String token = authHeader.replace("Bearer ", "");
    User user = service.validateToken(token);
    return Response.ok(user).build();
}
```

You can also combine these with custom security annotations like `@RolesAllowed("ADMIN")` to declaratively restrict endpoint access.

---

## Key Takeaways

**Use `@PathParam`** when identifying specific resources—these values are required and define which resource you're operating on.

**Prefer `@QueryParam`** for optional filtering, sorting, and pagination—they modify how data is retrieved without changing the core resource.

**Leverage `@BeanParam`** when dealing with many related parameters—it reduces complexity and improves maintainability.

**Apply `@DefaultValue`** to optional parameters for consistent behavior across all client requests.

**Keep business logic in services**—resource classes should focus on HTTP concerns and delegate actual processing elsewhere.

**Avoid matrix parameters** unless you have a specific reason—they're valid but uncommon in practice.

Parameter handling is fundamental to building clean, maintainable REST APIs. By using the right annotation for each data source and organizing your code thoughtfully, you create APIs that are both powerful and easy to work with.