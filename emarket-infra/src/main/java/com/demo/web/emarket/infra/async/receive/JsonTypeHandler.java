package com.demo.web.emarket.infra.async.receive;

@FunctionalInterface
public interface JsonTypeHandler {
    void handle(String json, String msgType, String msgId);
}
