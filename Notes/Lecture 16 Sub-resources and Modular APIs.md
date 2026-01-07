## 1. What are sub-resources?

A **sub-resource** is a class that handles **nested parts of a REST URL**.

Example:

```
/messages/{messageId}/comments
```

* `messages` → parent resource
* `comments` → sub-resource

Sub-resources help organize code **modularly**, especially for hierarchical data.

---

## 2. How sub-resources work in JAX-RS

1. Parent resource class defines the main path:

```java
@Path("/messages")
public class MessageResource {
    ...
}
```

2. Parent class has a method returning the sub-resource:

```java
@Path("/{messageId}/comments")
public CommentResource getCommentResource(@PathParam("messageId") long messageId) {
    return new CommentResource(messageId);
}
```

* Notice **no HTTP method annotation** on this delegation method.
* Its job is just **to return the sub-resource instance**.

3. Sub-resource class handles actual HTTP methods:

```java
public class CommentResource {

    private long messageId;

    public CommentResource(long messageId) {
        this.messageId = messageId;
    }

    @GET
    public List<Comment> getComments() {
        return service.getCommentsForMessage(messageId);
    }

    @POST
    public Comment addComment(Comment comment) {
        return service.addComment(messageId, comment);
    }
}
```

---

## 3. Path concatenation in Jersey

The full URL is formed by combining:

1. Parent class `@Path`
2. Delegation method `@Path`
3. Sub-resource class `@Path` (if any)
4. Sub-resource method `@Path` (if any)

Example:

| Layer                   | Path                    |
| ----------------------- | ----------------------- |
| Parent class            | `/messages`             |
| Delegation method       | `/{messageId}/comments` |
| Sub-resource method GET | `/` (default)           |

Final URL:

```
/messages/123/comments
```

---

## 4. Accessing parent parameters

Sub-resources can access parent path parameters using **constructor injection** or storing them in fields.

Example:

```java
public CommentResource(long messageId) {
    this.messageId = messageId;
}
```

* This allows all sub-resource methods to **know which parent resource they belong to**.

---

## 5. Supported HTTP methods

Sub-resources can define **all standard HTTP methods**:

* `@GET`
* `@POST`
* `@PUT`
* `@DELETE`

Example:

```java
@DELETE
@Path("/{commentId}")
public void deleteComment(@PathParam("commentId") long commentId) {
    service.removeComment(messageId, commentId);
}
```

---

## 6. Controlling serialization

If a parent object has a **nested collection**, you may want to hide it during serialization.

```java
@XmlTransient
private List<Comment> comments;
```

* Prevents automatic inclusion in parent resource
* Useful when sub-resources handle the nested data

---

## 7. Benefits of sub-resources

1. **Modularity** – Each nested resource has its own class
2. **Maintainability** – Logic for parent and child resources is separated
3. **Scalability** – Easy to add new nested endpoints without bloating the parent
4. **Clarity** – URLs map clearly to objects and their relationships

---

## 8. Summary

**Chapter 11 teaches how to structure REST APIs with nested resources:**

* Sub-resources delegate nested URL segments
* Parent method returns sub-resource instances
* Full paths are automatically concatenated
* Supports all HTTP methods
* Serialization can be controlled with `@XmlTransient`
* Improves modularity, maintainability, and scalability