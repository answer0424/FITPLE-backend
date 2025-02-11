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
    public boolean createTrainerProfile(TrainerProfileDTO trainerProfileDTO,
                                        PrincipalDetails user,
                                        List<String> skills,
                                        List<MultipartFile> images) throws IOException {
        try {
            // 현재 로그인한 유저 가져오기
            User trainer = user.getUser();
            System.out.println("현재 로그인한 유저 : " + trainer.getUsername());

            if (!trainer.getAuthority().equals("ROLE_TRAINER")) {
                throw new AccessDeniedException("트레이너 권한이 필요합니다");
            }

            if (skills.size() != images.size()) {
                throw new IllegalArgumentException("자격증과 이미지의 개수가 일치하지 않습니다.");
            }

            List<SkillsDTO> certificationSkills = new ArrayList<>();
            for (int i = 0; i < skills.size(); i++) {
                SkillsDTO skillsDTO = new SkillsDTO();
                skillsDTO.setSkills(skills.get(i).trim());
                skillsDTO.setImg(images.get(i));
                certificationSkills.add(skillsDTO);
            }
//            trainerProfileDTO.setCertificationSkills(certificationSkills);


            // 트레이너 프로필이 이미 존재하는지 확인
            Optional<TrainerProfile> existingProfile = trainerProfileRepository.findByTrainer(trainer);

            if (existingProfile.isPresent()) {
                System.out.println("기존 트레이너 프로필이 존재하므로 업데이트를 수행합니다: " + existingProfile.get().getId());
                trainerProfileDTO.setTrainerId(existingProfile.get().getId());
                return updateTrainerProfile(certificationSkills, trainerProfileDTO);
            }



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

          saveCertification(certificationSkills, trainerProfile);
            System.out.println("저장완료 : " + trainer.getUsername());

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
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
                System.out.println("#######삭제될 자격증 리스트 :  " + certificationIdsToDelete);
                // 각 자격증 ID에 대해 삭제 수행

                    certificationRepository.deleteCertifications(profile.getId(), certificationIdsToDelete);


                certificationRepository.flush();
                System.out.println("#########삭제 반영된 자격증 리스트 : " + certificationRepository.findCredentialsByTrainerProfileId(profile.getId()));
            }

            // 기존 자격증 이미지 리스트 가져오기
            List<String> existingImageUrls = certificationRepository.findCredentialsByTrainerProfileId(profile.getId());

            System.out.println();
            System.out.println("existingImageUrls: " + existingImageUrls);
            // 📌 새로운 자격증(사진)이 존재하는 경우에만 저장 수행
            if (certificationSkills != null && !certificationSkills.isEmpty() && certificationSkills.stream().anyMatch(s -> s.getImg() != null && !s.getImg().isEmpty())) {
                saveCertification(certificationSkills, profile);
            }


            System.out.println("수정완료 : " + profile.getId());

            // 트레이너 프로필 저장
//            trainerProfileRepository.save(profile);

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
    void saveCertification(List<SkillsDTO> certificationsList, TrainerProfile trainerProfile){

        List<Certification> certifications = new ArrayList<>();

        for (SkillsDTO skillsDTO : certificationsList) {
            if (skillsDTO.getImg() == null || skillsDTO.getImg().isEmpty()) {
                throw new IllegalArgumentException("자격증 이미지가 필요합니다.");
            }

            try {
                // 이미지 저장 및 경로 반환
                String savePath = imgService.saveImage(skillsDTO.getImg(), trainerDir);
                System.out.println("자격증 이미지 저장 경로: " + savePath);

                // CertificationId 설정 (복합 키)
                CertificationId certificationId = new CertificationId(
                        ID_GENERATOR.getAndIncrement(),
                        trainerProfile.getId()
                );
                System.out.println("#################certificationId: " + certificationId);

                // Certification 객체 생성
                Certification certification = Certification.builder()
                        .id(certificationId)
                        .credentials(savePath)
                        .skills(skillsDTO.getSkills() )
                        .trainerProfile(trainerProfile)
                        .build();
                certifications.add(certification);

            } catch (IOException e) {
                System.out.println("자격증 이미지 저장 중 오류 발생: " + e.getMessage());
                throw new ServiceException("자격증 이미지 저장 실패", e);
            }
        }

        certificationRepository.saveAll(certifications);
        System.out.println("트레이너 프로필 및 자격증 저장 완료: " + trainerProfile.getId());
    }

}// end TrainerDetailService

