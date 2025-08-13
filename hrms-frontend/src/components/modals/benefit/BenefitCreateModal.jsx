import React, { useState, } from 'react';
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
import benefitService from "../../../services/benefitService.js";
const { RangePicker } = DatePicker;

const BenefitCreateModal = ({onCreated}) => {

    const [open, setOpen] = useState(false);
    const [form] = Form.useForm();
    const [error, setError] = useState(null);


    const benefitType = Form.useWatch('benefitType', form);
    const formulaType = Form.useWatch('formulaType', form);
    console.log("Obj trong BenefitCreateModal: ", {onCreated})
    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();



            const payload = {
                title: values.title,
                description: values.description || '',
                benefitType: values.benefitType,
                defaultFormulaType: values.formulaType,
                defaultFormulaValue: values.formulaValue,
                isActive: values.isActive,
                detail: values.detail || ''
            };

            // Thêm vào nếu có công thức tính
            if (['PHU_CAP', 'KHAU_TRU'].includes(values.benefitType)) {
                payload.formulaType = values.formulaType;
                payload.formulaValue = values.formulaValue;
            }

            const res =  await benefitService.create(payload);

            console.log(res)
            // if (!res.ok) throw new Error('Tạo thất bại');
            message.success('Tạo phúc lợi thành công!');
            setOpen(false);
            form.resetFields();
            onCreated?.();

        } catch (err) {
            console.log('Full error object:', err);
            console.log('Response data:', err.response?.data);
            console.error(err);

            let errorMsg = 'Đã có lỗi xảy ra.';

            if (err.response?.data) {
                const data = err.response.data;
                errorMsg = data.message || 'Lỗi không xác định từ server.';
            } else if (err.request) {
                errorMsg = 'Không nhận được phản hồi từ máy chủ.';
            } else {
                errorMsg = err.message || 'Tạo phúc lợi thất bại.';
            }


            setError(errorMsg);          // nếu bạn muốn hiển thị ở UI
            message.error(errorMsg);     // thông báo popup
        }


    };

    return (
        <div>
            <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => setOpen(true)}
                style={{
                    borderRadius: 14,
                    padding: '23px 20px',
                    display: 'flex',
                    alignItems: 'center',
                    gap: 16,
                    cursor: 'pointer',
                    boxShadow: '11px 4px 14px 0px rgba(0, 0, 0, 0.12)',
                    backgroundColor: '#3f861e',
                    border: 'none',
                    fontWeight: 600,
                    fontSize: '18px',
                    color: '#fff'
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
                        label="Loại phúc lợi"
                        name="benefitType"
                        rules={[{ required: true, message: 'Vui lòng chọn loại phúc lợi' }]}
                    >
                        <Select placeholder="Chọn loại phúc lợi ">
                            <Select.Option value={"SU_KIEN"}>Sự kiện</Select.Option>
                            <Select.Option value={"PHU_CAP"}>Phụ cấp</Select.Option>
                            <Select.Option value={"KHAU_TRU"}>Khấu trừ</Select.Option>
                        </Select>
                    </Form.Item>

                    {['PHU_CAP', 'KHAU_TRU'].includes(benefitType) && (
                        <Form.Item label="Cách tính">
                            <Input.Group compact>
                                <Form.Item
                                    name="formulaType"
                                    noStyle
                                    rules={[{ required: true, message: 'Chọn cách tính' }]}
                                >
                                    <Select placeholder="Chọn cách tính" style={{ width: '50%' }}>
                                        <Select.Option value="AMOUNT">Số tiền cố định</Select.Option>
                                        <Select.Option value="PERCENTAGE">Theo phần trăm</Select.Option>
                                    </Select>
                                </Form.Item>

                                {formulaType && (
                                    <Form.Item
                                        name="formulaValue"
                                        noStyle
                                        rules={[{ required: true, message: 'Nhập giá trị' }]}
                                    >
                                        <InputNumber
                                            min={0}
                                            style={{ width: '50%', textAlign: 'right' }}
                                            placeholder={formulaType === 'AMOUNT' ? 'Nhập số tiền' : 'Nhập phần trăm'}
                                            addonAfter={formulaType === 'AMOUNT' ? 'VND' : '%'}
                                            formatter={(value) =>
                                                formulaType === 'AMOUNT'
                                                    ? `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',') // thêm dấu phẩy
                                                    : value
                                            }
                                            parser={(value) =>
                                                value?.replace(/[^\d.]/g, '') // loại bỏ ký tự không phải số hoặc dấu .
                                            }

                                            stringMode
                                        />
                                    </Form.Item>
                                )}
                            </Input.Group>
                        </Form.Item>
                    )}




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

                    <Form.Item label="Đặc tả chi tiết" name="detail">
                        <Input.TextArea rows={3} placeholder="Nhập đặc tả (không bắt buộc)" />
                    </Form.Item>
                </Form>
            </Modal>

        </div>
    )
}

export default BenefitCreateModal;

