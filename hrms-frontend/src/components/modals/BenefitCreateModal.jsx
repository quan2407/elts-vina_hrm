import React, { useState } from 'react';
import {
    Button,
    Modal,
    Form,
    Input,
    DatePicker,
    InputNumber,
    Select,
    message,
} from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import benefitService from "../../services/benefitService.js";
const { RangePicker } = DatePicker;

const BenefitCreateModal = ({onCreated}) => {

    const [open, setOpen] = useState(false);
    const [form] = Form.useForm();


    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();

            const [startDate, endDate] = values.dateRange;

            const payload = {
                title: values.title,
                description: values.description || '',
                startDate: startDate.format('YYYY-MM-DD'),
                endDate: endDate.format('YYYY-MM-DD'),
                maxParticipants: values.maxParticipants,
                isActive: values.isActive,
            };

            const res =  await benefitService.create(payload);

            console.log(res)
            // if (!res.ok) throw new Error('Tạo thất bại');
            message.success('Tạo phúc lợi thành công!');
            setOpen(false);
            form.resetFields();
            onCreated?.();

        } catch (err) {
            console.error(err);
            message.error('Tạo thất bại!');
        }


    };

    return (
        <div>
            <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => setOpen(true)}
                style={{
                    backgroundColor: '#388e3c',
                    border: 'none',
                    borderRadius: 20,
                    fontWeight: 600,
                    boxShadow: '0 2px 6px rgba(0,0,0,0.2)',
                }}
            >
                Tạo phúc lợi
            </Button>

            <Modal
                title="Tạo phúc lợi mới"
                open={open}
                onCancel={() => setOpen(false)}
                onOk={handleSubmit}
                okText="Tạo mới"
                cancelText="Hủy"
            >
                <Form layout="vertical" form={form}>
                    <Form.Item
                        label="Tiêu đề"
                        name="title"
                        rules={[{ required: true, message: 'Vui lòng nhập tiêu đề' }]}
                    >
                        <Input placeholder="Nhập tiêu đề phúc lợi" />
                    </Form.Item>

                    <Form.Item label="Mô tả" name="description">
                        <Input.TextArea rows={3} placeholder="Nhập mô tả (không bắt buộc)" />
                    </Form.Item>

                    <Form.Item
                        label="Ngày bắt đầu - Ngày kết thúc"
                        name="dateRange"
                        rules={[{ required: true, message: 'Chọn khoảng ngày' }]}
                    >
                        <RangePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
                    </Form.Item>

                    <Form.Item
                        label="Số lượng người tham gia tối đa"
                        name="maxParticipants"
                        rules={[{ required: true, message: 'Nhập số lượng tối đa' }]}
                    >
                        <InputNumber min={1} style={{ width: '100%' }} />
                    </Form.Item>

                    <Form.Item
                        label="Trạng thái"
                        name="isActive"
                        rules={[{ required: true, message: 'Chọn trạng thái' }]}
                    >
                        <Select placeholder="Chọn trạng thái">
                            <Select.Option value={true}>Hoạt động</Select.Option>
                            <Select.Option value={false}>Ngưng hoạt động</Select.Option>
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>


        </div>
    )
}

export default BenefitCreateModal;

