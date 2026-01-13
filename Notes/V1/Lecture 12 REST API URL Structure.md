## 1. Anatomy of a REST API URL

A REST API URL is composed of several logical parts:

```
http://localhost:8080/myapp/webapi/myresource
```

Breakdown:

1. **Server address**
   `http://localhost:8080`
2. **Application context**
   `/myapp`
3. **Base path** (configured globally)
   `/webapi`
4. **Resource identifier**
   `/myresource`

💡 This separation helps keep URLs organized and scalable.

---

## 2. Base path (`/webapi`)

The base path is usually defined once for the whole application.

Example (Jersey configuration):

```java
@ApplicationPath("/webapi")
public class MyApplication extends Application {
}
```

All REST endpoints start with:

```
/webapi
```

---

## 3. Class-level `@Path`

Class-level `@Path` defines the **base URI for a resource**.

```java
@Path("/messages")
public class MessageResource {
}
```

This maps to:

```
/webapi/messages
```

---

## 4. Method-level `@Path`

Method-level `@Path` extends the class-level path.

```java
@GET
@Path("/{id}")
public Message getMessage(@PathParam("id") long id) {
    return service.getMessage(id);
}
```

Final URL:

```
/webapi/messages/{id}
```

💡 JAX-RS combines **class path + method path** automatically.

---

## 5. Path parameters with `{}`

Curly braces `{}` indicate **path parameters**.

```java
@Path("/{id}")
```

This means:

* `id` is dynamic
* Value is extracted from the URL

Example request:

```http
GET /webapi/messages/10
```

---

## 6. Accessing path parameters with `@PathParam`

```java
@GET
@Path("/{id}")
public Message getMessage(@PathParam("id") long id) {
    return service.getMessage(id);
}
```

* The value `10` is injected into `id`
* No manual parsing needed

---

## 7. Automatic type conversion

JAX-RS automatically converts path parameters to Java types:

Supported types include:

* `int`
* `long`
* `double`
* `String`
* `UUID`
* Custom types (with converters)

```java
@Path("/{year}")
public Response getByYear(@PathParam("year") int year) {
}
```

If conversion fails → request is rejected.

---

## 8. Multiple path parameters

You can capture multiple parameters in a single path.

```java
@Path("/{year}/{month}")
public Response getByDate(
    @PathParam("year") int year,
    @PathParam("month") int month
) {
}
```

URL:

```
/webapi/reports/2023/10
```

---

## 9. Using regular expressions in `@Path`

Regular expressions restrict acceptable path values.

```java
@Path("/{id: \\d+}")
```

This matches:

* Only numeric IDs

Example:

```java
@GET
@Path("/{id: \\d+}")
public Message getNumericId(@PathParam("id") long id) {
}
```

Requests like:

```
/messages/abc   ❌
/messages/123   ✅
```

---

## 10. Regex for advanced routing

Example with multiple regex constraints:

```java
@Path("/{year: \\d{4}}/{month: \\d{2}}")
```

Valid:

```
/reports/2023/10
```

Invalid:

```
/reports/23/1
```

💡 Regex helps validate input **at the routing level**, before code executes.

---

## 11. Why this structure matters

A well-defined URL structure:

* Improves readability
* Reduces validation code
* Prevents invalid requests early
* Makes APIs predictable