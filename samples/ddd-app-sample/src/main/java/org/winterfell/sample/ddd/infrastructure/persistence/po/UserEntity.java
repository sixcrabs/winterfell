package org.winterfell.sample.ddd.infrastructure.persistence.po;

import org.winterfell.sorm.core.activerecord.plugin.activerecord.Model;
import org.winterfell.sorm.core.annotation.Table;

/**
 * <p>
 * 数据库实体
 * </p>
 *
 * @author Alex
 * @since 2025/10/23
 */
@Table(name = "ddd_t_user")
public class UserEntity extends Model<UserEntity> {
}
