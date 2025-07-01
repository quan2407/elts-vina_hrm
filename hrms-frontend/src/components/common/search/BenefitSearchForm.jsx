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
import dayjs from 'dayjs';

const BenefitSearchForm = ({ onSearch }) => {
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
            submitter={{
                render: (_, dom) => (
                    <div style={{ textAlign: 'right' }}>
                        {dom}
                        <Button htmlType="reset" style={{ marginLeft: 8 }}>Xem toàn bộ phúc lợi</Button>
                        {/*<Button*/}
                        {/*    key="viewAll"*/}
                        {/*    onClick={() => {*/}
                        {/*        // Đặt lại bộ lọc và tải tất cả dữ liệu*/}
                        {/*        setFilters({});*/}
                        {/*        setPageNumber(1);*/}
                        {/*    }}>View All Benefits*/}
                        {/*</Button>/*/}

                    </div>
                ),
            }}
            layout="vertical"
            onFinish={handleFinish}
        >
            <ProFormText name="title" label="Tiêu đề" placeholder="Nhập tiêu đề" />
            <ProFormText name="description" label="Mô tả" placeholder="Nhập mô tả" />
            <ProFormDateRangePicker name="dateRange" label="Ngày bắt đầu - Ngày kết thúc" />
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
