package sep490.com.example.hrms_backend.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
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
import sep490.com.example.hrms_backend.enums.CandidateStatus;
import sep490.com.example.hrms_backend.enums.InterviewResult;
import sep490.com.example.hrms_backend.enums.InterviewScheduleStatus;
import sep490.com.example.hrms_backend.enums.NotificationType;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.mapper.InterviewScheduleMapper;
import sep490.com.example.hrms_backend.repository.*;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private NotificationService notificationService;

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

    @Transactional
    public InterviewScheduleDTO createInterviewSchedule(@Valid InterviewScheduleDTO interviewScheduleDTO, Long employeeId) {
        Candidate candidate = candidateRepository.findById(interviewScheduleDTO.getCandidateId())
                .orElse(null);
        Employee interviewer = employeeRepository.findById(interviewScheduleDTO.getInterviewerId())
                .orElse(null);
        Recruitment recruitment = recruitmentRepository.findById(interviewScheduleDTO.getRecruitmentId())
                .orElse(null);

        Account recipientAccount = interviewer.getAccount();

        Employee sender = employeeRepository.findById(employeeId).orElse(null);
        Account senderAccount = sender.getAccount();
        Set<Account> recipients = new HashSet<>();
        recipients.add(recipientAccount);

        InterviewSchedule interviewSchedule = InterviewScheduleMapper.mapToInterviewScheduleEntity(interviewScheduleDTO);
        interviewSchedule.setCandidate(candidate);
        interviewSchedule.setInterviewer(interviewer);
        interviewSchedule.setRecruitment(recruitment);
        interviewSchedule.setStatus(InterviewScheduleStatus.WAITING_INTERVIEW);
        InterviewSchedule savedInterviewSchedule = interviewScheduleRepository.save(interviewSchedule);

        CandidateRecruitment candidateRecruitment = candidateRecruitmentRepository.findByCandidateIdAndRecruitmentId(interviewScheduleDTO.getCandidateId(), interviewScheduleDTO.getRecruitmentId())
                .orElse(null);

        candidateRecruitment.setStatus(CandidateStatus.INTERVIEW_SCHEDULED);

        candidateRecruitmentRepository.save(candidateRecruitment);

        sendInterviewEmail(candidate.getEmail(), interviewer.getEmail(), candidate.getCandidateName(), interviewSchedule.getScheduledAt());
        notificationService.addNotification(NotificationType.INTERVIEW_SCHEDULE, senderAccount, recipients);
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

    public InterviewScheduleDTO editInterview(Long id, InterviewScheduleDTO interviewScheduleDTO) {
        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(id).orElse(null);

        LocalDateTime oldTime = interviewSchedule.getScheduledAt();
        LocalDateTime newTime = interviewScheduleDTO.getScheduledAt();

        if (!oldTime.isEqual(newTime)) {

            sendInterviewEmail(
                    interviewSchedule.getCandidate().getEmail(),
                    interviewSchedule.getInterviewer().getEmail(),
                    interviewSchedule.getCandidate().getCandidateName(),
                    newTime
            );

            interviewSchedule.setScheduledAt(newTime);
        }

        interviewSchedule.setStatus(interviewScheduleDTO.getStatus());
        interviewSchedule.setFeedback(interviewScheduleDTO.getFeedback());

        Employee employee = employeeRepository.findById(interviewScheduleDTO.getInterviewerId()).orElse(null);

        interviewSchedule.setInterviewer(employee);
        InterviewSchedule savedInterviewSchedule = interviewScheduleRepository.save(interviewSchedule);


        return InterviewScheduleMapper.mapToInterviewScheduleDTO(savedInterviewSchedule);
    }

    public InterviewScheduleDTO getInterviewById(@Valid Long id) {

        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(id).orElse(null);

        return InterviewScheduleMapper.mapToInterviewScheduleDTO(interviewSchedule);
    }

    public void updateStatus(Long id, String status) {
        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(id).orElse(null);
        interviewSchedule.setStatus(InterviewScheduleStatus.valueOf(status));

        interviewScheduleRepository.save(interviewSchedule);
    }

    public void updateResult(Long id, String result) {
        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(id).orElse(null);
        interviewSchedule.setResult(InterviewResult.valueOf(result));
        interviewScheduleRepository.save(interviewSchedule);
    }
}