package sep490.com.example.hrms_backend.service.impl;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.benefit.BenefitDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;
import sep490.com.example.hrms_backend.dto.benefit.PatchBenefitDTO;
import sep490.com.example.hrms_backend.entity.Benefit;
import sep490.com.example.hrms_backend.entity.BenefitPosition;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.Position;
import sep490.com.example.hrms_backend.enums.BenefitType;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.mapper.BenefitMapper;
import sep490.com.example.hrms_backend.repository.BenefitPositionRepository;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.repository.PositionRepository;
import sep490.com.example.hrms_backend.service.BenefitService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BenefitServiceImpl implements BenefitService {


    private final BenefitRepository benefitRepository;


    private final ModelMapper modelMapper;

    private final BenefitMapper benefitMapper;

    private final EmployeeRepository employeeRepository;

    private final PositionRepository positionRepository;

    private final BenefitPositionRepository benefitPositionRepository;

    //Tested
    @Transactional
    @Override
    public BenefitResponse getAllBenefitsForHr(String username, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,
                                               String title, String description, Boolean isActive,  BenefitType benefitType) {





        // 1. Tao đoi tuong sap xep (theo field va huong sap xep: asc/desc)
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // 2. Tao doi tuong phan trang (Pageable) dua tren so trang, kich thuoc trang va sap xep
        Pageable pageable = PageRequest.of(pageNumber  - 1 , pageSize, sortByAndOrder);


        //3. Goi repository voi dieu kien loc dong (specification)
        Page<Benefit> benefitPage = benefitRepository.findAll((root, query, cb ) -> {
                List<Predicate> predicates = new ArrayList<>();

                //Lọc title và description theo or
            if ((title != null && !title.isEmpty()) || (description != null && !description.isEmpty()) ) {
                List<Predicate> orPredicates = new ArrayList<>();

                if (title != null && !title.isEmpty()) {
                    orPredicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
                }

                if (description != null && !description.isEmpty()) {
                    orPredicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
                }

                predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
            }

            //3.3. Loc theo trang thai (isActive)
            if(isActive != null){
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }



        //3.6 Loc so nguoi tham gia >=


            if(benefitType != null){
                predicates.add(cb.equal(root.get("benefitType"), benefitType));
            }

            //3.8 Tra ve tat ca dieu kien AND lai
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        List<Benefit> benefits = benefitPage.getContent();

//        Benefit benefit = new Benefit();
//        benefit.getBenefitPositions()
//                .stream()
//                .flatMap(bp -> bp.getRegistrations().stream())
//                .map(r -> {
//                    Employee e = r.getEmployee();
//                    System.out.println(e.getEmployeeName() + " → Dept: " + (e.getDepartment() != null ? e.getDepartment().getDepartmentName() : "null"));
//                    return null;
//                }).count();




        //Kiem tra xem danh sach co benefit khong
        if (benefits.isEmpty()) {
            throw new HRMSAPIException("Không có phúc lợi nào tương ứng");
        }



        //Neu oke thi chuyen ve DTO
        //List<BenefitDTO> benefitDTOList = modelMapper.map(benefits, List.class); //ko nen dung
//        List<BenefitDTO> benefitDTOList = benefits.stream()
//                .map(benefit -> modelMapper.map(benefit, BenefitDTO.class)).toList();
        List<BenefitDTO> benefitDTOList = benefitMapper.toBenefitDTOs(benefits);


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

    //Tested
    @Transactional
    @Override
    public BenefitDTO addBenefit(BenefitDTO benefitDTO) {
        // Step 0: lấy danh sách nhân sự đang active để trả về số lượng (như logic cũ)
        List<Employee> employees = employeeRepository.findAllActive();

        // Step 1: DTO -> Entity
        Benefit benefit = modelMapper.map(benefitDTO, Benefit.class);

        // >>> NEW: set mặc định isActive = 1 cho Benefit
        benefit.setIsActive(true); // nếu field là boolean: benefit.setIsActive(true);

        // Step 2: kiểm tra trùng tiêu đề
        Benefit benefitFromDb = benefitRepository.findByTitle(benefit.getTitle());
        if (benefitFromDb != null) {
            throw new HRMSAPIException("Phúc lợi với tiêu đề " + benefit.getTitle() + " đã tồn tại.");
        }

        // Step 3: lưu Benefit mới
        Benefit savedBenefit = benefitRepository.save(benefit);

        // >>> NEW: Gắn tất cả Position hiện có vào Benefit vừa tạo
        List<Position> positions = positionRepository.findAll();

        if (!positions.isEmpty()) {
            List<BenefitPosition> benefitPositions = positions.stream()
                    // Tránh tạo trùng nếu vì lý do nào đó đã tồn tại
                    .filter(position -> !benefitPositionRepository.existsByBenefitAndPosition(savedBenefit, position))
                    .map(position -> {
                        BenefitPosition bp = new BenefitPosition();
                        bp.setBenefit(savedBenefit);
                        bp.setPosition(position);

                        // Gán các trường mặc định từ Benefit
                        bp.setFormulaValue(savedBenefit.getDefaultFormulaValue());
                        bp.setFormulaType(savedBenefit.getDefaultFormulaType());



                        return bp;
                    })
                    .toList();

            if (!benefitPositions.isEmpty()) {
                benefitPositionRepository.saveAll(benefitPositions);
            }
        }
        // <<< END NEW

        // Step 4: trả về DTO
        BenefitDTO updatedBenefitDTO = modelMapper.map(savedBenefit, BenefitDTO.class);
        updatedBenefitDTO.setNumberOfEmployee(employees.size());
        return updatedBenefitDTO;
    }



    //Tested
    @Transactional
    @Override
    public BenefitDTO updateBenefit(PatchBenefitDTO benefitDTO, Long benefitId) {
        //check exist
        Benefit benefitFromDb = benefitRepository.findById(benefitId)
                .orElseThrow(() -> new HRMSAPIException("Benefit with id " + benefitId + " is not existed."));

        List<Employee> employees = employeeRepository.findAllActive();


        //DTO--> model
        Benefit benefit = modelMapper.map(benefitDTO, Benefit.class);



        if(benefit.getIsActive() != null){
            benefitFromDb.setIsActive(benefit.getIsActive());
        }





        if(benefit.getDescription() != null){
            benefitFromDb.setDescription(benefit.getDescription());
        }

        if(benefit.getTitle() != null){
            benefitFromDb.setTitle(benefit.getTitle());
        }

        if(benefit.getDetail() != null){
            benefitFromDb.setDetail(benefit.getDetail());
        }




        //save to db
        Benefit savedBenefit = benefitRepository.save(benefitFromDb);

        //return DTO;
        return modelMapper.map(savedBenefit, BenefitDTO.class);


    }



    //Tested
    @Override
    public BenefitDTO updateInactiveStatus(Long id, boolean isActive) {
        Benefit benefit = benefitRepository.findById(id)
                .orElseThrow(() -> new HRMSAPIException("Benefit with id " + id + " is not existed."));

        benefit.setIsActive(isActive);
        Benefit updated = benefitRepository.save(benefit);

        return modelMapper.map(updated, BenefitDTO.class);
    }

    //Tested
    @Transactional
    @Override
    public BenefitDTO deleteBenefit(Long benefitId) {
        Benefit benefit = benefitRepository.findById(benefitId)
                .orElseThrow(() -> new HRMSAPIException("Benefit with id " + benefitId + " is not existed."));

        benefitRepository.deleteById(benefitId);
        return modelMapper.map(benefit, BenefitDTO.class);
    }

    //Tested
    @Override
    public BenefitResponse getEmployeeAndPositionRegistrationByBenefitId(Long benefitId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        // 1. Tạo đối tượng sắp xếp (theo field và hướng sắp xếp: asc/desc)
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // 2. Tạo đối tượng phân trang (Pageable) dựa trên số trang, kích thước trang và sắp xếp
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);

        // 3. Gọi repository với điều kiện lọc động (Specification)
        Page<Benefit> benefitPage = benefitRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 3.1 Lọc theo id
            predicates.add(cb.equal(root.get("id"), benefitId));

            // 3.2 Lọc theo title
//            if (title != null && !title.isEmpty()) {
//                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
//            }



            // 3.10 Trả về tất cả điều kiện AND lại
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        // Lấy danh sách các Benefit từ trang hiện tại
        List<Benefit> benefits = benefitPage.getContent();

        // Kiểm tra nếu không có kết quả nào
        if (benefits.isEmpty()) {
            throw new HRMSAPIException("Không có phúc lợi tương ứng với id " + benefitId );
        }

        // Chuyển đổi danh sách Benefit sang BenefitDTO
        List<BenefitDTO> benefitDTOList = benefitMapper.toBenefitDTOs(benefits);

        // Tạo đối tượng BenefitResponse và lưu thông tin phân trang
        BenefitResponse benefitResponse = new BenefitResponse();
        benefitResponse.setContent(benefitDTOList);
        benefitResponse.setPageNumber(benefitPage.getNumber());
        benefitResponse.setPageSize(benefitPage.getSize());
        benefitResponse.setTotalElements(benefitPage.getTotalElements());
        benefitResponse.setTotalPages(benefitPage.getTotalPages());
        benefitResponse.setLastPage(benefitPage.isLast());

        return benefitResponse;




    }

    //Tested
    @Override
    public BenefitDTO getBenefitById(Long id) {
        Benefit benefit = benefitRepository.findById(id)
                .orElseThrow(() -> new HRMSAPIException("Phúc lợi với id " + id + " không tồn tại."));

        return modelMapper.map(benefit, BenefitDTO.class);
    }

    //Tested
    @Override
    public BenefitResponse getEmployeeByPositionAndBenefit(Long benefitId, Long positionId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // 1. Tạo đối tượng sắp xếp (theo field và hướng sắp xếp: asc/desc)
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // 2. Tạo đối tượng  phân trang (Pageable) dựa trên số trang, kích thước trang và sắp xếp
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);

        // 3. Gọi repository với điều kiện lọc động (Specification)
        Page<Benefit> benefitPage = benefitRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 3.1 Lọc theo id
            predicates.add(cb.equal(root.get("id"), benefitId));

            if (positionId != null) {
                Join<Object, Object> benefitPositionJoin = root.join("benefitPositions", JoinType.INNER);
                Join<Object, Object> positionJoin = benefitPositionJoin.join("position", JoinType.INNER);
                predicates.add(cb.equal(positionJoin.get("id"), positionId));
            }
            // 3.2 Lọc theo title
//            if (title != null && !title.isEmpty()) {
//                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
//            }



            // 3.10 Trả về tất cả điều kiện AND lại
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        // Lấy danh sách các Benefit từ trang hiện tại
        List<Benefit> benefits = benefitPage.getContent();

        // Kiểm tra nếu không có kết quả nào
        if (benefits.isEmpty()) {
            String message = positionId != null
                    ? "Không có BenefitPostiotion nào được tìm thấy với benefit id " + benefitId + " và position id " + positionId + "."
                    : "Không có phúc lợi nào với id " + benefitId + ".";
            throw new HRMSAPIException(message);
        }

        for (Benefit benefit : benefits) {
            if (positionId != null) {
                List<BenefitPosition> filtered = benefit.getBenefitPositions().stream()
                        .filter(bp -> bp.getPosition().getPositionId().equals(positionId))
                        .collect(Collectors.toList());
                benefit.setBenefitPositions(filtered);
            }
        }

        // Chuyển đổi danh sách Benefit sang BenefitDTO
        List<BenefitDTO> benefitDTOList = benefitMapper.toBenefitDTOs(benefits);

        // Tạo đối tượng BenefitResponse và lưu thông tin phân trang
        BenefitResponse benefitResponse = new BenefitResponse();
        benefitResponse.setContent(benefitDTOList);
        benefitResponse.setPageNumber(benefitPage.getNumber());
        benefitResponse.setPageSize(benefitPage.getSize());
        benefitResponse.setTotalElements(benefitPage.getTotalElements());
        benefitResponse.setTotalPages(benefitPage.getTotalPages());
        benefitResponse.setLastPage(benefitPage.isLast());

        return benefitResponse;
    }

    //Tested
    public List<Benefit> getAllActive() {
        return benefitRepository.findByIsActiveTrue();
    }

    //Tested
    @Transactional
    @Override
    public BenefitResponse searchBenefitByKeyword(String keyword, Integer pageNumber, Integer  pageSize, String sortBy, String sortOrder) {
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
