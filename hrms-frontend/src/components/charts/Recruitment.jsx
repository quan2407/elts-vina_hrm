
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from "recharts";

const RecruitmentChart = ({ data }) => {
  return (
    <div className="Interview-table-wrapper">
      <div className="Interview-table">
        <div style={{ width: '100%', height: 400 }}>
          <h2>Kết quả tuyển dụng</h2>
          <ResponsiveContainer>
            <BarChart
              data={data}
              margin={{ top: 20, right: 30, left: 0, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="recruitmentTitle" />
              <YAxis />
              <Tooltip />
              <Legend />
              
              <Bar dataKey="canTuyen" name="Cần tuyển" fill="#00bfff" />
              <Bar dataKey="daTuyen" name="Đã tuyển" fill="#32cd32" />
              <Bar dataKey="ungTuyen" name="Ứng tuyển" fill="#ffa500" />

            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};

export default RecruitmentChart;
