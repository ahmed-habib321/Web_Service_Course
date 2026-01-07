## **1. Local Testing Tools**

Testing SOAP services is crucial to verify that your service works correctly, handles inputs/outputs, and returns proper fault messages. You have several options:

### **a) GlassFish Tester Page**

* Built-in testing interface provided by GlassFish.
* Auto-generates **forms for each operation**, letting you input parameters and invoke methods.
* Pros: Quick, no extra setup.
* Cons: Limited functionality, especially for testing **fault messages** or complex SOAP scenarios.

### **b) SoapUI**

* Free and open-source SOAP testing tool.
* Features:

  * Import **WSDL** to generate requests automatically.
  * Input parameters interactively and see **SOAP responses**.
  * Supports **fault messages** to verify error handling.
  * Can test multiple services and complex workflows.
* Pros: Most widely used for professional SOAP testing.
* Great for both local and remote services.

### **c) Eclipse Web Services Explorer**

* Integrated tool in Eclipse IDE.
* Browse WSDLs, send requests, view responses **within your development environment**.
* Useful for quick tests without leaving your IDE.

---

## **2. Quick Local Deployment**

Sometimes you don’t want to deploy on GlassFish—especially during development. Java provides a **lightweight way to run SOAP services locally**:

```java
import jakarta.xml.ws.Endpoint;

public class ServicePublisher {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/calculator", new CalculatorService());
        System.out.println("Service running at http://localhost:8080/calculator");
    }
}
```

* `Endpoint.publish(url, service)`: starts the web service on the specified URL.
* Pros: Quick, easy, no application server needed.
* Cons: Single-threaded, **not suitable for production**.

**Note:** Metro (the JAX-WS Reference Implementation) handles:

* Annotation processing (`@WebService`, `@WebMethod`)
* SOAP message creation and parsing
* Method invocation

This allows you to focus on your **business logic** without worrying about the SOAP infrastructure.

