package sep490.com.example.hrms_backend.service.impl;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import sep490.com.example.hrms_backend.dto.InterviewScheduleDTO;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.CandidateStatus;
import sep490.com.example.hrms_backend.enums.InterviewResult;
import sep490.com.example.hrms_backend.enums.InterviewScheduleStatus;
import sep490.com.example.hrms_backend.enums.NotificationType;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.mapper.InterviewScheduleMapper;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.InterviewScheduleService;
import sep490.com.example.hrms_backend.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewScheduleServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private InterviewScheduleRepository interviewScheduleRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private RecruitmentRepository recruitmentRepository;
    @Mock
    private CandidateRecruitmentRepository candidateRecruitmentRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InterviewScheduleService service; // @RequiredArgsConstructor injects mailSender; others via @Autowired fields -> Mockito handles with @InjectMocks

    private static MockedStatic<InterviewScheduleMapper> mapperMock;

    @BeforeAll
    static void beforeAll() {
        mapperMock = mockStatic(InterviewScheduleMapper.class);
    }

    @AfterAll
    static void afterAll() {
        mapperMock.close();
    }

    private Candidate mkCandidate(Long id) {
        Candidate c = new Candidate();
        c.setId(id);
        c.setCandidateName("Alice");
        c.setEmail("alice@example.com");
        c.setPhoneNumber("0900000000");
        c.setGender("FEMALE");
        c.setDob(java.time.LocalDate.of(1995, 1, 1));
        return c;
    }

    private Employee mkEmployee(Long id, String name, String email, String positionName) {
        Employee e = new Employee();
        e.setEmployeeId(id);
        e.setEmployeeName(name);
        e.setEmail(email);
        Position p = new Position();
        p.setPositionName(positionName);
        e.setPosition(p);
        Account acc = new Account();
        acc.setAccountId(id + 1000);
        e.setAccount(acc);
        return e;
    }

    private Recruitment mkRecruitment(Long id) {
        Recruitment r = new Recruitment();
        r.setId(id);
        r.setTitle("Java Dev");
        r.setJobDescription("Build APIs");
        Department d = new Department();
        d.setDepartmentName("Engineering");
        r.setDepartment(d);
        r.setJobRequirement("Spring Boot");
        r.setEmploymentType("Full-time");
        r.setBenefits("Insurance");
        return r;
    }

    private InterviewSchedule mkSchedule(Long id) {
        InterviewSchedule s = new InterviewSchedule();
        s.setId(id);
        s.setScheduledAt(LocalDateTime.of(2025, 8, 20, 14, 0));
        s.setStatus(InterviewScheduleStatus.WAITING_INTERVIEW);
        return s;
    }

    // ---------- getInterviewByCandidateRecruitmentId ----------
    @Test
    void getInterviewByCandidateRecruitmentId_returnsMappedInitData() {
        Long crId = 10L;
        CandidateRecruitment cr = new CandidateRecruitment();
        cr.setId(crId);
        cr.setCandidate(mkCandidate(1L));
        cr.setRecruitment(mkRecruitment(2L));
        when(candidateRecruitmentRepository.findById(crId)).thenReturn(Optional.of(cr));

        InterviewScheduleDTO dto = service.getInterviewByCandidateRecruitmentId(crId);

        assertEquals(1L, dto.getCandidateId());
        assertEquals("Alice", dto.getCandidateName());
        assertEquals("alice@example.com", dto.getCandidateEmail());
        assertEquals(2L, dto.getRecruitmentId());
        assertEquals("Java Dev", dto.getRecruitmentTitle());
        assertEquals("Engineering", dto.getRecruitmentDepartment());
    }

    // ---------- createInterviewSchedule ----------
    @Test
    void createInterviewSchedule_savesEntities_sendsEmail_notifies_andReturnsDTO() {
        // input
        InterviewScheduleDTO input = InterviewScheduleDTO.builder()
                .candidateId(1L)
                .interviewerId(5L)
                .recruitmentId(2L)
                .scheduledAt(LocalDateTime.of(2025, 8, 25, 9, 30))
                .build();

        Candidate candidate = mkCandidate(1L);
        Employee interviewer = mkEmployee(5L, "Bob", "bob@corp.com", "Engineer");
        Employee sender = mkEmployee(99L, "HR", "hr@corp.com", "HR");
        Recruitment recruitment = mkRecruitment(2L);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(interviewer));
        when(recruitmentRepository.findById(2L)).thenReturn(Optional.of(recruitment));
        when(employeeRepository.findById(99L)).thenReturn(Optional.of(sender));

        CandidateRecruitment cr = new CandidateRecruitment();
        cr.setCandidate(candidate);
        cr.setRecruitment(recruitment);
        when(candidateRecruitmentRepository.findByCandidateIdAndRecruitmentId(1L, 2L))
                .thenReturn(Optional.of(cr));

        // mapping in
        InterviewSchedule mappedEntity = mkSchedule(null);
        mapperMock.when(() -> InterviewScheduleMapper.mapToInterviewScheduleEntity(any(InterviewScheduleDTO.class)))
                .thenReturn(mappedEntity);

        // repository save returns entity with id
        InterviewSchedule saved = mkSchedule(123L);
        saved.setCandidate(candidate);
        saved.setInterviewer(interviewer);
        saved.setRecruitment(recruitment);
        saved.setScheduledAt(input.getScheduledAt());
        when(interviewScheduleRepository.save(any(InterviewSchedule.class))).thenReturn(saved);

        // mapping out
        InterviewScheduleDTO mappedOut = InterviewScheduleDTO.builder().id(123L).build();
        mapperMock.when(() -> InterviewScheduleMapper.mapToInterviewScheduleDTO(saved)).thenReturn(mappedOut);

        // mail
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        InterviewScheduleDTO result = service.createInterviewSchedule(input, 99L);

        // assert mapping and return
        assertEquals(123L, result.getId());

        // status cập nhật cho CR
        assertEquals(CandidateStatus.INTERVIEW_SCHEDULED, cr.getStatus());
        verify(candidateRecruitmentRepository).save(cr);

        // verify email + notify
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(notificationService).addNotification(eq(NotificationType.INTERVIEW_SCHEDULE),
                eq(sender.getAccount()), eq(interviewer.getAccount()));

        // verify set status WAITING_INTERVIEW trước khi save
        ArgumentCaptor<InterviewSchedule> captor = ArgumentCaptor.forClass(InterviewSchedule.class);
        verify(interviewScheduleRepository).save(captor.capture());
        assertEquals(InterviewScheduleStatus.WAITING_INTERVIEW, captor.getValue().getStatus());
        assertSame(candidate, captor.getValue().getCandidate());
        assertSame(interviewer, captor.getValue().getInterviewer());
        assertSame(recruitment, captor.getValue().getRecruitment());
    }

    @Test
    void createInterviewSchedule_emailFails_wrapsIntoHRMSAPIException() {
        InterviewScheduleDTO input = InterviewScheduleDTO.builder()
                .candidateId(1L).interviewerId(2L).recruitmentId(3L)
                .scheduledAt(LocalDateTime.now().plusDays(1)).build();

        Candidate c = mkCandidate(1L);
        Employee i = mkEmployee(2L, "Bob", "bob@corp.com", "Engineer");
        Employee s = mkEmployee(9L, "HR", "hr@corp.com", "HR");
        Recruitment r = mkRecruitment(3L);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(c));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(i));
        when(recruitmentRepository.findById(3L)).thenReturn(Optional.of(r));
        when(employeeRepository.findById(9L)).thenReturn(Optional.of(s));

        CandidateRecruitment cr = new CandidateRecruitment();
        cr.setCandidate(c);
        cr.setRecruitment(r);
        when(candidateRecruitmentRepository.findByCandidateIdAndRecruitmentId(1L, 3L)).thenReturn(Optional.of(cr));

        mapperMock.when(() -> InterviewScheduleMapper.mapToInterviewScheduleEntity(any()))
                .thenReturn(mkSchedule(null));
        when(interviewScheduleRepository.save(any())).thenReturn(mkSchedule(11L));

        // mail throws
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doThrow(new RuntimeException("SMTP down")).when(mailSender).send(any(MimeMessage.class));

        HRMSAPIException ex = assertThrows(HRMSAPIException.class,
                () -> service.createInterviewSchedule(input, 9L));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        assertTrue(ex.getMessage().contains("Lỗi khi gửi email phỏng vấn"));
    }

    // ---------- getAllInterviewSchedule ----------
    @Test
    void getAllInterviewSchedule_roleHR_returnsAll() {
        Employee hr = mkEmployee(1L, "HR1", "hr@corp.com", "HR");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(hr));

        InterviewSchedule s1 = mkSchedule(1L);
        InterviewSchedule s2 = mkSchedule(2L);
        when(interviewScheduleRepository.findAll()).thenReturn(List.of(s1, s2));

        mapperMock.when(() -> InterviewScheduleMapper.maptoInterviewcheduleDTOList(anyList()))
                .thenReturn(List.of(new InterviewScheduleDTO(), new InterviewScheduleDTO()));

        List<InterviewScheduleDTO> list = service.getAllInterviewSchedule(1L);
        assertEquals(2, list.size());
        verify(interviewScheduleRepository).findAll();
        verify(interviewScheduleRepository, never()).findByInterviewer_EmployeeId(anyLong());
    }

    @Test
    void getAllInterviewSchedule_notHR_returnsByInterviewer() {
        Employee eng = mkEmployee(5L, "Bob", "bob@corp.com", "Engineer");
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(eng));

        when(interviewScheduleRepository.findByInterviewer_EmployeeId(5L))
                .thenReturn(List.of(mkSchedule(3L)));

        mapperMock.when(() -> InterviewScheduleMapper.maptoInterviewcheduleDTOList(anyList()))
                .thenReturn(List.of(new InterviewScheduleDTO()));

        List<InterviewScheduleDTO> list = service.getAllInterviewSchedule(5L);
        assertEquals(1, list.size());
        verify(interviewScheduleRepository).findByInterviewer_EmployeeId(5L);
        verify(interviewScheduleRepository, never()).findAll();
    }

    // ---------- editInterview ----------
    @Test
    void editInterview_timeChanged_sendsEmail_updatesAndMaps() {
        Long id = 50L;
        InterviewSchedule existing = mkSchedule(id);
        Candidate c = mkCandidate(1L);
        Employee oldInterviewer = mkEmployee(2L, "Old", "old@corp.com", "Engineer");
        Recruitment r = mkRecruitment(3L);
        existing.setCandidate(c);
        existing.setInterviewer(oldInterviewer);
        existing.setRecruitment(r);

        when(interviewScheduleRepository.findById(id)).thenReturn(Optional.of(existing));

        InterviewScheduleDTO input = InterviewScheduleDTO.builder()
                .scheduledAt(existing.getScheduledAt().plusDays(1))
                .status(InterviewScheduleStatus.INTERVIEWED)
                .feedback("Good")
                .interviewerId(9L)
                .build();

        Employee newInterviewer = mkEmployee(9L, "New", "new@corp.com", "Engineer");
        when(employeeRepository.findById(9L)).thenReturn(Optional.of(newInterviewer));

        // mail ok
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        InterviewSchedule saved = mkSchedule(id);
        saved.setCandidate(c);
        saved.setRecruitment(r);
        saved.setInterviewer(newInterviewer);
        saved.setScheduledAt(input.getScheduledAt());
        saved.setStatus(InterviewScheduleStatus.INTERVIEWED);
        saved.setFeedback("Good");
        when(interviewScheduleRepository.save(any())).thenReturn(saved);

        InterviewScheduleDTO out = InterviewScheduleDTO.builder().id(id).build();
        mapperMock.when(() -> InterviewScheduleMapper.mapToInterviewScheduleDTO(saved)).thenReturn(out);

        InterviewScheduleDTO result = service.editInterview(id, input);

        assertEquals(id, result.getId());
        verify(mailSender).send(any(MimeMessage.class)); // email sent because time changed
        assertEquals(InterviewScheduleStatus.INTERVIEWED, existing.getStatus());
        assertEquals("Good", existing.getFeedback());
        assertSame(newInterviewer, existing.getInterviewer());
        assertEquals(input.getScheduledAt(), existing.getScheduledAt());
    }

    @Test
    void editInterview_timeNotChanged_noEmailStillUpdates() {
        Long id = 60L;
        InterviewSchedule existing = mkSchedule(id);
        when(interviewScheduleRepository.findById(id)).thenReturn(Optional.of(existing));

        InterviewScheduleDTO input = InterviewScheduleDTO.builder()
                .scheduledAt(existing.getScheduledAt()) // không đổi
                .status(InterviewScheduleStatus.CANCEL)
                .feedback("Busy")
                .interviewerId(2L)
                .build();

        Employee interviewer = mkEmployee(2L, "Bob", "b@c.com", "Engineer");
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(interviewer));

        when(interviewScheduleRepository.save(any())).thenReturn(existing);
        mapperMock.when(() -> InterviewScheduleMapper.mapToInterviewScheduleDTO(existing))
                .thenReturn(InterviewScheduleDTO.builder().id(id).build());

        InterviewScheduleDTO res = service.editInterview(id, input);

        verify(mailSender, never()).send(any(MimeMessage.class));
        assertEquals(InterviewScheduleStatus.CANCEL, existing.getStatus());
        assertEquals("Busy", existing.getFeedback());
        assertSame(interviewer, existing.getInterviewer());
        assertEquals(id, res.getId());
    }

    // ---------- getInterviewById ----------
    @Test
    void getInterviewById_mapsAndReturns() {
        InterviewSchedule sch = mkSchedule(77L);
        when(interviewScheduleRepository.findById(77L)).thenReturn(Optional.of(sch));

        InterviewScheduleDTO out = InterviewScheduleDTO.builder().id(77L).build();
        mapperMock.when(() -> InterviewScheduleMapper.mapToInterviewScheduleDTO(sch)).thenReturn(out);

        InterviewScheduleDTO res = service.getInterviewById(77L);
        assertEquals(77L, res.getId());
    }

    // ---------- updateStatus ----------
    @Test
    void updateStatus_validEnum_saves() {
        InterviewSchedule sch = mkSchedule(80L);
        when(interviewScheduleRepository.findById(80L)).thenReturn(Optional.of(sch));

        service.updateStatus(80L, "INTERVIEWED");

        assertEquals(InterviewScheduleStatus.INTERVIEWED, sch.getStatus());
        verify(interviewScheduleRepository).save(sch);
    }

    @Test
    void updateStatus_invalidEnum_throwsIllegalArgument() {
        InterviewSchedule sch = mkSchedule(81L);
        when(interviewScheduleRepository.findById(81L)).thenReturn(Optional.of(sch));

        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(81L, "NOT_A_STATUS"));
        verify(interviewScheduleRepository, never()).save(any());
    }

    // ---------- updateResult ----------
    @Test
    void updateResult_validEnum_saves() {
        InterviewSchedule sch = mkSchedule(90L);
        when(interviewScheduleRepository.findById(90L)).thenReturn(Optional.of(sch));

        service.updateResult(90L, "PASS");
        assertEquals(InterviewResult.PASS, sch.getResult());
        verify(interviewScheduleRepository).save(sch);
    }

    @Test
    void updateResult_invalidEnum_throwsIllegalArgument() {
        InterviewSchedule sch = mkSchedule(91L);
        when(interviewScheduleRepository.findById(91L)).thenReturn(Optional.of(sch));

        assertThrows(IllegalArgumentException.class, () -> service.updateResult(91L, "???"));
        verify(interviewScheduleRepository, never()).save(any());
    }
}