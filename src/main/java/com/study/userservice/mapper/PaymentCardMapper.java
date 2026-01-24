package com.study.userservice.mapper;

import com.study.userservice.dto.PaymentCardDto;
import com.study.userservice.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//через MapStruct переписать
@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(source = "user.id", target = "userId")
    PaymentCardDto toDto(PaymentCard card);

    @Mapping(source = "userId", target = "user.id")
    PaymentCard toEntity(PaymentCardDto dto);
}
