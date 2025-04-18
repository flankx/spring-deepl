package com.github.springdeepl.config;

import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.SAXException;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TikaConfiguration {

    private final ResourceLoader resourceLoader;

    @Bean
    public Tika tika() throws IOException, TikaException, SAXException {
        Resource resource = resourceLoader.getResource("classpath:tika-config.xml");
        TikaConfig config = new TikaConfig(resource.getInputStream());
        return new Tika(config);
    }

}
