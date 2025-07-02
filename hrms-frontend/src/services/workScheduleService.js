import axiosClient from "./axiosClient";

const workScheduleService = {
  getWorkScheduleByMonth: (month, year) => {
    return axiosClient.get("/work-schedule-details/view-by-month", {
      params: { month, year },
    });
  },
  createWorkSchedulesForMonth(month, year) {
    return axiosClient.post("/work-schedules", {
      month,
      year,
    });
  },
  getAvailableMonths: () => axiosClient.get("/work-schedules/available"),
  createWorkScheduleDetail: (payload) => {
    return axiosClient.post("/work-schedule-details", payload);
  },
  resolveWorkScheduleId: (departmentId, lineId, dateWork) => {
    return axiosClient.get("/work-schedules/resolve-id", {
      params: { departmentId, lineId, dateWork },
    });
  },
};

export default workScheduleService;
