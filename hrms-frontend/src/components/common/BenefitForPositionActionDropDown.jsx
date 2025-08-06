import { Dropdown, Button } from 'antd';
import { EditOutlined, EyeOutlined, DeleteOutlined, DownOutlined, UnorderedListOutlined} from '@ant-design/icons';

const BenefitForPositionActionDropDown = ({ onView, onEdit, onDelete, onDetails, positionName }) => {
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
            label: `Áp dụng cho nhân viên ở vị trí ${positionName}`,
        },

        // {
        //     key: 'view',
        //     icon: <EyeOutlined />,
        //     label: 'Xem thông tin thêm',
        // },
        {
            key: 'edit',
            icon: <EditOutlined />,
            label: `Cập nhật lương cho ${positionName}`,
        },
        {
            key: 'delete',
            icon: <DeleteOutlined />,
            label: `Xóa vị trí ${positionName} khỏi phúc lợi`,
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
