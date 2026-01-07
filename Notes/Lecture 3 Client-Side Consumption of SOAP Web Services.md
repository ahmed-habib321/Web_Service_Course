## **1. Generating Client Stubs with `wsimport`**

When you want to **call a SOAP service from Java**, you don’t write XML or SOAP messages manually. Instead, you generate **client-side stubs** (proxies) from the service’s WSDL using the **`wsimport` tool**.

* **Command syntax:**

```bash
wsimport -keep -s src -d bin -p com.example.client http://example.com/service?wsdl
```

* **Options explained:**

  * `-keep` → keep generated Java source files (useful for debugging or customization).
  * `-s` → specify source directory for generated `.java` files.
  * `-d` → specify class directory for compiled `.class` files.
  * `-p` → package name for generated files.
  * `-b` → use binding files to handle naming conflicts or customize mapping.

**What happens:**

* Java classes representing the SOAP service (stubs) are created.
* These stubs act as **local proxies**. Calling a method on them **sends the SOAP request**, receives the response, and maps it to Java objects automatically.

---

## **2. Using Generated Stubs**

Once stubs are generated:

1. **Instantiate the service class**: Represents the overall web service.
2. **Get the port interface**: Represents a specific endpoint (port) with callable operations.
3. **Invoke operations like normal Java methods**.

**Example:**

```java
// Generated service class
CalculatorService service = new CalculatorService();

// Get the port (interface)
Calculator port = service.getCalculatorPort();

// Invoke operations
int sum = port.add(5, 10);
System.out.println("Sum: " + sum);
```

**Notes:**

* WSDL is your reference for **service names, ports, and operations**.
* Generated stubs handle:

  * SOAP message creation & parsing
  * Network communication
  * Mapping complex types to Java objects
* Enables **end-to-end testing**: your client can call the service running on any IP/host without extra SOAP code.

✅ **Key takeaway:** Using `wsimport`–generated stubs makes consuming SOAP services as easy as calling local Java methods.

* Generated stubs can be messy if you don’t manage them.
  * Use **binding files** to resolve naming conflicts.
  * Customize package names with `-p` in `wsimport`.
  * Keep stubs in a separate module for maintainability.

---

## **3. Service Endpoint Interface (SEI) Pattern**

**SEI = Interface that defines the service contract.**

* Instead of annotating implementation classes (`@WebService`), you annotate **interfaces**:

```java
import jakarta.jws.WebService;

@WebService
public interface Calculator {
    int add(int a, int b);
}
```

* Implementation class:

```java
public class CalculatorImpl implements Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

**Benefits of SEI:**

1. **Decouples contract from logic:**

   * Clients depend only on the interface, not the implementation.
2. **Multiple implementations:**

   * You can swap or extend implementations without changing the client.
3. **API versioning:**

   * Maintain backward compatibility by keeping old interfaces while adding new ones.

**Workflow:**

```
Client -> SEI -> Implementation -> SOAP Service
```