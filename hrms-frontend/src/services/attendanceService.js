import axiosClient from "./axiosClient";

const attendanceService = {
  getMonthlyAttendance: (month, year, page = 0, size = 10) => {
    return axiosClient.get("/attendances/view-by-month", {
      params: { month, year, page, size },
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
