package com.lec.spring.training.service;

import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.UserRepository;
import com.lec.spring.training.DTO.TrainerProfileDTO;
import com.lec.spring.training.domain.TrainerProfile;
import com.lec.spring.training.repository.CertificationRepository;
import com.lec.spring.training.repository.TrainerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lec.spring.training.domain.GrantStatus.승인;


@Service
public class TrainerDetailServiceImpl implements TrainerDetailService {

    private final TrainerProfileRepository trainerProfileRepository;
    private final CertificationRepository certificationRepository;
    // UserRepository 필요
    private final ImgService imgService;
    private final  String imgDir ="com/lec/spring/util";
    private final UserRepository userRepository;

    @Autowired
    public TrainerDetailServiceImpl(TrainerProfileRepository trainerProfileRepository, CertificationRepository certificationRepository, ImgService imgService, UserRepository userRepository) {
        this.trainerProfileRepository = trainerProfileRepository;
        this.certificationRepository = certificationRepository;
        this.imgService = imgService;
        this.userRepository = userRepository;
    }


    // # 트레이너 프로필 생성
    @Transactional
    @Override
    public boolean createTrainerProfile(TrainerProfileDTO trainerProfileDTO,
                                        PrincipalDetails user,
                                        List<String> skills,
                                        List<MultipartFile> images) {
        try {

//            User trainer  = user.getUser();

            User trainer = new User();
            trainer.setId(1L);
            trainer.setNickname("지윤");
            trainer.setUsername("wldbs");
            trainer.setEmail("johndoe@example.com"); // 임시 이메일
            trainer.setPassword("123456");
            TrainerProfile trainerProfile = TrainerProfile.builder()
                    .trainer(trainer) // UserRepository 필요(정보를 가져오는것 DB, DB와 연결 => Repository)
                    .career(trainerProfileDTO.getCareer())
                    .content(trainerProfileDTO.getContent())
                    .perPrice(trainerProfileDTO.getPerPrice())
                    .isAccess(승인) // 원래는 없어야 하는 것이 맞음
                    .build();
//            System.out.println("TrainerProfile : " + trainerProfile);
            trainerProfileRepository.save(trainerProfile);// 프로필 저장후 skills 저장해야함.


            // 이미지 저장을 imgService에서 다루기
            List<String> skillList = new ArrayList<>();
            for(MultipartFile skill : images){
                String savePath = imgService.saveImage(skill, "./uploads/");
                skillList.add(savePath);
            }

                return true;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    public TrainerProfileDTO getTrainerProfileByNickname(String nickname, PrincipalDetails users) {
        // 유저 리포지토리에서 닉네임으로 유저 조회
        User user = userRepository.findByNickname(nickname);

        // 트레이너 프로필 조회
        TrainerProfile trainerProfile = trainerProfileRepository.findByTrainerId(user.getId());

        return TrainerProfileDTO.builder()
                .trainerId(trainerProfile.getTrainer().getId())
                .career(trainerProfile.getCareer())
                .content(trainerProfile.getContent())
                .perPrice(trainerProfile.getPerPrice())
                .build();
    }


    // 트레이너 프로필 수정
    @Override
    @Transactional
    public boolean updateTrainerProfile(TrainerProfileDTO trainerProfile,  List<String> skills, List<MultipartFile> images) throws IOException {
        /*
        *  현재 흐름 -> 수정된 것만 가져오는 것
        *   react에서 useState하는 과정에서 별개의 값이 아닌 전체값을 가져오게 될 경우
        *   기존데이터를 삭제하고 새로 넣는과정을 해야함. 현재 코드와 충돌이 일어날 가능성이 높음
        *   기존 useState 새로운 값을 넣는 useState를 따로 만들어서 후자를 백단으로 보내는게 좋을 것 같음.
        * */

        try{
            TrainerProfile profile = trainerProfileRepository.findById(trainerProfile.getTrainerId()) //현재 로그인한 유저 or 마이페이지 url로 확인
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 트레이너 프로필이 존재하지 않습니다."));

            if (trainerProfile.getPerPrice() != null) profile.setPerPrice(trainerProfile.getPerPrice());
            if (trainerProfile.getContent() != null) profile.setContent(trainerProfile.getContent());
            if (trainerProfile.getCareer() != null) profile.setCareer(trainerProfile.getCareer());
            if (trainerProfile.getDeletedSkillsId() != null && trainerProfile.getDeletedSkillsId() .length > 0) {
                // 삭제된 경력이 존재한다면
                for (Long id : trainerProfile.getDeletedSkillsId() ) {
                    certificationRepository.deleteById(id);
                }
            }
            trainerProfileRepository.save(profile);
            // 이미지 저장을 imgService에서 다루기
         List<String> skillList = new ArrayList<>();
            for(MultipartFile skill : images){
                String savePath = imgService.saveImage(skill, "./uploads/");
                skillList.add(savePath);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



}// end TrainerDetailService

/*
*TODO
* 설계!! 를 생각하면서 코드 짜기!
* imgService 중첩적인 것 합치기 (어떤 차이가 있는지 공부하기!)
* img 데이터 베이스에 저장하는 코드 작성하기
* 리뷰서비스 가져와서 잘 출력되는지 확인하기
* 오후 8시 30분 -> 안된다고 하면 내일
* */