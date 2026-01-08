package org.example.service;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@WebService(serviceName = "GroupService")
public class Group {

    private List<Student> students = new ArrayList<>();

    {
        students.add(new Student("ahmed", 15));
        students.add(new Student("mohamed", 16));
    }


    @WebMethod
    public String getStudents() {
        return toXML();
    }
    @WebMethod
    public int getnext(int i) {
        return i+1;
    }



    private String toXML() {
        try {
            StudentList studentList = new StudentList(students);
            JAXBContext context = JAXBContext.newInstance(StudentList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(studentList, writer);
            return writer.toString();
        } catch (Exception e) {
            System.out.println("Error converting to XML: " + e.getMessage());
            e.printStackTrace();
            return "<error>" + e.getMessage() + "</error>";
        }
    }
}
