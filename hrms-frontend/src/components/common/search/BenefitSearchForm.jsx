import React, { useRef } from 'react';
import {
    ProForm,
    ProFormText,
    ProFormDateRangePicker,
    ProFormSelect,
    ProFormDigitRange,
} from '@ant-design/pro-components';
import { Button, Divider, Select, Space } from 'antd';
import { ConfigProvider } from 'antd';
import viVN from 'antd/locale/vi_VN';
import dayjs from 'dayjs';
import '../../../styles/BenefitSearchForm.css'

const QuickFilters = ({ onQuickFilter }) => {
    return (
        <Space style={{ marginBottom: 16 }}>
            <Select
                placeholder="Trạng thái"
                onChange={(value) => onQuickFilter({ isActive: value })}
                allowClear
                options={[
                    { label: 'Đang hoạt động', value: true },
                    { label: 'Ngừng hoạt động', value: false },
                ]}
                style={{ width: 160 }}
            />
            <Select
                placeholder="Loại phúc lợi"
                onChange={(value) => onQuickFilter({ benefitType: value })}
                allowClear
                options={[
                    { label: 'Sự kiện', value: 'SU_KIEN' },
                    { label: 'Phụ cấp', value: 'PHU_CAP' },
                    { label: 'Khấu trừ', value: 'KHAU_TRU' },
                ]}
                style={{ width: 160 }}
            />
            <Button onClick={() => onQuickFilter({})}>Xóa bộ lọc</Button>
        </Space>
    );
};

const BenefitSearchForm = ({ onSearch }) => {
    const formRef = useRef();
    const handleFinish = (values) => {
        console.log('Received values of form: ', values);
        const filters = {
            ...values,
            minParticipants: values.maxParticipants?.[0],
            maxParticipants: values.maxParticipants?.[1],
            startDate: values.dateRange?.[0] ? dayjs(values.dateRange[0]).format("YYYY-MM-DD") : undefined,
            endDate: values.dateRange?.[1] ? dayjs(values.dateRange[1]).format("YYYY-MM-DD") : undefined
        };
        delete filters.dateRange;
        console.log("Filters gửi lên cha:", filters);
        onSearch(filters); // Gửi filter lên component cha
    };

    return (
        <ConfigProvider locale={viVN}>
            <ProForm
                formRef={formRef}
                submitter={false}
                grid
                layout="vertical"
                rowProps={{ gutter: [16, 8], wrap: false, align: 'top' }}
                onFinish={handleFinish}
                className="benefit-search-form"   // ⬅ để scope CSS
            >
                <ProFormText
                    colProps={{ xs: 24, lg: 4 }}
                    name="title"
                    label="Tiêu đề"
                    placeholder="Nhập tiêu đề"
                    fieldProps={{ size: 'large' }}   // ⬅ to hơn theo AntD
                />

                <ProFormText
                    colProps={{ xs: 24, lg: 4 }}
                    name="description"
                    label="Mô tả"
                    placeholder="Nhập mô tả"
                    fieldProps={{ size: 'large' }}
                />

                <ProFormSelect
                    colProps={{ xs: 24, lg: 4 }}
                    name="benefitType"
                    label="Loại phúc lợi"
                    options={[
                        { label: 'Sự kiện', value: 'SU_KIEN' },
                        { label: 'Phụ cấp', value: 'PHU_CAP' },
                        { label: 'Khấu trừ', value: 'KHAU_TRU' },
                    ]}
                    allowClear
                    placeholder="Chọn loại phúc lợi"
                    fieldProps={{ size: 'large' }}
                />

                <ProFormSelect
                    colProps={{ xs: 24, lg: 4 }}
                    name="isActive"
                    label="Trạng thái"
                    options={[
                        { label: 'Đang hoạt động', value: true },
                        { label: 'Ngừng hoạt động', value: false },
                    ]}
                    allowClear
                    placeholder="Chọn trạng thái"
                    fieldProps={{ size: 'large' }}
                />

                <ProForm.Item
                    label=" " colon={false}
                    colProps={{ xs: 24, lg: 8 }}
                    style={{ marginBottom: 0, paddingTop: 2 }}
                >
                    <div
                        style={{
                            display: 'flex',
                            justifyContent: 'flex-end',
                            gap: 10,
                            whiteSpace: 'nowrap',
                        }}
                    >
                        <Button size="large" onClick={() => formRef.current?.resetFields()}>
                            Làm lại
                        </Button>
                        <Button size="large" type="primary" htmlType="submit">
                            Gửi đi
                        </Button>
                        <Button
                            size="large"
                            onClick={() => {
                                formRef.current?.resetFields();
                                onSearch({});
                            }}
                        >
                            Xem toàn bộ phúc lợi
                        </Button>
                    </div>
                </ProForm.Item>
            </ProForm>


        </ConfigProvider>
    );
};

export default BenefitSearchForm;