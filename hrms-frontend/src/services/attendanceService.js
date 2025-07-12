import axiosClient from "./axiosClient";

const attendanceService = {
  getMonthlyAttendance: (month, year) => {
    return axiosClient.get("/attendances/view-by-month", {
      params: { month, year },
    });
  },
  getEmployeeMonthlyAttendanceById: (month, year) => {
    return axiosClient.get(`/attendances/employee`, {
      params: { month, year },
    });
  },
  getAvailableMonths: () => {
    return axiosClient.get("/attendances/available-months");
  },
  updateCheckInOut: (id, payload) => {
    return axiosClient.put(`/attendances/${id}`, payload);
  },
  updateLeaveCode: (id, payload) => {
    return axiosClient.put(`/attendances/${id}/leave-code`, payload);
  },
};

export default attendanceService;
