package com.lec.spring._common.listener;

import java.time.LocalDateTime;

public interface Auditable {
    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime regDate);
}
