USE hrms;
SET SQL_SAFE_UPDATES = 0;
-- Tạm xóa liên kết leader và line
UPDATE employee SET line_id = NULL;
UPDATE `lines` SET leader_id = NULL;


-- Xóa dữ liệu cũ theo thứ tự phụ thuộc khóa ngoại (tuyển dụng + hệ thống)
DELETE FROM interview_schedule;
DELETE FROM candidate;
DELETE FROM recruitment;
-- Xóa dữ liệu cũ theo thứ tự phụ thuộc khóa ngoại
DELETE FROM account_role;
DELETE FROM `lines`;
DELETE FROM account;
DELETE FROM employee;
DELETE FROM department;
DELETE FROM role;


-- Roles
INSERT INTO role (role_id, role_name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_HR'),
(3, 'ROLE_LINE_LEADER'),
(4, 'ROLE_PRODUCTION_MANAGER'),
(5, 'ROLE_CANTEEN'),
(6, 'ROLE_EMPLOYEE'),
(7, 'ROLE_PMC');

-- Department
INSERT INTO department (department_id, department_name) VALUES
(1, 'Sản xuất'),
(2, 'QC'),
(3, 'IQC'),
(4, 'Vật tư'),
(5, 'Bán Tự động'),
(6, 'Tự Động'),
(7, 'Lái Xe');

-- Employees
INSERT INTO employee (
    employee_id, employee_code, employee_name, gender, dob,
    place_of_birth, image, nationality, address, start_work_at,
    phone_number, citizen_id, department_id
) VALUES
(1, 'EMP001', 'Test User A', 'MALE', '1991-01-15', NULL, NULL, NULL, NULL, '2016-01-01', '0900000001', NULL, 1),
(2, 'EMP002', 'Test User B', 'FEMALE', '1992-02-15', NULL, NULL, NULL, NULL, '2017-01-01', '0900000002', NULL, 1),
(3, 'EMP003', 'Test User C', 'MALE', '1993-03-15', NULL, NULL, NULL, NULL, '2018-01-01', '0900000003', NULL, 1),
(4, 'EMP004', 'Test User D', 'FEMALE', '1994-04-15', NULL, NULL, NULL, NULL, '2019-01-01', '0900000004', NULL, 1),
(5, 'EMP005', 'Test User E', 'MALE', '1995-05-15', NULL, NULL, NULL, NULL, '2020-01-01', '0900000005', NULL, 1),
(6, 'EMP006', 'Test User F', 'FEMALE', '1996-06-15', NULL, NULL, NULL, NULL, '2021-01-01', '0900000006', NULL, 2),
(7, 'EMP007', 'Test User G', 'MALE', '1997-07-15', NULL, NULL, NULL, NULL, '2022-01-01', '0900000007', NULL, 2),
(8, 'EMP008', 'Test User H', 'FEMALE', '1998-08-15', NULL, NULL, NULL, NULL, '2023-01-01', '0900000008', NULL, 2),
(9, 'EMP009', 'Test User I', 'MALE', '1999-09-15', NULL, NULL, NULL, NULL, '2024-01-01', '0900000009', NULL, 2),
(10, 'EMP010', 'Test User J', 'FEMALE', '1990-10-15', NULL, NULL, NULL, NULL, '2025-01-01', '0900000010', NULL, 2);


-- Accounts
INSERT INTO account (account_id, username, password_hash, email, is_active, created_at, updated_at, last_login_at, login_attempts, must_change_password, employee_id) VALUES
(1, 'user1', '$2a$10$uGd7nKSUHtRz6mQ1n/JTgOd9MKyU4H8v1Q.g4BR0T1U7FXxMDfZzS', 'user1@example.com', true, NOW(), NOW(), NULL, 0, false, 1),
(2, 'user2', '$2a$10$uGd7nKSUHtRz6mQ1n/JTgOd9MKyU4H8v1Q.g4BR0T1U7FXxMDfZzS', 'user2@example.com', true, NOW(), NOW(), NULL, 0, false, 2),
(3, 'user3', '$2a$10$uGd7nKSUHtRz6mQ1n/JTgOd9MKyU4H8v1Q.g4BR0T1U7FXxMDfZzS', 'user3@example.com', true, NOW(), NOW(), NULL, 0, false, 3),
(4, 'user4', '$2a$10$uGd7nKSUHtRz6mQ1n/JTgOd9MKyU4H8v1Q.g4BR0T1U7FXxMDfZzS', 'user4@example.com', true, NOW(), NOW(), NULL, 0, false, 4),
(5, 'user5', '$2a$10$uGd7nKSUHtRz6mQ1n/JTgOd9MKyU4H8v1Q.g4BR0T1U7FXxMDfZzS', 'user5@example.com', true, NOW(), NOW(), NULL, 0, false, 5),
(6, 'user6', '$2a$10$uGd7nKSUHtRz6mQ1n/JTgOd9MKyU4H8v1Q.g4BR0T1U7FXxMDfZzS', 'user6@example.com', true, NOW(), NOW(), NULL, 0, false, 6),
(7, 'user7', '$2a$10$uGd7nKSUHtRz6mQ1n/JTgOd9MKyU4H8v1Q.g4BR0T1U7FXxMDfZzS', 'user7@example.com', true, NOW(), NOW(), NULL, 0, false, 7),
(8, 'user8', '$2a$10$uGd7nKSUHtRz6mQ1n/JTgOd9MKyU4H8v1Q.g4BR0T1U7FXxMDfZzS', 'user8@example.com', true, NOW(), NOW(), NULL, 0, false, 8),
(9, 'user9', '$2a$10$uGd7nKSUHtRz6mQ1n/JTgOd9MKyU4H8v1Q.g4BR0T1U7FXxMDfZzS', 'user9@example.com', true, NOW(), NOW(), NULL, 0, false, 9),
(10, 'user10', '$2a$10$uGd7nKSUHtRz6mQ1n/JTgOd9MKyU4H8v1Q.g4BR0T1U7FXxMDfZzS', 'user10@example.com', true, NOW(), NOW(), NULL, 0, false, 10);

-- Account_Role Mappings
INSERT INTO account_role (account_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 1),
(7, 2),
(8, 2),
(9, 3),
(10, 7);

-- Lines (with leader_id)
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

-- Gán line_id cho employee
UPDATE employee SET line_id = 1 WHERE employee_id = 1;
UPDATE employee SET line_id = 2 WHERE employee_id = 2;
UPDATE employee SET line_id = 3 WHERE employee_id = 3;
UPDATE employee SET line_id = 4 WHERE employee_id = 4;
UPDATE employee SET line_id = 5 WHERE employee_id = 5;
UPDATE employee SET line_id = 6 WHERE employee_id = 6;
UPDATE employee SET line_id = 7 WHERE employee_id = 7;
UPDATE employee SET line_id = 8 WHERE employee_id = 8;
UPDATE employee SET line_id = 9 WHERE employee_id = 9;
UPDATE employee SET line_id = 1 WHERE employee_id = 10;

-- Recruitment data
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

-- Candidate data
INSERT INTO candidate (
    candidate_id, candidate_name, email, phone_number,
    note, status, submitted_at, recruitment_id
) VALUES
(1, 'Nguyễn Văn A', 'a@gmail.com', '0901234567', 'Ứng viên tiềm năng', 'APPLIED', '2025-06-01 10:00:00', 1),
(2, 'Trần Thị B', 'b@gmail.com', '0909876543', 'Đã có kinh nghiệm', 'INTERVIEWED', '2025-06-03 15:00:00', 1),
(3, 'Lê Văn C', 'c@gmail.com', '0912345678', 'Ứng viên trẻ, cần đào tạo', 'APPLIED', '2025-06-05 09:00:00', 2);

-- Interview Schedule data
INSERT INTO interview_schedule (
    interview_schedule_id, scheduled_at, status, feedback,
    candidate_id, interviewer_id
) VALUES
(1, '2025-06-10 09:00:00', 'COMPLETED', 'Ứng viên phù hợp vị trí QC.', 1, 4),
(2, '2025-06-12 10:30:00', 'SCHEDULED', NULL, 2, 3),
(3, '2025-06-14 14:00:00', 'COMPLETED', 'Ứng viên chưa đủ kỹ năng kỹ thuật.', 3, 6);

