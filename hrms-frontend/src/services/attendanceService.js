import axiosClient from "./axiosClient";

const attendanceService = {
  getMonthlyAttendance: (
    month,
    year,
    page = 0,
    size = 10,
    search = "",
    departmentId = null,
    positionId = null,
    lineId = null
  ) => {
    return axiosClient.get("/attendances/view-by-month", {
      params: {
        month,
        year,
        page,
        size,
        search,
        departmentId,
        positionId,
        lineId,
      },
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
  exportAttendanceToExcel: (month, year) => {
    return axiosClient.post(
      "/attendances/export",
      { month, year },
      {
        responseType: "blob",
      }
    );
  },
  importAttendanceFromExcel: (file, date) => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("date", date);
    return axiosClient.post("/attendances/import", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
};

export default attendanceService;
