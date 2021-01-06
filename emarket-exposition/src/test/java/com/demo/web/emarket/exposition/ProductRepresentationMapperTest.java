package com.demo.web.emarket.exposition;

import com.demo.web.emarket.domain.product.Product;
import com.demo.web.emarket.exposition.product.ProductRepresentation;
import com.demo.web.emarket.exposition.product.ProductRepresentationMapper;
import org.junit.Test;

public class ProductRepresentationMapperTest {

        @Test
        public void test(){
            ProductRepresentation prod = new ProductRepresentation();
            prod.id = "dsdasdsadasdadsa";
            prod.price = "2";
            Product productDom = ProductRepresentationMapper.INSTANCE.toDomain(prod);
            System.out.println(productDom.getLocalDate());
        }
}
