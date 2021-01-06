package com.demo.web.emarket.exposition.order;

import com.demo.web.emarket.application.order.OrderSingleProductCommand;
import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.exposition.product.ProductRepresentation;
import com.demo.web.emarket.exposition.product.ProductRepresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {ProductRepresentationMapper.class})
public abstract class OrderRepresentationMapper {

    @Mappings({
            @Mapping(target="customerId", source = "representation"),
            @Mapping(target="product", source = "product"),
            @Mapping(target="quantity.value", source = "quantity")
    })
    abstract OrderSingleProductCommand toOrderSingleProductCommand(OrderRepresentation representation);


    UniqueId mapId(OrderRepresentation representation){
        return new UniqueId(representation.id);
    }
}
