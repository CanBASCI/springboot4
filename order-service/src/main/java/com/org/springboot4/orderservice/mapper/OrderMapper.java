package com.org.springboot4.orderservice.mapper;

import com.org.springboot4.orderservice.domain.Order;
import com.org.springboot4.orderservice.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderDto toDto(Order order);
    Order toEntity(OrderDto dto);
}

