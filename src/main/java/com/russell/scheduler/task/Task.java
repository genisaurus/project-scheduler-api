package com.russell.scheduler.task;

import com.russell.scheduler.project.Project;
import com.russell.scheduler.resource.Resource;
import com.russell.scheduler.user.User;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="tasks")
public class Task {

    @Id
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description = ""; // defaults blank
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private Resource assignee;
    @ManyToOne
    @JoinColumn(name = "assigned_by")
    private User assigner;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @ManyToOne
    @JoinColumn(name="project", nullable = false)
    private Project project;
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;


    public Task() {
        super();
    }

    public Task(String name, String description, LocalDate startDate, LocalDate endDate, Project project) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
    }
    public Task(String name, String description, Resource assignee, User assigner, LocalDate startDate, LocalDate endDate, Project project, LocalDate createdDate) {
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.assigner = assigner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
        this.createdDate = createdDate;
    }

    public Task(UUID id, String name, String description, Resource assignee, User assigner, LocalDate startDate, LocalDate endDate, Project project, LocalDate createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.assigner = assigner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
        this.createdDate = createdDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Resource getAssignee() {
        return assignee;
    }

    public void setAssignee(Resource assignee) {
        this.assignee = assignee;
    }

    public User getAssigner() {
        return assigner;
    }

    public void setAssigner(User assigner) {
        this.assigner = assigner;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id)
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(assignee, task.assignee)
                && Objects.equals(assigner, task.assigner)
                && Objects.equals(startDate, task.startDate)
                && Objects.equals(endDate, task.endDate)
                && Objects.equals(project, task.project)
                && Objects.equals(createdDate, task.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, assignee, assigner, startDate, endDate, project, createdDate);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", assignee=" + assignee +
                ", assigner=" + assigner +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", project=" + project +
                ", createdDate=" + createdDate +
                '}';
    }
}
