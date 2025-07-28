import MainLayout from "../components/MainLayout";
import RecruitmentChart from "../components/charts/Recruitment";
// import RecruitmentDashboardTable from "../components/charts/RecruitmentTable";
import {
  getRecruitmentGraphChart,
  getEmployeeGenderDistribution, // Đảm bảo hàm này được import
  getEmployeeDepartmentDistribution,
} from "../services/dashboardService";

import GenderDistributionChart from "../components/charts/GenderDistributionChart";
import DepartmentDistributionChart from "../components/charts/DepartmentDistributionChart";
import "../styles/Dashboard.css";

import React, { useState, useEffect, useRef } from "react";
import "react-datepicker/dist/react-datepicker.css";
import "../styles/EmployeeDetails.css";

function Dashboard() {
  const [activeSection, setActiveSection] = useState("basic-info");
  const [selectedFromDate, setSelectedFromDate] = useState(() => {
    const today = new Date();
    const lastWeek = new Date();
    lastWeek.setDate(today.getDate() - 7);
    return lastWeek.toISOString().split("T")[0]; // format YYYY-MM-DD
  });

  const [ToDate, setToDate] = useState(() => {
    const today = new Date();
    return today.toISOString().split("T")[0]; // format YYYY-MM-DD
  });

  const [data, setData] = useState([]);
  const [genderData, setGenderData] = useState([]);
  const [departmentData, setDepartmentData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // Fetch data for recruitment, gender and department distributions
  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch recruitment data
        const recruitmentResponse = await getRecruitmentGraphChart(
          selectedFromDate,
          ToDate
        );
        console.log("Recruitment Data:", recruitmentResponse); // Kiểm tra dữ liệu tuyển dụng
        setData(recruitmentResponse);

        // Fetch gender distribution
        const genderResponse = await getEmployeeGenderDistribution(
          selectedFromDate,
          ToDate
        );
        console.log("Gender Data:", genderResponse); // Kiểm tra dữ liệu giới tính
        setGenderData(genderResponse);

        // Fetch department distribution
        const departmentResponse = await getEmployeeDepartmentDistribution(
          selectedFromDate,
          ToDate
        );
        console.log("Department Data:", departmentResponse); // Kiểm tra dữ liệu phòng ban
        setDepartmentData(departmentResponse);
      } catch (error) {
        console.error("Lỗi khi lấy dữ liệu:", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [selectedFromDate, ToDate]);

  useEffect(() => {
    const contentEl = document.querySelector(".employeedetail-form-content");
    const handleScroll = () => {
      if (isClickScrolling.current) return;
      const sections = ["basic-info", "contact-info", "job-info"];
      for (let id of sections) {
        const el = document.getElementById(id);
        if (el) {
          const rect = el.getBoundingClientRect();
          if (rect.top >= 150 && rect.top < window.innerHeight / 2) {
            setActiveSection(id);
            break;
          }
        }
      }
    };
    if (contentEl) {
      contentEl.addEventListener("scroll", handleScroll);
      return () => contentEl.removeEventListener("scroll", handleScroll);
    }
  }, []);

  const isClickScrolling = useRef(false);

  const scrollToSection = (id) => {
    const el = document.getElementById(id);
    if (el) {
      isClickScrolling.current = true;
      el.scrollIntoView({ behavior: "smooth", block: "start" });
      setActiveSection(id);
      setTimeout(() => {
        isClickScrolling.current = false;
      }, 500);
    }
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Tổng quan</h1>
        </div>

        <div className="date-range-container">
          <label className="date-label">Từ:</label>
          <input
            type="date"
            value={selectedFromDate}
            name="selectedFromDate"
            onChange={(e) => setSelectedFromDate(e.target.value)}
            style={{ width: "240px" }}
            className="form-control"
          />
          <label className="date-label">đến:</label>
          <input
            type="date"
            value={ToDate}
            name="toDate"
            onChange={(e) => setToDate(e.target.value)}
            style={{ width: "240px" }}
            className="form-control"
          />
        </div>

        <div className="employeedetail-form-container">
          <div className="employeedetail-form-navigation">
            <div
              className={`employeedetail-nav-item ${
                activeSection === "basic-info" ? "employeedetail-active" : ""
              }`}
              onClick={() => scrollToSection("basic-info")}
            >
              Thống kê tuyển dụng
            </div>
            <div
              className={`employeedetail-nav-item ${
                activeSection === "contact-info" ? "employeedetail-active" : ""
              }`}
              onClick={() => scrollToSection("contact-info")}
            >
              Phân bổ theo phòng ban
            </div>
            <div
              className={`employeedetail-nav-item ${
                activeSection === "job-info" ? "employeedetail-active" : ""
              }`}
              onClick={() => scrollToSection("job-info")}
            >
              Phân bổ nhân viên theo giới tính
            </div>
          </div>

          <div className="employeedetail-form-content">
            <div
              id="basic-info"
              className="employeedetail-basic-information"
            >
              <div className="employeedetail-form-title">
                Thống kê tuyển dụng
              </div>
              <div className="dashboard-content-card">
                {isLoading ? (
                  <p style={{ textAlign: "center" }}>Đang tải dữ liệu...</p>
                ) : (
                  <>
                    <div className="dashboard-chart-wrapper">
                      <RecruitmentChart data={data} />
                    </div>
                    <div className="dashboard-table-wrapper">
                      {/* <RecruitmentDashboardTable data={data} /> */}
                    </div>
                  </>
                )}
              </div>
            </div>

            <div
              id="contact-info"
              className="employeedetail-contact-information"
            >
              <div className="employeedetail-form-title">
                Phân bổ theo phòng ban
              </div>
              <div className="dashboard-content-card">
                {isLoading ? (
                  <p style={{ textAlign: "center" }}>Đang tải dữ liệu...</p>
                ) : (
                  <>
                    <div className="dashboard-chart-wrapper">
                      <DepartmentDistributionChart data={departmentData} />
                    </div>
                  </>
                )}
              </div>
            </div>

            <div
              id="job-info"
              className="employeedetail-job-information"
            >
              <div className="employeedetail-form-title">
                Phân bổ nhân viên theo giới tính
              </div>
              {/* Add interview schedule information here */}
              <div className="dashboard-content-card">
                {isLoading ? (
                  <p style={{ textAlign: "center" }}>Đang tải dữ liệu...</p>
                ) : (
                  <>
                    <div className="dashboard-chart-wrapper">
                      <GenderDistributionChart data={genderData} />
                    </div>
                  </>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}

export default Dashboard;
