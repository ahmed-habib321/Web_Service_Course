package org.example.rest.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.example.rest.Models.Person;

import java.util.List;

@Path("MyResource")
public class HelloResource {
    /*
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello, World!";
    }
    */
    /*
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> sendAllObjects() {
        List<Person> persons = new ArrayList<>();

        Person person = new Person();
        person.setFirstName("First");
        person.setLastName("Last");
        person.setEmail("Email");

        Person person2 = new Person();
        person2.setFirstName("First2");
        person2.setLastName("Last2");
        person2.setEmail("Email2");
        persons.add(person);
        persons.add(person2);
        return persons;
    }
     */

    private MockData mockData = MockData.getInstance();


    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> sendAllObjects() {
        return mockData.getPersons();
    }

    @GET
    @Path("{id}")
    public Person getObject(@PathParam("id") int id) {
        return mockData.getPersons().stream().filter(person -> person.getId() == id).findFirst().get();
    }

    @POST
    @Path("add")
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    public void addObject(Person p) {
        mockData.addPerson(p);

        /* how to send a post request
        * in post man it expect
        *
        * {
        *   "id": 10,
        *   "firstName": "j"
        * }
        *
        * */
    }

}