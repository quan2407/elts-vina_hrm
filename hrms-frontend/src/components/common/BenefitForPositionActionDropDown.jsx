import { Dropdown, Button } from 'antd';
import { EditOutlined, EyeOutlined, DeleteOutlined, DownOutlined, UnorderedListOutlined} from '@ant-design/icons';

const BenefitForPositionActionDropDown = ({ benefitType,onView, onEdit, onDelete, onDetails, positionName }) => {
    const handleMenuClick = (e) => {
        if (e.key === 'view') onView?.();
        else if (e.key === 'edit') onEdit?.();
        else if (e.key === 'delete') onDelete?.();
        else if (e.key === 'details') onDetails?.();
    };

    const items = [
        {
            key: 'details',
            icon: <UnorderedListOutlined />,
            label: `Xem danh sách nhân viên đăng kí`,
        },
        ...(benefitType !== "SU_KIEN"
            ? [
                {
                    key: 'edit',
                    icon: <EditOutlined />,
                    label: `Cập nhật giá trị phúc lợi cho ${positionName}`,
                },
            ]
            : []),
        {
            key: 'delete',
            icon: <DeleteOutlined />,
            label: `Hủy đăng kí vị trí ${positionName} khỏi phúc lợi`,
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
