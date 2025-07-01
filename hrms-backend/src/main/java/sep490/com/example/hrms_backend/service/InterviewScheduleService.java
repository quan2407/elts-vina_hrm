package sep490.com.example.hrms_backend.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.InterviewScheduleDTO;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.mapper.InterviewScheduleMapper;
import sep490.com.example.hrms_backend.repository.*;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewScheduleService {

    private final JavaMailSender mailSender;

    @Autowired
    InterviewScheduleRepository interviewScheduleRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private CandidateRecruitmentRepository candidateRecruitmentRepository;

    public InterviewScheduleDTO getInterviewByCandidateRecruitmentId(Long id) {
        CandidateRecruitment candidateRecruitment = candidateRecruitmentRepository.findById(id).orElse(null);
        Candidate candidate = candidateRecruitment.getCandidate();
        Recruitment recruitment = candidateRecruitment.getRecruitment();
        return InterviewScheduleDTO.builder().candidateId(candidate.getId())
                .candidateName(candidate.getCandidateName())
                .candidateEmail(candidate.getEmail())
                .candidatePhone(candidate.getPhoneNumber())
                .candidateGender(candidate.getGender())
                .dob(candidate.getDob())
                .recruitmentId(recruitment.getId())
                .recruitmentTitle(recruitment.getTitle())
                .recruitmentDescription(recruitment.getJobDescription())
                .recruitmentDepartment(recruitment.getDepartment().getDepartmentName())
                .jobRequirement(recruitment.getJobRequirement())
                .employmentType(recruitment.getEmploymentType())
                .benefits(recruitment.getBenefits())
                .build();
    }

    public InterviewScheduleDTO createInterviewSchedule(@Valid InterviewScheduleDTO interviewScheduleDTO) {
        Candidate candidate = candidateRepository.findById(interviewScheduleDTO.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Ứng viên không tồn tại"));
        Employee interviewer = employeeRepository.findById(interviewScheduleDTO.getInterviewerId())
                .orElseThrow(() -> new RuntimeException("Người phỏng vấn không tồn tại"));
        Recruitment recruitment = recruitmentRepository.findById(interviewScheduleDTO.getRecruitmentId())
                .orElseThrow(() -> new RuntimeException("Đợt tuyển dụng không tồn tại"));

        InterviewSchedule interviewSchedule = InterviewScheduleMapper.mapToInterviewScheduleEntity(interviewScheduleDTO);
        interviewSchedule.setCandidate(candidate);
        interviewSchedule.setInterviewer(interviewer);
        interviewSchedule.setRecruitment(recruitment);
        InterviewSchedule savedInterviewSchedule = interviewScheduleRepository.save(interviewSchedule);

        sendInterviewEmail(candidate.getEmail(), interviewer.getEmail(), candidate.getCandidateName(), interviewSchedule.getScheduledAt());

        return InterviewScheduleMapper.mapToInterviewScheduleDTO(savedInterviewSchedule);
    }

    public InterviewSchedule save(InterviewSchedule schedule) {
        return interviewScheduleRepository.save(schedule);
    }


    public List<InterviewScheduleDTO> getAllInterviewSchedule() {
        return InterviewScheduleMapper.maptoInterviewcheduleDTOList(interviewScheduleRepository.findAll());
    }

    private void sendInterviewEmail(String to, String cc, String username, LocalDateTime interviewTime) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(to);
            helper.setCc(cc);
            helper.setSubject("[ELTS VINA] Thư mời phỏng vấn");

            String formattedTime = interviewTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm 'ngày' dd/MM/yyyy"));

            String body = String.format(
                    "Kính gửi %s,<br><br>" +
                            "Chúng tôi rất vui mừng được mời bạn tham gia buổi phỏng vấn cho vị trí bạn đã ứng tuyển tại ELTS VINA.<br><br>" +
                            "<b>Thời gian phỏng vấn:</b> %s<br>" +
                            "<b>Hình thức:</b> Trực tiếp tại văn phòng công ty ELTS VINA<br><br>" +
                            "<b>Địa chỉ:</b> Lô P, Khu công nghiệp Bình Xuyên, Thị Trấn Hương Canh, Huyện Bình Xuyên, Tỉnh Vĩnh Phúc, Việt Nam<br><br>" +
                            "Vui lòng phản hồi email này để xác nhận sự tham gia của bạn.<br><br>" +
                            "Trân trọng,<br>" +
                            "Phòng Nhân sự ELTS VINA",
                    username, formattedTime
            );

            helper.setText(body, true); // true để bật HTML content

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new HRMSAPIException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi gửi email phỏng vấn: " + e.getMessage());
        }
    }

    public InterviewScheduleDTO editInterview(Long id, @Valid InterviewScheduleDTO interviewScheduleDTO) {
        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(id).orElse(null);

        if (interviewSchedule.getScheduledAt().isEqual(interviewScheduleDTO.getScheduledAt())) {
            sendInterviewEmail(interviewSchedule.getCandidate().getEmail(), interviewSchedule.getInterviewer().getEmail(), interviewSchedule.getCandidate().getCandidateName(), interviewScheduleDTO.getScheduledAt());
        }

        interviewSchedule.setScheduledAt(interviewScheduleDTO.getScheduledAt());
        interviewSchedule.setStatus(interviewScheduleDTO.getStatus());
        interviewSchedule.setFeedback(interviewScheduleDTO.getFeedback());

        Employee employee = employeeRepository.findById(interviewScheduleDTO.getInterviewerId()).orElseThrow(null);

        interviewSchedule.setInterviewer(employee);
        InterviewSchedule savedInterviewSchedule = interviewScheduleRepository.save(interviewSchedule);


        return InterviewScheduleMapper.mapToInterviewScheduleDTO(savedInterviewSchedule);
    }

    public InterviewScheduleDTO getInterviewById(@Valid Long id) {
        return null;
    }

    public void updateStatus(Long id, String status) {
    }
}