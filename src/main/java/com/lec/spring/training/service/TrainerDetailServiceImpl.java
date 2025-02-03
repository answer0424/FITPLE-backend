package com.lec.spring.training.service;

import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.UserRepository;
import com.lec.spring.training.DTO.SkillsDTO;
import com.lec.spring.training.DTO.TrainerProfileDTO;
import com.lec.spring.training.domain.Certification;
import com.lec.spring.training.domain.CertificationId;
import com.lec.spring.training.domain.TrainerProfile;
import com.lec.spring.training.repository.CertificationRepository;
import com.lec.spring.training.repository.TrainerProfileRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.lec.spring.training.domain.GrantStatus.대기;
import static com.lec.spring.training.domain.GrantStatus.승인;


@Service
public class TrainerDetailServiceImpl implements TrainerDetailService {

    private final TrainerProfileRepository trainerProfileRepository;
    private final CertificationRepository certificationRepository;
    // UserRepository 필요
    private final ImgService imgService;

    private final UserRepository userRepository;


    public TrainerDetailServiceImpl(TrainerProfileRepository trainerProfileRepository, CertificationRepository certificationRepository, ImgService imgService, UserRepository userRepository) {
        this.trainerProfileRepository = trainerProfileRepository;
        this.certificationRepository = certificationRepository;
        this.imgService = imgService;
        this.userRepository = userRepository;
    }




// 기타 필요한 import 문

    @Transactional
    @Override
    public boolean createTrainerProfile(TrainerProfileDTO trainerProfileDTO,
                                        PrincipalDetails user,
                                        List<String> skills,
                                        List<MultipartFile> images) throws IOException {
        try {
            // 현재 로그인 한 유저 가져오기
            PrincipalDetails principal = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User trainer = principal.getUser();  // PrincipalDetails에서 User 객체 가져오기
            System.out.println("현재 로그인한 유저 : " + trainer.getUsername());

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

            // 기존 TrainerProfile 가져오기 또는 새로 생성
            TrainerProfile trainerProfile = trainerProfileRepository.findById(trainerProfileDTO.getTrainerId())
                    .orElse(TrainerProfile.builder()
                            .trainer(trainer)
                            .career(trainerProfileDTO.getCareer())
                            .content(trainerProfileDTO.getContent())
                            .perPrice(trainerProfileDTO.getPerPrice())
                            .isAccess(승인)
                            .build());
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
                    String savePath = imgService.saveImage(skillsDTO.getImg(), "./uploads/certification/");
                    System.out.println("자격증 이미지 저장 경로: " + savePath);

                    // CertificationId 설정 (복합 키)
                    CertificationId certificationId = new CertificationId();
                    certificationId.setTrainerProfileId(trainerProfile.getId()); // TrainerProfile의 ID
                    certificationId.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE); // 랜덤 ID(무작위 값)
                    // CertificationId(복합키)는 generatedValue사용할 수 없음. -> id 부여 생각해야함.

                    // Certification 객체 생성
                    Certification certification = Certification.builder()
                            .id(certificationId) // 🔹 복합 키 설정
                            .credentials(savePath) // 🔹 저장된 이미지 경로
                            .skills(skillsDTO.getSkills())
                            .trainerProfile(trainerProfile) // 🔹 TrainerProfile 설정
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
                System.out.println("삭제할 자격증 ID: " + idsToDelete);
                certificationRepository.deleteAllByIdInBatch(idsToDelete);
                System.out.println("자격증 삭제 완료");
            }

            // 새 자격증 추가 처리
            if (skills == null || images == null || skills.size() != images.size()) {
                System.out.println("skills: " + skills.size() + ", images: " + images.size());
                throw new IllegalArgumentException("자격증과 이미지의 개수가 일치하지 않습니다.");
            }

            // 새 자격증 저장할 리스트 생성
            List<Certification> certifications = new ArrayList<>();

            for (int i = 0; i < skills.size(); i++) {
                if (images.get(i) == null || images.get(i).isEmpty()) {
                    throw new IllegalArgumentException("자격증 이미지가 필요합니다.");
                }

                // 이미지 저장
                String savePath = imgService.saveImage(images.get(i), "./uploads/certification/");
                System.out.println("자격증 이미지 저장 경로: " + savePath);

                // CertificationId 설정 (복합 키)
                CertificationId certificationId = new CertificationId(profile.getId(), UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);

                // Certification 객체 생성
                Certification certification = Certification.builder()
                        .id(certificationId) // 복합 키 설정
                        .trainerProfile(profile)
                        .skills(skills.get(i))
                        .credentials(savePath)
                        .build();

                certifications.add(certification);
            }

            // 새로운 자격증 저장
            if (!certifications.isEmpty()) {
                certificationRepository.saveAll(certifications);
            }

            // 트레이너 프로필 저장
            trainerProfileRepository.save(profile);
            System.out.println("트레이너 프로필 수정 완료: " + profile.getId());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }





}// end TrainerDetailService

