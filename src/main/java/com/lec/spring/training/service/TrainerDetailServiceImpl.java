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
import jakarta.persistence.EntityManager;
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
import java.util.concurrent.atomic.AtomicLong;
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
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

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
    public Long createTrainerProfile(TrainerProfileDTO trainerProfileDTO,
                                        PrincipalDetails user,
                                        List<String> skills,
                                        List<MultipartFile> images) throws IOException {
        Long trianerId = user.getId();
        try {
            // 현재 로그인한 유저 가져오기
            User trainer = user.getUser();



            if (!trainer.getAuthority().equals("ROLE_TRAINER")) {
                throw new AccessDeniedException("트레이너 권한이 필요합니다");
            }

            // images가 null이면 빈 리스트로 초기화
            if (images == null) {
                images = new ArrayList<>();
            }

            List<SkillsDTO> certificationSkills = new ArrayList<>();
            for (int i = 0; i < skills.size(); i++) {
                SkillsDTO skillsDTO = new SkillsDTO();
                skillsDTO.setSkills(skills.get(i).trim());

                // images 리스트 길이보다 index가 작을 때만 추가
                if (i < images.size()) {
                    skillsDTO.setImg(images.get(i));
                } else {
                    skillsDTO.setImg(null);  // 이미지가 없으면 null 처리
                }
                certificationSkills.add(skillsDTO);
            }

            // 트레이너 프로필이 이미 존재하는지 확인
            Optional<TrainerProfile> existingProfile = trainerProfileRepository.findByTrainer(trainer);

            if (existingProfile.isPresent()) {
                trainerProfileDTO.setTrainerId(existingProfile.get().getId());
                boolean updated = updateTrainerProfile(certificationSkills, trainerProfileDTO);
                return updated ? existingProfile.get().getId() : null;
            }

            // 신규 트레이너 프로필 생성
            TrainerProfile trainerProfile = TrainerProfile.builder()
                    .trainer(trainer)
                    .career(trainerProfileDTO.getCareer())
                    .content(trainerProfileDTO.getContent())
                    .perPrice(trainerProfileDTO.getPerPrice())
                    .isAccess(대기) // 문자열 "대기"로 변경
                    .build();

           TrainerProfile savedTrainerProfile =  trainerProfileRepository.save(trainerProfile);

            saveCertification(certificationSkills, trainerProfile);

            return savedTrainerProfile.getId();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }





    @Override
    @Transactional
    public boolean updateTrainerProfile(List<SkillsDTO> certificationSkills, TrainerProfileDTO trainerProfileDTO) throws IOException {
        try {
            // 트레이너 프로필 조회
            TrainerProfile profile = trainerProfileRepository.findById(trainerProfileDTO.getTrainerId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 트레이너 프로필이 존재하지 않습니다."));

            // 수정 가능한 필드 업데이트
            if (trainerProfileDTO.getPerPrice() != null) profile.setPerPrice(trainerProfileDTO.getPerPrice());
            if (trainerProfileDTO.getContent() != null) profile.setContent(trainerProfileDTO.getContent());
            if (trainerProfileDTO.getCareer() != null) profile.setCareer(trainerProfileDTO.getCareer());

            // 프론트에서 삭제한 자격증 삭제 처리
            if (trainerProfileDTO.getDeletedSkillsId() != null && trainerProfileDTO.getDeletedSkillsId().length > 0) {
                List<Long> certificationIdsToDelete = Arrays.stream(trainerProfileDTO.getDeletedSkillsId())
                        .collect(Collectors.toList());
                // 각 자격증 ID에 대해 삭제 수행

                    certificationRepository.deleteCertifications(profile.getId(), certificationIdsToDelete);


                certificationRepository.flush();
            }

            // 기존 자격증 이미지 리스트 가져오기
            List<String> existingImageUrls = certificationRepository.findCredentialsByTrainerProfileId(profile.getId());

            // 📌 새로운 자격증(사진)이 존재하는 경우에만 저장 수행
            if (certificationSkills != null && !certificationSkills.isEmpty()) {
                // 새 이미지가 있을 경우 저장
                if (certificationSkills.stream().anyMatch(s -> s.getImg() != null && !s.getImg().isEmpty())) {
                    saveCertification(certificationSkills, profile);
                }
            }



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
                .orElse(null);

        return convertToDTO(trainerProfile);
    }


    private TrainerProfileReadDTO convertToDTO(TrainerProfile trainerProfile) {

        Long userId = trainerProfile.getTrainer().getId();


        String profileImage = trainerProfile.getTrainer().getProfileImage();


        HBTI hbti = hbtiRepository.findByUser_Id(userId).orElse(null);


        Gym gym = trainerProfile.getTrainer().getGym();

        return TrainerProfileReadDTO.builder()
                .id(trainerProfile.getId())
                .trainerId(trainerProfile.getTrainer().getId())
                .trainerName(trainerProfile.getTrainer().getNickname())
                .trainerEmail(trainerProfile.getTrainer().getEmail())
                .trainerProfileImage(profileImage)
                .perPrice(trainerProfile.getPerPrice())
                .content(trainerProfile.getContent())
                .career(trainerProfile.getCareer())
                .isAccess(trainerProfile.getIsAccess().name())
                .certifications(trainerProfile.getCertificationList().stream()
                        .map(cert -> CertificationDTO.builder()
                                .certificationId(cert.getId().getId())
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


    // certification 저장
    void saveCertification(List<SkillsDTO> certificationsList, TrainerProfile trainerProfile) {
        List<Certification> certifications = new ArrayList<>();

        for (SkillsDTO skillsDTO : certificationsList) {
            try {
                // 이미지가 있는 경우에만 저장
                String savePath = null;
                if (skillsDTO.getImg() != null && !skillsDTO.getImg().isEmpty()) {
                    savePath = imgService.saveImage(skillsDTO.getImg(), trainerDir);
                }
                String dbSavePath = savePath.replaceFirst("./FITPLE-backend/", "");

                // CertificationId 설정 (복합 키)
                CertificationId certificationId = new CertificationId(
                        ID_GENERATOR.getAndIncrement(),
                        trainerProfile.getId()
                );

                // Certification 객체 생성
                Certification certification = Certification.builder()
                        .id(certificationId)
                        .credentials(dbSavePath)
                        .skills(skillsDTO.getSkills())
                        .trainerProfile(trainerProfile)
                        .build();
                certifications.add(certification);

            } catch (IOException e) {
                throw new ServiceException("자격증 이미지 저장 실패", e);
            }
        }

        if (!certifications.isEmpty()) {
            certificationRepository.saveAll(certifications);
        }
    }

    @Override
    @Transactional
    public void updateTrainerGrantStatus(Long trainerId, GrantStatus status) {
        TrainerProfile profile = trainerProfileRepository.findByTrainerId(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer profile not found for trainer ID: " + trainerId));

        profile.setIsAccess(status);
        trainerProfileRepository.save(profile);
    }

}// end TrainerDetailService

