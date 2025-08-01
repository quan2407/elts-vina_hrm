import axiosClient from "./axiosClient";

const benefitService = {
    getAll: ({page, size, ...filters}) => axiosClient.get("/hr/benefits", {

        params: {
            pageNumber: page,
            pageSize: size
            , ...filters
        }

    }),

    create: (createdBenefit) => axiosClient.post("/hr/benefits", createdBenefit),
    update: (updatedBenefit, benefitId) => axiosClient.patch(`/hr/benefits/${benefitId}`
        , updatedBenefit),
    delete: (benefitId) => axiosClient.delete(`/hr/benefits/${benefitId}`),
    changeStatus: (benefitId) => axiosClient.patch(`benefit/${benefitId}`),
    getByKeyword: (keyword) => axiosClient.get(`/benefit/keyword/${keyword}`)
}

export default benefitService;