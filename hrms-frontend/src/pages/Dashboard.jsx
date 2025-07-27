import MainLayout from "../components/MainLayout";
import RecruitmentChart from "../components/charts/Recruitment";
import RecruitmentDashboardTable from "../components/charts/RecruitmentTable";
import { getRecruitmentGraphChart } from "../services/dashboardService";
import React, { useState, useEffect } from "react";
import "../styles/Dashboard.css";

function Dashboard() {
  const [data, setData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await getRecruitmentGraphChart();
        if (Array.isArray(response)) {
          setData(response);
        } else {
          console.warn("Dữ liệu không phải mảng:", response);
          setData([]);
        }
      } catch (error) {
        console.error("Lỗi khi lấy dữ liệu tuyển dụng:", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  return (
    <MainLayout>
      <div className="dashboard-container">
        <div className="dashboard-header">
          <h2 className="dashboard-title">Thống kê tuyển dụng</h2>

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
                <RecruitmentDashboardTable data={data} />
              </div>
            </>
          )}
        </div>
      </div>
    </MainLayout>
  );
}

export default Dashboard;