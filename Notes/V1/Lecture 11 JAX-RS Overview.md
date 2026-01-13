## 1. What is JAX-RS?

**JAX-RS (Java API for RESTful Web Services)** is the **official Java standard** for building REST APIs.

* It defines *how* Java classes expose REST endpoints
* It is part of **Jakarta EE** (formerly Java EE)
* It focuses on **HTTP + REST concepts**, not frameworks

💡 JAX-RS is to REST what **JPA** is to databases: a standard API.

---

## 2. JAX-RS is an API, not an implementation

JAX-RS provides:

* Interfaces
* Annotations
* Contracts

It does **not** provide:

* An HTTP server
* A runtime engine

That’s why **libraries implement JAX-RS**.

---

## 3. Popular JAX-RS implementations

Several libraries provide runtime implementations of JAX-RS:

| Implementation | Description                         |
| -------------- | ----------------------------------- |
| **Jersey**     | Reference implementation (official) |
| **RESTEasy**   | Red Hat / JBoss implementation      |
| **RESTlet**    | Lightweight REST framework          |

Because they all follow the JAX-RS standard:

* The same annotations work everywhere
* Code is portable with minimal changes

---

## 4. Why learning JAX-RS matters

Learning JAX-RS means you are learning:

* **Standard REST concepts**
* **Portable Java API design**

Benefits:

* You’re not locked into one framework
* Easier migration between servers
* Skills apply across multiple stacks

💡 Frameworks may change, standards last.

---

## 5. Jersey: the reference implementation

**Jersey** is:

* The official reference implementation of JAX-RS
* Actively maintained
* Feature-rich
* Often used for learning and production

Many JAX-RS examples and tutorials use Jersey because:

* It strictly follows the specification
* Behavior matches the standard closely

---

## 6. Annotation-based programming model

JAX-RS uses annotations to map Java code to HTTP requests.

### Mapping HTTP methods

| HTTP Method | JAX-RS Annotation |
| ----------- | ----------------- |
| GET         | `@GET`            |
| POST        | `@POST`           |
| PUT         | `@PUT`            |
| DELETE      | `@DELETE`         |

---

### Mapping URLs with `@Path`

```java
@Path("/messages")
public class MessageResource {

    @GET
    public List<Message> getMessages() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public Message getMessage(@PathParam("id") long id) {
        return service.getById(id);
    }
}
```

* `@Path` on class → base URI
* `@Path` on method → sub-path
* Combined to form the full endpoint

---

## 7. How annotations link everything together

A complete mapping includes:

* **HTTP method** (`@GET`)
* **URI** (`@Path`)
* **Media types** (`@Produces`, `@Consumes`)

This declarative style:

* Makes code readable
* Clearly documents API behavior
* Aligns closely with REST principles

---

## 8. JAX-RS vs frameworks like Spring

* JAX-RS → **standard API**
* Spring MVC → **framework-specific**

Spring Boot can even **run JAX-RS** using Jersey, proving how flexible the standard is.