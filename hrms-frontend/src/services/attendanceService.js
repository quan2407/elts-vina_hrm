import axiosClient from "./axiosClient";

const attendanceService = {
  getMonthlyAttendance: (month, year) => {
    return axiosClient.get("/attendances/view-by-month", {
      params: { month, year },
    });
  },
};

export default attendanceService;
