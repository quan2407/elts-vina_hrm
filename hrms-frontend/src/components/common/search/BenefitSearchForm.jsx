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
            {/*<QuickFilters
                onQuickFilter={(quickFilter) => {
                    onSearch(quickFilter);
                }}
            />*/}
            <ProForm
                formRef={formRef}
                submitter={{
                    render: (_, dom) => (
                        <div style={{ textAlign: 'right' }}>
                            {dom}
                            <Button
                                style={{ marginLeft: 8 }}
                                onClick={() => {
                                    formRef.current?.resetFields();  // reset toàn bộ form
                                    onSearch({});                    // gửi filter rỗng lên cha
                                }}
                            >
                                Xem toàn bộ phúc lợi
                            </Button>
                        </div>
                    ),
                }}
                grid={true}
                layout="vertical"
                onFinish={handleFinish}
            >
                <ProFormText
                    colProps={{ span: 4 }}
                    name="title"
                    label="Tiêu đề"
                    placeholder="Nhập tiêu đề"
                />
                <ProFormText
                    colProps={{ span: 4 }}
                    name="description"
                    label="Mô tả"
                    placeholder="Nhập mô tả"
                />
                <ProFormSelect
                    colProps={{ span: 4 }}
                    name="benefitType"
                    label="Loại phúc lợi"
                    options={[
                        { label: 'Sự Kiện', value: "SU_KIEN" },
                        { label: 'Phụ cấp', value: "PHU_CAP" },
                        { label: 'Khấu trừ', value: 'KHAU_TRU' }
                    ]}
                    allowClear
                    placeholder="Chọn loại phúc lợi"
                />
                <ProFormDateRangePicker
                    colProps={{ span: 4 }}
                    name="dateRange"
                    label="Ngày bắt đầu - Ngày kết thúc"
                />
                <ProFormSelect
                    colProps={{ span: 4 }}
                    name="isActive"
                    label="Trạng thái"
                    options={[
                        { label: 'Đang hoạt động', value: true },
                        { label: 'Ngừng hoạt động', value: false },
                    ]}
                    allowClear
                    placeholder="Chọn trạng thái"
                />
                <ProFormDigitRange
                    colProps={{ span: 4 }}
                    name="maxParticipants"
                    label="Số lượng người tham gia"
                    separator="~"
                />
            </ProForm>
            <Divider style={{ margin: '16px 0' }} />
        </ConfigProvider>
    );
};

export default BenefitSearchForm;