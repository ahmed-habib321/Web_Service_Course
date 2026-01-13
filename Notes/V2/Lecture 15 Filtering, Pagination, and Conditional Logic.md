# Filtering, Pagination, and Conditional Logic in REST APIs

## Why These Capabilities Matter

REST APIs frequently return collections of data, but clients rarely need everything at once. They need the ability to narrow down results, retrieve data in manageable chunks, and adjust what they receive based on their specific needs.

Implementing filtering and pagination delivers several critical benefits. Performance improves dramatically when you're not processing entire datasets. Network payloads shrink, reducing bandwidth costs and speeding up response times. Your API becomes more flexible and user-friendly, letting clients request exactly what they need.

---

## Building Filtering Logic

Filtering means returning only items that match specific criteria. Imagine an API with thousands of messages—clients might only want messages from a particular year.

Here's how you might implement year-based filtering in memory:

```java
public List<Message> getMessagesForYear(int year) {
    List<Message> result = new ArrayList<>();
    for (Message message : messages.values()) {
        if (message.getCreated().getYear() == year) {
            result.add(message);
        }
    }
    return result;
}
```

This approach works for learning and small datasets. In production applications, you'd translate this logic into database queries where filtering happens much more efficiently.

---

## Understanding Pagination Fundamentals

Pagination breaks large result sets into smaller, manageable pages. Rather than overwhelming clients with thousands of records, you send them a subset they can actually process.

The standard approach uses two parameters:
- **offset** (or start): Where to begin in the result set
- **limit** (or size): How many items to return

A request like `/messages?start=0&size=10` asks for the first 10 messages.

### Implementing Basic Pagination

```java
public List<Message> getPaginatedMessages(int start, int size) {
    List<Message> list = new ArrayList<>(messages.values());
    return list.subList(start, start + size);
}
```

This uses Java's `subList` method to extract a slice from the full collection, mimicking how databases return paginated results.

---

## Applying Conditional Logic with Query Parameters

Query parameters are optional by nature, which means your API needs to handle different combinations intelligently. When multiple parameters might be present, you need clear logic for what takes precedence.

```java
@GET
public List<Message> getMessages(
    @QueryParam("year") int year,
    @QueryParam("start") int start,
    @QueryParam("size") int size
) {
    if (year > 0) {
        return service.getMessagesForYear(year);
    }
    if (start >= 0 && size > 0) {
        return service.getPaginatedMessages(start, size);
    }
    return service.getAllMessages();
}
```

The order of these checks matters—more specific filters should be evaluated first. This resource method checks for year filtering first, then pagination, and finally falls back to returning everything.

---

## Managing Missing Parameters

When clients omit query parameters, JAX-RS injects default values automatically: `0` for primitive integers, `null` for objects. This behavior enables simple conditional checks like `if (year > 0)` to determine whether filtering was requested.

For more explicit control, you can specify defaults:

```java
@QueryParam("size") @DefaultValue("10") int size
```

This ensures predictable behavior when clients don't specify a value.

---

## Working with Zero-Based Indexing

Pagination typically uses zero-based indexing, matching how most programming languages handle collections:
- `start=0` points to the first element
- `start=10` points to the eleventh element

This consistency with Java collections prevents confusion and off-by-one errors.

---

## Protecting Against Edge Cases

Without proper validation, pagination can easily crash your API. You need to handle several problematic scenarios:

- Negative offsets
- Zero or negative sizes
- Offsets beyond the collection size
- Requested ranges that extend past the end

Here's robust pagination logic with edge case protection:

```java
public List<Message> getPaginatedMessages(int start, int size) {
    List<Message> list = new ArrayList<>(messages.values());

    // Validate inputs
    if (start < 0 || size <= 0 || start >= list.size()) {
        return Collections.emptyList();
    }

    // Calculate safe end boundary
    int end = Math.min(start + size, list.size());
    return list.subList(start, end);
}
```

The `Math.min` call ensures you never request elements beyond the list's actual size, preventing `IndexOutOfBoundsException` errors.

---

## Combining Filtering with Pagination

Real-world APIs often need both filtering and pagination working together. The key is applying them in the correct order:

1. **Filter first** to narrow down the dataset
2. **Paginate second** to return a manageable chunk

```java
List<Message> filtered = filterByYear(year);
return paginate(filtered, start, size);
```

This sequence ensures clients receive consistent, predictable results.

---

## Organizing Your Code Properly

The architecture of these features matters just as much as their implementation:

- **Resource classes** extract parameters from the HTTP request
- **Service layer** contains all filtering, sorting, and pagination logic

This separation keeps resource classes thin and focused on HTTP concerns while making business logic reusable and testable. Your service methods become easy to unit test without involving JAX-RS or HTTP infrastructure.

---

## Adding Sorting Capabilities

Sorting lets clients control the order of returned items. You might support requests like `/messages?sort=created&order=desc`.

Here's an in-memory implementation:

```java
public List<Message> getSortedMessages(String sortBy, String order) {
    List<Message> list = new ArrayList<>(messages.values());
    Comparator<Message> comparator;

    switch (sortBy) {
        case "created":
            comparator = Comparator.comparing(Message::getCreated);
            break;
        case "author":
            comparator = Comparator.comparing(Message::getAuthor);
            break;
        default:
            comparator = Comparator.comparing(Message::getId);
    }

    if ("desc".equalsIgnoreCase(order)) {
        comparator = comparator.reversed();
    }

    list.sort(comparator);
    return list;
}
```

Sorting works beautifully alongside filtering and pagination—filter first, sort second, paginate third.

---

## Exploring Cursor-Based Pagination

Offset-based pagination has a weakness: when data changes between requests (new items added, old items deleted), offsets can point to wrong positions, causing duplicates or skipped items.

Cursor-based pagination solves this by using a reference to the last seen element. A request like `/messages?afterId=105&limit=10` means "give me 10 messages starting after ID 105."

```java
public List<Message> getMessagesAfter(long lastId, int limit) {
    return messages.values().stream()
        .filter(m -> m.getId() > lastId)
        .sorted(Comparator.comparing(Message::getId))
        .limit(limit)
        .collect(Collectors.toList());
}
```

Benefits of this approach:
- Immune to data changes between requests
- More efficient for large, frequently updated datasets
- Eliminates the "deep pagination" performance problem

---

## Validating Pagination Parameters

Never trust client input. Always validate pagination parameters to prevent errors and abuse:

```java
// Set sensible defaults
if (limit <= 0) limit = 10;

// Enforce maximum to prevent resource exhaustion
if (limit > 100) limit = 100;

// Ensure non-negative offset
if (start < 0) start = 0;
```

These checks prevent `IndexOutOfBoundsException` errors, stop clients from requesting enormous result sets that could overwhelm your server, and ensure consistent API behavior.

---

## Moving to Database-Level Operations

The in-memory approaches shown here are excellent for learning, but they have serious limitations. Loading entire datasets into memory doesn't scale. Filtering with Java loops is inefficient compared to database queries. You can't take advantage of database indexes.

Production APIs push filtering, sorting, and pagination to the database level.

### Using SQL directly:

```sql
SELECT * 
FROM messages
WHERE YEAR(created) = 2023
ORDER BY created DESC
LIMIT 10 OFFSET 0;
```

### Using JPA/Hibernate:

```java
TypedQuery<Message> query = em.createQuery(
    "SELECT m FROM Message m WHERE YEAR(m.created) = :year ORDER BY m.created DESC", 
    Message.class
);
query.setParameter("year", 2023);
query.setFirstResult(0);    // offset
query.setMaxResults(10);    // limit
List<Message> results = query.getResultList();
```

The database handles everything efficiently using indexes. Your service layer calls repository methods that execute these queries, keeping resource classes thin and focused on their HTTP responsibilities.

---

## Core Principles to Remember

**Filter before paginating**—apply criteria first, then slice the results. This ensures accurate page boundaries.

**Validate all inputs**—negative offsets, zero-sized pages, and excessive limits can all cause problems. Set sensible defaults and enforce maximums.

**Keep resources thin**—extract parameters in resource classes, but implement all filtering, sorting, and pagination logic in the service layer.

**Plan for scale**—in-memory operations work for learning, but real applications need database-level filtering and pagination.

**Consider cursor-based pagination**—for frequently changing data or very large datasets, cursor-based approaches outperform offset-based ones.

These patterns make your APIs performant, reliable, and pleasant to use. Clients get exactly the data they need without overwhelming their systems or yours.