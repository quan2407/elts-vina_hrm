const getBenefitTypeDisplay = (type) => {
    if (!type) return '';

    const upperCaseType = type.toUpperCase();

    if (upperCaseType === 'KHAU_TRU') {
        return 'Khấu Trừ';
    }
    if (upperCaseType === 'PHU_CAP') {
        return 'Phụ Cấp';
    }

    if (upperCaseType === "SU_KIEN") {
        return "Sự Kiện";
    }


}

export default getBenefitTypeDisplay;