import React, { useEffect } from 'react';
import { Modal, Form, Input, DatePicker, InputNumber } from 'antd';
import dayjs from 'dayjs';

const { TextArea } = Input;

const UpdateProgramModal = ({ open, onCancel, onSubmit, initialData }) => {
    const [form] = Form.useForm();

    // Khi mở modal thì set giá trị mặc định từ initialData
    useEffect(() => {
        if (open && initialData) {
            form.setFieldsValue({
                title: initialData.title,
                description: initialData.description,
                startDate: dayjs(initialData.startDate),
                endDate: dayjs(initialData.endDate),
                maxParticipants: initialData.maxParticipants,
            });
        }
    }, [open, initialData, form]);

    const handleOk = () => {
        form
            .validateFields()
            .then(values => {
                onSubmit({
                    ...values,
                    startDate: values.startDate.format('YYYY-MM-DD'),
                    endDate: values.endDate.format('YYYY-MM-DD'),
                });
                form.resetFields();
            })
            .catch(info => {
                console.log('Validation Failed:', info);
            });
    };

    return (
        <Modal
            title="Cập nhật chương trình"
            open={open}
            onCancel={() => {
                form.resetFields();
                onCancel();
            }}
            onOk={handleOk}
            okText="Lưu"
            cancelText="Hủy"
        >
            <Form form={form} layout="vertical">
                <Form.Item
                    name="title"
                    label="Tiêu đề"
                    rules={[{ required: true, message: 'Vui lòng nhập tiêu đề!' }]}
                >
                    <Input placeholder="Nhập tiêu đề" />
                </Form.Item>

                <Form.Item
                    name="description"
                    label="Mô tả"
                    rules={[{ required: true, message: 'Vui lòng nhập mô tả!' }]}
                >
                    <TextArea rows={3} placeholder="Nhập mô tả" />
                </Form.Item>

                <Form.Item
                    name="startDate"
                    label="Ngày bắt đầu"
                    rules={[{ required: true, message: 'Vui lòng chọn ngày bắt đầu!' }]}
                >
                    <DatePicker style={{ width: '100%' }} />
                </Form.Item>

                <Form.Item
                    name="endDate"
                    label="Ngày kết thúc"
                    rules={[{ required: true, message: 'Vui lòng chọn ngày kết thúc!' }]}
                >
                    <DatePicker style={{ width: '100%' }} />
                </Form.Item>

                <Form.Item
                    name="maxParticipants"
                    label="Số lượng người tham gia tối đa"
                    rules={[{ required: true, message: 'Vui lòng nhập số lượng!' }]}
                >
                    <InputNumber min={1} style={{ width: '100%' }} />
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default UpdateProgramModal;
