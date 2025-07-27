USE hrms;
SET SQL_SAFE_UPDATES = 0;


UPDATE employee SET line_id = NULL;
UPDATE `lines` SET leader_id = NULL;
ALTER TABLE employee MODIFY COLUMN is_deleted TINYINT(1) NOT NULL DEFAULT 0;


DELETE FROM application_approval_step;
DELETE FROM application;
DELETE FROM account_request;
DELETE FROM salary;
ALTER TABLE salary AUTO_INCREMENT = 1;
DELETE FROM attendance_record;
DELETE FROM benefit;
DELETE FROM work_schedule_detail;
DELETE FROM work_schedule;
DELETE FROM department_position;
DELETE FROM interview_schedule;
DELETE FROM candidate_recruitment;
DELETE FROM candidate;
DELETE FROM recruitment;
DELETE FROM holidays;

DELETE FROM `lines`;
DELETE FROM account;
DELETE FROM employee;
DELETE FROM position;
DELETE FROM department;
DELETE FROM role_permission;
DELETE FROM permission;
DELETE FROM role;


-- Reset AUTO_INCREMENT cho các bảng
ALTER TABLE permission AUTO_INCREMENT = 1;
ALTER TABLE role_permission AUTO_INCREMENT = 1;
ALTER TABLE application_approval_step AUTO_INCREMENT = 1;
ALTER TABLE application AUTO_INCREMENT = 1;
ALTER TABLE account_request AUTO_INCREMENT = 1;
ALTER TABLE attendance_record AUTO_INCREMENT = 1;
ALTER TABLE benefit AUTO_INCREMENT = 1;
ALTER TABLE work_schedule AUTO_INCREMENT = 1;
ALTER TABLE work_schedule_detail AUTO_INCREMENT = 1;
ALTER TABLE department_position AUTO_INCREMENT = 1;
ALTER TABLE interview_schedule AUTO_INCREMENT = 1;
ALTER TABLE candidate_recruitment AUTO_INCREMENT = 1;
ALTER TABLE candidate AUTO_INCREMENT = 1;
ALTER TABLE recruitment AUTO_INCREMENT = 1;
ALTER TABLE `lines` AUTO_INCREMENT = 1;
ALTER TABLE account AUTO_INCREMENT = 1;
ALTER TABLE employee AUTO_INCREMENT = 1;
ALTER TABLE position AUTO_INCREMENT = 1;
ALTER TABLE department AUTO_INCREMENT = 1;
ALTER TABLE role AUTO_INCREMENT = 1;
ALTER TABLE holidays AUTO_INCREMENT = 1;
ALTER TABLE employee
MODIFY COLUMN union_fee DECIMAL(10,2) DEFAULT 50000;


INSERT INTO role (role_id, role_name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_HR'),
(3, 'ROLE_LINE_LEADER'),
(4, 'ROLE_PRODUCTION_MANAGER'),
(6, 'ROLE_EMPLOYEE'),
(7, 'ROLE_PMC'),
(8, 'ROLE_HR_MANAGER');
-- Insert quyền cho module Role Management
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(1, 'PUT', '/api/roles/**', 'Cập nhật phân quyền', 'Role'),
(2, 'POST', '/api/permissions', 'Tạo mới quyền', 'Role'),
(3, 'GET', '/api/permissions', 'Xem danh sách quyền', 'Role');

-- Insert quyền cho module Account
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(4, 'GET', '/api/accounts', 'Xem danh sách tài khoản', 'Account'),
(5, 'PUT', '/api/accounts/*/toggle-status', 'Kích hoạt/Vô hiệu hóa tài khoản', 'Account');

-- Gán các permission mới vào role admin
INSERT INTO role_permission (role_id, permission_id) VALUES 
(1, 4), 
(1, 5);

-- Insert quyền cho module Account Request
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(6, 'GET', '/api/account-requests/*', 'Xem danh sách yêu cầu tạo tài khoản', 'Account Request'),
(7, 'POST', '/api/account-requests/*/approve', 'Duyệt yêu cầu tạo tài khoản', 'Account Request'),
(8, 'POST', '/api/account-requests/*/reject', 'Từ chối yêu cầu tạo tài khoản', 'Account Request');

-- Gán các permission mới vào role admin
INSERT INTO role_permission (role_id, permission_id) VALUES 
(1, 6), 
(1, 7), 
(1, 8);

-- Gán cho role admin
INSERT INTO role_permission (role_id, permission_id) VALUES 
(1, 2), (1, 3), (1, 1);

-- Module: Application
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(9, 'POST', '/api/applications', 'Tạo mới đơn từ', 'Application'),
(10, 'GET', '/api/applications/me', 'Xem danh sách đơn từ của bản thân', 'Application'),
(11, 'GET', '/api/applications/*', 'Xem chi tiết đơn từ', 'Application'),
(12, 'PUT', '/api/applications/*', 'Cập nhật đơn từ', 'Application'),

(13, 'GET', '/api/applications/step-1', 'Xem danh sách đơn chờ duyệt bước 1', 'Application'),
(14, 'PUT', '/api/applications/*/approve-step-1', 'Duyệt/từ chối đơn bước 1', 'Application'),

(15, 'GET', '/api/applications/step-2', 'Xem danh sách đơn chờ duyệt bước 2', 'Application'),
(16, 'PUT', '/api/applications/*/approve-step-2', 'Duyệt/từ chối đơn bước 2', 'Application');

-- Phân quyền cho các role tương ứng
-- ADMIN: tất cả các quyền
INSERT INTO role_permission (role_id, permission_id) VALUES 
(1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16);

-- PMC, EMPLOYEE: chỉ được tạo, xem danh sách của bản thân, chi tiết, sửa đơn
INSERT INTO role_permission (role_id, permission_id) VALUES 
(7, 9), (7, 10), (7, 11), (7, 12), -- PMC
(6, 9), (6, 10), (6, 11), (6, 12); -- EMPLOYEE

-- PRODUCTION_MANAGER: tạo, xem, sửa đơn và duyệt bước 1
INSERT INTO role_permission (role_id, permission_id) VALUES 
(4, 9), (4, 10), (4, 11), (4, 12), (4, 13), (4, 14);

-- HR, HR_MANAGER: xem chi tiết, xem và duyệt bước 2
INSERT INTO role_permission (role_id, permission_id) VALUES 
(2, 11), (2, 15), (2, 16), -- HR
(8, 11), (8, 15), (8, 16); -- HR_MANAGER
-- Module: Attendance 
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(17, 'GET', '/api/attendances/view-by-month', 'Xem bảng công theo tháng', 'Attendance '),
(18, 'GET', '/api/attendances/employee', 'Xem bảng công cá nhân theo tháng', 'Attendance '),
(19, 'GET', '/api/attendances/available-months', 'Xem danh sách tháng có dữ liệu bảng công', 'Attendance '),
(20, 'PUT', '/api/attendances/*', 'Cập nhật giờ vào – giờ ra', 'Attendance '),
(21, 'PUT', '/api/attendances/*/leave-code', 'Cập nhật mã nghỉ phép', 'Attendance ');

-- Gán quyền Attendance  cho các role tương ứng:
-- ADMIN, HR, HR_MANAGER, PRODUCTION_MANAGER được quyền xem bảng công tháng, cập nhật giờ vào ra và mã nghỉ phép:
INSERT INTO role_permission (role_id, permission_id) VALUES 
(1,17),(1,18),(1,19),(1,20),(1,21),   -- ADMIN
(2,17),(2,18),(2,19),(2,20),(2,21),   -- HR
(8,17),(8,18),(8,19),(8,20),(8,21),   -- HR_MANAGER
(4,17),(4,18),(4,19);                 -- PRODUCTION_MANAGER chỉ xem, không sửa giờ ra vào/mã nghỉ

-- EMPLOYEE, LINE_LEADER, PMC được quyền xem bảng công cá nhân và danh sách tháng:
INSERT INTO role_permission (role_id, permission_id) VALUES 
(6,18),(6,19),  -- EMPLOYEE
(3,18),(3,19),  -- LINE_LEADER
(7,18),(7,19);  -- PMC
-- Module: Auth
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(22, 'POST', '/api/auth/login', 'Đăng nhập', 'Auth'),
(23, 'POST', '/api/auth/reset-password', 'Reset mật khẩu bằng email', 'Auth'),
(24, 'POST', '/api/auth/change-password', 'Đổi mật khẩu cá nhân', 'Auth'),
(25, 'POST', '/api/auth/request-reset-password', 'Yêu cầu reset mật khẩu', 'Auth'),
(26, 'GET', '/api/auth/admin/pending-reset-requests', 'Xem yêu cầu reset đang chờ duyệt', 'Auth'),
(27, 'POST', '/api/auth/admin/approve-reset-password', 'Phê duyệt reset mật khẩu', 'Auth');

-- Gán permission:
INSERT INTO role_permission (role_id, permission_id) VALUES 
(1,24),(1,26),(1,27),       -- ADMIN
(2,24),                     -- HR
(3,24),                     -- LINE_LEADER
(4,24),                     -- PRODUCTION_MANAGER
(6,24),                     -- EMPLOYEE
(7,24);                     -- PMC
-- Module: Recruitment
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(28, 'GET', '/api/candidates/*', 'Xem danh sách ứng viên theo tin tuyển dụng', 'Recruitment');

-- Gán quyền cho HR và HR_MANAGER
INSERT INTO role_permission (role_id, permission_id) VALUES 
(2, 28),  -- HR
(8, 28);  -- HR_MANAGER
-- Module: Recruitment
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(29, 'GET', '/api/recruitment', 'Xem danh sách tin tuyển dụng', 'Recruitment'),
(30, 'GET', '/api/recruitment/*', 'Xem chi tiết tin tuyển dụng', 'Recruitment'),
(31, 'POST', '/api/recruitment', 'Tạo tin tuyển dụng', 'Recruitment'),
(32, 'PUT', '/api/recruitment/*', 'Chỉnh sửa tin tuyển dụng', 'Recruitment');

-- Gán cho HR và HR_MANAGER quyền tạo/sửa
INSERT INTO role_permission (role_id, permission_id) VALUES 
(2, 31), (2, 32), (2,29),(2,30),   -- HR
(8, 31), (8, 32),(8,29),(8,30);    -- HR_MANAGER
-- Module: Dashboard
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(33, 'GET', '/api/dashboard/recruitment-graph', 'Xem biểu đồ tuyển dụng', 'Dashboard');

-- Gán quyền cho HR và HR_MANAGER
INSERT INTO role_permission (role_id, permission_id) VALUES 
(2, 33),   -- HR
(8, 33);   -- HR_MANAGER
-- Module: Department
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(34, 'GET', '/api/departments', 'Xem danh sách phòng ban', 'Department'),
(35, 'GET', '/api/departments/*/positions', 'Xem vị trí theo phòng ban', 'Department'),
(36, 'GET', '/api/departments/*/lines', 'Xem line theo phòng ban', 'Department');

-- Gán cho tất cả các role (id: 1-8)
INSERT INTO role_permission (role_id, permission_id) VALUES 
(1, 34), (1, 35), (1, 36),
(2, 34), (2, 35), (2, 36),
(3, 34), (3, 35), (3, 36),
(4, 34), (4, 35), (4, 36),
(6, 34), (6, 35), (6, 36),
(7, 34), (7, 35), (7, 36),
(8, 34), (8, 35), (8, 36);
-- Module: Holiday
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES 
(37, 'GET', '/api/holidays', 'Xem danh sách ngày nghỉ', 'Holiday'),
(38, 'POST', '/api/holidays', 'Tạo ngày nghỉ', 'Holiday'),
(39, 'GET', '/api/holidays/check/*', 'Kiểm tra ngày có phải ngày nghỉ', 'Holiday'),
(40, 'DELETE', '/api/holidays/*', 'Xóa ngày nghỉ', 'Holiday'),
(41, 'GET', '/api/holidays/*', 'Xem chi tiết ngày nghỉ', 'Holiday'),
(42, 'PUT', '/api/holidays/*', 'Cập nhật ngày nghỉ', 'Holiday');

-- Gán cho các role được phép
INSERT INTO role_permission (role_id, permission_id) VALUES 
-- ADMIN (1)
(1, 37), (1, 38), (1, 39), (1, 40), (1, 41), (1, 42),
-- HR (2)
(2, 37), (2, 38), (2, 39), (2, 40), (2, 41), (2, 42),
-- PRODUCTION_MANAGER (4)
(4, 37), (4, 38), (4, 39), (4, 40), (4, 41), (4, 42),
-- PMC (7)
(7, 37), (7, 38), (7, 39), (7, 40), (7, 41), (7, 42),
-- HR_MANAGER (8)
(8, 37), (8, 38), (8, 39), (8, 40), (8, 41), (8, 42);
-- Module: Employee
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(43, 'GET', '/api/employees', 'Xem danh sách nhân viên', 'Employee'),
(44, 'POST', '/api/employees', 'Tạo mới nhân viên', 'Employee'),
(45, 'PUT', '/api/employees/*', 'Cập nhật nhân viên', 'Employee'),
(46, 'DELETE', '/api/employees/*', 'Xóa nhân viên', 'Employee'),
(47, 'GET', '/api/employees/*', 'Xem chi tiết nhân viên', 'Employee'),
(48, 'GET', '/api/employees/profile', 'Xem hồ sơ cá nhân', 'Employee'),
(49, 'PUT', '/api/employees/profile', 'Cập nhật hồ sơ cá nhân', 'Employee'),
(50, 'GET', '/api/employees/next-code', 'Lấy mã nhân viên tiếp theo', 'Employee'),
(51, 'GET', '/api/employees/next-code/*', 'Lấy mã nhân viên theo vị trí', 'Employee'),
(52, 'GET', '/api/employees/export', 'Export danh sách nhân viên', 'Employee'),
(53, 'GET', '/api/employees/department/*', 'Lấy nhân viên theo phòng ban', 'Employee'),
(54, 'GET', '/api/employees/line/*', 'Lấy nhân viên theo chuyền', 'Employee'),
(55, 'GET', '/api/employees/not-in-line/*', 'Lấy NV chưa thuộc chuyền', 'Employee'),
(56, 'PUT', '/api/employees/add-to-line/*', 'Thêm NV vào chuyền', 'Employee');
-- ADMIN (1)
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 43), (1, 44), (1, 45), (1, 46), (1, 47),
(1, 48), (1, 49), (1, 50), (1, 51), (1, 52), (1, 53), (1, 54), (1, 55), (1, 56);

-- HR (2)
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 43), (2, 44), (2, 45), (2, 46), (2, 47),
(2, 48), (2, 49), (2, 50), (2, 51), (2, 52), (2, 53), (2, 54);

-- HR_MANAGER (8)
INSERT INTO role_permission (role_id, permission_id) VALUES
(8, 43), (8, 44), (8, 45), (8, 46), (8, 47),
(8, 48), (8, 49), (8, 50), (8, 51), (8, 52), (8, 53), (8, 54);

-- PMC (7)
INSERT INTO role_permission (role_id, permission_id) VALUES
(7, 48), (7, 49), (7, 54), (7, 55), (7, 56);

-- PRODUCTION_MANAGER (4)
INSERT INTO role_permission (role_id, permission_id) VALUES
(4, 48), (4, 49);

-- EMPLOYEE (6)
INSERT INTO role_permission (role_id, permission_id) VALUES
(6, 48), (6, 49);

-- LINE_LEADER (3)
INSERT INTO role_permission (role_id, permission_id) VALUES
(3, 48), (3, 49);
-- Module: HumanReport
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(57, 'GET', '/api/human-report/full-emp', 'Xem nhân lực đủ', 'HumanReport'),
(58, 'GET', '/api/human-report/absent', 'Xem nhân lực vắng', 'HumanReport'),
(59, 'GET', '/api/human-report/absentkl', 'Xem nhân lực vắng KL', 'HumanReport'),
(60, 'GET', '/api/human-report/export', 'Export báo cáo nhân lực', 'HumanReport');
-- HR (2)
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 57), (2, 58), (2, 59), (2, 60);

-- HR_MANAGER (8)
INSERT INTO role_permission (role_id, permission_id) VALUES
(8, 57), (8, 58), (8, 59);

-- PRODUCTION_MANAGER (4)
INSERT INTO role_permission (role_id, permission_id) VALUES
(4, 57), (4, 58), (4, 59);

-- ADMIN (1)
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 60);
-- HR_MANAGER (8) - thêm quyền export
INSERT INTO role_permission (role_id, permission_id) VALUES
(8, 60);
-- Interview permissions (module = 'Interview')
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(61, 'GET', '/api/interview', 'Xem danh sách lịch phỏng vấn', 'Interview'),
(62, 'GET', '/api/interview/*', 'Xem chi tiết lịch phỏng vấn', 'Interview'),
(63, 'GET', '/api/interview/candidate-recruitment/*', 'Xem lịch phỏng vấn theo ứng viên', 'Interview'),
(64, 'POST', '/api/interview', 'Tạo lịch phỏng vấn', 'Interview'),
(65, 'PUT', '/api/interview/*', 'Cập nhật lịch phỏng vấn', 'Interview'),
(66, 'PUT', '/api/interview/*/status', 'Cập nhật trạng thái phỏng vấn', 'Interview'),
(67, 'PUT', '/api/interview/*/result', 'Cập nhật kết quả phỏng vấn', 'Interview');
-- HR (role_id = 2)
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 61), (2, 62), (2, 63), (2, 64), (2, 65), (2, 66), (2, 67);

-- HR_MANAGER (role_id = 8)
INSERT INTO role_permission (role_id, permission_id) VALUES
(8, 61), (8, 62), (8, 63), (8, 64), (8, 65), (8, 66), (8, 67);
-- Module: Line
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(68, 'GET', '/api/lines/*/department', 'Lấy phòng ban theo chuyền', 'Line'),
(69, 'GET', '/api/lines', 'Xem danh sách chuyền', 'Line'),
(70, 'GET', '/api/lines/*', 'Xem chi tiết chuyền', 'Line'),
(71, 'PUT', '/api/lines/*/leader', 'Gán tổ trưởng cho chuyền', 'Line');

-- HR (2)
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 68), (2, 69), (2, 70), (2, 71);

-- HR_MANAGER (8)
INSERT INTO role_permission (role_id, permission_id) VALUES
(8, 68), (8, 69), (8, 70), (8, 71);

-- PMC (7)
INSERT INTO role_permission (role_id, permission_id) VALUES
(7, 68), (7, 69), (7, 70), (7, 71);
-- Module: OCR
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(72, 'POST', '/api/ocr/scan-cccd', 'Scan CCCD bằng OCR', 'OCR'),
(73, 'GET', '/api/ocr/face-image', 'Tách ảnh khuôn mặt từ CCCD', 'OCR');

-- ADMIN (1)
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 72), (1, 73);

-- HR (2)
INSERT INTO role_permission (role_id, permission_id) VALUES (2, 72), (2, 73);

-- HR_MANAGER (8)
INSERT INTO role_permission (role_id, permission_id) VALUES (8, 72), (8, 73);
-- Permission ID 74: Cập nhật quyền
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(74, 'PUT', '/api/permissions/*', 'Cập nhật quyền', 'Role');

-- Permission ID 75: Xóa quyền
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(75, 'DELETE', '/api/permissions/*', 'Xóa quyền', 'Role');
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 74), (1, 75);
-- ID tiếp theo: 76
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(76, 'GET', '/api/roles/**', 'Xem quyền theo vai trò', 'Role');

-- Gán cho ADMIN (role_id = 1)
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 76);
-- Module: Salary
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(77, 'POST', '/api/salaries', 'Tạo bảng lương', 'Salary'),
(78, 'GET', '/api/salaries', 'Xem bảng lương', 'Salary'),
(79, 'PUT', '/api/salaries/regenerate', 'Tạo lại bảng lương', 'Salary'),
(80, 'GET', '/api/salaries/available-months', 'Xem các tháng có lương', 'Salary'),
(81, 'GET', '/api/salaries/employee-months', 'Xem lương theo tháng (nhân viên)', 'Salary'),
(82, 'PUT', '/api/salaries/lock', 'Chốt/bỏ chốt bảng lương', 'Salary');
-- HR và HR_MANAGER có toàn quyền
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 77), (2, 78), (2, 79), (2, 80), (2, 82),
(8, 77), (8, 78), (8, 79), (8, 80), (8, 82);

-- EMPLOYEE được xem lương của mình và danh sách tháng
INSERT INTO role_permission (role_id, permission_id) VALUES
(6, 80), (6, 81);
-- Module: WorkSchedule
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(83, 'POST', '/api/work-schedules', 'Tạo lịch làm việc cho toàn bộ', 'WorkSchedule'),
(84, 'GET', '/api/work-schedules/available', 'Xem các tháng có lịch làm việc', 'WorkSchedule'),
(85, 'GET', '/api/work-schedules/resolve-id', 'Tìm ID lịch làm việc theo phòng/chuyền/ngày', 'WorkSchedule'),
(86, 'PUT', '/api/work-schedules/submit', 'Gửi lịch làm việc', 'WorkSchedule'),
(87, 'PUT', '/api/work-schedules/accept', 'Duyệt lịch làm việc', 'WorkSchedule'),
(88, 'GET', '/api/work-schedules/employee-view', 'Xem lịch làm việc nhân viên', 'WorkSchedule'),
(89, 'PUT', '/api/work-schedules/reject', 'Từ chối lịch làm việc', 'WorkSchedule'),
(90, 'PUT', '/api/work-schedules/custom-range', 'Tạo lịch theo khoảng tuỳ chỉnh', 'WorkSchedule'),
(91, 'PUT', '/api/work-schedules/request-revision', 'Yêu cầu chỉnh sửa lại lịch đã duyệt', 'WorkSchedule');
-- ADMIN (1)
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 83), (1, 84), (1, 85), (1, 86), (1, 87), (1, 88), (1, 89), (1, 90), (1, 91);

-- PMC (7): tạo, dải, gửi
INSERT INTO role_permission (role_id, permission_id) VALUES
(7, 83), (7, 84), (7, 85), (7, 86), (7, 90);

-- PRODUCTION_MANAGER (4): duyệt, từ chối, yêu cầu sửa
INSERT INTO role_permission (role_id, permission_id) VALUES
(4, 85), (4, 87), (4, 88), (4, 89), (4, 91);

-- EMPLOYEE (6): chỉ xem lịch của mình
INSERT INTO role_permission (role_id, permission_id) VALUES
(6, 88);
-- Gán quyền permission_id = 84 cho các role còn thiếu
INSERT INTO role_permission (role_id, permission_id) VALUES
(4, 84),  -- PRODUCTION_MANAGER
(6, 84),  -- EMPLOYEE
(2, 84),  -- HR
(8, 84);  -- HR_MANAGER
-- Module: WorkScheduleDetail
INSERT INTO permission (permission_id, method, api_path, name, module) VALUES
(92, 'GET', '/api/work-schedule-details/view-by-month', 'Xem lịch theo tháng', 'WorkScheduleDetail'),
(93, 'POST', '/api/work-schedule-details', 'Tạo chi tiết lịch làm việc', 'WorkScheduleDetail'),
(94, 'PUT', '/api/work-schedule-details', 'Cập nhật chi tiết lịch làm việc', 'WorkScheduleDetail'),
(95, 'DELETE', '/api/work-schedule-details/*', 'Xoá chi tiết lịch làm việc', 'WorkScheduleDetail');
-- ADMIN (1): full quyền
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 92), (1, 93), (1, 94), (1, 95);

-- PMC (7): tạo, sửa, xoá, xem
INSERT INTO role_permission (role_id, permission_id) VALUES
(7, 92), (7, 93), (7, 94), (7, 95);

-- PRODUCTION_MANAGER (4): chỉ xem
INSERT INTO role_permission (role_id, permission_id) VALUES
(4, 92);

-- HR (2): chỉ xem
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 92);

-- HR_MANAGER (8): mới bổ sung – cũng chỉ xem
INSERT INTO role_permission (role_id, permission_id) VALUES
(8, 92);


INSERT INTO department (department_id, department_name) VALUES
(1, 'Bán Tự Động'),
(4, 'QC'),
(5, 'Sản Xuất'),
(6, 'Tự Động'),
(8, 'Nhân Sự'),
(9, 'PMC'),
(10, 'Quản lý');

INSERT INTO position (position_id, position_name, description) VALUES
(1, 'Công Nhân', NULL),
(2, 'Công Nhân Kiểm Tra', NULL),
(3, 'Data', NULL),
(6, 'Nhân Viên Vệ Sinh', NULL),
(7, 'Nhân Viên QC', NULL),
(10, 'Quản Lý Sản Xuất', NULL),
(11, 'Quản Lý Kỹ Thuật', NULL),
(12, 'Quản Lý Vật Tư', NULL),
(15, 'Tổ Trưởng', NULL),
(17, 'HR', 'Nhân viên nhân sự'),
(18, 'PMC', 'Nhân viên phòng kế hoạch sản xuất & vật tư'),
(19, 'Trưởng Phòng Nhân Sự', 'Quản lý phòng Nhân sự');

INSERT INTO department_position (department_id, position_id) VALUES
(1, 1),  
(1, 15), 
(5,15), 
(10,10),
(4, 2),  
(4, 15),
(5, 1),  
(6, 1),  
(6, 15), 
(8, 17), 
(9, 18),
(8, 19);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id,
    basic_salary, allowance_phone, allowance_meal, allowance_attendance, allowance_transport
) VALUES
(1, 'ELTSSX0001', 'Nguyễn Văn A', 'NAM', '1991-01-15',
 NULL, NULL, 'Vietnam', '0123456781',
 '2010-01-01', '2030-01-01', 'Hà Nội',
 NULL, NULL, '2016-01-01', '0901234567', 'user1@example.com',
 1, 1,
 4620000, 100000, 200000, 500000, 40000),

(2, 'ELTSHC0001', 'Trần Thị B', 'NỮ', '1992-02-15',
 NULL, NULL, 'Vietnam', '0123456782',
 '2011-01-01', '2031-01-01', 'Hồ Chí Minh',
 NULL, NULL, '2017-01-01', '0902345678', 'user2@example.com',
 8, 17,
 4800000, 90000, 180000, 450000, 35000),

(3, 'ELTSSX0002', 'Lê Văn C', 'NAM', '1993-03-15',
 NULL, NULL, 'Vietnam', '0123456783',
 '2012-01-01', '2032-01-01', 'Đà Nẵng',
 NULL, NULL, '2018-01-01', '0903456789', 'user3@example.com',
 4, 2,
 5000000, 95000, 220000, 470000, 30000),

(4, 'ELTSSX0003', 'Phạm Thị D', 'NỮ', '1994-04-15',
 NULL, NULL, 'Vietnam', '0123456784',
 '2013-01-01', '2033-01-01', 'Hải Phòng',
 NULL, NULL, '2019-01-01', '0904567890', 'user4@example.com',
 5, 15,
 4750000, 98000, 210000, 490000, 37000),

(5, 'ELTSSX0004', 'Hoàng Văn E', 'NAM', '1995-05-15',
 NULL, NULL, 'Vietnam', '0123456785',
 '2014-01-01', '2034-01-01', 'Cần Thơ',
 NULL, NULL, '2020-01-01', '0905678901', 'user5@example.com',
 1, 1,
 4900000, 92000, 205000, 460000, 36000),

(6, 'ELTSSX0005', 'Nguyễn Thị F', 'NỮ', '1996-06-15',
 NULL, NULL, 'Vietnam', '0123456786',
 '2015-01-01', '2035-01-01', 'Huế',
 NULL, NULL, '2021-01-01', '0906789012', 'user6@example.com',
 5, 1,
 4700000, 87000, 200000, 430000, 34000),

(7, 'ELTSSX0006', 'Vũ Văn G', 'NAM', '1997-07-15',
 NULL, NULL, 'Vietnam', '0123456787',
 '2016-01-01', '2036-01-01', 'Quảng Ninh',
 NULL, NULL, '2022-01-01', '0907890123', 'user7@example.com',
 10, 10,
 4850000, 99000, 215000, 510000, 39000),

(8, 'ELTSSX0007', 'Đoàn Thị H', 'NỮ', '1998-08-15',
 NULL, NULL, 'Vietnam', '0123456788',
 '2017-01-01', '2037-01-01', 'Nghệ An',
 NULL, NULL, '2023-01-01', '0908901234', 'user8@example.com',
 5, 1,
 4950000, 93000, 198000, 440000, 32000),

(9, 'ELTSSX0008', 'Phan Văn I', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456789',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0909012345', 'user9@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(10, 'ELTSSX0009', 'Bùi Thị J', 'NỮ', '1990-10-15',
 NULL, NULL, 'Vietnam', '0123456790',
 '2009-01-01', '2029-01-01', 'Bắc Ninh',
 NULL, NULL, '2025-01-01', '0900123456', 'user10@example.com',
 9, 18,
 5100000, 96000, 212000, 480000, 38000),

(11, 'ELTSSX0011', 'Nguyễn Văn K', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456791',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0901234567', 'user11@example.com',
 5, 15,
 5050000, 94000, 208000, 450000, 33000),

(12, 'ELTSSX0012', 'Trần Thị L', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456792',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0902345678', 'user12@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(13, 'ELTSSX0013', 'Lê Văn M', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456793',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0903456789', 'user13@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(14, 'ELTSSX0014', 'Phạm Thị N', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456794',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0904567890', 'user14@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(15, 'ELTSSX0015', 'Hoàng Văn O', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456795',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0905678901', 'user15@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(16, 'ELTSSX0016', 'Nguyễn Thị P', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456796',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0906789012', 'user16@example.com',
 5, 15,
 5050000, 94000, 208000, 450000, 33000),

(17, 'ELTSSX0017', 'Vũ Văn Q', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456797',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0907890123', 'user17@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(18, 'ELTSSX0018', 'Đoàn Thị R', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456798',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0908901234', 'user18@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(19, 'ELTSSX0019', 'Phan Văn S', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456799',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0909012345', 'user19@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(20, 'ELTSSX0020', 'Bùi Thị T', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456800',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0900123456', 'user20@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(21, 'ELTSSX0021', 'Nguyễn Văn U', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456801',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0901234567', 'user21@example.com',
 5, 15,
 5050000, 94000, 208000, 450000, 33000),

(22, 'ELTSSX0022', 'Trần Thị V', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456802',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0902345678', 'user22@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(23, 'ELTSSX0023', 'Lê Văn W', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456803',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0903456789', 'user23@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(24, 'ELTSSX0024', 'Phạm Thị X', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456804',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0904567890', 'user24@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(25, 'ELTSSX0025', 'Hoàng Văn Y', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456805',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0905678901', 'user25@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(26, 'ELTSSX0026', 'Nguyễn Thị Z', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456806',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0906789012', 'user26@example.com',
 5, 15,
 5050000, 94000, 208000, 450000, 33000),

(27, 'ELTSSX0027', 'Vũ Văn AA', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456807',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0907890123', 'user27@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(28, 'ELTSSX0028', 'Đoàn Thị AB', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456808',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0908901234', 'user28@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(29, 'ELTSSX0029', 'Phan Văn AC', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456809',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0909012345', 'user29@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(30, 'ELTSSX0030', 'Bùi Thị AD', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456810',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0900123456', 'user30@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(31, 'ELTSSX0031', 'Nguyễn Văn AE', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456811',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0901234567', 'user31@example.com',
 5, 15,
 5050000, 94000, 208000, 450000, 33000),

(32, 'ELTSSX0032', 'Trần Thị AF', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456812',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0902345678', 'user32@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(33, 'ELTSSX0033', 'Lê Văn AG', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456813',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0903456789', 'user33@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(34, 'ELTSSX0034', 'Phạm Thị AH', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456814',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0904567890', 'user34@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(35, 'ELTSSX0035', 'Hoàng Văn AI', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456815',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0905678901', 'user35@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(36, 'ELTSSX0036', 'Nguyễn Thị AJ', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456816',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0906789012', 'user36@example.com',
 5, 15,
 5050000, 94000, 208000, 450000, 33000),

(37, 'ELTSSX0037', 'Vũ Văn AK', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456817',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0907890123', 'user37@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(38, 'ELTSSX0038', 'Đoàn Thị AL', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456818',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0908901234', 'user38@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(39, 'ELTSSX0039', 'Phan Văn AM', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456819',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0909012345', 'user39@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(40, 'ELTSSX0040', 'Bùi Thị AN', 'NỮ', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456820',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0900123456', 'user40@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000);

-- Phòng ban Bán Tự Động
INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    department_id, position_id, basic_salary, phone_number, email,
    allowance_phone, allowance_meal, allowance_attendance, allowance_transport
)
VALUES
(41, 'ELTSSX0041', 'Nguyễn Văn B1', 'NAM', '1985-02-20', 1, 15, 5000000, '0901000001', 'user41@example.com', 94000, 208000, 450000, 33000),
(42, 'ELTSSX0042', 'Trần Thị B2', 'NỮ', '1990-04-25', 1, 1, 4800000, '0901000002', 'user42@example.com', 94000, 208000, 450000, 33000),
(43, 'ELTSSX0043', 'Lê Văn B3', 'NAM', '1992-07-15', 1, 1, 5000000, '0901000003', 'user43@example.com', 94000, 208000, 450000, 33000);

-- Phòng ban QC
INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    department_id, position_id, basic_salary, phone_number, email,
    allowance_phone, allowance_meal, allowance_attendance, allowance_transport
)
VALUES
(44, 'ELTSSX0044', 'Nguyễn Thị C1', 'NỮ', '1990-05-22', 4, 15, 5200000, '0902000001', 'user44@example.com', 94000, 208000, 450000, 33000),
(45, 'ELTSSX0045', 'Vũ Thị C2', 'NỮ', '1993-10-18', 4, 2, 5100000, '0902000002', 'user45@example.com', 94000, 208000, 450000, 33000),
(46, 'ELTSSX0046', 'Trần Văn C3', 'NAM', '1988-03-30', 4, 2, 5300000, '0902000003', 'user46@example.com', 94000, 208000, 450000, 33000);

-- Phòng ban Tự Động
INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    department_id, position_id, basic_salary, phone_number, email,
    allowance_phone, allowance_meal, allowance_attendance, allowance_transport
)
VALUES
(47, 'ELTSSX0047', 'Phạm Thị D1', 'NỮ', '1991-09-05', 6, 15, 5200000, '0903000001', 'user47@example.com', 94000, 208000, 450000, 33000),
(48, 'ELTSSX0048', 'Bùi Văn D2', 'NAM', '1994-01-12', 6, 1, 5100000, '0903000002', 'user48@example.com', 94000, 208000, 450000, 33000),
(49, 'ELTSSX0049', 'Nguyễn Thị D3', 'NỮ', '1992-11-05', 6, 1, 5300000, '0903000003', 'user49@example.com', 94000, 208000, 450000, 33000);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id,
    basic_salary, allowance_phone, allowance_meal, allowance_attendance, allowance_transport
) VALUES
(50, 'ELTSHC0002', 'Ngô Văn HR', 'NAM', '1980-12-12',
 NULL, NULL, 'Vietnam', '0123456999',
 '2005-01-01', '2025-01-01', 'Hà Nội',
 NULL, NULL, '2010-01-01', '0911999999', 'truongphonghr@example.com',
 8, 19,
 8000000, 120000, 250000, 550000, 50000);


INSERT INTO account (account_id, username, password_hash, email, is_active, created_at, updated_at, last_login_at, login_attempts, must_change_password, employee_id, role_id) VALUES
(1, 'user1', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user1@example.com', true, NOW(), NOW(), NULL, 5, false, 1, 1),
(2, 'user2', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user2@example.com', true, NOW(), NOW(), NULL, 5, false, 2, 2),
(3, 'user3', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user3@example.com', true, NOW(), NOW(), NULL, 5, false, 3, 6),
(4, 'user4', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user4@example.com', true, NOW(), NOW(), NULL, 5, false, 4, 3),
(5, 'user5', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user5@example.com', true, NOW(), NOW(), NULL, 5, false, 5, 6),
(6, 'user6', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user6@example.com', true, NOW(), NOW(), NULL, 5, false, 6, 6),
(7, 'user7', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user7@example.com', true, NOW(), NOW(), NULL, 5, false, 7, 4),
(8, 'user8', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user8@example.com', true, NOW(), NOW(), NULL, 5, false, 8, 6),
(9, 'user9', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user9@example.com', true, NOW(), NOW(), NULL, 5, false, 9, 6),
(10, 'user10', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user10@example.com', true, NOW(), NOW(), NULL, 5, false, 10, 7),
-- add new account
(11, 'user11', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user11@example.com', true, NOW(), NOW(), NULL, 5, false, 11, 3),
(12, 'user12', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user12@example.com', true, NOW(), NOW(), NULL, 5, false, 12, 6),
(13, 'user13', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user13@example.com', true, NOW(), NOW(), NULL, 5, false, 13, 6),
(14, 'user14', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user14@example.com', true, NOW(), NOW(), NULL, 5, false, 14, 6),
(15, 'user15', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user15@example.com', true, NOW(), NOW(), NULL, 5, false, 15, 6),

(16, 'user16', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user16@example.com', true, NOW(), NOW(), NULL, 5, false, 16, 3),
(17, 'user17', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user17@example.com', true, NOW(), NOW(), NULL, 5, false, 17, 6),
(18, 'user18', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user18@example.com', true, NOW(), NOW(), NULL, 5, false, 18, 6),
(19, 'user19', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user19@example.com', true, NOW(), NOW(), NULL, 5, false, 19, 6),
(20, 'user20', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user20@example.com', true, NOW(), NOW(), NULL, 5, false, 20, 6),

(21, 'user21', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user21@example.com', true, NOW(), NOW(), NULL, 5, false, 21, 3),
(22, 'user22', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user22@example.com', true, NOW(), NOW(), NULL, 5, false, 22, 6),
(23, 'user23', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user23@example.com', true, NOW(), NOW(), NULL, 5, false, 23, 6),
(24, 'user24', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user24@example.com', true, NOW(), NOW(), NULL, 5, false, 24, 6),
(25, 'user25', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user25@example.com', true, NOW(), NOW(), NULL, 5, false, 25, 6),

(26, 'user26', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user26@example.com', true, NOW(), NOW(), NULL, 5, false, 26, 3),
(27, 'user27', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user27@example.com', true, NOW(), NOW(), NULL, 5, false, 27, 6),
(28, 'user28', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user28example.com', true, NOW(), NOW(), NULL, 5, false, 28, 6),
(29, 'user29', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user29@example.com', true, NOW(), NOW(), NULL, 5, false, 29, 6),
(30, 'user30', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user30@example.com', true, NOW(), NOW(), NULL, 5, false, 30, 6),

(31, 'user31', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user31@example.com', true, NOW(), NOW(), NULL, 5, false, 31, 3),
(32, 'user32', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user32@example.com', true, NOW(), NOW(), NULL, 5, false, 32, 6),
(33, 'user33', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user33@example.com', true, NOW(), NOW(), NULL, 5, false, 33, 6),
(34, 'user34', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user34@example.com', true, NOW(), NOW(), NULL, 5, false, 34, 6),
(35, 'user35', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user35@example.com', true, NOW(), NOW(), NULL, 5, false, 35, 6),

(36, 'user36', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user36@example.com', true, NOW(), NOW(), NULL, 5, false, 36, 3),
(37, 'user37', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user37@example.com', true, NOW(), NOW(), NULL, 5, false, 37, 6),
(38, 'user38', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user38@example.com', true, NOW(), NOW(), NULL, 5, false, 38, 6),
(39, 'user39', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user39@example.com', true, NOW(), NOW(), NULL, 5, false, 39, 6),
(40, 'user40', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user40@example.com', true, NOW(), NOW(), NULL, 5, false, 40, 6),
(41, 'user41', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user41@example.com', true, NOW(), NOW(), NULL, 5, false, 41, 3),
(42, 'user42', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user42@example.com', true, NOW(), NOW(), NULL, 5, false, 42, 6),
(43, 'user43', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user43@example.com', true, NOW(), NOW(), NULL, 5, false, 43, 6),
(44, 'user44', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user44@example.com', true, NOW(), NOW(), NULL, 5, false, 44, 3),
(45, 'user45', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user45@example.com', true, NOW(), NOW(), NULL, 5, false, 45, 6),
(46, 'user46', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user46@example.com', true, NOW(), NOW(), NULL, 5, false, 46, 6),
(47, 'user47', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user47@example.com', true, NOW(), NOW(), NULL, 5, false, 47, 3),
(48, 'user48', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user48@example.com', true, NOW(), NOW(), NULL, 5, false, 48, 6),
(49, 'user49', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user49@example.com', true, NOW(), NOW(), NULL, 5, false, 49, 6);

INSERT INTO account (
    account_id, username, password_hash, email, is_active, created_at, updated_at,
    last_login_at, login_attempts, must_change_password, employee_id, role_id
) VALUES
(50, 'hrmanager', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy',
 'truongphonghr@example.com', true, NOW(), NOW(),
 NULL, 5, false, 50, 8);




INSERT INTO `lines` (line_id, line_name, department_id,leader_id) VALUES
(1, 'Line 1', 5,11 ),  
(2, 'Line 2', 5,16),   
(3, 'Line 3', 5,21),  
(4, 'Line 4', 5,6),  
(5, 'Line 5', 5,26),   
(6, 'Line 6', 5,31),   
(7, 'Line 7', 5,36);   


UPDATE employee SET line_id = 1 WHERE employee_id = 11; 
UPDATE employee SET line_id = 1 WHERE employee_id = 12;  
UPDATE employee SET line_id = 1 WHERE employee_id = 13;
UPDATE employee SET line_id = 1 WHERE employee_id = 14; 
UPDATE employee SET line_id = 1 WHERE employee_id = 15; 

UPDATE employee SET line_id = 2 WHERE employee_id = 16; 
UPDATE employee SET line_id = 2 WHERE employee_id = 17;  
UPDATE employee SET line_id = 2 WHERE employee_id = 18;
UPDATE employee SET line_id = 2 WHERE employee_id = 19; 
UPDATE employee SET line_id = 2 WHERE employee_id = 20; 

UPDATE employee SET line_id = 3 WHERE employee_id = 21; 
UPDATE employee SET line_id = 3 WHERE employee_id = 22;  
UPDATE employee SET line_id = 3 WHERE employee_id = 23;
UPDATE employee SET line_id = 3 WHERE employee_id = 24; 
UPDATE employee SET line_id = 3 WHERE employee_id = 25; 

UPDATE employee SET line_id = 4 WHERE employee_id = 6; 
UPDATE employee SET line_id = 4 WHERE employee_id = 8;  
UPDATE employee SET line_id = 4 WHERE employee_id = 9; 
UPDATE employee SET line_id = 4 WHERE employee_id = 10; 
UPDATE employee SET line_id = 4 WHERE employee_id = 4; 

UPDATE employee SET line_id = 5 WHERE employee_id = 26; 
UPDATE employee SET line_id = 5 WHERE employee_id = 27;  
UPDATE employee SET line_id = 5 WHERE employee_id = 28; 
UPDATE employee SET line_id = 5 WHERE employee_id = 29; 
UPDATE employee SET line_id = 5 WHERE employee_id = 30; 

UPDATE employee SET line_id = 6 WHERE employee_id = 31; 
UPDATE employee SET line_id = 6 WHERE employee_id = 32;  
UPDATE employee SET line_id = 6 WHERE employee_id = 33; 
UPDATE employee SET line_id = 6 WHERE employee_id = 34; 
UPDATE employee SET line_id = 6 WHERE employee_id = 35; 

UPDATE employee SET line_id = 7 WHERE employee_id = 36; 
UPDATE employee SET line_id = 7 WHERE employee_id = 37;  
UPDATE employee SET line_id = 7 WHERE employee_id = 38; 
UPDATE employee SET line_id = 7 WHERE employee_id = 39; 
UPDATE employee SET line_id = 7 WHERE employee_id = 40; 

INSERT INTO application_type (application_type_name, description)
VALUES 
  ('Nghỉ phép', 'Đơn xin nghỉ phép có lương hoặc không lương'),
  ('Bù công', 'Đơn đề nghị bù công do thiếu chấm công hoặc lý do khác');

INSERT INTO recruitment (
    recruitment_id, title, employment_type, job_description,
    job_requirement, benefits, min_salary, max_salary, quantity,
    expired_at, create_at, update_at, status,
    department_id, created_by
) VALUES
(1, 'Tuyển công nhân QC', 'Full-time', 'Kiểm tra chất lượng sản phẩm.',
 'Tốt nghiệp THPT, chịu khó.', 'Phụ cấp ăn trưa, thưởng lễ.', 8000000, 10000000, 5,
 '2025-07-15 00:00:00', NOW(), NOW(), 'OPEN', 4, 2),
(2, 'Tuyển kỹ sư bảo trì', 'Full-time', 'Bảo trì dây chuyền bán tự động.',
 'CĐ/ĐH chuyên ngành cơ khí', 'Bảo hiểm full, đào tạo nội bộ.', 12000000, 15000000, 2,
 '2025-07-01 00:00:00', NOW(), NOW(), 'OPEN', 4, 5);


INSERT INTO candidate (
    candidate_id, candidate_name, gender, dob, email, phone_number
) VALUES
(1, 'Nguyễn Văn A', 'Nam', '1990-01-15', 'a@gmail.com', '0901234567'),
(2, 'Trần Thị B', 'Nữ', '1992-05-20', 'b@gmail.com', '0909876543'),
(3, 'Lê Văn C', 'Nam', '1988-11-30', 'c@gmail.com', '0912345678');

SHOW COLUMNS FROM candidate_recruitment;


INSERT INTO candidate_recruitment (
    candidate_id, recruitment_id, status, submitted_at
) VALUES
(1, 1, 'APPLIED', '2025-06-01 10:00:00'),
(2, 1, 'INTERVIEW_SCHEDULED', '2025-06-03 15:00:00'),
(2, 2, 'INTERVIEW_SCHEDULED', '2025-06-03 15:00:00'),
(3, 2, 'APPLIED', '2025-06-05 09:00:00');




INSERT INTO interview_schedule (
    interview_schedule_id, scheduled_at, status, feedback,
    candidate_id, interviewer_id, recruitment_id
) VALUES
(1, '2025-06-10 09:00:00', 'WAITING_INTERVIEW', 'Ứng viên phù hợp vị trí QC.', 1, 4, 1),
(2, '2025-06-12 10:30:00', 'INTERVIEWED', NULL, 2, 3, 1),
(3, '2025-06-14 14:00:00', 'INTERVIEWED', 'Ứng viên chưa đủ kỹ năng kỹ thuật.', 3, 6, 2);


-- ALTER TABLE benefit 
-- MODIFY COLUMN created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE benefit
MODIFY COLUMN created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE benefit
MODIFY COLUMN updated_at TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);



INSERT INTO benefit(title, description, end_date, is_active, max_participants, start_date ) values
 ('Bảo hiểm sức khỏe mở rộng', 'Cung cấp gói bảo hiểm sức khỏe cao cấp cho nhân viên và người thân.', '2025-12-31', 1, 200, '2025-06-29'),
('Khóa học nâng cao kỹ năng', 'Tài trợ 100% chi phí các khóa học trực tuyến hoặc offline để nâng cao kỹ năng mềm và chuyên môn.', '2025-09-30', 1, 150, '2025-06-30'),
('Du lịch công ty 2025', 'Chuyến du lịch thường niên cùng công ty đến Đà Nẵng trong 3 ngày 2 đêm.', '2025-08-15', 0, 100, '2025-06-28'),
('Gói hỗ trợ sức khỏe tinh thần', 'Miễn phí 5 buổi tư vấn tâm lý cùng chuyên gia.', '2026-01-15', 1, 300, '2025-07-01'),
('Phụ cấp thể thao', 'Hỗ trợ chi phí tham gia phòng gym, yoga, hoặc các hoạt động thể thao.', '2025-11-01', 1, 5, '2025-06-28');

INSERT INTO holidays (start_date, end_date, name, is_recurring,is_deleted) VALUES
('2025-01-01', '2025-01-01', 'Tết dương lịch', true,false),
('2025-04-30', '2025-04-30', 'Giải phóng miền Nam', true,false),
('2025-05-01', '2025-05-01', 'Quốc tế Lao động', true,false),
('2025-09-02', '2025-09-02', 'Quốc khánh', true,false);

