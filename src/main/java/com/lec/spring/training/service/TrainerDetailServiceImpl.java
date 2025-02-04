package com.lec.spring.training.service;

import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.domain.Gym;
import com.lec.spring.base.domain.HBTI;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.GymRepository;
import com.lec.spring.base.repository.HbtiRepository;
import com.lec.spring.base.repository.UserRepository;
import com.lec.spring.training.DTO.CertificationDTO;
import com.lec.spring.training.DTO.SkillsDTO;
import com.lec.spring.training.DTO.TrainerProfileDTO;
import com.lec.spring.training.DTO.TrainerProfileReadDTO;
import com.lec.spring.training.domain.Certification;
import com.lec.spring.training.domain.CertificationId;
import com.lec.spring.training.domain.GrantStatus;
import com.lec.spring.training.domain.TrainerProfile;
import com.lec.spring.training.repository.CertificationRepository;
import com.lec.spring.training.repository.TrainerProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.lec.spring.training.domain.GrantStatus.*;


@Service
public class TrainerDetailServiceImpl implements TrainerDetailService {

    private final TrainerProfileRepository trainerProfileRepository;
    private final CertificationRepository certificationRepository;
    private final ImgService imgService;
    private final UserRepository userRepository;
    private final HbtiRepository hbtiRepository;
    private final GymRepository gymRepository;

    @Value("${app.image.upload}")
    private String trainerDir;

    @Autowired
    public TrainerDetailServiceImpl(TrainerProfileRepository trainerProfileRepository, CertificationRepository certificationRepository, ImgService imgService, UserRepository userRepository, HbtiRepository hbtiRepository, GymRepository gymRepository) {
        this.trainerProfileRepository = trainerProfileRepository;
        this.certificationRepository = certificationRepository;
        this.imgService = imgService;
        this.userRepository = userRepository;
        this.hbtiRepository = hbtiRepository;
        this.gymRepository = gymRepository;
    }



    @Transactional
    @Override
    public boolean createTrainerProfile(TrainerProfileDTO trainerProfileDTO,
                                        PrincipalDetails user,
                                        List<String> skills,
                                        List<MultipartFile> images) throws IOException {
        try {
            // 현재 로그인한 유저 가져오기
            PrincipalDetails principal = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User trainer = principal.getUser();
            System.out.println("현재 로그인한 유저 : " + trainer.getUsername());

            if (!trainer.getAuthority().equals("ROLE_TRAINER")) {
                throw new AccessDeniedException("트레이너 권한이 필요합니다");
            }

            // 트레이너 프로필이 이미 존재하는지 확인
            Optional<TrainerProfile> existingProfile = trainerProfileRepository.findByTrainer(trainer);

            if (existingProfile.isPresent()) {
                System.out.println("기존 트레이너 프로필이 존재하므로 업데이트를 수행합니다: " + existingProfile.get().getId());
                trainerProfileDTO.setTrainerId(existingProfile.get().getId());
                return updateTrainerProfile(trainerProfileDTO, skills, images);
            }

            // SkillsDTO 리스트 생성 및 데이터 매핑
            if (skills.size() != images.size()) {
                throw new IllegalArgumentException("자격증과 이미지의 개수가 일치하지 않습니다.");
            }

            List<SkillsDTO> certificationSkills = new ArrayList<>();
            for (int i = 0; i < skills.size(); i++) {
                SkillsDTO skillsDTO = new SkillsDTO();
                skillsDTO.setSkills(skills.get(i));
                skillsDTO.setImg(images.get(i));
                certificationSkills.add(skillsDTO);
            }
            trainerProfileDTO.setCertificationSkills(certificationSkills);

            // 신규 트레이너 프로필 생성
            TrainerProfile trainerProfile = TrainerProfile.builder()
                    .trainer(trainer)
                    .career(trainerProfileDTO.getCareer())
                    .content(trainerProfileDTO.getContent())
                    .perPrice(trainerProfileDTO.getPerPrice())
                    .isAccess(승인)
                    .build();

            System.out.println("db저장 시작");
            trainerProfileRepository.save(trainerProfile);
            System.out.println("TrainerProfile 저장 완료: " + trainerProfile.getId());

            // Certification 저장
            List<Certification> certifications = new ArrayList<>();

            for (SkillsDTO skillsDTO : certificationSkills) {
                if (skillsDTO.getImg() == null || skillsDTO.getImg().isEmpty()) {
                    throw new IllegalArgumentException("자격증 이미지가 필요합니다.");
                }

                try {
                    // 이미지 저장 및 경로 반환
                    String savePath = imgService.saveImage(skillsDTO.getImg(), trainerDir);
                    System.out.println("자격증 이미지 저장 경로: " + savePath);

                    // CertificationId 설정 (복합 키)
                    CertificationId certificationId = new CertificationId(trainerProfile.getId(), UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
                    System.out.println("#################certificationId: " + certificationId);

                    // Certification 객체 생성
                    Certification certification = Certification.builder()
                            .id(certificationId)
                            .credentials(savePath)
                            .skills(skillsDTO.getSkills())
                            .trainerProfile(trainerProfile)
                            .build();

                    certifications.add(certification);
                } catch (IOException e) {
                    System.out.println("자격증 이미지 저장 중 오류 발생: " + e.getMessage());
                    throw new ServiceException("자격증 이미지 저장 실패", e);
                }
            }

            certificationRepository.saveAll(certifications);
            System.out.println("트레이너 프로필 및 자격증 저장 완료: " + trainer.getUsername());

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }



    @Override
    @Transactional
    public boolean updateTrainerProfile(TrainerProfileDTO trainerProfileDTO, List<String> skills, List<MultipartFile> images) throws IOException {
        try {
            // 트레이너 프로필 조회
            TrainerProfile profile = trainerProfileRepository.findById(trainerProfileDTO.getTrainerId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 트레이너 프로필이 존재하지 않습니다."));

            // 수정 가능한 필드 업데이트
            if (trainerProfileDTO.getPerPrice() != null) profile.setPerPrice(trainerProfileDTO.getPerPrice());
            if (trainerProfileDTO.getContent() != null) profile.setContent(trainerProfileDTO.getContent());
            if (trainerProfileDTO.getCareer() != null) profile.setCareer(trainerProfileDTO.getCareer());

            // 기존 자격증 삭제 처리
            if (trainerProfileDTO.getDeletedSkillsId() != null && trainerProfileDTO.getDeletedSkillsId().length > 0) {
                List<CertificationId> idsToDelete = Arrays.stream(trainerProfileDTO.getDeletedSkillsId())
                        .map(id -> new CertificationId(profile.getId(), id))
                        .collect(Collectors.toList());
                System.out.println("deleted ids: " + idsToDelete);
                certificationRepository.deleteAllByIdInBatch(idsToDelete);
            }

            // 기존 자격증 이미지 리스트 가져오기
            List<String> existingImageUrls = trainerProfileRepository.findByTrainerId(profile.getId())
                    .stream()
                    //flatMap은 스트림 메서드로 각각 다른 스트림으로 매핑후 하나의 스트림으로 병합
                    .flatMap(trainerProfile -> trainerProfile.getCertificationList().stream())
                    .map(Certification::getCredentials)
                    .collect(Collectors.toList());

            System.out.println("existingImageUrls: " + existingImageUrls);

            // 새 이미지 추가할 리스트
            List<String> newImageUrls = new ArrayList<>(existingImageUrls);

            // 새 자격증 추가 처리
            if (skills != null && images != null) {
                if (skills.size() != images.size()) {
                    throw new IllegalArgumentException("자격증과 이미지의 개수가 일치하지 않습니다.");
                }

                List<Certification> certifications = new ArrayList<>();

                for (int i = 0; i < skills.size(); i++) {
                    MultipartFile file = images.get(i);

                    if (file != null && !file.isEmpty()) {
                        // 새 이미지 저장
                        String savePath = imgService.saveImage(file, "trainer");
                        newImageUrls.add(savePath);  // 기존 이미지와 함께 저장
                    }

                    // CertificationId 설정 (복합 키)
                    CertificationId certificationId = new CertificationId(profile.getId(), UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);

                    // Certification 객체 생성
                    Certification certification = Certification.builder()
                            .id(certificationId) // 복합 키 설정
                            .trainerProfile(profile)
                            .skills(skills.get(i))
                            .credentials(newImageUrls.get(i)) // 기존 + 새로운 이미지 저장
                            .build();

                    certifications.add(certification);
                }

                // 새로운 자격증 저장
                if (!certifications.isEmpty()) {
                    certificationRepository.saveAll(certifications);
                }
            }

            // 트레이너 프로필 저장
            trainerProfileRepository.save(profile);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    // 특정 트레이너 ID로 트레이너 프로필 조회 (DTO 변환)
    public TrainerProfileReadDTO getTrainerProfileById(Long trainerId) {
        TrainerProfile trainerProfile = trainerProfileRepository.findByTrainerId(trainerId)
                .orElseThrow(() -> new RuntimeException("신규등록한 회원임다.")) ;

        return convertToDTO(trainerProfile);
    }

    // 승인된 트레이너 목록 조회 (DTO 변환)
    public List<TrainerProfileReadDTO> getApprovedTrainers() {
        List<TrainerProfile> trainers = trainerProfileRepository.findByIsAccess(GrantStatus.승인);
        return trainers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }



    private TrainerProfileReadDTO convertToDTO(TrainerProfile trainerProfile) {

        Long userId = trainerProfile.getTrainer().getId();


        String profileImage = trainerProfile.getTrainer().getProfileImage();


        HBTI hbti = hbtiRepository.findByUser_Id(userId).orElse(null);


        Gym gym = trainerProfile.getTrainer().getGym();

        return TrainerProfileReadDTO.builder()
                .id(trainerProfile.getId())
                .trainerName(trainerProfile.getTrainer().getUsername())
                .trainerEmail(trainerProfile.getTrainer().getEmail())
                .trainerProfileImage(profileImage) //
                .perPrice(trainerProfile.getPerPrice())
                .content(trainerProfile.getContent())
                .career(trainerProfile.getCareer()) //
                .isAccess(trainerProfile.getIsAccess().name())
                .certifications(trainerProfile.getCertificationList().stream()
                        .map(cert -> CertificationDTO.builder()
                                .skills(cert.getSkills())
                                .imageUrl(cert.getCredentials())
                                .build())
                        .collect(Collectors.toList()))

                .hbti(hbti != null ? hbti.getHbti() : "정보 없음")

                .gymName(gym != null ? gym.getName() : "체육관 정보 없음")
                .gymAddress(gym != null ? gym.getAddress() : "위치 정보 없음")
                .gymLatitude(gym != null ? gym.getLatitude() : null)
                .gymLongitude(gym != null ? gym.getLongitude() : null)
                .build();
    }

}// end TrainerDetailService

