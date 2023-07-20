package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.abstraction.GatewayClient;
import ru.practicum.shareit.user.UserDtoIn;

import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.USERS_PATH;

@Service
public class UserClient extends GatewayClient {

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + USERS_PATH))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> post(UserDtoIn dtoIn) {
        return post("", dtoIn);
    }

    public ResponseEntity<Object> put(UserDtoIn dtoIn) {
        return put("", dtoIn);
    }

    public ResponseEntity<Object> patch(Long userId, Map<String, Object> fields) {
        return patch("/" + userId, fields);
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public void delete(Long userId) {
        delete("/" + userId);
    }
}
