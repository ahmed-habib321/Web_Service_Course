## 1. Why use `Response` objects

Instead of returning a raw entity (e.g., `Message`), using `Response` gives **full control**:

* HTTP status code
* Headers
* Response body (entity)
* Metadata (e.g., caching info)

```java
@GET
@Path("/{id}")
public Response getMessage(@PathParam("id") long id) {
    Message msg = service.get(id);
    return Response.ok(msg).build(); // 200 OK
}
```

💡 Raw entity → default 200 OK, no headers.
`Response` → flexible, explicit, RESTful.

---

## 2. Returning `201 Created` for new resources

When creating resources via **POST**, best practice is to:

* Return **201 Created**
* Include a **Location header** pointing to the new resource
* Optionally return the created entity

```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
public Response addMessage(Message message, @Context UriInfo uriInfo) {
    Message created = service.add(message);
    URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
    return Response.created(uri).entity(created).build();
}
```

* `Response.created(uri)` → sets status 201 and Location header
* `entity(created)` → includes the resource in response body

---

## 3. Building URIs dynamically with `@Context UriInfo`

`UriInfo` allows dynamic URI construction:

```java
@Context
UriInfo uriInfo;
```

Then, build a URI for a resource:

```java
URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(newId)).build();
```

* `getAbsolutePathBuilder()` → returns the current request path builder
* `path(newId)` → appends resource ID
* `.build()` → final URI object

This ensures **correct, fully qualified URIs**.

---

## 4. Builder pattern for chaining

The `Response` class uses the **builder pattern**, allowing method chaining:

```java
Response.ok(entity)
        .header("X-Custom-Header", "value")
        .build();
```

Other examples:

```java
Response.status(404).entity("Message not found").build();
Response.accepted().build();      // 202 Accepted
Response.notModified().build();    // 304 Not Modified
```

---

## 5. Helper methods for common responses

JAX-RS provides **shortcut methods**:

| Method                   | Status | Usage                              |
| ------------------------ | ------ | ---------------------------------- |
| `Response.ok()`          | 200    | Successful retrieval               |
| `Response.created(URI)`  | 201    | Resource creation                  |
| `Response.accepted()`    | 202    | Request accepted, processing later |
| `Response.notModified()` | 304    | Resource not modified (caching)    |
| `Response.noContent()`   | 204    | Success with no body               |

These improve **readability** and **consistency**.

---

## 6. Best practices for response handling

1. **Always use meaningful status codes**:

   * 200 OK → successful GET
   * 201 Created → successful POST
   * 204 No Content → successful DELETE/PUT without body
   * 404 Not Found → resource missing
   * 400 Bad Request → validation errors

2. **Include Location headers when creating resources**

   * Helps clients discover newly created entities

3. **Return helpful entities when possible**

   * Especially after creation or update

4. **Use `Response` for flexibility**

   * Headers, caching, conditional responses

---

## 7. Example: Full POST method with best practices

```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response createMessage(Message message, @Context UriInfo uriInfo) {
    Message created = service.add(message);
    URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
    return Response.created(location)
                   .entity(created)
                   .header("X-API-Version", "1.0")
                   .build();
}
```

* Status → 201 Created
* Location → `/messages/{id}`
* Body → created resource
* Custom header → optional metadata