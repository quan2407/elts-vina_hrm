package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.dto.RecruitmentGraphResponse;
import sep490.com.example.hrms_backend.service.RecruitmentService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final RecruitmentService recruitmentService;

    @GetMapping("/recruitment-graph")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER')")
    public ResponseEntity<List<RecruitmentGraphResponse>> getRecruitmentGraph() {
        return ResponseEntity.ok(recruitmentService.getGraphData());
    }
}

