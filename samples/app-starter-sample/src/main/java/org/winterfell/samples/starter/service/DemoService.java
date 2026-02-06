package org.winterfell.samples.starter.service;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import org.winterfell.samples.starter.domain.Person;
import org.winterfell.samples.starter.repo.PersonRepository;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/12/26
 */
@Service
public class DemoService {

    private final PersonRepository personRepository;

    public DemoService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public String getGreeting(String name) {
        return "hello , ".concat(String.valueOf(name.charAt(0)).toUpperCase()).concat(name.substring(1));
    }

    public Person getPerson(String id) {
        try {
            return personRepository.findOne(id);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    public Person getPerson2(String id, String name) {
        try {
            Person one = personRepository.findOne(id);
            return one.setName(name);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String getTicket() {
        return RandomUtils.nextInt() + "";
    }
}