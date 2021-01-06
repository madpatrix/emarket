package com.demo.web.emarket.exposition.product;

import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.product.Price;
import com.demo.web.emarket.domain.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper(imports = {LocalDate.class, BigDecimal.class, Price.class})
public interface ProductRepresentationMapper {
    public ProductRepresentationMapper INSTANCE = Mappers.getMapper(ProductRepresentationMapper.class);

    @Mappings({
            @Mapping(target="id", source = "representation"),
            @Mapping(target = "price", expression="java(new Price(new BigDecimal(representation.price)))"),
            @Mapping(target = "localDate", expression = "java(LocalDate.now())")
    })

    abstract Product toDomain(ProductRepresentation representation);

   public static UniqueId mapId(ProductRepresentation representation){
        return new UniqueId(representation.id);
    }

}
