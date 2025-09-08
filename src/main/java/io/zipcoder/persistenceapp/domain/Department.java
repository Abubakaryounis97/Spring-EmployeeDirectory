package io.zipcoder.persistenceapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_department_name", columnNames = "name")
})
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Department name is required")
    @Column(nullable = false)
    private String name;

    /**
     * Optional at creation time. We’ll enforce/set this via service logic later.
     * When a manager is set, they should belong to this department.
     */
    @OneToOne
    @JoinColumn(name = "manager_id", foreignKey = @ForeignKey(name = "fk_department_manager"))
    private Employee manager;

    /**
     * Bidirectional one-to-many. We ignore the list in JSON to avoid recursion for now.
     * Controllers/DTOs can expose it later if needed.
     */
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = false)
    @org.hibernate.annotations.BatchSize(size = 20)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Employee> employees = new ArrayList<>();

    // ---- getters/setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Employee getManager() { return manager; }
    public void setManager(Employee manager) { this.manager = manager; }

    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    // convenience
    public void addEmployee(Employee e) {
        employees.add(e);
        e.setDepartment(this);
    }

    public void removeEmployee(Employee e) {
        employees.remove(e);
        if (e.getDepartment() == this) {
            e.setDepartment(null);
        }
    }
}
