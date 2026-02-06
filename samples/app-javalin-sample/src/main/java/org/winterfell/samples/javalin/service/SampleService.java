package org.winterfell.samples.javalin.service;

import org.winterfell.samples.javalin.domain.Person;
import org.winterfell.samples.javalin.domain.PersonRepository;
import org.winterfell.starter.javalin.annotation.JavalinComponent;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/9/27
 */
@JavalinComponent
public class SampleService {

    private final PersonRepository personRepository;

    @Inject
    public SampleService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person save(Person person) {
        personRepository.save(person);
        return personRepository.findByName(person.getName());
    }

    public List<Person> list() {
       return new ArrayList<>(personRepository.findAll());
    }
}
