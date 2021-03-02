package com.demo.web.emarket.application.customer;

import com.demo.web.emarket.application.order.OrderProduct;
import com.demo.web.emarket.application.order.OrderSingleProductCommand;
import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.order.*;
import com.demo.web.emarket.domain.order.event.order.OrderDomainEventHandler;
import com.demo.web.emarket.domain.product.Price;
import com.demo.web.emarket.domain.product.Product;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderProductSteps {
    private final OrdersPort inMemoryOrdersPort = new InMemoryOrdersPort();
    private final OrderDomainEventHandler orderDomainEventHandler = Mockito.mock(OrderDomainEventHandler.class);
    private final OrderProduct sut = new OrderProduct(inMemoryOrdersPort, orderDomainEventHandler); //system under test
    private UniqueId newOrderId;
    private Product phone;


    @Given("^there are no orders for a customer$")
    public void thereAreNoOrdersForACustomer() {
        assertThat(inMemoryOrdersPort.getAll()).isEmpty();
    }

    @When("^that customer buys a phone with a price of \"([^\"]*)\"$")
    public void thatCustomerBuysAPhoneWithAPriceOf(String price) {
        phone = new Product(new Price(price));
        UniqueId customerId = new UniqueId();
        Quantity quantity = new Quantity(1);
        newOrderId = sut.orderProduct(new OrderSingleProductCommand(customerId, phone, quantity));
    }

    @Then("^there is \"([^\"]*)\" \"([^\"]*)\" phone order for that customer$")
    public void thereIsPhoneOrderForThatCustomer(int noOfExpectedOrders, OrderStatus expectedOrderStatus) {
        assertThat(inMemoryOrdersPort.getAll()).hasSize(noOfExpectedOrders);

        Order newOrder = inMemoryOrdersPort.getOrThrow(newOrderId);
        assertThat(newOrder.status()).isEqualTo(expectedOrderStatus);

        Line singleOrderLine = newOrder.orderLines().get(0);
        assertThat(singleOrderLine.quantity()).isEqualTo(new Quantity(1));
        assertThat(singleOrderLine.productId()).isEqualTo(phone.getId());
    }
}
