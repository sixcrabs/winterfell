package org.winterfell.sample.ddd.domain.repository;

import org.winterfell.sample.ddd.domain.model.user.User;

import java.util.Optional;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/23
 */
public interface UserRepository {

    User save(User user);
    Optional<User> findById(String id);
    boolean delete(String id);
}
