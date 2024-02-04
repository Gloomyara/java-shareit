package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.abstraction.GatewayClient;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;

import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.ITEM_PATH;

@Service
public class ItemClient extends GatewayClient {

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ITEM_PATH))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllByOwnerId(Integer from, Integer size, Long userId) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> searchByNameOrDescription(Integer from, Integer size, String text, Long userId) {
        Map<String, Object> params = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> getById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> post(ItemDtoIn itemDtoIn, Long userId) {
        return post("", userId, itemDtoIn);
    }

    public ResponseEntity<Object> put(ItemDtoIn bookingDtoIn, Long userId) {
        return put("", userId, bookingDtoIn);
    }

    public ResponseEntity<Object> patch(Long itemId, Map<String, Object> fields, Long userId) {
        return patch("/" + itemId, userId, fields);
    }

    public ResponseEntity<Object> postComment(Long itemId, Long userId, CommentDtoIn commentDtoIn) {
        return post("/" + itemId + "/comment", userId, commentDtoIn);
    }
}
