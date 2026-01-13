## **1. Creating a SOAP Web Service in Java**

* **Key Idea:** A regular Java class can become a SOAP web service by using the `@WebService` annotation.
* **Deployment:** Using **GlassFish**, your service is automatically detected, deployed, and exposed. You don’t need to manually create the WSDL—it’s generated for you.

**Example:**

```java
import jakarta.jws.WebService;

@WebService
public class CalculatorService {
    public int add(int a, int b) {
        return a + b;
    }
}
```

* All **public methods** are exposed as web service operations.
* `@WebMethod` is **optional** unless you want to customize or exclude methods.
* **Business logic** should be kept separate from service definitions (good design practice).
* Web service methods can accept parameters, return data, or provide status values (e.g., boolean results).
* GlassFish keeps the service instance in memory; **changes persist across requests**.
* You can test your service using **GlassFish’s built-in SOAP tester**.

---

## **2. Design Approaches**

There are **two main ways to develop SOAP services**:

1. **Service-First (Code-First)**

   * Write Java code first; WSDL is generated automatically.
   * Easy to learn, fast to implement.
   * Not ideal for large production systems because WSDL can change when the code changes.

2. **Contract-First (WSDL-First)**

   * Define the WSDL first, then implement the service.
   * WSDL acts as a contract; ensures **interface stability** for clients.
   * Good for production because implementations can evolve without breaking clients.

**Tip:** Some projects use a **hybrid approach**: generate WSDL from code, then manually customize it.

* Prefer **contract-first (WSDL-first)** for production services:

  * Ensures the interface remains **stable**.
  * Allows **implementation changes** without breaking clients.
* Helps maintain **long-term interoperability** between services and consumers.
---

## **3. SOAP Messaging Concepts**

SOAP messages wrap multiple parameters into a **single input message**.

* `<types>` section defines **complex data structures** (maps Java classes to XML schema).
* **`minOccurs=0`** → parameter is optional or can be null.
* **Binding:** Specifies protocol & transport (e.g., SOAP over HTTP).
* **Service & port elements:** Define endpoints where clients access the service.

**Excluding Methods:**

```java
@WebMethod(exclude=true)
public void internalMethod() { }
```

---

## **4. Annotation Customization**

Annotations give precise control over your SOAP service:

| Annotation                | Purpose                                                 |
| ------------------------- | ------------------------------------------------------- |
| `@WebService`             | Set service name, port name, namespace.                 |
| `@WebMethod`              | Rename operation, exclude methods, define SOAP actions. |
| `@WebParam(partName=…)`   | Rename input parameters.                                |
| `@WebResult(partName=…)`  | Rename output parameters.                               |
| `@SOAPBinding(style=RPC)` | Change SOAP binding style (RPC or Document).            |

* Helps **align WSDL and XML with business requirements**.
* Avoids manual editing of WSDL.

**Binding Styles:**

* **Document (default):**

  * Each request/response uses its own XML schema.
  * Supports **validation**.
  * Ideal for **complex data structures**.

* **RPC style:**

  * Simpler WSDL.
  * Inline types.
  * Easier to read for **simple operations**.
  * Less validation support.

**Tip:** Use **Document style** for production and complex data, **RPC style** for simple, internal services.

---

## **5. Handling Complex Data**

* **JAXB** automatically maps Java objects (POJOs) to XML.
* **Annotations:**

```java
import jakarta.xml.bind.annotation.*;

@XmlRootElement
@XmlType(propOrder={"id","name"})
public class Person {
    @XmlElement(name="personId", nullable=false)
    private int id;
    
    @XmlElement(name="fullName")
    private String name;

    // Required no-arg constructor
    public Person() {}
}
```

* Rules:

  * Must have a **no-argument constructor**.
  * JAXB handles serialization and deserialization automatically.

---

## **6. Exception Handling in SOAP**

SOAP allows **structured fault messages**:

* Declare exceptions in method signatures:

```java
@WebMethod
public int divide(int a, int b) throws DivisionByZeroException {
    if (b == 0) throw new DivisionByZeroException("Cannot divide by zero");
    return a / b;
}
```

* **Custom exception** automatically generates a **fault message** in WSDL.

* Fault messages contain:

  * Fault code
  * Fault string
  * Optional details (`getFaultInfo()`)

* You can define **multiple fault messages** for different error scenarios.

* Use **structured SOAP faults** instead of generic errors.
* Benefits:

  * Clear communication of errors to clients.
  * Can include **fault codes, fault strings, and detailed info**.
* Supports multiple exception types per operation.

**Example:**

```java
@WebMethod
public int divide(int a, int b) throws DivisionByZeroException { ... }
```

* WSDL will automatically include the fault message for `DivisionByZeroException`.


## **7. API Versioning**

* **Problem:** Changing method names or parameters can break existing clients.
* **Solution:** Create new versions of methods instead of modifying old ones.
* **Example:**

```java
@WebMethod(operationName="add_v1")
public int add(int a, int b) { ... }

@WebMethod(operationName="add_v2")
public int add(int a, int b, int c) { ... }
```

* Old clients can continue using `add_v1`, new clients can use `add_v2`.
* Ensures **backward compatibility**.