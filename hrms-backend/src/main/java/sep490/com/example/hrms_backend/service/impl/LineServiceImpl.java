package sep490.com.example.hrms_backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LineDTO;
import sep490.com.example.hrms_backend.dto.LinePMCDto;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.NotificationType;
import sep490.com.example.hrms_backend.mapper.LinePMCMapper;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.LineService;
import sep490.com.example.hrms_backend.service.NotificationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {

    private final LineRepository lineRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PositionRepository positionRepository;
    private final NotificationService notificationService;

    //tested
    @Override
    public DepartmentDTO getDepartmentByLineId(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tổ"));

        return new DepartmentDTO(
                line.getDepartment().getDepartmentId(),
                line.getDepartment().getDepartmentName()
        );
    }

    //tested
    @Override
    public List<LinePMCDto> getAllLine(String search) {
        List<Line> lineServiceList;
        if (search != null && !search.isEmpty()) {
            lineServiceList = lineRepository.findByLineNameContainingIgnoreCase(search);
        } else {
            lineServiceList = lineRepository.findAll();
        }
        return lineServiceList.stream().map(LinePMCMapper::mapToLinePMCDto)
                .toList();
    }

    //tested
    @Override
    public LineDTO getLineByLineId(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tổ"));
        if (line.getLeader() == null) {
            return new LineDTO(line.getLineId(), line.getLineName());
        } else {
            return new LineDTO(line.getLineId(), line.getLineName(), line.getLeader().getEmployeeId());
        }
    }

    //tested
    @Transactional
    @Override
    public void assignLeaderToLine(Long lineId, Long leaderId, Long senderId) {

        Employee sender = employeeRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Khong tim thay nhan vien"));
        Account senderAcc = sender.getAccount();
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tổ"));
        if (line.getLeader() != null) {

            Long oldLeaderId = line.getLeader().getEmployeeId();

            Account oldLeaderAccount = accountRepository.findByEmployee_EmployeeId(oldLeaderId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản cho nhân viên này"));

            Role oldLeaderRole = roleRepository.findByRoleName("ROLE_EMPLOYEE")
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy quyền ROLE_EMPLOYEE"));
            Employee emp = oldLeaderAccount.getEmployee();
            Position p = positionRepository.findByPositionName("Công Nhân");
            emp.setPosition(p);
            oldLeaderAccount.setRole(oldLeaderRole);
            accountRepository.save(oldLeaderAccount);
            employeeRepository.save(emp);
        }

        Employee employee = employeeRepository.findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        Account account = accountRepository.findByEmployee_EmployeeId(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản cho nhân viên này"));

        Role role = roleRepository.findByRoleName("ROLE_LINE_LEADER")
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy quyền ROLE_LINE_LEADER"));

        Position p = positionRepository.findByPositionName("Tổ Trưởng");
        employee.setPosition(p);
        account.setRole(role);
        accountRepository.save(account);

        line.setLeader(employee);
        lineRepository.save(line);
        employeeRepository.save(employee);
        notificationService.addNotification(NotificationType.LEADER_CHANGE, senderAcc, account);
    }
}
