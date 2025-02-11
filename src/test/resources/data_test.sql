insert into hbti ( user_id, HBTI, mb_score
                 , ei_score
                 , cn_score
                 , pg_score)
VALUES (126, 'MENP', 68, 88, 32, 88);

delete from hbti where user_id = 126;

update User
set address = '서울시 여러분 식초'
where id = 127;
update User
set birth = '1998-06-16'
where id = 127;

INSERT INTO HBTI ( user_id, HBTI, mb_score
                 , ei_score
                 , cn_score
                 , pg_score)
VALUES
-- MICP (3)
(126, 'BENG', 70, 20, 58, 90);

# 매칭페이지 데이터 불리기 위해...
UPDATE HBTI
SET HBTI = 'BICG';
# 사진 경로 통일 시킴
UPDATE user
SET profile_image = 'upload/profile/njz.jpeg';


INSERT INTO Training (user_id, trainer_id, times, total_stamps, coupons) VALUES
                    (1, 126, 10, 5, 0),
                    (38, 126, 10, 5, 0),
                    (2, 126, 15, 7, 1),
                    (3, 126, 20, 9, 2),
                    (4, 126, 25, 3, 3);

UPDATE training
SET coupons = 3
WHERE id = 56;


    # reservation 더미데이터
UPDATE reservation
SET training_id = 126, date = '2025-01-01 09:00:00', status = '운동완료', start_time = '09:00:00', exercise_time = 45
WHERE id = 1;

UPDATE reservation
SET training_id = 126, date = '2025-01-02 14:30:00', status = '운동완료', start_time = '14:30:00', exercise_time = 60
WHERE id = 2;

UPDATE reservation
SET training_id = 126, date = '2025-01-03 07:45:00', status = '운동완료', start_time = '07:45:00', exercise_time = 30
WHERE id = 3;

UPDATE reservation
SET training_id = 126, date = '2025-01-04 18:15:00', status = '운동취소', start_time = '18:15:00', exercise_time = 90
WHERE id = 4;

UPDATE reservation
SET training_id = 126, date = '2025-01-05 12:00:00', status = '운동완료', start_time = '12:00:00', exercise_time = 20
WHERE id = 5;

UPDATE reservation
SET training_id = 126, date = '2025-01-06 11:30:00', status = '운동완료', start_time = '11:30:00', exercise_time = 50
WHERE id = 6;

UPDATE reservation
SET training_id = 126, date = '2025-01-07 16:00:00', status = '운동완료', start_time = '16:00:00', exercise_time = 40
WHERE id = 7;

UPDATE reservation
SET training_id = 126, date = '2025-01-08 08:15:00', status = '운동취소', start_time = '08:15:00', exercise_time = 70
WHERE id = 8;


UPDATE user_chat
SET user_id = 126;

UPDATE user_chat
SET user_id = 51
WHERE chat_id IN (11,12, 13, 14, 15, 16, 17);

TRUNCATE TABLE message;

TRUNCATE TABLE user_chat;

TRUNCATE TABLE training;

SELECT * FROM user_chat WHERE user_id = 51;




Select * from user_chat where user_id = 51;


UPDATE user
SET gym_id = 2
WHERE id IN (51, 52, 53);

UPDATE user
SET authority = 'ROLE_ADMIN'
WHERE id=172;

