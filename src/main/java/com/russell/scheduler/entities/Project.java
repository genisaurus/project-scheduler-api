package com.russell.scheduler.entities;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="projects")
public class Project {

    @Id
    private UUID id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @ManyToOne
    @JoinColumn(name="projects")
    private Resource owner;

    @OneToMany(mappedBy = "project")
    private Set<Task> tasks;

    public Project() {
        super();
    }

    public Project(String name, LocalDate startDate, LocalDate endDate, Set<Task> tasks) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tasks = tasks;
    }

    public Project(String name, LocalDate startDate, LocalDate endDate, Resource owner, Set<Task> tasks) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.owner = owner;
        this.tasks = tasks;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Resource getOwner() {
        return owner;
    }

    public void setOwner(Resource owner) {
        this.owner = owner;
    }

    public Set<Resource> getAllResources() {
        Set<Resource> res = new HashSet<>();
        res.add(this.owner);
        for (Task t : tasks)
            res.add(t.getAssignee());
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id)
                && Objects.equals(name, project.name)
                && Objects.equals(startDate, project.startDate)
                && Objects.equals(endDate, project.endDate)
                && Objects.equals(owner, project.owner)
                && Objects.equals(tasks, project.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate, endDate, owner, tasks);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", owner=" + owner +
                ", tasks=" + tasks +
                '}';
    }
}
