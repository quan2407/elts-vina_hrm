import React, { useEffect, useState } from "react";
import workScheduleService from "../services/workScheduleService";
import WorkScheduleModal from "./WorkScheduleModal";
import "../styles/WorkScheduleTable.css";

const weekdays = ["CN", "Th 2", "Th 3", "Th 4", "Th 5", "Th 6", "Th 7"];

function WorkScheduleTable({
  month,
  year,
  setMonth,
  setYear,
  onStatusChange,
  onMonthYearChange,
  canEdit = true,
  reloadTrigger,
}) {
  const [dates, setDates] = useState([]);
  const [data, setData] = useState([]);
  const [selectedDeptId, setSelectedDeptId] = useState(null);
  const [selectedLineId, setSelectedLineId] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");
  const [workType, setWorkType] = useState("normal");
  const [availableMonths, setAvailableMonths] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedDept, setSelectedDept] = useState("");
  const [selectedLine, setSelectedLine] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedWorkScheduleId, setSelectedWorkScheduleId] = useState(null);
  const [startTime, setStartTime] = useState("08:00");
  const [endTime, setEndTime] = useState("17:00");
  const [selectedDetailId, setSelectedDetailId] = useState(null);
  const getTotalMonthValue = (m, y) => y * 12 + m;
  const handleDelete = (id) => {
    workScheduleService
      .deleteWorkScheduleDetail(id)
      .then(() => {
        setModalOpen(false);
        fetchDataAndStatus(month, year);
      })
      .catch((err) => console.error("Lỗi xóa lịch làm việc:", err));
  };

  const getLastAvailableMonth = () => {
    if (availableMonths.length === 0) return null;
    return availableMonths.reduce((a, b) =>
      getTotalMonthValue(b.month, b.year) > getTotalMonthValue(a.month, a.year)
        ? b
        : a
    );
  };

  const getMinAvailableMonth = () => {
    if (availableMonths.length === 0) return null;
    return availableMonths.reduce((a, b) =>
      getTotalMonthValue(b.month, b.year) < getTotalMonthValue(a.month, a.year)
        ? b
        : a
    );
  };

  const getNextAvailableMonth = () => {
    if (availableMonths.length === 0) return false;
    const sorted = [...availableMonths].sort(
      (a, b) =>
        getTotalMonthValue(a.month, a.year) -
        getTotalMonthValue(b.month, b.year)
    );
    const last = sorted.at(-1);
    if (!last) return false;
    return (
      getTotalMonthValue(month, year) <
      getTotalMonthValue(last.month, last.year) + 1
    );
  };

  const getPrevAvailableMonth = () => {
    if (availableMonths.length === 0) return false;
    const sorted = [...availableMonths].sort(
      (a, b) =>
        getTotalMonthValue(a.month, a.year) -
        getTotalMonthValue(b.month, b.year)
    );
    const first = sorted[0];
    if (!first) return false;
    return (
      getTotalMonthValue(month, year) >
      getTotalMonthValue(first.month, first.year) - 1
    );
  };

  const isMonthSelectable = (m, y) => {
    if (availableMonths.length === 0) return false;
    return (
      getTotalMonthValue(m, y) <=
      Math.max(
        ...availableMonths.map((d) => getTotalMonthValue(d.month, d.year))
      ) +
        1
    );
  };
  const fetchDataAndStatus = (m, y) => {
    workScheduleService
      .getWorkScheduleByMonth(m, y)
      .then((res) => {
        setData(res.data);

        if (onStatusChange) {
          const allLines = res.data.flatMap((dept) => dept.lines);

          const allSubmitted =
            allLines.length > 0 &&
            allLines.every((line) => String(line.submitted) === "true");

          const allAccepted =
            allLines.length > 0 &&
            allLines.every((line) => String(line.accepted) === "true");

          console.log(
            "🔥 Gọi onStatusChange từ WorkScheduleTable với:",
            allAccepted,
            allSubmitted
          );

          if (allAccepted) onStatusChange("approved");
          else if (allSubmitted) onStatusChange("submitted");
          else onStatusChange("not-submitted");
        }
      })
      .catch(() => {
        setData([]);
        if (onStatusChange) onStatusChange("not-submitted");
      });
  };

  const handleCreateSchedule = (month, year) => {
    workScheduleService
      .createWorkSchedulesForMonth(month, year)
      .then(() => fetchDataAndStatus(month, year))
      .catch((err) => console.error("Lỗi tạo lịch làm việc:", err));
  };

  useEffect(() => {
    const numDays = new Date(year, month, 0).getDate();
    const tempDates = Array.from({ length: numDays }, (_, i) => {
      const dateObj = new Date(year, month - 1, i + 1);
      return {
        full: dateObj.toLocaleDateString("vi-VN"),
        weekday: weekdays[dateObj.getDay()],
        isSunday: dateObj.getDay() === 0,
        isSaturday: dateObj.getDay() === 6,
        iso: `${dateObj.getFullYear()}-${(dateObj.getMonth() + 1)
          .toString()
          .padStart(2, "0")}-${dateObj.getDate().toString().padStart(2, "0")}`,
      };
    });
    setDates(tempDates);

    fetchDataAndStatus(month, year);

    workScheduleService
      .getAvailableMonths()
      .then((res) => setAvailableMonths(res.data))
      .catch((err) => console.error("Lỗi lấy danh sách tháng:", err));
  }, [month, year, reloadTrigger]);

  const handlePrevMonth = () => {
    const newMonth = month === 1 ? 12 : month - 1;
    const newYear = month === 1 ? year - 1 : year;
    if (isMonthSelectable(newMonth, newYear)) {
      setMonth(newMonth);
      setYear(newYear);
      if (onMonthYearChange) onMonthYearChange(newMonth, newYear);
    }
  };

  const handleNextMonth = () => {
    const newMonth = month === 12 ? 1 : month + 1;
    const newYear = month === 12 ? year + 1 : year;
    if (isMonthSelectable(newMonth, newYear)) {
      setMonth(newMonth);
      setYear(newYear);
      if (onMonthYearChange) onMonthYearChange(newMonth, newYear);
    }
  };

  const nextDisabled = !getNextAvailableMonth();
  const prevDisabled = !getPrevAvailableMonth();
  const handleOpenModal = (
    deptId,
    deptName,
    lineId,
    lineName,
    dateIso,
    workScheduleId,
    detail = null
  ) => {
    // Gán thông tin cơ bản
    setSelectedDeptId(deptId);
    setSelectedLineId(lineId);
    setSelectedDept(deptName);
    setSelectedLine(lineName);
    setSelectedDate(dateIso);
    setSelectedWorkScheduleId(workScheduleId);

    // Xử lý giờ làm việc nếu có detail
    if (detail?.startTime && detail?.endTime) {
      console.log("Chi tiết lịch:", detail);
      const start = detail.startTime.slice(0, 5);
      const end = detail.endTime.slice(0, 5);

      // Tự động xác định loại ca làm việc
      if (start === "08:00" && end === "17:00") {
        setWorkType("normal");
      } else if (start === "08:00" && end === "20:00") {
        setWorkType("overtime");
      } else {
        setWorkType("custom");
      }

      setStartTime(start);
      setEndTime(end);
      setSelectedDetailId(detail.id);

      console.log("Giờ làm việc:", start, "-", end);
    } else {
      // Mặc định nếu chưa có giờ
      setWorkType("normal");
      setStartTime("08:00");
      setEndTime("17:00");
      setSelectedDetailId(null);
    }

    // Mở modal
    setModalOpen(true);
  };

  const handleSave = () => {
    if (endTime <= startTime) {
      alert("Giờ kết thúc phải sau giờ bắt đầu.");
      return;
    }

    if (selectedDetailId) {
      const payload = {
        workScheduleDetailId: selectedDetailId,
        startTime,
        endTime,
      };
      workScheduleService
        .updateWorkScheduleDetail(payload)
        .then(() => {
          setModalOpen(false);
          fetchDataAndStatus(month, year);
          setErrorMessage("");
        })
        .catch((err) => console.error("Lỗi cập nhật lịch làm việc:", err));
    } else {
      workScheduleService
        .resolveWorkScheduleId(selectedDeptId, selectedLineId, selectedDate)
        .then((res) => {
          const workScheduleId = res.data;
          const payload = {
            dateWork: selectedDate,
            startTime,
            endTime,
            workScheduleId,
          };
          return workScheduleService.createWorkScheduleDetail(payload);
        })
        .then(() => {
          setModalOpen(false);
          fetchDataAndStatus(month, year);
          setErrorMessage("");
        })
        .catch((err) => console.error("Lỗi thêm lịch làm việc:", err));
    }
  };

  return (
    <div className="work-schedule-container">
      <div className="work-schedule-toolbar-wrapper">
        <div className="work-schedule-toolbar">
          <button
            onClick={handlePrevMonth}
            className="work-schedule-nav-btn"
            style={{
              backgroundColor: prevDisabled ? "#ccc" : "#000",
              color: "#fff",
            }}
            disabled={prevDisabled}
          >
            ◀
          </button>

          <select
            value={month}
            onChange={(e) => {
              const m = parseInt(e.target.value);
              if (isMonthSelectable(m, year)) {
                setMonth(m);
                if (onMonthYearChange) onMonthYearChange(m, year);
              }
            }}
          >
            {Array.from({ length: 12 }, (_, i) => {
              const m = i + 1;
              return (
                <option
                  key={m}
                  value={m}
                  disabled={!isMonthSelectable(m, year)}
                >
                  Tháng {m}
                </option>
              );
            })}
          </select>

          <select
            value={year}
            onChange={(e) => {
              const y = parseInt(e.target.value);
              if (isMonthSelectable(month, y)) {
                setYear(y);
                if (onMonthYearChange) onMonthYearChange(month, y);
              }
            }}
          >
            {Array.from({ length: 5 }, (_, i) => {
              const y = 2023 + i;
              return (
                <option
                  key={y}
                  value={y}
                  disabled={!availableMonths.some((m) => m.year === y)}
                >
                  Năm {y}
                </option>
              );
            })}
          </select>

          <button
            onClick={handleNextMonth}
            className="work-schedule-nav-btn"
            style={{
              backgroundColor: nextDisabled ? "#ccc" : "#000",
              color: "#fff",
            }}
            disabled={nextDisabled}
          >
            ▶
          </button>
        </div>
      </div>

      {data.length === 0 ? (
        <div className="work-schedule-empty">
          <p className="work-schedule-empty-text">
            Chưa có lịch làm việc nào được tạo cho tháng {month}/{year}
          </p>
          {canEdit && (
            <button
              className="work-schedule-create-btn"
              onClick={() => handleCreateSchedule(month, year)}
            >
              Tạo lịch cho tháng này
            </button>
          )}
        </div>
      ) : (
        <div className="work-schedule-layout">
          <div className="work-schedule-left">
            <div className="work-schedule-header work-schedule-header-dept work-schedule-span-2">
              Phòng ban
            </div>
            <div className="work-schedule-header work-schedule-header-line work-schedule-span-2">
              Chuyền
            </div>

            {data.map((dept) =>
              dept.lines.map((line, idx) => (
                <React.Fragment key={`${dept.departmentId}-${idx}`}>
                  {idx === 0 && (
                    <div
                      className="work-schedule-cell work-schedule-dept-name"
                      style={{ gridRow: `span ${dept.lines.length}` }}
                    >
                      {dept.departmentName}
                    </div>
                  )}
                  <div className="work-schedule-cell work-schedule-line-name">
                    {line.lineName || <i></i>}
                  </div>
                </React.Fragment>
              ))
            )}
          </div>

          <div className="work-schedule-scroll">
            <div
              className="work-schedule-grid"
              style={{
                gridTemplateColumns: `repeat(${dates.length}, 100px)`,
              }}
            >
              {dates.map((d, idx) => (
                <div
                  key={`date-${idx}`}
                  className={`work-schedule-header work-schedule-header-date ${
                    d.isSunday ? "work-schedule-weekend" : ""
                  }`}
                >
                  {d.full}
                </div>
              ))}
              {dates.map((d, idx) => (
                <div
                  key={`weekday-${idx}`}
                  className={`work-schedule-header work-schedule-header-weekday ${
                    d.isSunday ? "work-schedule-weekend" : ""
                  }`}
                >
                  {d.weekday}
                </div>
              ))}

              {data.map((dept) =>
                dept.lines.map((line, idx) =>
                  line.workDetails.map((detail, i) => {
                    const isOvertime =
                      detail.overtime === true || detail.overtime === "true";

                    return (
                      <div
                        key={`${dept.departmentId}-${idx}-${i}`}
                        className="work-schedule-cell work-schedule-day-cell"
                      >
                        {detail.startTime && detail.endTime ? (
                          <span
                            className={`work-schedule-time-text ${
                              isOvertime ? "work-schedule-overtime" : ""
                            }`}
                            style={{ cursor: "pointer" }}
                            onClick={() =>
                              handleOpenModal(
                                dept.departmentId,
                                dept.departmentName,
                                line?.lineId,
                                line?.lineName ?? "",
                                dates[i].iso,
                                detail.workScheduleId,
                                detail
                              )
                            }
                          >
                            {detail.startTime?.slice(0, 5)} -{" "}
                            {detail.endTime?.slice(0, 5)}
                          </span>
                        ) : (
                          canEdit && (
                            <button
                              className="work-schedule-add-btn"
                              onClick={() =>
                                handleOpenModal(
                                  dept.departmentId,
                                  dept.departmentName,
                                  line?.lineId,
                                  line?.lineName ?? "",
                                  dates[i].iso,
                                  detail.workScheduleId,
                                  null
                                )
                              }
                            >
                              +
                            </button>
                          )
                        )}
                      </div>
                    );
                  })
                )
              )}
            </div>
          </div>
        </div>
      )}

      <WorkScheduleModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onSave={handleSave}
        data={{
          departmentName: selectedDept,
          lineName: selectedLine,
          date: selectedDate,
          startTime,
          endTime,
          workType,
          setWorkType,
          onChange: (field, value) => {
            if (field === "startTime") setStartTime(value);
            if (field === "endTime") setEndTime(value);
            if (field === "workType") {
              setWorkType(value);
              if (value === "normal") {
                setStartTime("08:00");
                setEndTime("17:00");
              } else if (value === "overtime") {
                setStartTime("08:00");
                setEndTime("20:00");
              }
            }
          },
          workScheduleId: selectedWorkScheduleId,
          id: selectedDetailId,
          onDelete: handleDelete,
        }}
      />
    </div>
  );
}

export default WorkScheduleTable;
