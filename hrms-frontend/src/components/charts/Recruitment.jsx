import React from 'react';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const RecruitmentChart = ({ data = [] }) => {
  if (!Array.isArray(data) || data.length === 0) {
    return <p style={{ textAlign: "center" }}>Không có dữ liệu để hiển thị biểu đồ.</p>;
  }

  const chartData = {
    labels: data.map(item => item.recruitmentTitle),
    datasets: [
      {
        label: 'Cần tuyển',
        data: data.map(item => item.canTuyen ?? 0),
        backgroundColor: 'rgba(75, 192, 192, 0.6)',
      },
      {
        label: 'Ứng tuyển',
        data: data.map(item => item.ungTuyen ?? 0),
        backgroundColor: 'rgba(153, 102, 255, 0.6)',
      },
      {
        label: 'Đã tuyển',
        data: data.map(item => item.daTuyen ?? 0),
        backgroundColor: 'rgba(255, 159, 64, 0.6)',
      }
    ]
  };

  const options = {
    maintainAspectRatio: false,
    responsive: true,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 1
        }
      }
    }
  };

  return (
    <div className="recruitment-chart" style={{ height: "400px" }}>
      <Bar data={chartData} options={options} />
    </div>
  );
};

export default RecruitmentChart;