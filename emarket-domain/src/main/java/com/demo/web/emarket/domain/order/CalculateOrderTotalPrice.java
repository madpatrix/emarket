package com.demo.web.emarket.domain.order;

import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.product.Price;
import com.demo.web.emarket.domain.product.Product;
import com.demo.web.emarket.domain.product.ProductsPort;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toMap;

@DDD.DomainService
public class CalculateOrderTotalPrice {
    private OrdersPort ordersPort;
    private ProductsPort productsPort;

    @Autowired
    public CalculateOrderTotalPrice(OrdersPort ordersPort, ProductsPort productsPort) {
        this.ordersPort = ordersPort;
        this.productsPort = productsPort;
    }

    public Price totalPriceOfOrder(UniqueId orderId) {
        Order order = ordersPort.getOrThrow(orderId);
        List<Line> orderLines = order.orderLines();
        Map<UniqueId, Quantity> productIdsQuantities = orderLines.stream()
                .collect(toMap(Line::productId, Line::quantity));
        Map<UniqueId, Product> orderProducts = productsPort.findAll(productIdsQuantities.keySet())
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        return orderLines.stream().reduce(
                new Price(ZERO),
                (Price p, Line l) -> p.sumUp(totalPriceForASingleTypeOfProduct(orderProducts, l)),
                Price::sumUp
        );
    }

    private Price totalPriceForASingleTypeOfProduct(Map<UniqueId, Product> orderProducts, Line orderLine) {
        return orderLine.totalPrice(orderProducts.get(orderLine.productId()));
    }

}
