package com.demo.web.emarket.domain.order;

import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.product.ProductsPort;
import com.demo.web.emarket.domain.product.InMemoryProductsPort;
import com.demo.web.emarket.domain.product.Price;
import com.demo.web.emarket.domain.product.Product;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class CalculateOrderTotalPriceTest {

    private Product smartphone = someSmartphone();
    private Product tv = someTV();

    private OrdersPort inMemoryOrdersPort = new InMemoryOrdersPort();
    private ProductsPort inMemoryProductsPort = new InMemoryProductsPort();
    private CalculateOrderTotalPrice sut = new CalculateOrderTotalPrice(inMemoryOrdersPort, inMemoryProductsPort);


    @Before
    public void when_tv_and_smartphone_in_system() {
        inMemoryProductsPort.add(smartphone);
        inMemoryProductsPort.add(tv);
    }

    @Test
    public void when_2_and_2_types_of_products_then_total_price_should_be_the_total_of_4_products() {
        Line smartphoneLine = new Line(new Quantity(2), smartphone.getId());
        Line tvLine = new Line(new Quantity(2), tv.getId());
        Order order = new Order(asList(smartphoneLine, tvLine), OrderStatus.INITIATED, new UniqueId());
        inMemoryOrdersPort.add(order);
        Price actualPrice = sut.totalPriceOfOrder(order.getId());
        assertThat(actualPrice).isEqualTo(new Price("9600"));
    }

    private Product someTV() {
        return new Product(new Price("1000.00"));
    }

    private Product someSmartphone() {
        return new Product(new Price("3800"));
    }
}
