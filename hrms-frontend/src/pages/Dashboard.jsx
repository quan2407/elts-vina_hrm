import MainLayout from "../components/MainLayout";
import RecruitmentChart from "../components/charts/Recruitment";
import { getRecruitmentGraphChart } from "../services/dashboardService";
import React, { useState, useEffect } from "react";

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
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Thống kê tuyển dụng</h1>
        </div>
        <div className="page-actions">
          {isLoading ? (
            <p style={{ textAlign: 'center' }}>Đang tải dữ liệu...</p>
          ) : (
            <RecruitmentChart data={data} />
          )}
        </div>
      </div>
    </MainLayout>
  );
}


export default Dashboard;
