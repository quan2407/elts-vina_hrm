USE hrms;
SET SQL_SAFE_UPDATES = 0;


UPDATE employee SET line_id = NULL;
UPDATE `lines` SET leader_id = NULL;
ALTER TABLE employee MODIFY COLUMN is_deleted TINYINT(1) NOT NULL DEFAULT 0;
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
(2, 'IQC'),
(4, 'QC'),
(5, 'Sản Xuất'),
(6, 'Tự Động'),
(7, 'Vật Tư'),
(8, 'Nhân Sự'),
(9, 'PMC');


INSERT INTO position (position_id, position_name, description) VALUES
(1, 'Công Nhân', NULL),
(2, 'Công Nhân Kiểm Tra', NULL),
(3, 'Data', NULL),
(6, 'Nhân Viên Vệ Sinh', NULL),
(7, 'Nhân Viên QC', NULL),
(8, 'Nhân Viên Sản Xuất', NULL),
(10, 'Quản Lý', NULL),
(11, 'Quản Lý Kỹ Thuật', NULL),
(12, 'Quản Lý Vật Tư', NULL),
(15, 'Tổ Trưởng', NULL),
(17, 'HR', 'Nhân viên nhân sự'),
(18, 'PMC', 'Nhân viên phòng kế hoạch sản xuất & vật tư');


INSERT INTO department_position (department_id, position_id) VALUES
(1, 1),  
(1, 15), 
(5,15),
(2, 2),  

(4, 1),  
(4, 2),  
(4, 3), 
(4, 15),
(4, 7), 

(5, 10), 
(5, 11), 
(5, 6), 
(5, 8),  

(6, 1),  
(6, 15), 

(7, 1), 
(7, 12), 
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
(1, 'ELTSSX0001', 'Test User A', 'NAM', '1991-01-15',
 NULL, NULL, 'Vietnam', '0123456781',
 '2010-01-01', '2030-01-01', 'Hà Nội',
 NULL, NULL, '2016-01-01', '0900000001', 'usera@example.com',
 1, 1,
 4620000, 100000, 200000, 500000, 40000),

(2, 'ELTSSX0002', 'Test User B', 'NỮ', '1992-02-15',
 NULL, NULL, 'Vietnam', '0123456782',
 '2011-01-01', '2031-01-01', 'Hồ Chí Minh',
 NULL, NULL, '2017-01-01', '0900000002', 'userb@example.com',
 2, 2,
 4800000, 90000, 180000, 450000, 35000),

(3, 'ELTSSX0003', 'Test User C', 'NAM', '1993-03-15',
 NULL, NULL, 'Vietnam', '0123456783',
 '2012-01-01', '2032-01-01', 'Đà Nẵng',
 NULL, NULL, '2018-01-01', '0900000003', 'userc@example.com',
 4, 3,
 5000000, 95000, 220000, 470000, 30000),

(4, 'ELTSSX0004', 'Test User D', 'NỮ', '1994-04-15',
 NULL, NULL, 'Vietnam', '0123456784',
 '2013-01-01', '2033-01-01', 'Hải Phòng',
 NULL, NULL, '2019-01-01', '0900000004', 'userd@example.com',
 5, 15,
 4750000, 98000, 210000, 490000, 37000),

(5, 'ELTSSX0005', 'Test User E', 'NAM', '1995-05-15',
 NULL, NULL, 'Vietnam', '0123456785',
 '2014-01-01', '2034-01-01', 'Cần Thơ',
 NULL, NULL, '2020-01-01', '0900000005', 'usere@example.com',
 1, 1,
 4900000, 92000, 205000, 460000, 36000),

(6, 'ELTSSX0006', 'Test User F', 'NỮ', '1996-06-15',
 NULL, NULL, 'Vietnam', '0123456786',
 '2015-01-01', '2035-01-01', 'Huế',
 NULL, NULL, '2021-01-01', '0900000006', 'userf@example.com',
 5, 6,
 4700000, 87000, 200000, 430000, 34000),

(7, 'ELTSSX0007', 'Test User G', 'NAM', '1997-07-15',
 NULL, NULL, 'Vietnam', '0123456787',
 '2016-01-01', '2036-01-01', 'Quảng Ninh',
 NULL, NULL, '2022-01-01', '0900000007', 'userg@example.com',
 4, 7,
 4850000, 99000, 215000, 510000, 39000),

(8, 'ELTSSX0008', 'Test User H', 'NỮ', '1998-08-15',
 NULL, NULL, 'Vietnam', '0123456788',
 '2017-01-01', '2037-01-01', 'Nghệ An',
 NULL, NULL, '2023-01-01', '0900000008', 'userh@example.com',
 5, 8,
 4950000, 93000, 198000, 440000, 32000),

(9, 'ELTSSX0009', 'Test User I', 'NAM', '1999-09-15',
 NULL, NULL, 'Vietnam', '0123456789',
 '2018-01-01', '2038-01-01', 'Thanh Hóa',
 NULL, NULL, '2024-01-01', '0900000009', 'useri@example.com',
 5, 1,
 5050000, 94000, 208000, 450000, 33000),

(10, 'ELTSSX0010', 'Test User J', 'NỮ', '1990-10-15',
 NULL, NULL, 'Vietnam', '0123456790',
 '2009-01-01', '2029-01-01', 'Bắc Ninh',
 NULL, NULL, '2025-01-01', '0900000010', 'userj@example.com',
 5, 10,
 5100000, 96000, 212000, 480000, 38000);


INSERT INTO account (account_id, username, password_hash, email, is_active, created_at, updated_at, last_login_at, login_attempts, must_change_password, employee_id, role_id) VALUES
(1, 'user1', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user1@example.com', true, NOW(), NOW(), NULL, 5, false, 1, 1),
(2, 'user2', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user2@example.com', true, NOW(), NOW(), NULL, 5, false, 2, 2),
(3, 'user3', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user3@example.com', true, NOW(), NOW(), NULL, 5, false, 3, 3),
(4, 'user4', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user4@example.com', true, NOW(), NOW(), NULL, 5, false, 4, 4),
(5, 'user5', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user5@example.com', true, NOW(), NOW(), NULL, 5, false, 5, 5),
(6, 'user6', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user6@example.com', true, NOW(), NOW(), NULL, 5, false, 6, 6),
(7, 'user7', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user7@example.com', true, NOW(), NOW(), NULL, 5, false, 7, 1),
(8, 'user8', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user8@example.com', true, NOW(), NOW(), NULL, 5, false, 8, 2),
(9, 'user9', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user9@example.com', true, NOW(), NOW(), NULL, 5, false, 9, 3),
(10, 'user10', '$2a$10$GjpaNl5KbwTEY.nbDrX20O4ZZbgdaGxIzeqScMdB1gsnDLillFIJy', 'user10@example.com', true, NOW(), NOW(), NULL, 5, false, 10, 7);





INSERT INTO `lines` (line_id, line_name, department_id) VALUES
(1, 'Line 1', 5 ),  
(2, 'Line 2', 5),   
(4, 'Line 4', 5),  
(6, 'Line 6', 5),  
(7, 'Line 7', 5),   
(8, 'Line 8', 5),   
(9, 'Line 9', 5);   




UPDATE employee SET line_id = 4 WHERE employee_id = 6; 
UPDATE employee SET line_id = 4 WHERE employee_id = 8;  
UPDATE employee SET line_id = 4 WHERE employee_id = 9; 
UPDATE employee SET line_id = 4 WHERE employee_id = 10; 


INSERT INTO recruitment (
    recruitment_id, title, employment_type, job_description,
    job_requirement, benefits, min_salary, max_salary, quantity,
    expired_at, create_at, update_at, status,
    department_id, created_by
) VALUES
(1, 'Tuyển công nhân QC', 'Full-time', 'Kiểm tra chất lượng sản phẩm.',
 'Tốt nghiệp THPT, chịu khó.', 'Phụ cấp ăn trưa, thưởng lễ.', 8000000, 10000000, 5,
 '2025-07-15 00:00:00', NOW(), NOW(), 'OPEN', 2, 2),
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

INSERT INTO benefit(title, description, end_date, is_active, max_participants, start_date, benefit_type, detail, default_formula_type, default_formula_value ) values
 ('Bảo hiểm sức khỏe mở rộng', 'Cung cấp gói bảo hiểm sức khỏe cao cấp cho nhân viên và người thân.', '2025-12-31', 1, 200, '2025-06-29', 'PHU_CAP', 'Gói bảo hiểm mở rộng áp dụng cho tất cả nhân viên chính thức và người thân trực hệ.', "PERCENTAGE", 50),
('Khóa học nâng cao kỹ năng', 'Tài trợ 100% chi phí các khóa học trực tuyến hoặc offline để nâng cao kỹ năng mềm và chuyên môn.', '2025-09-30', 1, 150, '2025-06-30','PHU_CAP', 'Nhân viên đăng ký trên hệ thống nội bộ và được phê duyệt trước khi tham gia.', "AMOUNT", 20000),
('Du lịch công ty 2025', 'Chuyến du lịch thường niên cùng công ty đến Đà Nẵng trong 3 ngày 2 đêm.', '2025-08-15', 0, 100, '2025-06-28','SU_KIEN','Chương trình chỉ áp dụng cho nhân viên đạt đủ điều kiện làm việc từ 6 tháng trở lên.', "PERCENTAGE", 50000),
('Gói hỗ trợ sức khỏe tinh thần', 'Miễn phí 5 buổi tư vấn tâm lý cùng chuyên gia.', '2026-01-15', 1, 300, '2025-07-01','PHU_CAP','Đăng ký qua phòng nhân sự, ưu tiên nhân viên làm việc trên 1 năm.',  "AMOUNT",20000),
('Phụ cấp thể thao', 'Hỗ trợ chi phí tham gia phòng gym, yoga, hoặc các hoạt động thể thao.', '2025-11-01', 1, 5, '2025-06-28','KHAU_TRU','Hỗ trợ 50% chi phí hàng tháng, tối đa 500.000đ/người.',"AMOUNT",500000);

INSERT INTO benefit_position (benefit_id, position_id, formula_value, formula_type)
VALUES
(1, 3, 500000, 'AMOUNT'),
(1, 6, 10.0, 'PERCENTAGE');

INSERT INTO benefit_registrations (is_register, registered_at, benefit_position_id, employee_id)
VALUES
(true,  NOW(), 1, 1),
(true,  NOW(), 1, 2)




