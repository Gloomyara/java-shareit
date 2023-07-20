package ru.practicum.shareit.abstraction;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.OWNER_ID_HEADER;

public class GatewayClient {
    protected final RestTemplate rest;

    public GatewayClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> params) {
        return sendRequest(HttpMethod.GET, path, null, params, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> params) {
        return sendRequest(HttpMethod.GET, path, userId, params, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, long userId, T body) {
        return post(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> params, T body) {
        return sendRequest(HttpMethod.POST, path, userId, params, body);
    }

    protected <T> ResponseEntity<Object> put(String path, long userId, T body) {
        return put(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> put(String path, long userId, @Nullable Map<String, Object> params, T body) {
        return sendRequest(HttpMethod.PUT, path, userId, params, body);
    }

    protected <T> ResponseEntity<Object> put(String path, T body) {
        return sendRequest(HttpMethod.PUT, path, null, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId) {
        return patch(path, userId, null, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId, T body) {
        return patch(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> params, T body) {
        return sendRequest(HttpMethod.PATCH, path, userId, params, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    protected ResponseEntity<Object> delete(String path, long userId) {
        return delete(path, userId, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> params) {
        return sendRequest(HttpMethod.DELETE, path, userId, params, null);
    }

    private <T> ResponseEntity<Object> sendRequest(HttpMethod method, String path, Long userId,
                                                   @Nullable Map<String, Object> params,
                                                   @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        ResponseEntity<Object> shareItServerResponse;
        try {
            if (params != null) {
                shareItServerResponse = rest.exchange(path, method, requestEntity, Object.class, params);
            } else {
                shareItServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareItServerResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set(OWNER_ID_HEADER, String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}
