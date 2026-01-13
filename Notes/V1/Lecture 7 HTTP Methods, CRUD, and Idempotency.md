## 1. HTTP methods define operations on resources

In REST, **what you do** to a resource is defined by the **HTTP method**, not the URL.

| HTTP Method | Purpose                 | CRUD Equivalent |
| ----------- | ----------------------- | --------------- |
| **GET**     | Retrieve data           | Read            |
| **POST**    | Create new resource     | Create          |
| **PUT**     | Update/replace resource | Update          |
| **DELETE**  | Remove resource         | Delete          |

📌 Example resource:

```http
/users/10
```

Different methods on the same URI perform different actions:

```http
GET    /users/10   → get user
PUT    /users/10   → update user
DELETE /users/10   → delete user
```

---

## 2. Same URI, multiple operations

REST allows **one URI** to support **many operations** depending on the HTTP method.

This avoids action-based URLs like:

```http
/updateUser
/deleteUser
```

Instead, REST uses:

```http
/users/{id}
```

This design keeps APIs:

* Clean
* Consistent
* Easy to extend

---

## 3. Idempotency: a critical REST concept

An operation is **idempotent** if repeating it **does not change the result** after the first execution.

### Non-idempotent method

❌ **POST**

* Each request creates a **new resource**
* Repeating it causes duplicates

```http
POST /users
```

Calling it twice → two users created

---

### Idempotent methods

✅ **GET**

* Always returns the same data (no state change)

✅ **PUT**

* Replaces or updates a resource to a specific state

```http
PUT /users/10
```

Calling it multiple times results in the same final state

✅ **DELETE**

* Deletes a resource once
* Repeating it has no additional effect

---

## 4. Why idempotency matters

Idempotency is important for:

* **Network failures**
* **Retries**
* **Distributed systems**

Example:
If a client sends a request and loses the connection, it may retry.

* Idempotent methods → safe to retry
* Non-idempotent methods → risk of duplication

This is why **PUT is preferred over POST for updates**.

---

## 5. CRUD on collections and single resources

CRUD applies to:

* **Collections**
* **Individual resources**
* **Nested resources**

### Collection operations

```http
GET  /users        → get all users
POST /users        → create user
```

### Single resource operations

```http
GET    /users/10
PUT    /users/10
DELETE /users/10
```

### Nested resources

```http
GET  /users/10/orders
POST /users/10/orders
```

This reflects real-world relationships naturally.

---

## 6. Correct method selection = predictable APIs

Choosing the correct HTTP method ensures:

* Predictable behavior
* Correct caching
* Proper client-side handling
* Safe retries
* Clear API semantics

Example problems when methods are misused:

* Using POST for reads → breaks caching
* Using POST for updates → breaks idempotency
* Using GET for deletes → dangerous and insecure

---

## 7. POST vs PUT: when to use each

### Use **POST** when:

* Creating a **new resource**
* Server generates the resource ID
* Operation is non-idempotent

```http
POST /users
```

### Use **PUT** when:

* Updating an **existing resource**
* Client knows the resource ID
* You want idempotent behavior

```http
PUT /users/10
```

---

## Java / Spring Boot mapping (quick view)

| HTTP Method | Spring Annotation |
| ----------- | ----------------- |
| GET         | `@GetMapping`     |
| POST        | `@PostMapping`    |
| PUT         | `@PutMapping`     |
| DELETE      | `@DeleteMapping`  |