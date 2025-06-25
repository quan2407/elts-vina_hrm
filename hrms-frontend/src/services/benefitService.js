import axiosClient from "./axiosClient";

const benefitService = {
    getAll: () => axiosClient.get("/benefit"),
    create: (createdBenefit) => axiosClient.post("/benefit", createdBenefit),
    update: (updatedBenefit, benefitId) => axiosClient.put(`/benefit/${benefitId}`
        , updatedBenefit),
    changeStatus: (benefitId) => axiosClient.patch(`benefit/${benefitId}`),
    getByKeyword: (keyword) => axiosClient.get(`/benefit/keyword/${keyword}`)
}

export default benefitService;