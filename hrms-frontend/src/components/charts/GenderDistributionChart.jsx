import React from "react";
import { Pie } from "react-chartjs-2";
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement } from "chart.js";

ChartJS.register(Title, Tooltip, Legend, ArcElement);

function GenderDistributionChart({ data }) {
  if (!Array.isArray(data) || data.length === 0) {
    return (
      <p style={{ textAlign: "center" }}>
        Không có dữ liệu để hiển thị biểu đồ.
      </p>
    );
  }

  const chartData = {
    labels: data.map((item) => item.gender),
    datasets: [
      {
        label: "Số lượng nhân viên theo giới tính",
        data: data.map((item) => item.count),
        backgroundColor: ["#FF5733", "#33FF57"],
      },
    ],
  };

  const options = {
    maintainAspectRatio: false,
    responsive: true,
    plugins: {
      legend: {
        position: "top",
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
      <Pie
        data={chartData}
        options={options}
      />
    </div>
  );
}

export default GenderDistributionChart;
