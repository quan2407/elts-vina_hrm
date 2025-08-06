import React, { useEffect } from 'react';
import {Modal, Form, Input, DatePicker, InputNumber, Select} from 'antd';
import dayjs from 'dayjs';
import { Switch } from 'antd';

const { TextArea } = Input;

const UpdateProgramModal = ({ open, onCancel, onSubmit, initialData }) => {
    const [form] = Form.useForm();

    console.log("Đây là initialData", initialData)
    // 🧠 Watch giá trị của formulaType
    const formulaType = Form.useWatch('formulaType', form);


    // Khi mở modal thì set giá trị mặc định từ initialData
    useEffect(() => {
        if (open && initialData) {
            form.setFieldsValue({


                // detail: initialData.detail || ''
                positionName: initialData.positions.positionName,
                formulaType: initialData.positions.formulaType,
                formulaValue: initialData.positions.formulaValue,
            });
        }
    }, [open, initialData, form]);



    const handleOk = () => {
        form
            .validateFields()
            .then(values => {
                onSubmit({
                    benefitId: initialData.id,
                    positionId: initialData.positions.positionId,
                    ...values,
                });
                form.resetFields();
            })
            .catch(info => {
                console.log('Validation Failed:', info);
            });
    };

    return (
        <Modal
            title="Cập nhật giá trị phụ cấp"
            open={open}
            onCancel={() => {
                form.resetFields();
                onCancel();
            }}
            onOk={handleOk}
            okText="Lưu"
            cancelText="Hủy"
        >
            <Form form={form} layout="vertical"

            >

                <Form.Item
                    name="positionName"
                    label="Tên vị trí"
                    rules={[{ required: true, message: 'Vui lòng nhập tiêu đề!' }]}
                >
                    <Input placeholder="Nhập tiêu đề" disabled />
                </Form.Item>


                <Form.Item
                    label="Cách tính"
                    name="formulaType"
                    rules={[{ required: true, message: 'Vui lòng chọn loại phúc lợi' }]}
                >
                    <Select placeholder="Chọn loại phúc lợi">
                        <Select.Option value="AMOUNT">Số tiền cố định</Select.Option>
                        <Select.Option value="PERCENTAGE">Tính theo phần trăm</Select.Option>
                    </Select>
                </Form.Item>

                {/* 🎯 Hiển thị ô nhập giá trị phụ thuộc vào formulaType */}
                {formulaType && (
                    <Form.Item
                        name="formulaValue"
                        label="Giá trị"
                        rules={[{ required: true, message: 'Vui lòng nhập giá trị!' }]}
                    >
                        <InputNumber
                            min={0}
                            step={formulaType === 'PERCENTAGE' ? 0.1 : 1000} // bước nhảy
                            style={{ width: '100%' }}
                            formatter={(value) =>
                                formulaType === 'AMOUNT'
                                    ? `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',') // thêm dấu phẩy
                                    : value
                            }
                            parser={(value) =>
                                value?.replace(/[^\d.]/g, '') // loại bỏ ký tự không phải số hoặc dấu .
                            }
                            addonAfter={formulaType === 'AMOUNT' ? 'VND' : '%'}
                            stringMode // đảm bảo số lớn không bị làm tròn
                        />
                    </Form.Item>
                )}


            </Form>
        </Modal>
    );
};

export default UpdateProgramModal;
