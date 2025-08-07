import { Dropdown, Button } from 'antd';
import {
    EditOutlined,
    EyeOutlined,
    DeleteOutlined,
    DownOutlined,
    UnorderedListOutlined,
    SettingOutlined
} from '@ant-design/icons';

const BenefitForPositionActionDropDown = ({ onView, onEdit, onDelete, onDetails }) => {
    const handleMenuClick = (e) => {
        if (e.key === 'view') onView?.();
        else if (e.key === 'edit') onEdit?.();
        else if (e.key === 'delete') onDelete?.();
        else if (e.key === 'details') onDetails?.();
    };

    const items = [
        // {
        //     key: 'details',  // Mới thêm mục Details
        //     icon: <SettingOutlined />,
        //     label: 'Điều chỉnh lương cơ bản',
        // },
        //
        // {
        //     key: 'view',
        //     icon: <EyeOutlined />,
        //     label: 'Xem thông tin chi tiết nhân viên',
        // },
        // {
        //     key: 'edit',
        //     icon: <EditOutlined />,
        //     label: 'Cập nhật',
        // },
        {
            key: 'delete',
            icon: <DeleteOutlined />,
            label: 'Hủy đăng kí phúc lợi',
            danger: true,
        },

    ];

    return (
        <Dropdown
            menu={{
                items,
                onClick: handleMenuClick,
            }}
        >
            <Button>
                Hành động <DownOutlined />
            </Button>
        </Dropdown>
    );
};

export default BenefitForPositionActionDropDown;
