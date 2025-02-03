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
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "HBTI", ignore = true)
    @Mapping(target = "gym", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "authority", ignore = true)

    MyPageUserInfoDTO toDto(User user);
    User toEntity(MyPageUserInfoDTO dto);

    void UserFromMyPageUserInfoDto(MyPageUserInfoDTO dto, @MappingTarget User user);
}