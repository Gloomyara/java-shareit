package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.abstraction.GatewayClient;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.request.RequestDtoIn;

import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.REQUESTS_PATH;

@Service
public class RequestClient extends GatewayClient {

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + REQUESTS_PATH))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getById(Long requestId, Long userId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> post(RequestDtoIn requestDtoIn, Long userId) {
        return post("", userId, requestDtoIn);
    }

    public ResponseEntity<Object> put(ItemDtoIn bookingDtoIn, Long userId) {
        return put("", userId, bookingDtoIn);
    }

    public ResponseEntity<Object> findAll(Integer from, Integer size, Long userId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAllCreatedByUser(Long ownerId) {
        return get("/", ownerId);
    }
}
