package org.winterfell.misc.zinc.action.api;

import org.winterfell.misc.zinc.ZincResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class UserListResult extends ZincResult {
    public UserListResult(Gson gson) {
        super(gson);
    }

    private List<User> users;


    public List<User> getUsers() {
        return users;
    }

    public UserListResult setUsers(List<User> users) {
        this.users = users;
        return this;
    }

    public void fromJsonArray(JsonArray jsonArray) {
        this.users = new ArrayList<>(jsonArray.size());
        for (JsonElement element : jsonArray) {
            JsonObject object = element.getAsJsonObject();
            try {
                String id = getAs(object.get("_id"), String.class);
                String name = getAs(object.get("name"), String.class);
                String role = getAs(object.get("role"), String.class);
                String created_at = getAs(object.get("created_at"), String.class);
                String updated_at = getAs(object.get("updated_at"), String.class);
                this.users.add(new User().setId(id).setName(name).setRole(role)
                        .setCreated_at(created_at).setUpdated_at(updated_at));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static class  User {

        private String id;

        private String name;

        private String role;

        private String created_at;

        private String updated_at;

        @Override
        public String toString() {
            return "User{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", role='" + role + '\'' +
                    ", created_at=" + created_at +
                    ", updated_at=" + updated_at +
                    '}';
        }

        public String getId() {
            return id;
        }

        public User setId(String id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public User setName(String name) {
            this.name = name;
            return this;
        }

        public String getRole() {
            return role;
        }

        public User setRole(String role) {
            this.role = role;
            return this;
        }

        public String getCreated_at() {
            return created_at;
        }

        public User setCreated_at(String created_at) {
            this.created_at = created_at;
            return this;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public User setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
            return this;
        }
    }




}
