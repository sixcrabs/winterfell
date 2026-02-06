package org.winterfell.sample.ddd.infrastructure.persistence.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.winterfell.sample.ddd.domain.model.user.User;
import org.winterfell.sample.ddd.domain.repository.UserRepository;
import org.winterfell.sample.ddd.infrastructure.persistence.UserDao;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * <p>
 * 这里是db类型的 repository 实现
 * </p>
 *
 * @author Alex
 * @since 2025/10/23
 */
@Repository
@ConditionalOnProperty(name = "repo.type", havingValue = "db")
public class UserRepositoryDbImpl implements UserRepository {
    @Resource
    private UserDao userDao;

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public boolean delete(String id) {
        return false;
    }
}
