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
  submitWorkSchedules(month, year) {
    return axiosClient.put(`/work-schedules/submit`, null, {
      params: { month, year },
    });
  },
  acceptWorkSchedules(month, year) {
    return axiosClient.put("/work-schedules/accept", null, {
      params: { month, year },
    });
  },
  updateWorkScheduleDetail: (payload) => {
    return axiosClient.put("/work-schedule-details", payload);
  },
  deleteWorkScheduleDetail: (id) => {
    return axiosClient.delete(`/work-schedule-details/${id}`);
  },
  getWorkScheduleForCurrentEmployee: (month, year) => {
    return axiosClient.get("/work-schedules/employee-view", {
      params: { month, year },
    });
  },
  rejectWorkSchedules: (month, year, reason) => {
    return axiosClient.put("/work-schedules/reject", null, {
      params: { month, year, reason },
    });
  },
  createCustomWorkSchedule: (payload) => {
    return axiosClient.put("/work-schedules/custom-range", payload);
  },
  requestRevision: (month, year, reason) => {
    return axiosClient.put("/work-schedules/request-revision", reason, {
      params: { month, year, reason },
      headers: { "Content-Type": "application/json" },
    });
  },
  exportWorkSchedule: (month, year) => {
    return axiosClient.post(
      "/work-schedule-details/export-work-schedule",
      { month, year },
      {
        responseType: "blob",
      }
    );
  },
};

export default workScheduleService;
