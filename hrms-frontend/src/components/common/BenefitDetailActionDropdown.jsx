import { Dropdown, Button } from 'antd';
import { EditOutlined, EyeOutlined, DeleteOutlined, DownOutlined, UnorderedListOutlined} from '@ant-design/icons';

const BenefitDetailActionDropdown = ({ onView, onEdit, onDelete, onDetails }) => {
    const handleMenuClick = (e) => {
        if (e.key === 'view') onView?.();
        else if (e.key === 'edit') onEdit?.();
        else if (e.key === 'delete') onDelete?.();
        else if (e.key === 'details') onDetails?.();
    };

    const items = [
        {
            key: 'details',  // Mới thêm mục Details
            icon: <UnorderedListOutlined />,
            label: 'Xem vị trí được áp dụng ',
        },

        {
            key: 'view',
            icon: <EyeOutlined />,
            label: 'Xem thông tin thêm',
        },
        {
            key: 'edit',
            icon: <EditOutlined />,
            label: 'Cập nhật phúc lợi',
        },
        {
            key: 'delete',
            icon: <DeleteOutlined />,
            label: 'Xóa phúc lợi',
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

export default BenefitDetailActionDropdown;
