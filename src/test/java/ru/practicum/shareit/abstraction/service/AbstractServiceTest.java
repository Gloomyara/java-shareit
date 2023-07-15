package ru.practicum.shareit.abstraction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.abstraction.model.EntityClass;
import ru.practicum.shareit.exceptions.JsonUpdateFieldsException;
import ru.practicum.shareit.util.config.ObjectMapperConfig;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class AbstractServiceTest<E extends EntityClass> {

    private final ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper();
    protected final EasyRandom generator = new EasyRandom();
    protected AbstractService service;


    protected abstract E getEntity();

    protected abstract Map<String, Object> getFields();

    protected abstract E getUpdated();

    @BeforeEach
    void setUp() {
        service = Mockito.mock(AbstractService.class, Mockito.CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(service, "objectMapper", objectMapper);
    }

    @Test
    void tryUpdateFieldsWithCorrectMapFields() {
        assertEquals(service.tryUpdateFields(getEntity(), getFields()), getUpdated());
    }

    @Test
    void tryUpdateFieldsWithIncorrectMapFields() {
        assertThrows(JsonUpdateFieldsException.class,
                () -> service.tryUpdateFields(getEntity(), Map.of("f2g3h5fd2asf", "zxc321gfd3")));
    }
}
