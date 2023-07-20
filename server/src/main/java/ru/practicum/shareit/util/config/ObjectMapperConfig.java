package ru.practicum.shareit.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

import static ru.practicum.shareit.util.UtilConstants.DATE_TIME_PATTERN;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_TIME_PATTERN));
        return objectMapper;
    }
}
