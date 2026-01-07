## 1. What is HATEOAS?

HATEOAS is a **REST principle** that allows clients to **navigate an API dynamically** using hyperlinks provided in responses.

* Clients don’t need to hardcode URLs
* Server guides the client through available actions
* Improves API discoverability and reduces coupling

Example JSON response for a message:

```json
{
  "id": 1,
  "text": "Hello REST",
  "author": "Ahmed",
  "links": [
    {"url": "/messages/1", "rel": "self"},
    {"url": "/messages/1/comments", "rel": "comments"}
  ]
}
```

---

## 2. Link model

Define a simple **Link class**:

```java
public class Link {
    private String url;
    private String rel;

    public Link() {}

    public Link(String url, String rel) {
        this.url = url;
        this.rel = rel;
    }

    // getters and setters
}
```
* Pure JAX-RS HATEOAS can be **verbose** due to manual URL building.
* Often, **custom Link classes** (`url` + `rel`) improve flexibility and readability.
* `url` → the hypermedia link
* `rel` → relationship type (e.g., `self`, `comments`, `update`)

---

## 3. Embedding links in resource models

Resource models can hold a **list of links**:

```java
public class Message {
    private long id;
    private String text;
    private String author;
    private List<Link> links = new ArrayList<>();

    public void addLink(String url, String rel) {
        links.add(new Link(url, rel));
    }
}
```

* Using convenience methods like `addLink` reduces boilerplate.
* Every resource can expose **related operations** as links
* Example: a message links to its comments, likes, or edit URL

---

## 4. Dynamic URL construction with `UriInfo`

Avoid **hardcoding URLs** by using `UriInfo`:

```java
@GET
@Path("/{id}")
@Produces(MediaType.APPLICATION_JSON)
public Message getMessage(@PathParam("id") long id, @Context UriInfo uriInfo) {
    Message msg = service.getMessage(id);

    String selfUri = uriInfo.getAbsolutePath().toString();
    String commentsUri = uriInfo.getBaseUriBuilder()
                                .path(MessageResource.class)
                                .path(MessageResource.class, "getCommentResource")
                                .path(CommentResource.class)
                                .build(id)
                                .toString();

    msg.addLink(selfUri, "self");
    msg.addLink(commentsUri, "comments");
    return msg;
}
```

* `getAbsolutePath()` → current resource URL
* `getBaseUriBuilder()` → base URL for dynamic paths
* Avoids errors when the API path changes

---

## 5. Convenience methods for links

* `addLink(String url, String rel)` → reusable for all resources
* Handles multiple related links (self, edit, delete, comments, etc.)
* Keeps resource models clean and maintainable

---

## 6. Include a self-link

Every resource should include a **canonical self-link**:

```json
"links": [{"url": "/messages/1", "rel": "self"}]
```

* Allows clients to reference or refresh the resource
* Acts as a **stable identifier in hypermedia**

---

## 7. Benefits of HATEOAS

1. **Discoverability** – Clients can navigate without prior knowledge
2. **Reduced client-server coupling** – URLs aren’t hardcoded in client logic
3. **Dynamic actions** – Server can guide available operations depending on state
4. **Consistency** – All resources provide a standardized way to link to related resources