## 1. Design first, implementation second

In REST, **API design is more important than code**.

* A poorly designed API is hard to fix later.
* Clients depend on URIs, so changing them breaks integrations.
* A good design:

  * Scales well
  * Is easy to understand
  * Requires minimal documentation

💡 Think of URIs as a **public contract**, not an internal detail.

---

## 2. Resource-based URI design

REST APIs model the world as **resources**.

* A resource represents an **entity or concept**
* Each resource is **uniquely identifiable by a URI**

Examples of resources:

* users
* messages
* orders
* products

```http
/messages
/messages/123
```

The URI identifies *what* the resource is, not *what you do* with it.

---

## 3. Use nouns, not verbs

URIs should represent **things**, not actions.

❌ Bad:

```http
/getMessages
/createMessage
/deleteMessage
```

✅ Good:

```http
/messages
/messages/123
```

The **HTTP method** defines the action:

* GET /messages
* POST /messages
* DELETE /messages/123

---

## 4. Use plural names for collections

Plural nouns make APIs more intuitive.

```http
/messages        → collection
/messages/123    → single resource
```

This clearly distinguishes between:

* A list of items
* One specific item

---

## 5. Instance vs collection resources

### Instance resource URIs

Identify a **single resource**:

```http
/messages/123
```

Operations:

* GET → retrieve
* PUT → update
* DELETE → remove

---

### Collection resource URIs

Represent a **list of resources**:

```http
/messages
```

Operations:

* GET → list resources
* POST → create a new resource

---

## 6. Nested URIs for relationships

Nested URIs express **relationships**, especially one-to-many.

Example:

```http
/messages/123/comments
```

Meaning:

* Message `123`
* All comments belonging to it

This is useful when:

* The child resource makes sense only within the parent context
* Clients frequently access data through the parent

⚠️ Avoid deep nesting:

```http
/users/5/messages/10/comments/3/likes   ❌
```

---

## 7. Balancing flat vs nested URIs

Good design balances:

* **Flat URIs** → flexibility
* **Nested URIs** → clarity

Example balance:

```http
/comments/3
/messages/123/comments
```

Both are valid, depending on client needs.

💡 Ask: *How will clients naturally access this data?*

---

## 8. Decouple URIs from backend implementation

URIs should **not expose internal technology details**.

❌ Bad:

```http
/spring/messages
/api/v1/jpa/messages
```

✅ Good:

```http
/messages
```

This ensures:

* You can change frameworks without breaking clients
* Long-term API stability

---

## 9. Query parameters for filtering & pagination

Query parameters refine results **without changing the resource**.

### Filtering

```http
/messages?year=2023
```

### Pagination

```http
/messages?offset=0&limit=10
```

Benefits:

* Prevents huge responses
* Improves performance
* Makes results predictable

---

## 10. Pagination best practices

* Always define a **default order**
* Return consistent results between pages
* Include metadata when possible

Example response:

```json
{
  "data": [...],
  "offset": 0,
  "limit": 10,
  "total": 125
}
```

---

## 11. Good URI design principles (summary)

A good RESTful URI is:

* **Clear** – easy to understand
* **Stable** – unlikely to change
* **Consistent** – follows patterns
* **Scalable** – supports growth
* **Client-focused** – designed around usage

---

## 11. Why version APIs?

APIs evolve over time. Versioning ensures that **existing clients don’t break** when you:

* Add new features
* Change response formats
* Modify endpoints
* Fix bugs that affect API structure

Without versioning, any change can break existing integrations.

---

## 12. Common versioning strategies

### **A. URI Versioning (Path versioning)**

* Version is included in the URI path
* Very explicit and visible

```http
GET /v1/users/10
GET /v2/users/10
```

**Pros:**

* Simple and intuitive
* Works with caching
* Easy to implement in Spring Boot with `@RequestMapping("/v1/users")`

**Cons:**

* Can clutter URIs if versions are many
* Changing resource location might be seen as a breaking change

---

### **B. Query Parameter Versioning**

* Version passed as a query parameter

```http
GET /users/10?version=1
GET /users/10?version=2
```

**Pros:**

* Doesn’t change URI path
* Can be flexible for experiments

**Cons:**

* Less visible than path versioning
* Harder to cache
* Less “RESTful” because the resource location stays the same but content changes

---

### **C. Header Versioning**

* Version is specified in HTTP headers

```http
GET /users/10
Accept: application/vnd.myapi.v1+json
```

**Pros:**

* Clean URIs
* Great for content negotiation
* Doesn’t clutter URL

**Cons:**

* Less discoverable for humans
* Slightly more complex to implement in Spring Boot (`@RequestMapping` with `headers`)

---

### **D. Content Negotiation / Media Type Versioning**

* Similar to header versioning but relies on custom media types

```http
Accept: application/vnd.myapi.v1+json
Accept: application/vnd.myapi.v2+json
```

**Pros:**

* Keeps URI clean
* Works well with HATEOAS or API hypermedia

**Cons:**

* Harder for simple clients to test
* Requires explicit handling in server code

---

### **E. No Versioning (Evolution via backward compatibility)**

* Only maintain one version and ensure **backward compatibility**
* Add new fields, deprecate old fields, keep existing contracts intact

**Pros:**

* Simplest approach if your API is small
* No multiple versions to maintain

**Cons:**

* Harder as the API grows
* Old clients may not handle changes well

💡 Usually used for **internal APIs** where client control is possible.

---

## 13. Versioning in Java/Spring Boot

**URI versioning example:**

```java
@RestController
@RequestMapping("/v1/users")
public class UserControllerV1 {
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) { ... }
}

@RestController
@RequestMapping("/v2/users")
public class UserControllerV2 {
    @GetMapping("/{id}")
    public UserV2 getUser(@PathVariable Long id) { ... }
}
```

**Header versioning example:**

```java
@GetMapping(value = "/users/{id}", headers = "X-API-VERSION=1")
public User getUserV1(@PathVariable Long id) { ... }

@GetMapping(value = "/users/{id}", headers = "X-API-VERSION=2")
public UserV2 getUserV2(@PathVariable Long id) { ... }
```

---

## 14. Best Practices

1. Prefer **URI versioning** for public APIs (simple, discoverable).
2. Consider **header or media type versioning** for complex APIs with frequent updates.
3. Keep versions **stable**; don’t change an existing version.
4. Use semantic versioning if possible: `v1`, `v2` for major changes.
5. Avoid breaking changes whenever possible; deprecate old fields gradually.