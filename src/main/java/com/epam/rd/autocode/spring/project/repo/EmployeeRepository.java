package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            select e.email
            from Employee e
            left join Order o on o.employee = e
            group by e.email
            order by count(o) asc, e.email asc
            """)
    List<String> findEmployeeEmailsOrderByLoad();
}
