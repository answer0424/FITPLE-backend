package com.lec.spring.base.service.mapper;

import com.lec.spring.base.DTO.MyPageUserInfoDTO;
import com.lec.spring.base.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel ="spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "HBTI", ignore = true)
    @Mapping(source = "birth", target = "birth", dateFormat = "yyyy-MM-dd")
    MyPageUserInfoDTO toDto(User user);

    @Mapping(source = "userId", target = "id")
    @Mapping(target = "gym", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "authority", ignore = true)
    User toEntity(MyPageUserInfoDTO dto);

    void UserFromMyPageUserInfoDto(MyPageUserInfoDTO dto, @MappingTarget User user);
}