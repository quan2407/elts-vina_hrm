package sep490.com.example.hrms_backend.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.benefit.BenefitDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;
import sep490.com.example.hrms_backend.entity.Benefit;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.service.BenefitService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BenefitServiceImpl implements BenefitService {

    @Autowired
    private BenefitRepository benefitRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @Override
    public BenefitResponse getAllBenefits(String username, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,
                                          String title, String description, Boolean isActive, LocalDate startDate, LocalDate endDate, Integer minParticipants, Integer maxParticipants) {



        // 1. Tao đoi tuong sap xep (theo field va huong sap xep: asc/desc)
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // 2. Tao doi tuong phan trang (Pageable) dua tren so trang, kich thuoc trang va sap xep
        Pageable pageable = PageRequest.of(pageNumber  - 1 , pageSize, sortByAndOrder);

        //3. Goi repository voi dieu kien loc dong (specification)
        Page<Benefit> benefitPage = benefitRepository.findAll((root, query, cb ) -> {
                List<Predicate> predicates = new ArrayList<>();

        //3.1 lOC Theo title
            if(title != null && !title.isEmpty()){
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }


        //3.2. Loc theo description
            if(description != null && !description.isEmpty()){
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            //3.3. Loc theo trang thai (isActive)
            if(isActive != null){
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

        //3.4. Loc tu startDate
            if(startDate != null){
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), startDate));
            }

        //3.5. Loc den endDate
            if(endDate != null){
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), endDate));
            }

        //3.6 Loc so nguoi tham gia >=
            if(minParticipants != null){
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxParticipants"), minParticipants));
            }
            //3.7  Loc so nguoi tham gia <=
            if(maxParticipants != null){
                predicates.add(cb.lessThanOrEqualTo(root.get("maxParticipants"), maxParticipants));
            }

            //3.8 Tra ve tat ca dieu kien AND lai
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        List<Benefit> benefits = benefitPage.getContent();




        //Kiem tra xem danh sach co benefit khong
        if (benefits.isEmpty()) {
            throw new HRMSAPIException("No Benefit is Exist. Please Add Benefit First.");
        }

        //Neu oke thi chuyen ve DTO
        //List<BenefitDTO> benefitDTOList = modelMapper.map(benefits, List.class); //ko nen dung
        List<BenefitDTO> benefitDTOList = benefits.stream()
                .map(benefit -> modelMapper.map(benefit, BenefitDTO.class)).toList();

        //Tao doi tuong BenefitResponse va luu nhung thong tin da dc dinh nghia trong BenefitResponse
        BenefitResponse benefitResponse = new BenefitResponse();
        benefitResponse.setContent(benefitDTOList);
        benefitResponse.setPageNumber(benefitPage.getNumber());
        benefitResponse.setPageSize(benefitPage.getSize());
        benefitResponse.setTotalElements(benefitPage.getTotalElements());
        benefitResponse.setTotalPages(benefitPage.getTotalPages());
        benefitResponse.setLastPage(benefitPage.isLast());

        return benefitResponse;
    }

    @Transactional
    @Override
    public BenefitDTO addBenefit(BenefitDTO benefitDTO) {
        //Step1: DTO -> Entiry
        Benefit benefit = modelMapper.map(benefitDTO, Benefit.class);

        //Step2: check existed in DB
        Benefit benefitFromDb = benefitRepository.findByTitle(benefit.getTitle());
        if (benefitFromDb != null) {
            throw new HRMSAPIException("Benefit with title " + benefit.getTitle() + " is already existed.");
        }

        //Step3: if not existed => save benefit
        Benefit savedCategory = benefitRepository.save(benefit);

        //Step 4: return DTO
        return modelMapper.map(savedCategory, BenefitDTO.class);
    }

    @Transactional
    @Override
    public BenefitDTO updateBenefit(BenefitDTO benefitDTO, Long benefitId) {
        //check exist
        Benefit benefitFromDb = benefitRepository.findById(benefitId)
                .orElseThrow(() -> new HRMSAPIException("Benefit with id " + benefitId + " is not existed."));

        //DTO--> model
        Benefit benefit = modelMapper.map(benefitDTO, Benefit.class);

        //save to db
        Benefit savedBenefit = benefitRepository.save(benefit);

        //return DTO;
        return modelMapper.map(savedBenefit, BenefitDTO.class);


    }

    @Transactional
    @Override
    public BenefitDTO updateInactiveStatus(Long id, boolean isActive) {
        Benefit benefit = benefitRepository.findById(id)
                .orElseThrow(() -> new HRMSAPIException("Benefit with id " + id + " is not existed."));

        benefit.setIsActive(isActive);
        Benefit updated = benefitRepository.save(benefit);

        return modelMapper.map(updated, BenefitDTO.class);
    }

    @Transactional
    @Override
    public BenefitResponse searchBenefitByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber  - 1 , pageSize, sortByAndOrder);
        Page<Benefit> benefitPage = benefitRepository.findByTitleLikeIgnoreCase('%' + keyword + '%', pageable);

        List<Benefit> benefits = benefitPage.getContent();
        if (benefits.isEmpty()) {
            throw new ResourceNotFoundException("Benefit with keyword ." + keyword + " not found. Please add Benefit with keyword .");
        }
        List<BenefitDTO> benefitsByKeywordDTOS = benefits.stream()
                .map(benefit -> modelMapper.map(benefit, BenefitDTO.class)).toList();

        BenefitResponse benefitResponse = new BenefitResponse();
        benefitResponse.setContent(benefitsByKeywordDTOS);
        benefitResponse.setPageNumber(benefitPage.getNumber());
        benefitResponse.setPageSize(benefitPage.getSize());
        benefitResponse.setTotalElements(benefitPage.getTotalElements());
        benefitResponse.setTotalPages(benefitPage.getTotalPages());
        benefitResponse.setLastPage(benefitPage.isLast());
        return benefitResponse;
    }


}
