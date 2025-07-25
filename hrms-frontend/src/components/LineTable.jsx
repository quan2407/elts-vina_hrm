import React, {
    useEffect,
    useState,
    forwardRef,
} from "react";
import "../styles/LineTable.css";
import * as XLSX from "xlsx";
import { useNavigate } from "react-router-dom";
import { getAllLines } from "../services/linesService";
import { jwtDecode } from "jwt-decode";


const LineTable = forwardRef(({ searchTerm }) => {

    const [line, setLine] = useState([]);
    const navigate = useNavigate();
    const [userRole, setUserRole] = useState("");



    useEffect(() => {
        const fetchLines = async () => {
            try {
                const data = await getAllLines(searchTerm);
                setLine(data);
            } catch (error) {
                console.error("Lỗi khi load danh sách line:", error);
            }
        };
        fetchLines();
    }, [searchTerm]);

    const handleEmployeeClick = (id, userRole) => {

        if (userRole === 'pmc') {
            navigate(`/employee/line/${id}`);
        }
        else if (userRole === 'hr') {
            navigate(`/employee/line-hr/${id}`);
        } else {
            console.error("Không có quyền truy cập vào danh sách nhân viên của line này.");
        }
    }

    useEffect(() => {
        const token = localStorage.getItem('accessToken');

        if (token) {
            try {
                const decodedToken = jwtDecode(token);
                console.log("Decoded token:", decodedToken);

                if (decodedToken.roles) {
                    const roles = decodedToken.roles;

                    if (roles.includes('ROLE_PMC')) {
                        setUserRole('pmc');
                    } else if (roles.includes('ROLE_HR')) {
                        setUserRole('hr');
                    }
                }
            } catch (error) {
                console.error('Error decoding token:', error);
                setUserRole('employee');
            }
        } else {
            setUserRole('employee');
        }
    }, []);
    return (
        <div className="line-table-wrapper">
            <div className="line-table">
                <div className="line-table-header">
                    <div className="line-header-cell">Id</div>
                    <div className="line-header-cell">tên line</div>
                    <div className="line-header-cell">Số lượng nhân viên</div>
                    <div className="line-header-cell">tổ trưởng</div>
                    <div className="line-header-cell">Hành động</div>
                </div>
                {line.length === 0 && (
                    <div className="line-table-empty">Không tìm thấy dòng nào phù hợp.</div>
                )}
                {line.map((a) => (
                    <div
                        key={a.id}
                        className="line-table-row"
                    >
                        <div className="line-table-cell">{a.id}</div>
                        <div className="line-table-cell">{a.name}</div>
                        <div className="line-table-cell">{a.quantity}</div>
                        <div className="line-table-cell">{a.leaderName}</div>
                        <div className="line-table-cell">

                            <button className="viewcandidate-button" onClick={() => handleEmployeeClick(a.id, userRole)}>Danh sách nhân viên</button>

                        </div>
                    </div>
                ))}

            </div>
        </div>
    );
});

export default LineTable;