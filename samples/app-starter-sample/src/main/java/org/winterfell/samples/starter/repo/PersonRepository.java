package org.winterfell.samples.starter.repo;

import org.winterfell.samples.starter.domain.Person;
import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/18
 */
@Repository
public class PersonRepository {

    private static final List<Person> persons = ImmutableList.of(new Person()
                    .setAge(30)
                    .setId("1")
                    .setName("Jacky")
                    .setAddress("江苏省南京市雨花台区安德门大街23号")
                    .setSlogan("Even after many years, will it hurt to think of you? My love is not easy to say."),
            new Person()
                    .setAge(40)
                    .setId("2")
                    .setName("Black")
                    .setAddress("江苏省南京市栖霞区仙隐北路2-17号")
                    .setSlogan("I don't have the strength to stay away from you anymore."));


    public Person findOne(String id) throws Throwable {
        Optional<Person> optional = persons.stream().filter(person -> person.getId().equalsIgnoreCase(id)).findFirst();
        return optional.orElseThrow((Supplier<Throwable>) () -> new RuntimeException("Not Found"));
    }
}
