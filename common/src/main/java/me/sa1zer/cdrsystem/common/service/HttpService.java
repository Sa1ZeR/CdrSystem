package me.sa1zer.cdrsystem.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class HttpService {

    private final RestTemplate restTemplate;

    public <T> ResponseEntity<T> sendGetRequest(String url, Class<T> responseType) {
        ResponseEntity<T> response = restTemplate.getForEntity(url, responseType);
        validate(response);

        return response;
    }

    public <T> ResponseEntity<T> sendGetRequest(String url, Class<T> responseType, String... args) {
        HashMap<String, String> map = new HashMap<>();
        if(args.length % 2 != 0)
            throw new RuntimeException("Incorrect args size");
        for(int i =0; i < args.length; i+=2) {
            map.put(args[i], args[i+1]);
        }

        ResponseEntity<T> response = restTemplate.getForEntity(url, responseType, map);
        validate(response);

        return response;
    }

    public <T> ResponseEntity<T> sendPostRequest(String url, Object request, Class<T> responseType) {
        return restTemplate.postForEntity(url, request, responseType);
    }

    public <T> T sendPatchRequest(String url, Object object, Class<T> responseType) {
        return restTemplate.patchForObject(url, object, responseType);
    }

    private void validate(ResponseEntity<?> response) {
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Bad request: {}", response);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    response.getBody() != null ? response.getBody().toString() : response.toString());
        }
    }
}
