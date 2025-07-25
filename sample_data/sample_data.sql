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
DELETE FROM role;


-- Reset AUTO_INCREMENT cho các bảng
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
(5, 'ROLE_CANTEEN'),
(6, 'ROLE_EMPLOYEE'),
(7, 'ROLE_PMC');



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
(18, 'PMC', 'Nhân viên phòng kế hoạch sản xuất & vật tư');


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
(9, 18); 


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


INSERT INTO account (account_id, username, password_hash, email, is_active, created_at, updated_at, last_login_at, login_attempts, must_change_password, employee_id, role_id) VALUES
(1, 'user1', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user1@example.com', true, NOW(), NOW(), NULL, 5, false, 1, 1),
(2, 'user2', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user2@example.com', true, NOW(), NOW(), NULL, 5, false, 2, 2),
(3, 'user3', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user3@example.com', true, NOW(), NOW(), NULL, 5, false, 3, 6),
(4, 'user4', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user4@example.com', true, NOW(), NOW(), NULL, 5, false, 4, 3),
(5, 'user5', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user5@example.com', true, NOW(), NOW(), NULL, 5, false, 5, 5),
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

