# REST API Design Guide: URIs, Resources, and Versioning

## Part 1: Core Design Philosophy

### Why Design Comes First

When building REST APIs, the design of your API interface matters more than the underlying code implementation. Here's why:

**The permanence problem**: Once clients start using your API endpoints, those URIs become commitments. Changing them later means breaking existing integrations, forcing clients to update their code, and potentially losing trust.

**The ripple effect**: A poorly designed API creates technical debt that compounds over time. It becomes harder to extend, requires excessive documentation to explain inconsistencies, and doesn't scale well as your application grows.

Think of your API design as a **public contract** between you and your clients. Just like a legal contract, changes have consequences. Invest time upfront to get it right.

---

## Part 2: Resource-Centric Thinking

### What Are Resources?

REST APIs organize everything around **resources** - the nouns of your application. A resource represents any meaningful entity or concept in your system that clients need to interact with.

**Examples of resources:**
- A user account
- A blog post
- A product in inventory
- An order transaction
- A comment on a post

Each resource gets a unique identifier (URI) that serves as its address on the web.

**Example:**
```http
/users/42          → The user with ID 42
/products/phone-x  → A specific product
/orders/2024-001   → A particular order
```

### The Golden Rule: Nouns, Not Verbs

Your URIs should describe **what something is**, not **what you're doing to it**. The action comes from the HTTP method, not the URI.

**❌ Avoid these anti-patterns:**
```http
/getAllUsers
/createNewProduct
/deleteOrder
/updateUserProfile
```

These URIs are problematic because they:
- Mix concerns (the resource and the action)
- Create redundancy (you'd need separate URIs for every operation)
- Don't leverage HTTP methods properly

**✅ Instead, design like this:**
```http
GET    /users           → Retrieve all users
POST   /users           → Create a new user
GET    /users/42        → Retrieve user 42
PUT    /users/42        → Update user 42
DELETE /users/42        → Delete user 42
```

The URI identifies the resource; the HTTP verb specifies the operation.

---

## Part 3: Collections vs Individual Resources

### Use Plural Nouns for Consistency

Always use plural names for your resource collections. This creates an intuitive pattern that clearly distinguishes between accessing multiple items versus a single item.

```http
/messages           → The collection of all messages
/messages/15        → One specific message (ID 15)
```

This pattern works because:
- It reads naturally ("get messages" or "get message 15")
- The structure is self-documenting
- It scales consistently across your entire API

### Two Types of Resources

**Collection Resource** - Represents a group of items:
```http
GET  /messages      → List all messages
POST /messages      → Create a new message
```

**Instance Resource** - Represents a single, specific item:
```http
GET    /messages/15    → Retrieve message 15
PUT    /messages/15    → Update message 15  
DELETE /messages/15    → Remove message 15
```

---

## Part 4: Expressing Relationships

### When to Use Nested URIs

Nested URIs help express hierarchical relationships, especially one-to-many relationships where a child resource belongs to a parent.

**Example: Comments belonging to a message**
```http
/messages/15/comments
```

This URI clearly communicates: "Get all comments that belong to message 15."

**When nesting makes sense:**
- The child resource rarely exists independently of the parent
- Clients typically access the child through the parent context
- The relationship is fundamental to understanding the data

**Example use cases:**
```http
/users/42/orders              → Orders for user 42
/articles/101/comments        → Comments on article 101
/projects/abc/tasks           → Tasks within project abc
```

### The Danger of Deep Nesting

Avoid creating URIs that are nested more than 2-3 levels deep:

**❌ Too complex:**
```http
/companies/5/departments/12/teams/7/members/99/tasks/3
```

Problems with deep nesting:
- Becomes difficult to understand and maintain
- Forces clients to know the entire hierarchy
- Creates rigid dependencies between resources

**✅ Better approach - flatten when possible:**
```http
/tasks/3
/tasks?team_id=7
/team-members/99/tasks
```

### Balancing Flat vs Nested Design

The best APIs provide **both flat and nested access** where appropriate:

```http
/comments/88                  → Direct access to comment 88
/messages/15/comments         → All comments for message 15
/messages/15/comments/88      → Comment 88 in the context of message 15
```

This gives clients flexibility to access data however makes sense for their use case.

**Design question to ask yourself:** "How will clients naturally want to access this data?"

---

## Part 5: Implementation Independence

### Hide Your Technology Stack

Your URI design should never expose internal implementation details. This creates unnecessary coupling between your API contract and your technology choices.

**❌ Implementation-dependent URIs:**
```http
/api/spring/messages
/rest/v1/jpa/users
/servlet/mongodb/products
```

These URIs are problematic because:
- They leak information about your framework (Spring, JPA)
- They leak information about your database (MongoDB)
- They create breaking changes if you switch technologies

**✅ Clean, stable URIs:**
```http
/messages
/users
/products
```

**The benefit**: You can migrate from Spring to another framework, from MongoDB to PostgreSQL, or refactor your entire backend without affecting a single client.

---

## Part 6: Filtering and Pagination

### Query Parameters Refine Results

Use query parameters to filter, sort, or paginate collections **without changing the resource identity**.

**Filtering examples:**
```http
/products?category=electronics
/messages?author=john&year=2024
/orders?status=pending&sort=date
```

**Pagination examples:**
```http
/messages?offset=20&limit=10    → Items 20-30
/users?page=3&per_page=25       → Page 3, 25 items per page
```

**Why this approach works:**
- The resource itself (`/messages`) remains the same
- You're just asking for a different "view" of that resource
- Clients can construct URLs programmatically

### Pagination Best Practices

**Always paginate large collections.** Returning thousands of items in a single response creates performance problems and poor user experience.

**Essential practices:**
1. **Set reasonable defaults** (e.g., 20 items per page)
2. **Enforce maximum limits** (prevent requests for 10,000 items)
3. **Use consistent ordering** (always sort by the same field by default)
4. **Include metadata** to help clients navigate

**Example response with metadata:**
```json
{
  "data": [
    {"id": 1, "title": "First message"},
    {"id": 2, "title": "Second message"}
  ],
  "pagination": {
    "offset": 0,
    "limit": 10,
    "total": 247,
    "has_more": true
  }
}
```

This metadata tells clients:
- Where they are in the results (offset)
- How many items they requested (limit)
- How many total items exist (total)
- Whether more results are available (has_more)

---

## Part 7: URI Design Principles Summary

A well-designed REST URI should be:

- **Clear** - Immediately understandable without documentation
- **Consistent** - Follows the same patterns throughout your API
- **Stable** - Unlikely to need changes once published
- **Scalable** - Supports growth and new features gracefully
- **Client-focused** - Designed around how clients will use it, not how your backend is structured

---

## Part 8: API Versioning

### Why Version Your API?

APIs change over time. You might need to:
- Add new features or fields
- Change response formats
- Modify business logic
- Fix design mistakes

**The problem**: Any change risks breaking existing clients who depend on your API behaving exactly as it did before.

**The solution**: Versioning allows you to introduce changes in a new version while keeping the old version stable for existing clients.

### Common Versioning Strategies

#### Strategy 1: URI Path Versioning

Put the version number directly in the URL path.

```http
GET /v1/users/10
GET /v2/users/10
```

**Advantages:**
- Extremely explicit and visible
- Easy to understand and test
- Works well with browser caching
- Simple to implement in frameworks

**Disadvantages:**
- Can make URIs longer
- Multiple versions can clutter your API
- Technically creates "different" resources (same user, different URI)

**When to use**: Public APIs, APIs consumed by many independent clients, when simplicity and discoverability matter most.

**Implementation in Spring Boot:**
```java
@RestController
@RequestMapping("/v1/users")
public class UserControllerV1 {
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        // Version 1 logic
    }
}

@RestController
@RequestMapping("/v2/users")
public class UserControllerV2 {
    @GetMapping("/{id}")
    public UserV2 getUser(@PathVariable Long id) {
        // Version 2 logic
    }
}
```

#### Strategy 2: Query Parameter Versioning

Pass the version as a query parameter.

```http
GET /users/10?version=1
GET /users/10?version=2
```

**Advantages:**
- Keeps the main URI path clean
- Flexible for testing different versions
- Easy to add to existing URIs

**Disadvantages:**
- Less visible than path versioning
- Harder to cache effectively
- The "same" resource URI returns different content (arguably less RESTful)

**When to use**: Internal APIs, experimental features, APIs where URI aesthetics matter.

#### Strategy 3: Header Versioning

Specify the version in HTTP headers.

```http
GET /users/10
X-API-Version: 1

GET /users/10
X-API-Version: 2
```

**Advantages:**
- Keeps URIs completely clean
- Separates versioning concern from resource identification
- Professional and "proper" HTTP usage

**Disadvantages:**
- Less discoverable (you can't see it in the browser address bar)
- Slightly harder to test with simple tools
- Requires more sophisticated client code

**When to use**: Complex APIs, APIs with frequent updates, when working with sophisticated clients.

**Implementation in Spring Boot:**
```java
@GetMapping(value = "/users/{id}", headers = "X-API-VERSION=1")
public User getUserV1(@PathVariable Long id) {
    // Version 1 logic
}

@GetMapping(value = "/users/{id}", headers = "X-API-VERSION=2")
public UserV2 getUserV2(@PathVariable Long id) {
    // Version 2 logic
}
```

#### Strategy 4: Content Negotiation / Media Type Versioning

Use custom media types in the Accept header.

```http
GET /users/10
Accept: application/vnd.myapi.v1+json

GET /users/10
Accept: application/vnd.myapi.v2+json
```

**Advantages:**
- Very RESTful (proper use of content negotiation)
- Keeps URIs clean
- Aligns with HTTP standards

**Disadvantages:**
- Most complex to implement
- Harder for developers to test manually
- Requires understanding of HTTP content negotiation

**When to use**: Highly RESTful APIs, APIs that need to support multiple representation formats.

#### Strategy 5: No Explicit Versioning (Continuous Evolution)

Maintain backward compatibility and never break existing clients.

**Approach:**
- Always add new fields, never remove or rename existing ones
- Deprecate old fields but keep them working
- Make all new fields optional
- Ensure old clients continue to work unchanged

**Advantages:**
- Simplest - no version management overhead
- Clients never break
- No need to maintain multiple versions

**Disadvantages:**
- Becomes increasingly difficult as the API grows
- Accumulates technical debt over time
- Eventually becomes unsustainable for major changes

**When to use**: Internal APIs where you control all clients, small APIs with infrequent changes, microservices within a single team.

---

## Part 9: Versioning Best Practices

1. **For public APIs, prefer URI versioning** - it's the most straightforward and discoverable approach

2. **Version at the right level** - typically version the entire API (e.g., `/v2/`) rather than individual resources

3. **Keep versions stable** - never change a published version's behavior; create a new version instead

4. **Use semantic versioning concepts**:
   - Major versions (v1, v2, v3) for breaking changes
   - Minor/patch updates shouldn't require version changes

5. **Communicate deprecation clearly**:
   - Announce when versions will be deprecated
   - Give clients plenty of time to migrate (6-12 months minimum)
   - Add deprecation warnings in response headers

6. **Don't create versions prematurely** - only version when you actually need to make breaking changes

7. **Limit supported versions** - don't try to maintain v1, v2, v3, v4 simultaneously; sunset old versions after migration periods

8. **Document differences** - make it easy for clients to understand what changed between versions

---

## Conclusion

Good REST API design is about creating intuitive, stable interfaces that serve your clients well over the long term. By focusing on resources, using consistent patterns, and versioning strategically, you create APIs that are easy to use, maintain, and evolve.