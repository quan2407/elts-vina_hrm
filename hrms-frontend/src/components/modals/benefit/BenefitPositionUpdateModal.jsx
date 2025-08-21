import React, { useEffect, useMemo, useRef } from 'react';
import { Modal, Form, Input, InputNumber, Select } from 'antd';

const { Option } = Select;
const MAX_AMOUNT = 10000000;

const UpdateProgramModal = ({ open, onCancel, onSubmit, initialData }) => {
    const [form] = Form.useForm();

    // Snapshot giá trị ban đầu khi mở modal
    const initialSnapshotRef = useRef({ type: 'AMOUNT', value: 0 });
    const prevTypeRef = useRef(null);

    const init = useMemo(
        () => ({
            positionName: initialData?.positions?.positionName || '',
            formulaType: initialData?.positions?.formulaType || 'AMOUNT',
            formulaValue: initialData?.positions?.formulaValue ?? 0,
        }),
        [initialData]
    );

    const formulaType = Form.useWatch('formulaType', form);

    // Set form và snapshot khi mở modal
    useEffect(() => {
        if (open && initialData) {
            const type = initialData.positions.formulaType || 'AMOUNT';
            const value = Number(initialData.positions.formulaValue ?? 0);

            initialSnapshotRef.current = { type, value };
            prevTypeRef.current = type;

            form.setFieldsValue({
                positionName: initialData.positions.positionName,
                formulaType: type,
                formulaValue: value,
            });
        }
    }, [open, initialData, form]);

    // Đổi "Cách tính": khác loại ban đầu => set 0; quay lại loại ban đầu => khôi phục giá trị gốc
    useEffect(() => {
        if (!open || !formulaType) return;

        if (prevTypeRef.current && formulaType !== prevTypeRef.current) {
            if (formulaType === initialSnapshotRef.current.type) {
                form.setFieldsValue({ formulaValue: initialSnapshotRef.current.value });
            } else {
                form.setFieldsValue({ formulaValue: 0 });
            }
            prevTypeRef.current = formulaType;
        }
    }, [formulaType, open, form]);

    const handleOk = () => {
        form
            .validateFields()
            .then((values) => {
                onSubmit({
                    benefitId: initialData.id,
                    positionId: initialData.positions.positionId,
                    ...values,
                });
                form.resetFields();
            })
            .catch(() => {});
    };

    // ====== Chặn nhập ký tự (gõ/dán) ======
    const handleKeyDown = (e) => {
        const ctrlCmd = e.ctrlKey || e.metaKey;
        const controlKeys = [
            'Backspace','Delete','Tab','Escape','Enter',
            'ArrowLeft','ArrowRight','Home','End'
        ];
        if (controlKeys.includes(e.key)) return;
        if (ctrlCmd && ['a','c','v','x','z','y'].includes(e.key.toLowerCase())) return;

        if (formulaType === 'AMOUNT') {
            if (!/^\d$/.test(e.key)) e.preventDefault();
        } else {
            if (e.key === '.') {
                const hasDot = String(e.currentTarget.value || '').includes('.');
                if (hasDot) e.preventDefault();
                return;
            }
            if (!/^\d$/.test(e.key)) e.preventDefault();
        }
    };

    const handlePaste = (e) => {
        const text = e.clipboardData.getData('text') ?? '';
        if (formulaType === 'AMOUNT') {
            if (!/^\d+$/.test(text)) e.preventDefault();
        } else {
            if (!/^\d*\.?\d*$/.test(text) || (text.match(/\./g) || []).length > 1) {
                e.preventDefault();
            }
        }
    };

    return (
        <Modal
            title="Cập nhật giá trị phúc lợi"
            open={open}
            onCancel={() => {
                form.resetFields();
                onCancel();
            }}
            onOk={handleOk}
            okText="Lưu"
            cancelText="Hủy"
            destroyOnClose
        >
            <Form form={form} layout="vertical" initialValues={init}>
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
                        <Option value="AMOUNT">Số tiền cố định</Option>
                        <Option value="PERCENTAGE">Tính theo phần trăm</Option>
                    </Select>
                </Form.Item>

                {formulaType && (
                    <Form.Item
                        name="formulaValue"
                        label="Giá trị"
                        rules={[
                            { required: true, message: 'Vui lòng nhập giá trị!' },
                            () => ({
                                validator(_, v) {
                                    const num = v === '' || v === null || v === undefined ? NaN : Number(v);
                                    if (Number.isNaN(num)) {
                                        return Promise.reject(new Error('Vui lòng nhập giá trị hợp lệ!'));
                                    }
                                    if (num <= 0) {
                                        // Không cho lưu khi = 0 (và tất nhiên <0)
                                        return Promise.reject(new Error('Giá trị phải lớn hơn 0'));
                                    }

                                    const type = form.getFieldValue('formulaType');
                                    if (type === 'PERCENTAGE') {
                                        // 1 <= value <= 500
                                        if (num < 1 || num > 500) {
                                            return Promise.reject(new Error('Giá trị (%) phải từ 1 đến 500'));
                                        }
                                    } else {
                                        // AMOUNT: tối đa 10,000,000
                                        if (num > MAX_AMOUNT) {
                                            return Promise.reject(new Error('Giá trị tối đa là 10000000'));
                                        }
                                    }
                                    return Promise.resolve();
                                },
                            }),
                        ]}
                        // validateTrigger="onBlur" // bật nếu muốn báo lỗi ngay khi rời ô
                    >
                        <InputNumber
                            style={{ width: '100%' }}
                            // KHÔNG đặt min để tránh tự nhảy sang 1
                            step={formulaType === 'PERCENTAGE' ? 0.1 : 1000}
                            formatter={(value) =>
                                formulaType === 'AMOUNT'
                                    ? (value ?? '').toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
                                    : value
                            }
                            parser={(value) => {
                                const raw = value || '';
                                if (formulaType === 'AMOUNT') {
                                    return raw.replace(/[^\d]/g, '');
                                }
                                return raw.replace(/[^\d.]/g, '').replace(/(\..*)\./g, '$1'); // tối đa 1 dấu .
                            }}
                            inputMode={formulaType === 'AMOUNT' ? 'numeric' : 'decimal'}
                            addonAfter={formulaType === 'AMOUNT' ? 'VND' : '%'}
                            stringMode
                            onKeyDown={handleKeyDown}
                            onPaste={handlePaste}
                        />
                    </Form.Item>
                )}
            </Form>
        </Modal>
    );
};

export default UpdateProgramModal;
