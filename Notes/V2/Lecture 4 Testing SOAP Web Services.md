# SOAP Web Services: Testing and Development Guide

## Why Testing SOAP Services Matters

Before deploying a SOAP web service to production, you need to verify that it:
- Correctly processes various inputs and returns expected outputs
- Handles invalid data gracefully with appropriate fault messages
- Responds with properly formatted SOAP envelopes
- Performs well under different conditions

Testing SOAP services is more complex than testing regular Java methods because you're dealing with XML messages, network communication, and the SOAP protocol itself.

## Part 1: Testing Tools for SOAP Services

### Tool 1: GlassFish Tester Page

**What is it?**
GlassFish application server includes a built-in web interface for testing deployed SOAP services. When you deploy a service, GlassFish automatically generates a simple testing page.

**How it works:**
1. Deploy your SOAP service to GlassFish
2. Navigate to the service URL in your browser
3. GlassFish displays an auto-generated form for each operation
4. Fill in parameter values and click "Invoke"
5. View the SOAP response directly in the browser

**Advantages:**
- **Zero setup**: Available immediately after deployment
- **Quick verification**: Test basic functionality without additional tools
- **Beginner-friendly**: No need to understand SOAP message structure

**Limitations:**
- **Basic functionality only**: Can't easily test complex scenarios
- **Limited fault testing**: Difficult to verify error handling and SOAP faults
- **No request history**: Can't save or replay previous test cases
- **Tied to GlassFish**: Only works with services deployed on this server

**Best for:** Quick sanity checks during initial development.

---

### Tool 2: SoapUI

**What is it?**
SoapUI is a professional-grade, open-source testing tool specifically designed for SOAP and REST web services. It's the industry standard for comprehensive web service testing.

**Key Features:**

**1. WSDL Import**
- Point SoapUI to your service's WSDL URL
- It automatically generates sample requests for every operation
- Pre-populates request templates with placeholder values

**2. Interactive Testing**
- Edit request parameters in a user-friendly interface
- Send requests with a single click
- View formatted responses (both SOAP XML and extracted values)

**3. Fault Message Testing**
- Deliberately send invalid inputs to trigger fault responses
- Verify that your service returns proper SOAP faults with correct error codes
- Ensure error messages are clear and helpful

**4. Advanced Capabilities**
- **Test suites**: Organize multiple test cases and run them sequentially
- **Assertions**: Automatically verify response content matches expectations
- **Load testing**: Simulate multiple concurrent users
- **Mock services**: Create fake services that respond with predefined data
- **Security testing**: Test authentication, encryption, and authorization

**Advantages:**
- **Industry standard**: Used by professional QA teams worldwide
- **Comprehensive**: Covers testing, debugging, and performance scenarios
- **Works anywhere**: Test local services, remote services, or production endpoints
- **Reusable tests**: Save test cases and share them with your team
- **CI/CD integration**: Can be automated in build pipelines

**Limitations:**
- **Learning curve**: More complex than simple tools
- **Separate application**: Requires installation and context switching from your IDE

**Best for:** Thorough testing, regression testing, and professional QA workflows.

**Example Workflow:**
```
1. File → New SOAP Project
2. Enter WSDL URL: http://localhost:8080/calculator?wsdl
3. SoapUI generates requests for add(), subtract(), etc.
4. Double-click "add" operation
5. Fill in: <a>5</a> and <b>3</b>
6. Click green arrow to send request
7. View response: <return>8</return>
```

---

### Tool 3: Eclipse Web Services Explorer

**What is it?**
A web service testing tool built directly into the Eclipse IDE. It provides a browser-like interface within Eclipse for interacting with SOAP services.

**How it works:**
1. Right-click your WSDL file in Eclipse
2. Select "Web Services" → "Test with Web Services Explorer"
3. Eclipse opens an embedded browser showing your service operations
4. Select an operation, fill in parameters, and invoke
5. View the response in the same window

**Advantages:**
- **IDE integration**: No need to switch applications
- **Convenient for developers**: Test while you code
- **WSDL browsing**: Explore service structure visually
- **Quick iteration**: Make code changes, redeploy, and test immediately

**Limitations:**
- **Eclipse-only**: Not available in other IDEs (IntelliJ, VS Code)
- **Basic features**: Less powerful than dedicated tools like SoapUI
- **Outdated interface**: Hasn't evolved much in recent years

**Best for:** Quick tests during active development when you're already in Eclipse.

---

## Part 2: Lightweight Local Testing with Endpoint.publish()

### The Problem with Full Deployment

During development, deploying to an application server like GlassFish for every test is time-consuming:
1. Package your application (create WAR/EAR file)
2. Deploy to the server
3. Wait for deployment to complete
4. Test the service
5. Make changes and repeat

This cycle can take minutes each time, slowing down development significantly.

### The Solution: Endpoint.publish()

Java's JAX-WS provides a lightweight way to run SOAP services directly from a simple Java main method, without any application server.

**Basic Example:**

```java
import jakarta.xml.ws.Endpoint;

public class ServicePublisher {
    public static void main(String[] args) {
        // Start the service on a specific URL
        Endpoint.publish("http://localhost:8080/calculator", new CalculatorService());
        
        System.out.println("Service is running at http://localhost:8080/calculator");
        System.out.println("WSDL available at http://localhost:8080/calculator?wsdl");
        System.out.println("Press Ctrl+C to stop...");
    }
}
```

### How It Works

**Step-by-step breakdown:**

1. **`Endpoint.publish(address, implementor)`**: This static method does all the heavy lifting
   - `address`: The URL where your service will be accessible (must include protocol, host, port, and path)
   - `implementor`: An instance of your service implementation class

2. **Behind the scenes**, the JAX-WS runtime (Metro):
   - Reads the `@WebService` and `@WebMethod` annotations on your class
   - Generates the WSDL dynamically
   - Creates a lightweight HTTP server
   - Listens for incoming SOAP requests
   - Parses SOAP messages into method calls
   - Invokes your business logic
   - Converts return values back into SOAP responses

3. **The service runs until you stop it** (Ctrl+C in the terminal)

### Complete Working Example

```java
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.xml.ws.Endpoint;

// Service implementation
@WebService
public class CalculatorService {
    
    @WebMethod
    public int add(int a, int b) {
        return a + b;
    }
    
    @WebMethod
    public int subtract(int a, int b) {
        return a - b;
    }
}

// Publisher
public class QuickStart {
    public static void main(String[] args) {
        String url = "http://localhost:9090/calc";
        Endpoint.publish(url, new CalculatorService());
        System.out.println("Calculator service running at " + url);
    }
}
```

**To test:**
1. Run the `QuickStart` class
2. Open browser to `http://localhost:9090/calc?wsdl` (you'll see the WSDL)
3. Use SoapUI or generate client stubs to call the service
4. Make code changes in `CalculatorService`
5. Stop and restart `QuickStart` to see changes

### Advantages of Endpoint.publish()

- **Fast startup**: Service runs in seconds, not minutes
- **No deployment artifacts**: No need to create WAR files or configure deployment descriptors
- **Simple debugging**: Run in debug mode like any Java application
- **Minimal dependencies**: Just need the JAX-WS library
- **Perfect for TDD**: Write tests, start service, run tests, repeat

### Important Limitations

**⚠️ Not for Production Use**

`Endpoint.publish()` has significant limitations that make it unsuitable for production:

1. **Single-threaded**: Can only handle one request at a time. Concurrent requests will block each other.

2. **No security**: No built-in support for authentication, authorization, or encryption.

3. **No management**: Can't monitor, log, or manage the service through standard tools.

4. **No clustering**: Can't distribute load across multiple servers.

5. **Limited features**: Missing many enterprise capabilities like:
   - Connection pooling
   - Transaction management
   - Resource injection
   - Advanced error handling

**Use for:** Development, testing, demos, and learning.
**Don't use for:** Production deployments, load testing, or public-facing services.

---

## Part 3: The Role of Metro (JAX-WS Reference Implementation)

### What is Metro?

Metro is Oracle's reference implementation of the JAX-WS specification. It's the engine that makes SOAP services work in Java.

### What Metro Handles for You

When you use annotations like `@WebService` and `@WebMethod`, Metro does the heavy lifting:

**1. Annotation Processing**
- Scans your classes for JAX-WS annotations
- Understands what should be exposed as web service operations
- Determines parameter and return types

**2. WSDL Generation**
- Automatically creates WSDL from your Java classes
- Maps Java types to XML Schema types
- Defines the service contract

**3. SOAP Message Handling**
- Parses incoming SOAP XML into Java objects (unmarshalling)
- Converts Java return values into SOAP XML (marshalling)
- Handles SOAP headers, body, and faults

**4. Network Communication**
- Manages HTTP requests and responses
- Handles content-type headers and SOAP action headers
- Deals with SOAP protocol versions (1.1 or 1.2)

**5. Method Invocation**
- Extracts parameters from SOAP messages
- Calls your business logic methods
- Catches exceptions and converts them to SOAP faults

### Why This Matters

Metro's automation means you can focus on **business logic** instead of infrastructure:

```java
// You write this simple Java code:
@WebService
public class OrderService {
    @WebMethod
    public Order createOrder(Customer customer, Product product) {
        // Your business logic here
        return new Order(customer, product);
    }
}

// Metro automatically handles:
// - Parsing SOAP request with Customer and Product XML
// - Converting XML to Java objects
// - Calling your createOrder method
// - Converting Order object back to XML
// - Building SOAP response envelope
// - Sending HTTP response
```

You never write XML parsing code, SOAP envelope construction, or HTTP handling logic. Metro makes SOAP development feel like regular Java programming.

---

## Testing Strategy Recommendations

**During Active Development:**
- Use `Endpoint.publish()` for rapid iteration
- Use Eclipse Web Services Explorer for quick in-IDE tests
- Focus on getting business logic right

**Before Committing Code:**
- Deploy to GlassFish (or similar server) to test in a more realistic environment
- Use SoapUI to create comprehensive test cases
- Test both success and failure scenarios

**For QA and Staging:**
- Use SoapUI test suites for regression testing
- Automate tests in your CI/CD pipeline
- Performance test with realistic load

**Key Principle:** Match your testing tool to the stage of development. Simple tools for quick feedback, comprehensive tools for thorough validation.