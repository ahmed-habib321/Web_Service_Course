# The Richardson Maturity Model: Measuring RESTful Design

The **Richardson Maturity Model** provides a framework for evaluating how closely an API adheres to REST principles. Created by Leonard Richardson, it defines a progression through four distinct levels, each building upon the previous one.

## The Four Levels of REST Maturity

### Level 0: The Swamp of POX (Plain Old XML/JSON)

At this foundational level, APIs use HTTP merely as a transport mechanism without leveraging any of its features. Everything funnels through a single endpoint, with the request payload determining what action occurs.

**Characteristics:**
- Single URL for all operations (e.g., `/api` or `/service`)
- Ignores HTTP methods—typically uses only POST for everything
- Action determined entirely by request body content
- No meaningful use of HTTP status codes

**Example:**
```
POST /api
{
  "action": "getUser",
  "userId": 123
}
```

This resembles RPC (Remote Procedure Call) more than REST. While functional, it misses opportunities for caching, standardization, and clarity that HTTP provides.

---

### Level 1: Resources

APIs at this level introduce the concept of distinct resources with dedicated URLs, moving away from the single-endpoint approach.

**Characteristics:**
- Multiple endpoints representing different resources
- Resource-oriented URL structure (e.g., `/messages`, `/users`, `/profiles`)
- Still may not properly use HTTP methods
- Beginning to model the problem domain through URL design

**Example:**
```
POST /messages
POST /users/123
POST /profiles
```

While this introduces better organization and the notion of resources as first-class entities, it still doesn't fully utilize HTTP's method vocabulary.

---

### Level 2: HTTP Verbs and Status Codes

This level embraces HTTP as an application protocol rather than just transport, using its methods and status codes semantically.

**Characteristics:**
- Proper use of HTTP verbs: GET (retrieve), POST (create), PUT/PATCH (update), DELETE (remove)
- Meaningful HTTP status codes: 200 (OK), 201 (Created), 404 (Not Found), 400 (Bad Request), 500 (Server Error)
- Enables caching for GET requests
- Idempotent operations where appropriate

**Example:**
```
GET /messages/123      → 200 OK
POST /messages         → 201 Created
PUT /messages/123      → 200 OK
DELETE /messages/123   → 204 No Content
GET /messages/999      → 404 Not Found
```

Most modern APIs operate at this level, providing a solid, predictable interface that leverages HTTP's built-in semantics.

---

### Level 3: Hypermedia Controls (HATEOAS)

The highest level incorporates hypermedia, making the API self-descriptive and discoverable through embedded links that guide clients through available actions.

**Characteristics:**
- Responses include hypermedia links showing related resources and available operations
- Clients navigate dynamically without hardcoding URLs
- Server controls workflow through the presence or absence of links
- True implementation of "Hypermedia as the Engine of Application State"

**Example:**
```json
{
  "id": 123,
  "text": "Hello",
  "links": [
    {"rel": "self", "url": "/messages/123"},
    {"rel": "comments", "url": "/messages/123/comments"},
    {"rel": "author", "url": "/users/456"}
  ]
}
```

Level 3 APIs are rare in practice but offer maximum flexibility and loose coupling between client and server.

---

## Understanding the Model's Purpose

**Assessment Tool**: The model helps you evaluate where your API currently stands and identify specific areas for improvement.

**Not Prescriptive**: Reaching Level 3 isn't mandatory. Many successful APIs operate at Level 2, which provides excellent clarity and standardization. Choose the level that matches your requirements, team capabilities, and client needs.

**Progressive Enhancement**: You can evolve your API incrementally—start at Level 2 for solid foundations, then consider Level 3 if dynamic discoverability would benefit your use case.

The Richardson Maturity Model ultimately serves as a conversation framework, helping teams make informed architectural decisions about their API design rather than imposing a one-size-fits-all mandate.