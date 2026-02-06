package org.winterfell.sample.ddd.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.winterfell.sample.ddd.domain.repository.UserRepository;

/**
 * <p>
 * 业务 service 依赖于 domain 层，调用domain层的接口方法（实际实现在infrastructure层，通过 spring 容器注入，在这里自动获取到实现）
 * </p>
 *
 * @author Alex
 * @since 2025/10/23
 */
@Service
@RequiredArgsConstructor
public class UserBizService {

    private final UserRepository userRepository;
}
