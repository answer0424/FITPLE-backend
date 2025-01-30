package com.lec.spring.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lec.spring._common.domain.BaseEntity;
import com.lec.spring.base.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Message")
public class Message extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "chatId", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Chat chat;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isChecked;

}

