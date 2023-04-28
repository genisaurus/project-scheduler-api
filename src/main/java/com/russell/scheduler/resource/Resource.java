package com.russell.scheduler.resource;

import com.russell.scheduler.task.Task;
import com.russell.scheduler.project.Project;

import javax.persistence.*;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="resources")
public class Resource {
    @Id
    private UUID id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @OneToMany(mappedBy = "owner")
    private Set<Project> projects;
    @OneToMany(mappedBy = "assignee")
    private Set<Task> assignedTasks;

    public Resource() {
        super();
    }

    public Resource(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public Resource(UUID id, String email, String firstName, String lastName, Set<Project> projects, Set<Task> assignedTasks) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.projects = projects;
        this.assignedTasks = assignedTasks;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(Set<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(id, resource.id)
                && Objects.equals(email, resource.email)
                && Objects.equals(firstName, resource.firstName)
                && Objects.equals(lastName, resource.lastName)
                && Objects.equals(projects, resource.projects)
                && Objects.equals(assignedTasks, resource.assignedTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, projects, assignedTasks);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", projects='" + projects + '\'' +
                ", assignedTasks=" + assignedTasks +
                '}';
    }
}
