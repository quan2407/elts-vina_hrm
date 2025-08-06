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
    getPositionRegisterationDetail: ({page, size, ...filters}, benefitId) => axiosClient.get(`hr/benefits/${benefitId}`, {
        params: {
            pageNumber: page,
            pageSize: size
            , ...filters
        }
    },
    ),
    getById: (id) => axiosClient.get(`/hr/benefit/${id}`),
    getAvailablePositions: (benefitId) => axiosClient.get(`/hr/benefits/${benefitId}/available-positions`),
    assignPositionsToBenefit: (benefitPositions) => axiosClient.post(`/hr/benefits/assign`, benefitPositions ),
    unassignPositionsFromBenefit: (benefitId,positionId) => axiosClient.delete(`/hr/benefits/unassign/benefit/${benefitId}/position/${positionId}` ),
    updateFormula: (updatedFormula) => axiosClient.put("/hr/benefits/formula", updatedFormula),
    getEmployeeByPositionAndBenefit: ( benefitId, positionId, {page, size, ...filters},) => axiosClient.get(`/hr/benefit/${benefitId}/position/${positionId}`, {
        params: {
            pageNumber: page,
            pageSize: size
            , ...filters
        }
    }),

    searchUnregisteredEmployees: ({benefitId, positionId, keyword}) => axiosClient.get(`hr/benefits/search-unregistered`,{
        params: {
            benefitId,
            positionId,
            keyword
        }
    }),
    quickRegister: (registeredEmployee) => axiosClient.post("/hr/benefits/quick-register", registeredEmployee),

    // updateOriginalSalary: (updatedSalary) => axiosClient.put("/employees/update-original-salary", updatedSalary),


    changeStatus: (benefitId) => axiosClient.patch(`benefit/${benefitId}`),
    getByKeyword: (keyword) => axiosClient.get(`/benefit/keyword/${keyword}`)
}

export default benefitService;