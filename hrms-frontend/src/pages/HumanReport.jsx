// import React, { useEffect, useState } from "react";
// import { getAllDepartments } from "../services/departmentService";
import MainLayout from "../components/MainLayout";
import "../styles/AttendanceMonthlyView.css";

const HumanReport = () => {
//     const [line, setLine] = useState([]);
//     const [department, setDepartment] = useState("");   

// useEffect(() => {
//         const fetchCandidates = async () => {
//             try {
//                 const data = await getAllDepartments();
//                 setDepartment(data);
//             } catch (error) {
//                 console.error("Lỗi khi load dep:", error);
//             }
//         };
//         fetchCandidates();
//     }, []);

 
  return (
    <MainLayout>
      <div className="attendance-container">
        <div className="attendance-controls">
          

        </div>

        <div className="attendance-table-wrapper">
          <table className="attendance-table">
            <thead>
              <tr>
                <th rowSpan="2">STT</th>
                <th rowSpan="2">Mã NV</th>
                <th rowSpan="2">Họ và Tên</th>
                <th rowSpan="2">Chức vụ</th>
                <th rowSpan="2">Lương cơ bản</th>
                <th colSpan="4">Phụ cấp</th>
                <th colSpan="2">Lương sản xuất</th>
                <th colSpan="2">Lương thêm giờ</th>
                <th colSpan="4">Các khoản khấu trừ</th>
                <th rowSpan="2">Tổng trừ</th>
                <th rowSpan="2">Thực lãnh</th>
              </tr>
              <tr>
                <th>Điện thoại</th>
                <th>Nhà ở</th>
                <th>Chuyên cần</th>
                <th>Đi lại</th>
                <th>Số công</th>
                <th>Tiền lương</th>
                <th>Số giờ</th>
                <th>Tiền lương</th>
                <th>BHXH</th>
                <th>BHYT</th>
                <th>BHTN</th>
                <th>Đoàn phí</th>
              </tr>
            </thead>
            <tbody>
              
            </tbody>
          </table>
        </div>
      </div>
    </MainLayout>
  );
};

export default HumanReport;
