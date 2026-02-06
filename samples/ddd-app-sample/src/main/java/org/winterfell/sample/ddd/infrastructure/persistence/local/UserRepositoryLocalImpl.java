package org.winterfell.sample.ddd.infrastructure.persistence.local;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.winterfell.sample.ddd.domain.model.user.User;
import org.winterfell.sample.ddd.domain.repository.UserRepository;

import java.util.Optional;

/**
 * <p>
 * 这里演示 本地数据存储实现 repository 持久层
 * </p>
 *
 * @author Alex
 * @since 2025/10/23
 */
@Repository
@ConditionalOnProperty(name = "repo.type", havingValue = "local")
public class UserRepositoryLocalImpl implements UserRepository {

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
