import React, { useEffect, useState, useCallback } from 'react';
import {
    Modal,
    Input,
    List,
    Button,
    Avatar,
    Typography,
    Spin,
    Checkbox,
    message,
    Select,
    Row,
    Col
} from 'antd';
import { UserOutlined } from '@ant-design/icons';
import debounce from 'lodash.debounce';
import benefitService from "../../../services/benefitService.js";

const { Text } = Typography;
const { Option } = Select;

const AssignEmployeeToBenefit = ({ benefitId, positionId, reloadKey, onForceReload }) => {
    const [visible, setVisible] = useState(false);
    const [searchText, setSearchText] = useState('');
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(false);
    const [submitLoading, setSubmitLoading] = useState(false);
    const [selectedEmails, setSelectedEmails] = useState([]);
    const [departmentList, setDepartmentList] = useState([]);
    const [selectedDept, setSelectedDept] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);

    const pageSize = 5;

    // === API: Tìm kiếm nhân viên chưa được gán ===
    const fetchEmployees = async (keyword = '', departmentId = null) => {
        if (!benefitId || !positionId) return;

        setLoading(true);
        try {
            const response = await benefitService.searchUnregisteredEmployees({
                benefitId,
                positionId,
                keyword
            });

            let data = response.data || [];

            // Lọc theo phòng ban nếu có
            if (departmentId) {
                data = data.filter(emp => emp.department?.id === departmentId);
            }

            // Lấy danh sách phòng ban để lọc
            const uniqueDepts = Array.from(new Set(data.map(emp => JSON.stringify(emp.department))))
                .map(str => JSON.parse(str));

            setDepartmentList(uniqueDepts);
            setEmployees(data);
        } catch (error) {
            console.error('Lỗi khi tìm nhân viên:', error);
        } finally {
            setLoading(false);
        }
    };

    // === Debounce search keyword ===
    const debounceSearch = useCallback(
        debounce((val) => {
            fetchEmployees(val, selectedDept);
        }, 400),
        [benefitId, positionId, selectedDept]
    );

    const handleSearchChange = (e) => {
        const value = e.target.value;
        setSearchText(value);
        debounceSearch(value);
    };

    const handleOpen = () => {
        setVisible(true);
        fetchEmployees('', null);
    };

    const handleClose = () => {
        setVisible(false);
        setSearchText('');
        setEmployees([]);
        setSelectedEmails([]);
        setSelectedDept(null);
        setCurrentPage(1);
    };

    const handleToggleSelect = (email) => {
        setSelectedEmails((prev) =>
            prev.includes(email) ? prev.filter(e => e !== email) : [...prev, email]
        );
    };

    const handleBatchAdd = async () => {
        if (selectedEmails.length === 0) {
            return message.warning("Vui lòng chọn ít nhất một nhân viên.");
        }

        setSubmitLoading(true);
        try {
            await benefitService.quickRegister({
                benefitId,
                positionId,
                keywords: selectedEmails
            });

            message.success(`Đã thêm ${selectedEmails.length} nhân viên thành công`);
            onForceReload();
            setSelectedEmails([]);
            fetchEmployees(searchText, selectedDept);
        } catch (error) {
            console.error('Lỗi khi thêm nhân viên:', error);
            message.error("Thêm nhân viên thất bại");
        } finally {
            setSubmitLoading(false);
        }
    };

    const handleSelectAll = () => {
        const emailsInPage = employees
            .slice((currentPage - 1) * pageSize, currentPage * pageSize)
            .map(emp => emp.email);
        const allSelected = emailsInPage.every(email => selectedEmails.includes(email));

        if (allSelected) {
            // Bỏ chọn tất cả
            setSelectedEmails(prev => prev.filter(email => !emailsInPage.includes(email)));
        } else {
            // Chọn tất cả trang hiện tại
            const newSelected = emailsInPage.filter(email => !selectedEmails.includes(email));
            setSelectedEmails(prev => [...prev, ...newSelected]);
        }
    };

    const handleDeptChange = (value) => {
        setSelectedDept(value);
        fetchEmployees(searchText, value);
    };

    return (
        <>
            <Button type="primary" onClick={handleOpen}>
                Gán nhân viên
            </Button>

            <Modal
                title="Gán nhân viên vào quyền lợi"
                visible={visible}
                onCancel={handleClose}
                footer={[
                    <Button key="cancel" onClick={handleClose}>
                        Đóng
                    </Button>,
                    <Button
                        key="submit"
                        type="primary"
                        onClick={handleBatchAdd}
                        loading={submitLoading}
                        disabled={selectedEmails.length === 0}
                    >
                        Xác nhận ({selectedEmails.length})
                    </Button>
                ]}
                width={700}
            >
                <Row gutter={12} style={{ marginBottom: 16 }}>
                    <Col span={16}>
                        <Input
                            placeholder="Tìm kiếm nhân viên..."
                            value={searchText}
                            onChange={handleSearchChange}
                        />
                    </Col>
                    <Col span={8}>
                        <Select
                            allowClear
                            placeholder="Lọc theo phòng ban"
                            value={selectedDept}
                            style={{ width: '100%' }}
                            onChange={handleDeptChange}
                        >
                            {departmentList.map(dept => (
                                <Option key={dept.id} value={dept.id}>
                                    {dept.name}
                                </Option>
                            ))}
                        </Select>
                    </Col>
                </Row>

                <Button
                    onClick={handleSelectAll}
                    style={{ marginBottom: 16 }}
                >
                    {employees
                        .slice((currentPage - 1) * pageSize, currentPage * pageSize)
                        .every(emp => selectedEmails.includes(emp.email))
                        ? 'Bỏ chọn tất cả'
                        : 'Chọn tất cả trang hiện tại'}
                </Button>

                {loading ? (
                    <Spin tip="Đang tải danh sách..." />
                ) : (
                    <List
                        dataSource={employees.slice((currentPage - 1) * pageSize, currentPage * pageSize)}
                        pagination={{
                            pageSize,
                            current: currentPage,
                            onChange: (page) => setCurrentPage(page),
                            showSizeChanger: false
                        }}
                        renderItem={(item) => {
                            const isChecked = selectedEmails.includes(item.email);
                            return (
                                <List.Item
                                    style={{
                                        padding: '16px',
                                        borderRadius: '12px',
                                        background: '#fafafa',
                                        marginBottom: '12px',
                                        transition: 'all 0.3s',
                                        boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
                                        cursor: 'pointer'
                                    }}
                                    onClick={() => handleToggleSelect(item.email)}
                                >
                                    <List.Item.Meta
                                        avatar={<Avatar size={50} icon={<UserOutlined />} />}
                                        title={
                                            <>
                                                <Text strong>{item.employeeName}</Text>
                                                <br />
                                                <Text type="secondary">{item.email}</Text>
                                            </>
                                        }
                                        description={
                                            <Text type="secondary">Phòng: {item.department?.name}</Text>
                                        }
                                    />
                                    <Checkbox
                                        checked={isChecked}
                                        onChange={() => handleToggleSelect(item.email)}
                                    />
                                </List.Item>
                            );
                        }}
                    />
                )}
            </Modal>
        </>
    );
};

export default AssignEmployeeToBenefit;
