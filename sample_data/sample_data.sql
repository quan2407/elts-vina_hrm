USE hrms;
SET SQL_SAFE_UPDATES = 0;

-- Tạm xóa liên kết leader và line
UPDATE employee SET line_id = NULL;
UPDATE `lines` SET leader_id = NULL;

-- Xóa dữ liệu phụ thuộc trước (để tránh lỗi khóa ngoại)
DELETE FROM interview_schedule;
DELETE FROM candidate_recruitment;
DELETE FROM candidate;
DELETE FROM recruitment;

DELETE FROM `lines`;
DELETE FROM account;
DELETE FROM employee;
DELETE FROM position;
DELETE FROM department;
DELETE FROM role;

-- Reset AUTO_INCREMENT cho các bảng
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


-- ====================
-- Roles
-- ====================
INSERT INTO role (role_id, role_name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_HR'),
(3, 'ROLE_LINE_LEADER'),
(4, 'ROLE_PRODUCTION_MANAGER'),
(5, 'ROLE_CANTEEN'),
(6, 'ROLE_EMPLOYEE'),
(7, 'ROLE_PMC');

-- ====================
-- Department
-- ====================
INSERT INTO department (department_id, department_name) VALUES
(1, 'Sản xuất'),
(2, 'QC'),
(3, 'IQC'),
(4, 'Vật tư'),
(5, 'Bán Tự động'),
(6, 'Tự Động'),
(7, 'Lái Xe');
INSERT INTO position (position_name, description) VALUES
('Công Nhân', NULL),
('Công Nhân (Ype)', NULL),
('Công Nhân Kiểm Tra', NULL),
('Công Nhân Kiểm Tra (Inno)', NULL),
('Công Nhân Kiểm Tra (Ype)', NULL),
('Công Nhân Kiểm Tra(Inno)', NULL),
('Công Nhân Kiểm Tra(Ype)', NULL),
('Công Nhân( Inno)', NULL),
('Công Nhân(Inno)', NULL),
('Công Nhân(Ype)', NULL),
('Data', NULL),
('Hỗ Trợ Lái Xe', NULL),
('Lái Xe', NULL),
('Nhân Viên Miss /Vệ Sinh', NULL),
('Nhân Viên QC', NULL),
('Nhân Viên Sản Xuất', NULL),
('Nhân Viên Vệ Sinh', NULL),
('Phó Phòng Sản Xuất', NULL),
('Quản Lý', NULL),
('Quản Lý Chất Lượng', NULL),
('Quản Lý Vật Tư', NULL),
('Silling/ Quản Lý Kỹ Thuật', NULL),
('Trưởng Ca Sản Xuất', NULL),
('Tổ Phó', NULL),
('Tổ Phó(Inno)', NULL),
('Tổ Trưởng', NULL),
('Tổ Trưởng(Inno)', NULL),
('Tổng Quản Lý Cấp 2', NULL);

-- ====================
-- Employees
-- ====================
INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id
) VALUES
(1, 'EMP001', 'Test User A', 'MALE', '1991-01-15',
 NULL, NULL, 'Vietnam', '0123456781',
 '2010-01-01', '2030-01-01', 'Hà Nội',
 NULL, NULL, '2016-01-01', '0900000001', 'usera@example.com',
 1, 1);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id
) VALUES
(2, 'EMP002', 'Test User B', 'FEMALE', '1992-02-15',
 NULL, NULL, 'Vietnam', '0123456782',
 '2011-01-01', '2031-01-01', 'Hồ Chí Minh',
 NULL, NULL, '2017-01-01', '0900000002', 'userb@example.com',
 1, 2);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id
) VALUES
(3, 'EMP003', 'Test User C', 'MALE', '1993-03-15',
 NULL, NULL, 'Vietnam', '0123456783',
 '2012-01-01', '2032-01-01', 'Đà Nẵng',
 NULL, NULL, '2018-01-01', '0900000003', 'userc@example.com',
 1, 3);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id
) VALUES
(4, 'EMP004', 'Test User D', 'FEMALE', '1994-04-15',
 NULL, NULL, 'Vietnam', '0123456784',
 '2013-01-01', '2033-01-01', 'Hải Phòng',
 NULL, NULL, '2019-01-01', '0900000004', 'userd@example.com',
 1, 4);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id
) VALUES
(5, 'EMP005', 'Test User E', 'MALE', '1995-05-15',
 NULL, NULL, 'Vietnam', '0123456785',
 '2014-01-01', '2034-01-01', 'Cần Thơ',
 NULL, NULL, '2020-01-01', '0900000005', 'usere@example.com',
 1, 5);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id
) VALUES
(6, 'EMP006', 'Test User F', 'FEMALE', '1996-06-15',
 NULL, NULL, 'Vietnam', '0123456786',
 '2015-01-01', '2035-01-01', 'Huế',
 NULL, NULL, '2021-01-01', '0900000006', 'userf@example.com',
 2, 6);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id
) VALUES
(7, 'EMP007', 'Test User G', 'MALE', '1997-07-15',
 NULL, NULL, 'Vietnam', '0123456787',
 '2016-01-01', '2036-01-01', 'Quảng Ninh',
 NULL, NULL, '2022-01-01', '0900000007', 'userg@example.com',
 2, 7);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id
) VALUES
(8, 'EMP008', 'Test User H', 'FEMALE', '1998-08-15',
 NULL, NULL, 'Vietnam', '0123456788',
 '2017-01-01', '2037-01-01', 'Nghệ An',
 NULL, NULL, '2023-01-01', '0900000008', 'userh@example.com',
 2, 8);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id
) VALUES
(9, 'EMP009', 'Test User I', 'MALE', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456789',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0900000009', 'useri@example.com',
 2, 9);

INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, origin_place, nationality, citizen_id,
    citizen_issue_date, citizen_expiry_date, citizen_issue_place,
    address, image, start_work_at, phone_number, email,
    department_id, position_id
) VALUES
(10, 'EMP010', 'Test User J', 'FEMALE', '1990-10-15',
 NULL, NULL, 'Vietnam', '0123456790',
 '2009-01-01', '2029-01-01', 'Bắc Ninh',
 NULL, NULL, '2025-01-01', '0900000010', 'userj@example.com',
 2, 10);



-- ====================
-- Accounts
-- ====================
INSERT INTO account (account_id, username, password_hash, email, is_active, created_at, updated_at, last_login_at, login_attempts, must_change_password, employee_id, role_id) VALUES
(1, 'user1', '$2a$10$qCXaQtEs0v9hxXE0X2LauOEsbuTKXFFbsIlGoaolaaQo.2/fjbJRa', 'user1@example.com', true, NOW(), NOW(), NULL, 0, false, 1, 1),
(2, 'user2', '$2a$10$qCXaQtEs0v9hxXE0X2LauOEsbuTKXFFbsIlGoaolaaQo.2/fjbJRa', 'user2@example.com', true, NOW(), NOW(), NULL, 0, false, 2, 2),
(3, 'user3', '$2a$10$qCXaQtEs0v9hxXE0X2LauOEsbuTKXFFbsIlGoaolaaQo.2/fjbJRa', 'user3@example.com', true, NOW(), NOW(), NULL, 0, false, 3, 3),
(4, 'user4', '$2a$10$qCXaQtEs0v9hxXE0X2LauOEsbuTKXFFbsIlGoaolaaQo.2/fjbJRa', 'user4@example.com', true, NOW(), NOW(), NULL, 0, false, 4, 4),
(5, 'user5', '$2a$10$qCXaQtEs0v9hxXE0X2LauOEsbuTKXFFbsIlGoaolaaQo.2/fjbJRa', 'user5@example.com', true, NOW(), NOW(), NULL, 0, false, 5, 5),
(6, 'user6', '$2a$10$qCXaQtEs0v9hxXE0X2LauOEsbuTKXFFbsIlGoaolaaQo.2/fjbJRa', 'user6@example.com', true, NOW(), NOW(), NULL, 0, false, 6, 6),
(7, 'user7', '$2a$10$qCXaQtEs0v9hxXE0X2LauOEsbuTKXFFbsIlGoaolaaQo.2/fjbJRa', 'user7@example.com', true, NOW(), NOW(), NULL, 0, false, 7, 1),
(8, 'user8', '$2a$10$qCXaQtEs0v9hxXE0X2LauOEsbuTKXFFbsIlGoaolaaQo.2/fjbJRa', 'user8@example.com', true, NOW(), NOW(), NULL, 0, false, 8, 2),
(9, 'user9', '$2a$10$qCXaQtEs0v9hxXE0X2LauOEsbuTKXFFbsIlGoaolaaQo.2/fjbJRa', 'user9@example.com', true, NOW(), NOW(), NULL, 0, false, 9, 3),
(10, 'user10', '$2a$10$qCXaQtEs0v9hxXE0X2LauOEsbuTKXFFbsIlGoaolaaQo.2/fjbJRa', 'user10@example.com', true, NOW(), NOW(), NULL, 0, false, 10, 7);



-- ====================
-- Lines
-- ====================
INSERT INTO `lines` (line_id, line_name, department_id, leader_id) VALUES
(1, 'Line 1', 1, 10),
(2, 'Line 2', 7, 1),
(3, 'Line 3', 3, 4),
(4, 'Line 4', 5, 3),
(5, 'Line 5', 5, 2),
(6, 'Line 6', 4, 6),
(7, 'Line 7', 1, 9),
(8, 'Line 8', 6, 7),
(9, 'Line 9', 6, 8);

-- ====================
-- Recruitment
-- ====================
INSERT INTO recruitment (
    recruitment_id, title, work_location, employment_type, job_description,
    job_requirement, benefits, salary_range, quantity,
    expired_at, create_at, update_at, status,
    department_id, created_by
) VALUES
(1, 'Tuyển công nhân QC', 'Bắc Ninh', 'Full-time', 'Kiểm tra chất lượng sản phẩm.',
 'Tốt nghiệp THPT, chịu khó.', 'Phụ cấp ăn trưa, thưởng lễ.', '8-10 triệu', 5,
 '2025-07-15 00:00:00', NOW(), NOW(), 'OPEN', 2, 2),
(2, 'Tuyển kỹ sư bảo trì', 'Hà Nội', 'Full-time', 'Bảo trì dây chuyền bán tự động.',
 'CĐ/ĐH chuyên ngành cơ khí', 'Bảo hiểm full, đào tạo nội bộ.', '12-15 triệu', 2,
 '2025-07-01 00:00:00', NOW(), NOW(), 'OPEN', 4, 5);

-- ====================
-- Candidate
-- ====================
INSERT INTO candidate (
    candidate_id, candidate_name, gender, dob, email, phone_number
) VALUES
(1, 'Nguyễn Văn A', 'Nam', '1990-01-15', 'a@gmail.com', '0901234567'),
(2, 'Trần Thị B', 'Nữ', '1992-05-20', 'b@gmail.com', '0909876543'),
(3, 'Lê Văn C', 'Nam', '1988-11-30', 'c@gmail.com', '0912345678');


-- ====================
-- Recruitment-Candidate mapping
-- ====================
INSERT INTO candidate_recruitment (
    candidate_id, recruitment_id, status, submitted_at
) VALUES
(1, 1, 'APPLIED', '2025-06-01 10:00:00'),
(2, 1, 'INTERVIEWED', '2025-06-03 15:00:00'),
(2, 2, 'INTERVIEWED', '2025-06-03 15:00:00'),
(3, 2, 'APPLIED', '2025-06-05 09:00:00');


-- ====================
-- Interview Schedule
-- ====================
INSERT INTO interview_schedule (
    interview_schedule_id, scheduled_at, status, feedback,
    candidate_id, interviewer_id, recruitment_id
) VALUES
(1, '2025-06-10 09:00:00', 'COMPLETED', 'Ứng viên phù hợp vị trí QC.', 1, 4, 1),
(2, '2025-06-12 10:30:00', 'SCHEDULED', NULL, 2, 3, 1),
(3, '2025-06-14 14:00:00', 'COMPLETED', 'Ứng viên chưa đủ kỹ năng kỹ thuật.', 3, 6, 2);
