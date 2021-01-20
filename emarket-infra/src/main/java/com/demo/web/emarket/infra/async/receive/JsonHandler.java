package com.demo.web.emarket.infra.async.receive;

@FunctionalInterface
public interface JsonHandler {
    void handle(String json, String msgId);
}
