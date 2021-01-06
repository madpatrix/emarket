package com.demo.web.emarket.exposition;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(value = "/api", produces = APPLICATION_JSON_UTF8_VALUE)
public @interface RootRestController {
}
