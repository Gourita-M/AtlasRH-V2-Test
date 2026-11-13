package org.example.atlasrh.repository;

import org.example.atlasrh.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndMatriculeNot(String email, long matricule);

    boolean existsByDepartment_Id(Long departmentId);

    List<Employee> findAllByManager_Matricule(long managerMatricule);

    @Query("""
            select e from Employee e
            where (:name = '' or lower(e.firstName) like lower(concat('%', :name, '%'))
                   or lower(e.lastName) like lower(concat('%', :name, '%')))
              and (:departmentId is null or e.department.id = :departmentId)
            """)
    Page<Employee> searchEmployees(@Param("name") String name,
                                   @Param("departmentId") Long departmentId,
                                   Pageable pageable);
}
