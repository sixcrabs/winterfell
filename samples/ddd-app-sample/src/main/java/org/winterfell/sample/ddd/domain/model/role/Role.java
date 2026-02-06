package org.winterfell.sample.ddd.domain.model.role;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/23
 */
@Data
public class Role {

    private String id;
    private String name;
    private String description;
    private Set<String> permissions;  // 权限列表
    private RoleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
