## 1. Why filtering and pagination matter

When APIs return collections, clients often need:

* Only **some** of the data
* Data in **small chunks**
* Different results based on **optional parameters**

Filtering and pagination:

* Improve performance
* Reduce payload size
* Make APIs flexible and client-friendly

---

## 2. Filtering by attributes

Filtering means selecting items that match certain criteria.

Example: filter messages by year.

### In-memory filtering logic

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

💡 In a real database, this logic would become a query.

---

## 3. Pagination basics

Pagination returns a **subset of a list**.

Common parameters:

* `start` or `offset`
* `size` or `limit`

Example request:

```
/messages?start=0&size=10
```

---

### Pagination using `subList`

```java
public List<Message> getPaginatedMessages(int start, int size) {
    List<Message> list = new ArrayList<>(messages.values());
    return list.subList(start, start + size);
}
```

This simulates database pagination.

---

## 4. Conditional logic using query parameters

Query parameters are **optional**, so APIs must decide what to do based on what’s provided.

Example resource method:

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

💡 Order matters: more specific filters first.

---

## 5. Handling missing parameters

If a query parameter is not provided:

* JAX-RS injects default values (`0` for int, `null` for objects)

This allows simple checks like:

```java
if (year > 0)
```

You can also use:

```java
@DefaultValue("0")
```

---

## 6. Zero-based indexing

Pagination commonly uses **zero-based indexing**:

* `start = 0` → first element
* `start = 10` → 11th element

This matches Java collections and avoids confusion.

---

## 7. Edge-case handling

Without checks, pagination can crash your API.

### Common edge cases

* `start` < 0
* `size` ≤ 0
* `start` ≥ list size
* `start + size` > list size

### Safe pagination logic

```java
public List<Message> getPaginatedMessages(int start, int size) {
    List<Message> list = new ArrayList<>(messages.values());

    if (start < 0 || size <= 0 || start >= list.size()) {
        return Collections.emptyList();
    }

    int end = Math.min(start + size, list.size());
    return list.subList(start, end);
}
```

💡 This prevents `IndexOutOfBoundsException`.

---

## 8. Combining filtering and pagination

In real APIs:

1. Filter first
2. Paginate second

```java
List<Message> filtered = filterByYear(year);
return paginate(filtered, start, size);
```

This ensures consistent results.

---

## 9. Where this logic belongs

* Resource class → reads parameters
* **Service layer → filtering & pagination logic**

This keeps:

* Resources thin
* Logic reusable
* Code testable

---

## 10. Limitations of in-memory logic

This approach is:

* Good for learning
* Easy to understand

But not suitable for large datasets because:

* Everything is loaded into memory
* Filtering is done in Java loops

In production:

* Databases handle filtering & pagination

## 11. Sorting

Sorting allows clients to receive items in a specific order.

### Query parameter example:

```
/messages?sort=created&order=desc
```

### Java in-memory implementation:

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

💡 Sorting is often combined with filtering and pagination.

---

## 12. Cursor-based pagination

Unlike **offset-based pagination**, cursor-based pagination uses a **reference to the last seen element**, which is more efficient for large datasets.

### Example:

```
/messages?afterId=105&limit=10
```

* `afterId=105` → start after message with ID 105
* `limit=10` → return 10 messages

### Benefits:

* Avoids issues with data insertion/deletion changing offsets
* Efficient for large datasets

### Simple Java logic:

```java
public List<Message> getMessagesAfter(long lastId, int limit) {
    List<Message> list = messages.values().stream()
        .filter(m -> m.getId() > lastId)
        .sorted(Comparator.comparing(Message::getId))
        .limit(limit)
        .collect(Collectors.toList());
    return list;
}
```

---

## 13. Validation of pagination parameters

Always validate inputs to prevent errors or abuse.

### Checks:

* `start` or `afterId` ≥ 0
* `size` or `limit` > 0
* `size` does not exceed a maximum (e.g., 100)

### Example:

```java
if (limit <= 0) limit = 10;          // default
if (limit > 100) limit = 100;        // max
if (start < 0) start = 0;            // default
```

💡 Helps prevent:

* IndexOutOfBoundsException
* Overloading server with huge responses

---

## 14. Moving logic to databases

In-memory filtering, sorting, and pagination work for learning, but real APIs should push this work to the **database**.

### Benefits:

* Efficient for large datasets
* Uses database indexes
* Reduces server memory usage
* Supports concurrent users safely

### Example with SQL:

```sql
SELECT * 
FROM messages
WHERE year(created) = 2023
ORDER BY created DESC
LIMIT 10 OFFSET 0;
```

### Example with JPA/Hibernate:

```java
TypedQuery<Message> query = em.createQuery(
    "SELECT m FROM Message m WHERE YEAR(m.created) = :year ORDER BY m.created DESC", Message.class
);
query.setParameter("year", 2023);
query.setFirstResult(0);   // offset
query.setMaxResults(10);   // limit
List<Message> results = query.getResultList();
```

💡 The service layer calls the repository, keeping **resource classes thin**.