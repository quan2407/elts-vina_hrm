import React, { useEffect, useState } from 'react';
import { Button, Modal, Select, Form, message } from 'antd';
import {PlusOutlined, SolutionOutlined} from '@ant-design/icons';
import benefitService from "../../../services/benefitService.js"; // cập nhật đường dẫn đúng với dự án của bạn
import { UserSwitchOutlined } from '@ant-design/icons';


const { Option } = Select;

const AssignBenefitToPositions = ({ benefitId, onSuccess }) => {
    const [open, setOpen] = useState(false);
    const [availablePositions, setAvailablePositions] = useState([]);
    const [selectedPositions, setSelectedPositions] = useState([]);
    const isAllSelected = availablePositions.length > 0 && selectedPositions.length === availablePositions.length;

    const [loading, setLoading] = useState(false);
    // Fetch available positions when modal opens

    const fetchAvailablePositions = async () => {
        try {
            setLoading(true);
            const res = await benefitService.getAvailablePositions(benefitId);
            console.log("Hi" + res)
            setAvailablePositions(res.data || []);
            // eslint-disable-next-line no-unused-vars
        } catch (error) {
            console.error(' API lỗi:', error?.response || error);
            message.error('Không thể tải danh sách vị trí');
        } finally {
            setLoading(false);
        }
    };

    const handleOpen = () => {
        setOpen(true);
        fetchAvailablePositions();
    };

    const handleCancel = () => {
        setOpen(false);
        setSelectedPositions([]);
    };

    const handleOk = async () => {
        if (selectedPositions.length === 0) {
            message.warning('Vui lòng chọn ít nhất một vị trí!');
            return;
        }

        try {
            setLoading(true);
            await benefitService.assignPositionsToBenefit( {
                benefitId,
                positionIds: selectedPositions,
            });

            message.success('Gán phúc lợi thành công!');
            setOpen(false);
            setSelectedPositions([]);
            if (onSuccess) onSuccess(); // callback nếu cần reload danh sách bên ngoài
            // eslint-disable-next-line no-unused-vars
        } catch (error) {
            message.error('Lỗi khi gán phúc lợi!');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <Button
                type="primary"
                icon={<SolutionOutlined />}
                onClick={handleOpen}
                style={{
                    backgroundColor: '#388e3c',
                    border: 'none',
                    borderRadius: 20,
                    fontWeight: 600,
                    boxShadow: '0 2px 6px rgba(0,0,0,0.2)',
                }}
            >
                Gán vị trí cho phúc lợi
            </Button>

            <Modal
                title="Gán phúc lợi cho các vị trí"
                open={open}
                onOk={handleOk}
                onCancel={handleCancel}
                okText={
                    selectedPositions.length > 0
                        ? `Xác nhận (${selectedPositions.length} vị trí)`
                        : 'Xác nhận'
                }
                cancelText="Hủy"
                confirmLoading={loading}
            >
                <Form layout="vertical">
                    <Form.Item
                        label="Chọn các vị trí"
                        tooltip="Chỉ hiện các vị trí chưa được gán phúc lợi này"
                    >
                        <Select
                            mode="multiple"
                            allowClear
                            showSearch
                            optionFilterProp="children"
                            placeholder="Chọn các vị trí"
                            value={selectedPositions}
                            onChange={setSelectedPositions}
                            loading={loading}
                            getPopupContainer={() => document.body}
                            dropdownRender={(menu) => (
                                <div>
                                    {/*  Nút chọn tất cả ở trên */}
                                    <div
                                        style={{
                                            padding: '8px',
                                            borderBottom: '1px solid #f0f0f0',
                                            textAlign: 'center',
                                            fontWeight: 500,
                                            color: '#1890ff',
                                            cursor: 'pointer',
                                        }}
                                        onMouseDown={(e) => {
                                            e.preventDefault();
                                            if (isAllSelected) {
                                                setSelectedPositions([]);
                                            } else {
                                                setSelectedPositions(availablePositions.map(pos => pos.id));
                                            }
                                        }}
                                    >
                                        {isAllSelected ? 'Bỏ chọn tất cả' : 'Chọn tất cả'}
                                    </div>

                                    {/*  Menu chính gồm tìm kiếm + danh sách */}
                                    {menu}

                                    {/*  Nút đóng danh sách */}
                                    <div
                                        style={{
                                            padding: '8px',
                                            textAlign: 'center',
                                            borderTop: '1px solid #f0f0f0',
                                            cursor: 'pointer',
                                            fontWeight: 500,
                                            color: '#1890ff',
                                        }}
                                        onMouseDown={(e) => {
                                            e.preventDefault();
                                            document.dispatchEvent(
                                                new MouseEvent('mousedown', { bubbles: true })
                                            );
                                        }}
                                    >
                                        Đóng danh sách
                                    </div>
                                </div>
                            )}
                        >
                            {availablePositions.map((pos) => (
                                <Option key={pos.id} value={pos.id}>
                                    {pos.name}
                                </Option>
                            ))}
                        </Select>



                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default AssignBenefitToPositions;
