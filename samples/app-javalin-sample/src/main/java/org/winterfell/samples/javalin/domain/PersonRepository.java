package org.winterfell.samples.javalin.domain;

import org.winterfell.starter.javalin.annotation.JavalinComponent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/9/27
 */
@JavalinComponent
public class PersonRepository {

    public static final Map<String,Person> REPO = new HashMap<>(1);

    public boolean save(Person person) {
        REPO.computeIfAbsent(person.getName(), s -> REPO.put(s, person));
        return true;
    }

    public Collection<Person> findAll(){
        return REPO.values();
    }

    public Person findByName(String name) {
        return REPO.getOrDefault(name, new Person());
    }
}
