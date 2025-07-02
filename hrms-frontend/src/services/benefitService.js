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
    update: (updatedBenefit, benefitId) => axiosClient.put(`hr/benefits/${benefitId}`
        , updatedBenefit),
    changeStatus: (benefitId) => axiosClient.patch(`benefit/${benefitId}`),
    getByKeyword: (keyword) => axiosClient.get(`/benefit/keyword/${keyword}`)
}

export default benefitService;