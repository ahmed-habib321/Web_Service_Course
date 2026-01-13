# SOAP Web Services: Client-Side Implementation Guide

## Overview: How SOAP Clients Work

When you need to consume a SOAP web service in Java, you don't manually craft XML messages or deal with HTTP requests directly. Instead, you use **automatically generated proxy classes** that handle all the communication details for you. These proxies make remote service calls look and feel like regular Java method calls.

## Part 1: Creating Client Proxies with wsimport

### What is wsimport?

`wsimport` is a command-line tool (included with the JDK) that reads a service's WSDL file and generates all the Java code you need to call that service. Think of it as a code generator that builds a local representation of the remote service.

### Basic Command Structure

```bash
wsimport -keep -s src -d bin -p com.example.client http://example.com/service?wsdl
```

### Understanding the Options

- **`-keep`**: Preserves the generated `.java` source files (not just compiled `.class` files). This is helpful when you want to examine or modify the generated code.

- **`-s src`**: Specifies where to save the generated source files. In this example, they go into a `src` directory.

- **`-d bin`**: Specifies where to save the compiled `.class` files (typically a `bin` or `target` directory).

- **`-p com.example.client`**: Sets the package name for all generated classes. Without this, wsimport uses the namespace from the WSDL, which might not match your project structure.

- **`-b <bindingFile>`**: (Optional) Points to a binding customization file that controls how WSDL types map to Java types. Useful for resolving naming conflicts or customizing the generation process.

### What Gets Generated?

When you run wsimport, several Java files are created:

1. **Service class**: Represents the entire web service (e.g., `CalculatorService`)
2. **Port interface**: Defines the operations you can call (e.g., `Calculator`)
3. **Type classes**: Java representations of complex data types used by the service
4. **Exception classes**: For any SOAP faults the service might throw

These generated classes act as **proxies** or **stubs** that hide all the SOAP complexity. When you call a method on them, they automatically:
- Construct the SOAP XML request
- Send it over HTTP to the service
- Parse the SOAP XML response
- Convert the response back into Java objects

## Part 2: Using the Generated Client Code

Once you have the generated stubs, consuming the service is straightforward.

### Three-Step Pattern

```java
// Step 1: Create an instance of the service class
CalculatorService service = new CalculatorService();

// Step 2: Get the port (the interface with the actual operations)
Calculator port = service.getCalculatorPort();

// Step 3: Call methods as if they were local
int result = port.add(5, 10);
System.out.println("Result: " + result);
```

### What's Happening Behind the Scenes

Even though it looks like a simple method call, here's what actually occurs:

1. Your call to `port.add(5, 10)` triggers the stub code
2. The stub creates a SOAP envelope with your parameters
3. The stub sends an HTTP POST request to the service endpoint
4. The remote service processes the request and returns a SOAP response
5. The stub parses the response and extracts the return value
6. You receive a regular Java int (or whatever type the operation returns)

### Key Advantages

- **No manual XML**: You never write or parse SOAP messages yourself
- **Type safety**: The compiler checks that you're passing the right parameter types
- **Simple testing**: You can test against any deployment of the service (local, staging, production) just by changing the endpoint URL
- **Complex type handling**: Nested objects and arrays are automatically serialized/deserialized

### Best Practices for Managing Generated Code

Generated stubs can become unwieldy if not managed properly:

- **Use binding files** (`-b` option) to resolve naming conflicts when WSDL elements have names that clash with Java keywords
- **Organize by package** using the `-p` option to keep generated code separate from your application code
- **Consider a separate module**: For larger projects, put all SOAP client code in its own Maven/Gradle module
- **Version control**: Some teams commit generated code, others regenerate it during builds. Choose based on your workflow

## Part 3: Service Endpoint Interface (SEI) Pattern

### What is an SEI?

A **Service Endpoint Interface** is a Java interface (not a class) that defines the contract for a web service. Instead of annotating your implementation class directly with `@WebService`, you annotate an interface and have your implementation class implement it.

### Example: Interface-First Approach

**Step 1: Define the interface**

```java
import jakarta.jws.WebService;

@WebService
public interface Calculator {
    int add(int a, int b);
    int subtract(int a, int b);
    int multiply(int a, int b);
}
```

**Step 2: Implement the interface**

```java
import jakarta.jws.WebService;

@WebService(endpointInterface = "com.example.Calculator")
public class CalculatorImpl implements Calculator {
    
    public int add(int a, int b) {
        return a + b;
    }
    
    public int subtract(int a, int b) {
        return a - b;
    }
    
    public int multiply(int a, int b) {
        return a * b;
    }
}
```

### Why Use SEI?

**1. Separation of Contract and Implementation**

The interface represents "what the service does" (the contract), while the implementation represents "how it does it" (the logic). Clients depend only on the interface, making your code more flexible.

**2. Multiple Implementations**

You can create different implementations of the same interface:
- A production implementation that uses a database
- A test implementation that returns mock data
- A logging implementation that wraps another implementation

**3. Easier Versioning**

When you need to update your service:
- Keep the old interface for backward compatibility
- Create a new interface for the updated version
- Clients can migrate at their own pace

**4. Better Testing**

You can easily create mock implementations of the interface for unit testing without needing a running SOAP service.

### The Complete Flow

```
┌────────────┐         ┌─────────────┐         ┌──────────────────┐
│   Client   │ calls   │ SEI (Port)  │ backed  │ Implementation   │
│            │────────>│ Interface   │────────>│ Class            │
└────────────┘         └─────────────┘         └──────────────────┘
                              │                          │
                              │                          │
                         (Contract)                  (Business
                         (WSDL)                       Logic)
```

The client only knows about the interface (SEI), which is generated from the WSDL. The implementation handles the actual work, but clients never directly reference it.

## Summary

**Key Concepts:**
- **wsimport** generates Java proxies from WSDL, eliminating manual SOAP coding
- **Generated stubs** make remote calls look like local method invocations
- **SEI pattern** separates service contracts from implementation, improving flexibility and maintainability

**Workflow:**
1. Run `wsimport` against a WSDL to generate client code
2. Instantiate the service class and get its port
3. Call methods on the port just like any Java object
4. Let the generated code handle all SOAP communication automatically