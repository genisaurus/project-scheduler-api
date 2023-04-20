package com.russell.scheduler.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name="user_roles")
public class UserRole {

    @Id
    private Integer id;
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
    @Column(name = "priority", nullable = false)
    private Integer priority;

    public UserRole() {
        super();
    }

    public UserRole(Integer id, String roleName, Integer priority) {
        this.id = id;
        this.roleName = roleName;
        this.priority = priority;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(id, userRole.id)
                && Objects.equals(roleName, userRole.roleName)
                && Objects.equals(priority, userRole.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleName, priority);
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
