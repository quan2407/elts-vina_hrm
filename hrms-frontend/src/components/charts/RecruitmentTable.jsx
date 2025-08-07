import React from "react";
import "../../styles/JobsDashboardTable.css";
import * as XLSX from "xlsx";

const RecruitmentDashboardTable = ({ data = [] }) => {

  const totalCanTuyen = data.reduce((sum, job) => sum + (job.canTuyen || 0), 0);
  const totalUngTuyen = data.reduce((sum, job) => sum + (job.ungTuyen || 0), 0);
  const totalDaTuyen = data.reduce((sum, job) => sum + (job.daTuyen || 0), 0);


  return (
    <div className="jobdash-table-wrapper">
      <div className="jobdash-table">
        <div className="jobdash-table-header">
          <div className="jobdash-header-cell">Id</div>
          <div className="jobdash-header-cell">Nội dung tuyển dụng</div>
          <div className="jobdash-header-cell">Số lượng tuyển dụng</div>
          <div className="jobdash-header-cell">Số lượng ứng tuyển</div>
          <div className="jobdash-header-cell">Số lượng đã tuyển</div>
        </div>

        {data.map((job) => (
          <div
            key={job.recruitmentId}
            className="jobdash-table-row"
          >
            <div className="jobdash-table-cell">{job.recuitmentId}</div>
            <div className="jobdash-table-cell">{job.recruitmentTitle}</div>
            <div className="jobdash-table-cell">{job.canTuyen}</div>
            <div className="jobdash-table-cell">{job.ungTuyen}</div>
            <div className="jobdash-table-cell">{job.daTuyen}</div>
          </div>
        ))}
        <div className="divider" />
        <div className="jobdash-table-row total-row">
          <div className="jobdash-table-cell" style={{ gridColumn: 'span 2' }}>Tổng</div>
          <div className="jobdash-table-cell">{totalCanTuyen}</div>
          <div className="jobdash-table-cell">{totalUngTuyen}</div>
          <div className="jobdash-table-cell">{totalDaTuyen}</div>
        </div>

      </div>
    </div>
  );
};

export default RecruitmentDashboardTable;
