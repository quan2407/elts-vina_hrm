package sep490.com.example.hrms_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sep490.com.example.hrms_backend.entity.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByCitizenId(String citizenId);

    boolean existsByCitizenIdAndEmployeeIdNot(String citizenId, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndEmployeeIdNot(String email, Long id);

    Optional<Employee> findByAccount_Username(String username);

    List<Employee> findByIsDeletedFalse();

    Optional<Employee> findByEmployeeIdAndIsDeletedFalse(Long employeeId);


    List<Employee> findByLine_LineId(Long lineId);

    List<Employee> findByDepartment_DepartmentId(Long departmentId);

    List<Employee> findByDepartment_DepartmentIdAndLine_LineIdAndIsDeletedFalse(Long departmentId, Long lineId);

    List<Employee> findByDepartment_DepartmentIdAndIsDeletedFalse(Long departmentId);

    @Query("SELECT e FROM Employee e WHERE e.isDeleted = false")
    List<Employee> findAllActive();

    @Query("SELECT e FROM Employee e WHERE e.isDeleted = false")
    Page<Employee> findAllActive(Pageable pageable);


    @Query("SELECT e FROM Employee e WHERE (e.line IS NULL OR e.line.lineId <> :lineId) AND e.department.departmentName = 'Sản Xuất' AND (e.position.positionName != 'TỔ TRƯỞNG')")
    List<Employee> findEmployeesNotInLine(@Param("lineId") Long lineId);

    @Query("""
                SELECT e FROM Employee e
                WHERE e.isDeleted = false
                  AND e.department.departmentName = 'Sản Xuất'
                  AND (
                      e.line IS NULL
                      OR (e.line IS NOT NULL AND e.line.lineId <> :lineId)
                  )
                  AND (e.position.positionName != 'TỔ TRƯỞNG')
                  AND LOWER(e.employeeName) LIKE :search
                """)
    List<Employee> findEmployeesNotInLineWithSearch(@Param("lineId") Long lineId, @Param("search") String search);

    // Đã có trong repository rồi:
    long countByPosition_PositionNameIgnoreCase(String positionName);

    @Query("SELECT COUNT(e) FROM Employee e WHERE LOWER(e.position.positionName) <> LOWER(:positionName)")
    long countByPosition_PositionNameNotIgnoreCase(@Param("positionName") String positionName);

    List<Employee> findByPosition_PositionName(String positionName);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    @Query("SELECT e.gender AS gender, COUNT(e) AS count " +
            "FROM Employee e WHERE e.isDeleted = false AND " +
            "(e.startWorkAt <= :endDate AND e.endWorkAt >= :startDate) " +
            "GROUP BY e.gender")
    List<Object[]> findGenderDistributionByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT e.department.departmentName AS departmentName, COUNT(e) AS count " +
            "FROM Employee e WHERE e.isDeleted = false AND " +
            "(e.startWorkAt <= :endDate AND e.endWorkAt >= :startDate) " +
            "GROUP BY e.department")
    List<Object[]> findDepartmentDistributionByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Page<Employee> findByIsDeletedFalse(Pageable pageable);


    Optional<Employee> findByEmployeeNameIgnoreCaseOrEmailIgnoreCase(String employeeName, String email);

    Optional<Employee> findByEmailIgnoreCase(String keyword);

    Optional<Employee> findByEmployeeNameIgnoreCase(String employeeName);

    @Query("SELECT e FROM Employee e WHERE e.position.positionId = :positionId AND (LOWER(e.employeeName) LIKE :keyword OR LOWER(e.email) LIKE :keyword)")
    List<Employee> searchByPositionAndKeyword(@Param("positionId") Long positionId, @Param("keyword") String keyword);


    List<Employee> findByPosition_PositionId(Long positionId);

    Page<Employee> findByIsDeletedFalseAndEmployeeCodeContainingIgnoreCaseOrEmployeeNameContainingIgnoreCase(String search, String search1, Pageable pageable);

    boolean existsByPhoneNumberAndEmployeeIdNot(String phoneNumber, Long id);

    boolean existsByPhoneNumber(String phoneNumber);
}
