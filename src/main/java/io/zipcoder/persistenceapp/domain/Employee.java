package io.zipcoder.persistenceapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // employee number / PK

    @NotBlank(message = "First name is required")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(nullable = false)
    private String lastName;

    private String title;

    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;

    private String phone;

    @PastOrPresent(message = "Hire date cannot be in the future")
    private LocalDate hireDate;

    // Many employees -> one department
    @ManyToOne
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_employee_department"))
    private Department department;

    // Self-referencing: many employees can report to one manager (who is also an Employee)
    @ManyToOne
    @JoinColumn(name = "manager_id", foreignKey = @ForeignKey(name = "fk_employee_manager"))
    private Employee manager;

    // Reverse side: one manager has many direct reports
    @OneToMany(mappedBy = "manager")
    @com.fasterxml.jackson.annotation.JsonIgnore // avoid infinite recursion for now
    private List<Employee> directReports = new ArrayList<>();

    // ---- getters/setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Employee getManager() { return manager; }
    public void setManager(Employee manager) { this.manager = manager; }

    public List<Employee> getDirectReports() { return directReports; }
    public void setDirectReports(List<Employee> directReports) { this.directReports = directReports; }

    // convenience
    public void addDirectReport(Employee report) {
        directReports.add(report);
        report.setManager(this);
        if (this.getDepartment() != null) {
            // optional convenience: keep department aligned with manager by default;
            // service will enforce the rule when setting manager.
            report.setDepartment(this.getDepartment());
        }
    }

    public void removeDirectReport(Employee report) {
        directReports.remove(report);
        if (report.getManager() == this) {
            report.setManager(null);
        }
    }
}
