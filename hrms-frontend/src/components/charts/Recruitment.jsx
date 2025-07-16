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
        backgroundColor: 'rgba(255, 0, 0, 0.6)',
        barThickness: 'flex',       
        maxBarThickness: 50,          

      },
      {
        label: 'Ứng tuyển',
        data: data.map(item => item.ungTuyen ?? 0),
        backgroundColor: 'rgba(12, 55, 248, 0.6)',
        barThickness: 'flex',       
        maxBarThickness: 50,          

      },
      {
        label: 'Đã tuyển',
        data: data.map(item => item.daTuyen ?? 0),
        backgroundColor: 'rgba(9, 252, 49, 0.6)',
        barThickness: 'flex',        
        maxBarThickness: 50,          

      }
    ]
  };

  const options = {
    maintainAspectRatio: false,
    responsive: true,
    scales: {
      x: {
        ticks: {
          color: '#333', // màu chữ trục X
          font: {
            size: 16,
            weight: '500',
            family: "'Product Sans', sans-serif"
          }
        }
      },
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 1,
          color: '#333', // màu chữ trục Y
          font: {
            size: 16,
            weight: '500',
            family: "'Product Sans', sans-serif"
          }
        }
      }
    },
    plugins: {
      legend: {
        labels: {
          color: '#000', // màu chữ legend
          font: {
            size: 16,
            weight: '600',
            family: "'Product Sans', sans-serif"
          }
        }
      },
      tooltip: {
        titleFont: {
          size: 16,
          weight: '700',
          family: "'Product Sans', sans-serif"
        },
        bodyFont: {
          size: 16,
          weight: '400',
          family: "'Product Sans', sans-serif"
        },
        bodyColor: '#000',
        titleColor: '#000',
        backgroundColor: '#fff',
        borderColor: '#ccc',
        borderWidth: 1
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