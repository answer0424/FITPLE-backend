-- 기존 테이블 삭제
SET SESSION FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS Gym, User, Training, Reservation, Review,
    Chat, UserChat, Message, AI_PROMPT,
    TrainerProfile, certifications, HBTI, certification, CertificationsImage, trainer_profile, user_chat;

-- 1. 핵심 테이블

-- 1-1. 헬스장 정보
CREATE TABLE Gym
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    name      VARCHAR(20)  NOT NULL,
    address   VARCHAR(100) NOT NULL,
    latitude  DOUBLE       NOT NULL COMMENT '위도',
    longitude DOUBLE       NOT NULL COMMENT '경도',
    PRIMARY KEY (id),
    UNIQUE INDEX id_UNIQUE (id),
    UNIQUE INDEX address_UNIQUE (address)
);

-- 1-2. 사용자 정보
CREATE TABLE User
(
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
    GymId        BIGINT       NULL,
    username     VARCHAR(30)  NOT NULL COMMENT '유저 ID',
    password     VARCHAR(50)  NOT NULL COMMENT '유저 비밀번호, 암호화 저장',
    email        VARCHAR(254) NOT NULL COMMENT '유저 이메일',
    address      VARCHAR(100) NULL COMMENT '거리 기반 주소지',
    birth        DATE         NULL,
    nickname     VARCHAR(45)  NOT NULL,
    provider     VARCHAR(70)  NULL,
    providerId   VARCHAR(70)  NULL,
    authority    VARCHAR(20)  NULL COMMENT 'admin, trainer, student',
    profileImage VARCHAR(50)  NULL,
    createdAt    DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX id_UNIQUE (id),
    UNIQUE INDEX username_UNIQUE (username),
    UNIQUE INDEX email_UNIQUE (email),
    UNIQUE INDEX nickname_UNIQUE (nickname),
    UNIQUE INDEX profile_image_UNIQUE (profileImage)
);

-- 2. 트레이닝 관련 테이블
-- 2-1. 트레이너-회원 관계
CREATE TABLE Training
(
    UserId    BIGINT NOT NULL,
    TrainerId BIGINT NOT NULL,
    times     INT    NULL COMMENT '잔여 pt 횟수',
    PRIMARY KEY (UserId, TrainerId)
);

-- 2-2. 트레이너 프로필
CREATE TABLE TrainerProfile
(
    id        BIGINT                  NOT NULL AUTO_INCREMENT,
    trainerId BIGINT                  NOT NULL,
    perPrice  INT                     NOT NULL,
    skills    JSON                    NOT NULL COMMENT '스킬 목록',
    content   LONGTEXT                NOT NULL,
    career    DATE                    NOT NULL,
    `grant`   ENUM ('승인', '대기', '거절') NOT NULL DEFAULT '대기',
    PRIMARY KEY (id),
    UNIQUE INDEX id_UNIQUE (id)
);

-- 2-3. 트레이너 자격증 사진
CREATE TABLE CertificationsImage
(
    id                BIGINT      NOT NULL COMMENT '자격증 ID',
    TrainerProfile_id BIGINT      NOT NULL,
    credentials       VARCHAR(50) NOT NULL,
    PRIMARY KEY (id, TrainerProfile_id)
);

-- 3. 예약 및 리뷰
CREATE TABLE Reservation
(
    id           BIGINT          NOT NULL AUTO_INCREMENT,
    userId       BIGINT          NOT NULL,
    trainerId    BIGINT          NOT NULL,
    date         DATETIME        NOT NULL COMMENT '예약 시간',
    status       ENUM ('status') NOT NULL COMMENT '예약/취소/완료',
    start_time   TIME            NULL COMMENT '시작 시각',
    ExerciseTime TIME            NULL COMMENT '완료 시각',
    PRIMARY KEY (id),
    UNIQUE INDEX id_UNIQUE (id)
);

CREATE TABLE Review
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    trainingId BIGINT  NOT NULL,
    rating     TINYINT NOT NULL,
    content    TEXT,
    PRIMARY KEY (id)
);


-- 4. 채팅 관련 테이블

CREATE TABLE Chat
(
    id        BIGINT      NOT NULL,
    name      VARCHAR(45) NOT NULL COMMENT '채팅방 이름',
    createdAt DATETIME    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE UserChat
(
    UserId     BIGINT NOT NULL,
    ChatRoomId BIGINT NOT NULL,
    PRIMARY KEY (UserId, ChatRoomId),
    UNIQUE INDEX User_id_UNIQUE (UserId),
    UNIQUE INDEX ChatRoom_id_UNIQUE (ChatRoomId)
);

CREATE TABLE Message
(
    MessageId  VARCHAR(45) NOT NULL,
    ChatId     BIGINT      NOT NULL,
    UserId     BIGINT      NOT NULL,
    content    LONGTEXT    NOT NULL,
    `check`    TINYINT     NOT NULL DEFAULT 0,
    created_at DATETIME    NOT NULL,
    PRIMARY KEY (MessageId, ChatId),
    UNIQUE INDEX MessageId_UNIQUE (MessageId)
);

-- 5. 부가 기능 테이블

CREATE TABLE AI_PROMPT
(
    id         BIGINT   NOT NULL,
    UserId     BIGINT   NOT NULL,
    prompt     JSON     NOT NULL COMMENT 'HBTI 및 퍼센트 데이터',
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX id_UNIQUE (id)
);

CREATE TABLE HBTI
(
    UserId BIGINT     NOT NULL,
    HBTI   VARCHAR(4) NOT NULL,
    1col   DOUBLE     NOT NULL,
    2col   DOUBLE     NOT NULL,
    3col   DOUBLE     NOT NULL,
    4col   DOUBLE     NOT NULL,
    PRIMARY KEY (UserId)
);

-- 6. 외래키 제약조건

-- User - Gym 관계
ALTER TABLE User
    ADD CONSTRAINT fk_User_Gym1 FOREIGN KEY (GymId)
        REFERENCES Gym (id);

-- Training 관계
ALTER TABLE Training
    ADD CONSTRAINT fk_Training_User1 FOREIGN KEY (UserId)
        REFERENCES User (id);

ALTER TABLE Training
    ADD CONSTRAINT fk_Training_User2 FOREIGN KEY (TrainerId)
        REFERENCES User (id);

-- TrainerProfile 관계
ALTER TABLE TrainerProfile
    ADD CONSTRAINT fk_TrainerProfile_User1 FOREIGN KEY (trainerId)
        REFERENCES User (id);

-- certifications 관계
ALTER TABLE CertificationsImage
    ADD CONSTRAINT fk_certifications_TrainerProfile1 FOREIGN KEY (TrainerProfile_id)
        REFERENCES TrainerProfile (id);

-- Reservation 관계
ALTER TABLE Reservation
    ADD CONSTRAINT fk_Reservation_Training1 FOREIGN KEY (userId, trainerId)
        REFERENCES Training (UserId, TrainerId);

-- Review 관계
ALTER TABLE Review
    ADD CONSTRAINT fk_review_training
        FOREIGN KEY (trainingId) REFERENCES Training(trainerid);

-- Chat 관계
ALTER TABLE UserChat
    ADD CONSTRAINT fk_User_has_ChatRoom_User1 FOREIGN KEY (UserId)
        REFERENCES User (id);
ALTER TABLE UserChat
    ADD CONSTRAINT fk_User_has_ChatRoom_ChatRoom1 FOREIGN KEY (ChatRoomId)
        REFERENCES Chat (id);

-- Message 관계
ALTER TABLE Message
    ADD CONSTRAINT fk_Message_UserChatRoom1 FOREIGN KEY (UserId, ChatId)
        REFERENCES UserChat (UserId, ChatRoomId);

-- AI_PROMPT 관계
ALTER TABLE AI_PROMPT
    ADD CONSTRAINT fk_AI_PROMPT_User1 FOREIGN KEY (UserId)
        REFERENCES User (id);

-- HBTI 관계
ALTER TABLE HBTI
    ADD CONSTRAINT fk_HBTI_User1 FOREIGN KEY (UserId)
        REFERENCES User (id);