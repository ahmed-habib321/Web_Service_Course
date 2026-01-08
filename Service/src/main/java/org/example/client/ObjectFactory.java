
package org.example.client;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.example.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _GetStudents_QNAME = new QName("http://service.example.org/", "getStudents");
    private static final QName _GetStudentsResponse_QNAME = new QName("http://service.example.org/", "getStudentsResponse");
    private static final QName _Getnext_QNAME = new QName("http://service.example.org/", "getnext");
    private static final QName _GetnextResponse_QNAME = new QName("http://service.example.org/", "getnextResponse");
    private static final QName _SetStudents_QNAME = new QName("http://service.example.org/", "setStudents");
    private static final QName _SetStudentsResponse_QNAME = new QName("http://service.example.org/", "setStudentsResponse");
    private static final QName _Student_QNAME = new QName("http://service.example.org/", "student");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.example.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetStudents }
     * 
     */
    public GetStudents createGetStudents() {
        return new GetStudents();
    }

    /**
     * Create an instance of {@link GetStudentsResponse }
     * 
     */
    public GetStudentsResponse createGetStudentsResponse() {
        return new GetStudentsResponse();
    }

    /**
     * Create an instance of {@link Getnext }
     * 
     */
    public Getnext createGetnext() {
        return new Getnext();
    }

    /**
     * Create an instance of {@link GetnextResponse }
     * 
     */
    public GetnextResponse createGetnextResponse() {
        return new GetnextResponse();
    }

    /**
     * Create an instance of {@link SetStudents }
     * 
     */
    public SetStudents createSetStudents() {
        return new SetStudents();
    }

    /**
     * Create an instance of {@link SetStudentsResponse }
     * 
     */
    public SetStudentsResponse createSetStudentsResponse() {
        return new SetStudentsResponse();
    }

    /**
     * Create an instance of {@link Student }
     * 
     */
    public Student createStudent() {
        return new Student();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStudents }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetStudents }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.example.org/", name = "getStudents")
    public JAXBElement<GetStudents> createGetStudents(GetStudents value) {
        return new JAXBElement<GetStudents>(_GetStudents_QNAME, GetStudents.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStudentsResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetStudentsResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.example.org/", name = "getStudentsResponse")
    public JAXBElement<GetStudentsResponse> createGetStudentsResponse(GetStudentsResponse value) {
        return new JAXBElement<GetStudentsResponse>(_GetStudentsResponse_QNAME, GetStudentsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Getnext }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Getnext }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.example.org/", name = "getnext")
    public JAXBElement<Getnext> createGetnext(Getnext value) {
        return new JAXBElement<Getnext>(_Getnext_QNAME, Getnext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetnextResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetnextResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.example.org/", name = "getnextResponse")
    public JAXBElement<GetnextResponse> createGetnextResponse(GetnextResponse value) {
        return new JAXBElement<GetnextResponse>(_GetnextResponse_QNAME, GetnextResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetStudents }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SetStudents }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.example.org/", name = "setStudents")
    public JAXBElement<SetStudents> createSetStudents(SetStudents value) {
        return new JAXBElement<SetStudents>(_SetStudents_QNAME, SetStudents.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetStudentsResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SetStudentsResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.example.org/", name = "setStudentsResponse")
    public JAXBElement<SetStudentsResponse> createSetStudentsResponse(SetStudentsResponse value) {
        return new JAXBElement<SetStudentsResponse>(_SetStudentsResponse_QNAME, SetStudentsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Student }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Student }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.example.org/", name = "student")
    public JAXBElement<Student> createStudent(Student value) {
        return new JAXBElement<Student>(_Student_QNAME, Student.class, null, value);
    }

}
