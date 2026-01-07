package org.example.rest.resources;

import org.example.rest.Models.Person;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    private static MockData instance;
    private List<Person> persons = new ArrayList<>();

    private MockData(){
        persons.add(new Person(1, "a"));
        persons.add(new Person(2, "b"));
        persons.add(new Person(3, "c"));
        persons.add(new Person(4, "d"));
        persons.add(new Person(5, "e"));
        persons.add(new Person(6, "f"));
        persons.add(new Person(7, "g"));
        persons.add(new Person(8, "h"));
        persons.add(new Person(9, "i"));
    }
    public static synchronized MockData getInstance() {
        if (instance == null) {
            instance = new MockData();
        }
        return instance;
    }
    public List<Person> getPersons() {
        return persons;
    }
    public void addPerson(Person person) {
        persons.add(person);
    }
}
