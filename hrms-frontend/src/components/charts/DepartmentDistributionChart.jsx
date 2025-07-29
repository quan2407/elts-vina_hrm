import React from "react";
import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from "chart.js";

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
);

function DepartmentDistributionChart({ data }) {
  if (!Array.isArray(data) || data.length === 0) {
    return (
      <p style={{ textAlign: "center" }}>
        Không có dữ liệu để hiển thị biểu đồ.
      </p>
    );
  }

  const chartData = {
    labels: data.map((item) => item.departmentName),
    datasets: [
      {
        label: "Số lượng nhân viên theo phòng ban",
        data: data.map((item) => item.count),
        backgroundColor: "#4e73df", // Màu của biểu đồ
        barThickness: 38, // Độ dày cột
      },
    ],
  };

  const options = {
    maintainAspectRatio: false,
    responsive: true,
    scales: {
      x: {
        categoryPercentage: 1.0,
        barPercentage: 1.0,
        ticks: {
          color: "#333",
          font: {
            size: 16,
            weight: "500",
            family: "'Product Sans', sans-serif",
          },
        },
      },
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 1,
          color: "#333",
          font: {
            size: 16,
            weight: "500",
            family: "'Product Sans', sans-serif",
          },
        },
      },
    },
    plugins: {
      legend: {
        labels: {
          color: "#000",
          font: {
            size: 16,
            weight: "600",
            family: "'Product Sans', sans-serif",
          },
        },
      },
      tooltip: {
        titleFont: {
          size: 16,
          weight: "700",
          family: "'Product Sans', sans-serif",
        },
        bodyFont: {
          size: 16,
          weight: "400",
          family: "'Product Sans', sans-serif",
        },
        bodyColor: "#000",
        titleColor: "#000",
        backgroundColor: "#fff",
        borderColor: "#ccc",
        borderWidth: 1,
      },
    },
  };

  return (
    <div
      className="chart-container"
      style={{ height: "400px" }}
    >
      <Bar
        data={chartData}
        options={options}
      />
    </div>
  );
}

export default DepartmentDistributionChart;
