package org.winterfell.sample.ddd.domain.model.user;


import lombok.Data;
import org.winterfell.misc.hutool.mini.StringUtil;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * <p>
 * 聚合根
 * </p>
 *
 * @author Alex
 * @since 2025/10/23
 */
@Data
public class User {

    private String id;

    private String name;

    private String passwordHash;

    private String email;

    private UserStatus status;

    private Set<String> roleIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static User create(String id, String username, String email, String passwordHash) {
        if (StringUtil.isBlank(username)) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        User user = new User(id, username, email);
        user.passwordHash = passwordHash;
        return user;
    }

    // ===== 角色管理行为 =====

    // 分配角色
    public void assignRole(String roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        if (this.status != UserStatus.NORMAL) {
            throw new IllegalStateException("只有激活状态的用户才能分配角色");
        }
        // 业务规则：一个用户最多10个角色
        if (this.roleIds.size() >= 10) {
            throw new IllegalStateException("用户角色数量不能超过10个");
        }

        this.roleIds.add(roleId);
        this.updatedAt = LocalDateTime.now();
    }

    // 移除角色
    public void removeRole(String roleId) {
        this.roleIds.remove(roleId);
        this.updatedAt = LocalDateTime.now();
    }

    // 检查是否有某个角色
    public boolean hasRole(String roleId) {
        return this.roleIds.contains(roleId);
    }

    // 清空所有角色
    public void clearRoles() {
        this.roleIds.clear();
        this.updatedAt = LocalDateTime.now();
    }

}
