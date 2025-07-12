import MainLayout from "../components/MainLayout";
import RecruitmentChart from "../components/charts/Recruitment";
import { getRecruitmentGraphChart } from "../services/dashboardService";
import React, { useState, useEffect } from "react";

function Dashboard() {
  const [data, setData] = useState([]);

  useEffect(() => {
    getRecruitmentGraphChart()
      .then(response => {
        setData(response.data);
      })
      .catch(error => {
        console.error('Lỗi lấy dữ liệu tuyển dụng:', error);
      });
  }, []);

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Thống kê tuyển dụng</h1>
          <div className="page-actions">
            <RecruitmentChart data={data} />
          </div>
        </div>
      </div>
    </MainLayout>
  );
}

export default Dashboard;
