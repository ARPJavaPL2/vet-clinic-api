package com.simonjoz.vetclinic.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

@Configuration
public class DateTimeFormatConfiguration implements WebMvcConfigurer {


    private static final String timeFormat = "HH:mm";
    private static final String dateFormat = "yyyy-MM-dd";

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonDateTimeCustomizer() {
        return builder -> {
            builder.serializers(new LocalTimeSerializer(DateTimeFormatter.ofPattern(timeFormat)));
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
        };
    }
}
