import { Dropdown, Button } from 'antd';
import { EditOutlined, EyeOutlined, DeleteOutlined, DownOutlined } from '@ant-design/icons';

const ActionDropdown = ({ onView, onEdit, onDelete }) => {
    const handleMenuClick = (e) => {
        if (e.key === 'view') onView?.();
        else if (e.key === 'edit') onEdit?.();
        else if (e.key === 'delete') onDelete?.();
    };

    const items = [
        {
            key: 'view',
            icon: <EyeOutlined />,
            label: 'Xem',
        },
        {
            key: 'edit',
            icon: <EditOutlined />,
            label: 'Cập nhật',
        },
        {
            key: 'delete',
            icon: <DeleteOutlined />,
            label: 'Xóa',
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

export default ActionDropdown;
