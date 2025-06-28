import React from 'react';
import {
    ProForm,
    ProFormText,
    ProFormDateRangePicker,
    ProFormSelect,
    ProFormDigitRange,
} from '@ant-design/pro-components';
import { Button } from 'antd';
import { ConfigProvider } from 'antd';
import viVN from 'antd/locale/vi_VN';

const BenefitSearchForm = ({ onSearch }) => {
    const handleFinish = (values) => {
        const filters = {
            ...values,
            startDate: values.dateRange?.[0],
            endDate: values.dateRange?.[1],
        };
        delete filters.dateRange;

        onSearch(filters); // Gửi filter lên component cha
    };

    return (
        <ConfigProvider locale={viVN}>
        <ProForm
            submitter={{
                render: (_, dom) => (
                    <div style={{ textAlign: 'right' }}>
                        {dom}
                        <Button htmlType="reset" style={{ marginLeft: 8 }}>Reset</Button>
                    </div>
                ),
            }}
            layout="vertical"
            onFinish={handleFinish}
        >
            <ProFormText name="title" label="Title" placeholder="Nhập tiêu đề" />
            <ProFormText name="description" label="Description" placeholder="Nhập mô tả" />
            <ProFormDateRangePicker name="dateRange" label="Start Date - End Date" />
            <ProFormSelect
                name="isActive"
                label="Trạng thái"
                options={[
                    { label: 'ACTIVE', value: true },
                    { label: 'INACTIVE', value: false },
                ]}
                allowClear
                placeholder="Chọn trạng thái"
            />
            <ProFormDigitRange name="maxParticipants" label="Số lượng người tham gia" separator="~" />
        </ProForm>
        </ConfigProvider>
    );
};

export default BenefitSearchForm;
