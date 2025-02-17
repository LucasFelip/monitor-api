package com.plataform.monitorsystem.domain.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class HealthCheckService {

    public boolean checkApiHealth(String url) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            Map body = response.getBody();
            if (body != null && "UP".equalsIgnoreCase(String.valueOf(body.get("status")))) {
                return true;
            }
        }
        throw new Exception("Health check retornou status diferente de UP: " + response.getBody());
    }
}

