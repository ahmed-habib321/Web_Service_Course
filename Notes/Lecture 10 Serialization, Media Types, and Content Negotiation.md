## 1. REST returns *representations*, not resources

In REST, the server never sends the **actual resource object**.
It sends a **representation** of that resource.

* A *resource* → conceptual entity (User, Message, Order)
* A *representation* → JSON, XML, etc.

```http
GET /messages/1
```

Response (JSON representation):

```json
{
  "id": 1,
  "text": "Hello REST"
}
```

This allows the **same resource** to be represented in **multiple formats**.

---

## 2. Why JSON is preferred

JSON is the dominant media type in REST APIs because it is:

* Simple and readable
* Compact (smaller payloads)
* Native to JavaScript and web clients
* Easy to map to Java objects

```http
Content-Type: application/json
```

XML is still supported but mainly for legacy systems.

---

## 3. Media types and content negotiation

**Content negotiation** lets the client and server agree on data format.

### Key headers

#### `Content-Type`

* Describes the **format of the request body**
* Used mainly with POST / PUT / PATCH

```http
Content-Type: application/json
```

#### `Accept`

* Describes the **response format the client wants**

```http
Accept: application/json
```

---

### Content negotiation flow

1. Client sends `Accept`
2. Server checks supported formats
3. Server responds in a matching format
4. If unsupported → `406 Not Acceptable`

```http
HTTP/1.1 406 Not Acceptable
```

💡 This ensures **interoperability** between diverse clients.

---

## 4. Clear distinction: Accept vs Content-Type

| Header         | Purpose                     |
| -------------- | --------------------------- |
| `Content-Type` | Format of **incoming** data |
| `Accept`       | Format of **outgoing** data |

Confusing these headers leads to broken APIs and client errors.

---

## 5. JAX-RS annotations for media types

### `@Produces`

Defines the **response format**.

```java
@Produces(MediaType.APPLICATION_JSON)
```

### `@Consumes`

Defines the **accepted request format**.

```java
@Consumes(MediaType.APPLICATION_JSON)
```

---

### Method-level control

```java
@GET
@Path("/{id}")
@Produces(MediaType.APPLICATION_JSON)
public Message getMessage(@PathParam("id") long id) {
    return service.getMessage(id);
}
```

Each endpoint can expose **different formats** if needed.

---

## 6. Jersey serialization & deserialization

Jersey automatically converts:

* Java objects → JSON/XML (serialization)
* JSON/XML → Java objects (deserialization)

This works via **MessageBodyReader** and **MessageBodyWriter**.

Common libraries:

* **Jackson** → JSON
* **MOXy / JAXB** → XML

---

### XML support

To enable XML serialization:

```java
@XmlRootElement
public class Message {
    private long id;
    private String text;
}
```

Without `@XmlRootElement`, XML conversion fails.

---

## 7. Switching formats is trivial

To change output format:

```java
@Produces(MediaType.APPLICATION_XML)
```

Or support both:

```java
@Produces({
    MediaType.APPLICATION_JSON,
    MediaType.APPLICATION_XML
})
```

The response format is chosen using the `Accept` header.

---

## 8. Automatic JSON deserialization

When a request body is JSON and:

* `Content-Type: application/json`
* Method parameter is a Java object

Jersey automatically converts JSON → Java.

```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
public Response createMessage(Message message) {
    service.save(message);
    return Response.status(201).build();
}
```

No manual parsing required.

---

## 9. Server-generated IDs

When creating resources:

* Client may omit the ID
* Server generates it

```json
{
  "text": "New message"
}
```

The server assigns the ID and returns the created resource or location.

---

## 10. Content-Type is mandatory for POST with JSON

POST requests with JSON **must** include:

```http
Content-Type: application/json
```

Without it:

* Server cannot deserialize payload
* Request fails

---

## 11. Multiple methods, same path, different formats

JAX-RS allows **same URI + different media types**:

```java
@POST
@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
public Response createJson(Message msg) { }

@POST
@Path("/messages")
@Consumes(MediaType.APPLICATION_XML)
public Response createXml(Message msg) { }
```

Benefits:

* Cleaner logic
* Easier maintenance
* Better separation of concerns

---

## 12. Unsupported media types

* Unsupported `Accept` → `406 Not Acceptable`
* Unsupported `Content-Type` → request rejected

This protects the API from invalid data formats.

---

## **13. PUT vs PATCH**

Both **PUT** and **PATCH** are used to update resources, but they differ in **scope** and **behavior**.

| Feature         | PUT                                        | PATCH                                   |
| --------------- | ------------------------------------------ | --------------------------------------- |
| **Purpose**     | Replace the entire resource                | Update part of the resource             |
| **Idempotency** | ✅ Idempotent                               | ✅ Idempotent (if implemented carefully) |
| **Payload**     | Full resource                              | Only the fields that need changes       |
| **Behavior**    | Missing fields may be overwritten or reset | Only specified fields are updated       |
| **Use case**    | Full updates                               | Partial updates                         |

**Example:** Suppose we have a User resource:

```json
{
  "id": 10,
  "name": "Ahmed",
  "email": "ahmed@example.com",
  "age": 25
}
```

* **PUT** update:

```http
PUT /users/10
```

Payload:

```json
{
  "id": 10,
  "name": "Ahmed H.",
  "email": "ahmed@example.com",
  "age": 26
}
```

* Must include **all fields**, otherwise missing ones may be reset.

* **PATCH** update:

```http
PATCH /users/10
```

Payload:

```json
{
  "age": 26
}
```

* Only updates the `age` field.
* Other fields remain unchanged.

---

## **14. PATCH & Partial Updates**

* **PATCH** is ideal for **partial resource updates**.
* It is especially useful when resources are **large**, to reduce payload size and network traffic.
* **Idempotency:** PATCH can be idempotent if the update operation leads to the same final state regardless of repeated requests.

**Java / JAX-RS Example:**

```java
@PATCH
@Path("/users/{id}")
@Consumes(MediaType.APPLICATION_JSON)
public Response updateUserPartial(@PathParam("id") long id, Map<String, Object> updates) {
    userService.applyUpdates(id, updates);
    return Response.ok().build();
}
```

* Here, `updates` contains only the fields to change.
* Server logic merges updates with existing resource.

---

## **15. Custom Media Types**

* **Custom media types** allow APIs to **version** or **specialize responses**.
* Syntax:

```
application/vnd.{company}.{resource}+json
```

**Example:**

```http
Accept: application/vnd.mycompany.user-v2+json
```

* This tells the server: "I want version 2 of the User resource in JSON format."
* Enables:

  * Smooth versioning
  * Multiple representations of the same resource
  * Backward compatibility for clients

**Java / JAX-RS Example:**

```java
@GET
@Path("/users/{id}")
@Produces("application/vnd.mycompany.user-v2+json")
public UserV2 getUserV2(@PathParam("id") long id) { ... }
```

---

## **16. Versioning with Media Types**

* Versioning is crucial for long-lived APIs.
* Common strategies:

  1. **URI versioning** (easiest to implement)

     ```http
     /v1/users/10
     /v2/users/10
     ```
  2. **Query parameters**

     ```http
     /users/10?version=2
     ```
  3. **Custom media types** (preferred for REST purists)

     ```http
     Accept: application/vnd.mycompany.user-v2+json
     ```
* **Advantages of media-type versioning:**

  * Keeps URI clean
  * Allows multiple versions to coexist
  * Works well with content negotiation