package com.demo.web.emarket.infra.async.receive;

@FunctionalInterface
public interface ObjectHandler<T> {
    void handle(T t, String msgId);
}
