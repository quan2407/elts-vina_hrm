package sep490.com.example.hrms_backend.service.impl;

import jdk.jfr.Category;
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

import java.util.List;

@Service
public class BenefitServiceImpl implements BenefitService {

    @Autowired
    private BenefitRepository benefitRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @Override
    public BenefitResponse getAllBenefits(String username, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //Khoi tao doi tuong sort
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //Khoi tao doi tuong Pageable
        Pageable pageable = PageRequest.of(pageNumber  - 1 , pageSize, sortByAndOrder);

        //Luu danh sách co kieu Page benefit bang phuogn thuc findAll(Pageable pageable) da dc ke thua (JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID>)
        Page benefitPage = benefitRepository.findAll(pageable);

        //Lay ve danh sach benefit bang get.content()
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
