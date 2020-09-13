package com.lydia.client.model.request;

import lombok.Value;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

import java.net.URI;

@Value
public class ApiRequest {

    URI uri;
    HttpEntity<MultiValueMap<String, String>> entity;
}
