import React, { useEffect, useState } from "react";
import workScheduleService from "../services/workScheduleService";
import WorkScheduleModal from "./WorkScheduleModal";
import "../styles/WorkScheduleTable.css";

const weekdays = ["CN", "Th 2", "Th 3", "Th 4", "Th 5", "Th 6", "Th 7"];

function WorkScheduleTable() {
  const today = new Date();
  const [month, setMonth] = useState(today.getMonth() + 1);
  const [year, setYear] = useState(today.getFullYear());
  const [dates, setDates] = useState([]);
  const [data, setData] = useState([]);

  const [errorMessage, setErrorMessage] = useState("");

  const [modalOpen, setModalOpen] = useState(false);
  const [selectedDept, setSelectedDept] = useState("");
  const [selectedLine, setSelectedLine] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedWorkScheduleId, setSelectedWorkScheduleId] = useState(null);
  const [startTime, setStartTime] = useState("08:00");
  const [endTime, setEndTime] = useState("17:00");

  const handleCreateSchedule = (month, year) => {
    workScheduleService
      .createWorkSchedulesForMonth(month, year) // bạn có thể đặt tên nào phù hợp
      .then(() => {
        return workScheduleService.getWorkScheduleByMonth(month, year);
      })
      .then((res) => {
        setData(res.data);
      })
      .catch((err) => {
        console.error("Lỗi tạo lịch làm việc:", err);
      });
  };

  useEffect(() => {
    const numDays = new Date(year, month, 0).getDate();
    const tempDates = [];
    for (let i = 1; i <= numDays; i++) {
      const dateObj = new Date(year, month - 1, i);
      tempDates.push({
        full: dateObj.toLocaleDateString("vi-VN"),
        weekday: weekdays[dateObj.getDay()],
        isSunday: dateObj.getDay() === 0,
        isSaturday: dateObj.getDay() === 6,
        iso: `${dateObj.getFullYear()}-${(dateObj.getMonth() + 1)
          .toString()
          .padStart(2, "0")}-${dateObj.getDate().toString().padStart(2, "0")}`,
      });
    }
    setDates(tempDates);

    workScheduleService
      .getWorkScheduleByMonth(month, year)
      .then((res) => {
        setData(res.data);
        setErrorMessage("");
      })
      .catch((err) => {
        const msg =
          err?.response?.data?.message ||
          "Không thể tải lịch làm việc. Vui lòng thử lại sau.";
        setData([]);
        setErrorMessage(msg);
      });
  }, [month, year]);

  const handlePrevMonth = () => {
    if (month === 1) {
      setMonth(12);
      setYear((y) => y - 1);
    } else {
      setMonth((m) => m - 1);
    }
  };

  const handleNextMonth = () => {
    if (month === 12) {
      setMonth(1);
      setYear((y) => y + 1);
    } else {
      setMonth((m) => m + 1);
    }
  };

  const handleOpenModal = (deptName, lineName, dateIso, workScheduleId) => {
    setSelectedDept(deptName);
    setSelectedLine(lineName);
    setSelectedDate(dateIso);
    setSelectedWorkScheduleId(workScheduleId);
    setStartTime("08:00");
    setEndTime("17:00");
    setModalOpen(true);
  };

  const handleSave = () => {
    const payload = {
      dateWork: selectedDate,
      startTime,
      endTime,
      workScheduleId: selectedWorkScheduleId,
    };

    workScheduleService
      .createWorkScheduleDetail(payload)
      .then(() => {
        setModalOpen(false);
        workScheduleService.getWorkScheduleByMonth(month, year).then((res) => {
          setData(res.data);
          setErrorMessage("");
        });
      })
      .catch((err) => console.error("Lỗi thêm lịch làm việc:", err));
  };

  return (
    <div className="work-schedule-container">
      <div className="work-schedule-toolbar-wrapper">
        <div className="work-schedule-toolbar">
          <button
            onClick={handlePrevMonth}
            className="work-schedule-nav-btn"
          >
            ◀
          </button>
          <select
            value={month}
            onChange={(e) => setMonth(parseInt(e.target.value))}
          >
            {Array.from({ length: 12 }, (_, i) => (
              <option
                key={i + 1}
                value={i + 1}
              >
                Tháng {i + 1}
              </option>
            ))}
          </select>
          <select
            value={year}
            onChange={(e) => setYear(parseInt(e.target.value))}
          >
            {Array.from({ length: 5 }, (_, i) => {
              const y = 2023 + i;
              return (
                <option
                  key={y}
                  value={y}
                >
                  Năm {y}
                </option>
              );
            })}
          </select>
          <button
            onClick={handleNextMonth}
            className="work-schedule-nav-btn"
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
          <button
            className="work-schedule-create-btn"
            onClick={() => handleCreateSchedule(month, year)}
          >
            Tạo lịch cho tháng này
          </button>
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
                    d.isSunday || d.isSaturday ? "work-schedule-weekend" : ""
                  }`}
                >
                  {d.full}
                </div>
              ))}
              {dates.map((d, idx) => (
                <div
                  key={`weekday-${idx}`}
                  className={`work-schedule-header work-schedule-header-weekday ${
                    d.isSunday || d.isSaturday ? "work-schedule-weekend" : ""
                  }`}
                >
                  {d.weekday}
                </div>
              ))}

              {data.map((dept) =>
                dept.lines.map((line, idx) =>
                  line.workDetails.map((detail, i) => (
                    <div
                      key={`${dept.departmentId}-${idx}-${i}`}
                      className="work-schedule-cell work-schedule-day-cell"
                    >
                      {detail.startTime && detail.endTime ? (
                        `${detail.startTime} - ${detail.endTime}`
                      ) : (
                        <button
                          className="work-schedule-add-btn"
                          onClick={() =>
                            handleOpenModal(
                              dept.departmentName,
                              line.lineName,
                              dates[i].iso,
                              detail.workScheduleId
                            )
                          }
                        >
                          +
                        </button>
                      )}
                    </div>
                  ))
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
          onChange: (field, value) => {
            if (field === "startTime") setStartTime(value);
            if (field === "endTime") setEndTime(value);
          },
        }}
      />
    </div>
  );
}

export default WorkScheduleTable;
