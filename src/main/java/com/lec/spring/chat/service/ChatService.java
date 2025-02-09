package com.lec.spring.chat.service;

import com.lec.spring.base.domain.User;
import com.lec.spring.chat.DTO.MessageDTO;
import com.lec.spring.chat.domain.Chat;
import com.lec.spring.chat.domain.Message;
import com.lec.spring.chat.domain.UserChat;
import com.lec.spring.chat.domain.UserChatId;
import com.lec.spring.chat.repository.ChatRepository;
import com.lec.spring.chat.repository.MessageRepository;
import com.lec.spring.chat.repository.UserChatRepository;
import com.lec.spring.chat.repository.ChatUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;
    private final ChatUserRepository chatUserRepository;
    private final MessageRepository messageRepository;

    public ChatService(ChatRepository chatRepository, UserChatRepository userChatRepository, ChatUserRepository chatUserRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.userChatRepository = userChatRepository;
        this.chatUserRepository = chatUserRepository;
        this.messageRepository = messageRepository;
    }

    // 1:1 채팅방 생성 또는 기존 채팅방 반환
    @Transactional
    public Chat createOrGetChat(Long userId, Long trainerId) {
        System.out.println("createOrGetChat() 호출");
        // 🔍 기존 채팅방이 있는지 확인
        Optional<Long> existingChatId = userChatRepository.findCommonChatId(userId, trainerId);
        if (existingChatId.isPresent()) {
            System.out.println("이미 존재하는 채팅방 ID: " + existingChatId.get());
            return chatRepository.findById(existingChatId.get())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방 ID"));
        }


        // 🔍 유저와 트레이너 정보 조회
        System.out.println("유저와 트레이너 정보 조회");
        User user = chatUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID"));
        User trainer = chatUserRepository.findById(trainerId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 트레이너 ID"));

        // 🔥 새로운 채팅방 생성 (ID 먼저 생성해야 함)
        System.out.println("채팅방 생성");

        Chat chat = chatRepository.saveAndFlush(new Chat());
        System.out.println("생성된 채팅방 " + chat);
        System.out.println(chatRepository.findById(chat.getId()));


        // 🔥 UserChatId를 명확히 설정
        UserChatId userChatId1 = new UserChatId(userId, chat.getId());
        UserChatId userChatId2 = new UserChatId(trainerId, chat.getId());
        System.out.println("저장된 유저 정보" + userChatId1);
        System.out.println("저장된 트레이너 정보" + userChatId2);



        // 🔥 두 개의 레코드(UserChat) 삽입
        userChatRepository.save(new UserChat(userChatId1, user, chat));
        userChatRepository.save(new UserChat(userChatId2, trainer, chat));

        return chat;
    }

    // 채팅방 나가기
    @Transactional
    public void leaveChat(Long chatId, Long userId) {
        UserChatId id = new UserChatId(userId, chatId);

        if (!userChatRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 유저는 이 채팅방에 존재하지 않습니다.");
        }

        // 채팅방에서 유저 제거
        userChatRepository.deleteById(id);

        // 채팅방에 남은 유저가 없다면 채팅방 삭제
        if (!userChatRepository.existsByChatId(chatId)) {
            chatRepository.deleteById(chatId);
        } else {
            // 남아 있는 유저가 있을 경우 해당 유저들 제거
            List<UserChat> remainingUsers = userChatRepository.findByChatId(chatId);
            for (UserChat userChat : remainingUsers) {
                userChatRepository.delete(userChat);
            }
        }

        // 채팅방 삭제
        chatRepository.deleteById(chatId);
    }

    @Transactional
    public MessageDTO saveMessage(Long chatId, MessageDTO messageDTO) {
        // 유저 정보 조회
        User sender = chatUserRepository.findById(messageDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID"));

        // 채팅방 존재 여부 확인
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방 ID"));

        System.out.println("메시지 저장 중");
        // 메시지 저장
        Message message = Message.builder()
                .user(sender)
                .chat(chat)
                .content(messageDTO.getContent())
                .isChecked(false)  // 초기 상태는 안 읽음
                .timestamp(new Date())
                .build();

        Message savedMessage = messageRepository.save(message);
        System.out.println("메시지 저장 완료");

        messageDTO.setMessageId(savedMessage.getMessageId());

        System.out.println("메시지가 저장되었습니다: " + message);

        // MessageDTO 변환 후 반환
        return MessageDTO.fromEntity(savedMessage);
    }

    // 채팅방 목록 불러오기
    @Transactional(readOnly = true)
    public List<Chat> getUserChats(Long userId) {
        return userChatRepository.findByUserId(userId).stream()
                .map(UserChat::getChat)
                .collect(Collectors.toList());
    }

    // 특정 1:1 채팅방에서 상대방 유저 찾기
    public User getOtherUserInChat(Long chatId, Long userId) {
        return userChatRepository.findOtherUserInChat(chatId, userId)
                .orElse(null); // 상대방이 없을 경우 null 반환
    }

    // 채팅 목록 불러오기
    @Transactional(readOnly = true)
    public List<MessageDTO> getChatMessages(Long chatId) {
        List<Message> messages = messageRepository.findByChatIdOrderByTimestampAsc(chatId);
        return messages.stream()
                .map(MessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void updateChatIsChecked(Long chatId, Long userId) {
        messageRepository.updateIsCheckedByChatId(chatId, userId);
    }


}
