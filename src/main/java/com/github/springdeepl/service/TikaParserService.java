package com.github.springdeepl.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * The type Tika parser service.
 */
@Service
@RequiredArgsConstructor
public class TikaParserService {

    private final Tika tika;

    /**
     * 解析文件内容
     *
     * @param inputStream the input stream
     * @return the string
     * @throws IOException the io exception
     * @throws TikaException the tika exception
     */
    public String parseContent(InputStream inputStream) throws IOException, TikaException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            return tika.parseToString(bufferedInputStream);
        }
    }

    /**
     * 检测文件类型
     *
     * @param inputStream the input stream
     * @return the string
     * @throws IOException the io exception
     */
    public String detectFileType(InputStream inputStream) throws IOException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            return tika.detect(bufferedInputStream);
        }
    }

    /**
     * 提取元数据
     *
     * @param inputStream the input stream
     * @return the map
     * @throws IOException the io exception
     */
    public Map<String, Object> extractMetadata(InputStream inputStream) throws IOException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            Metadata metadata = new Metadata();
            tika.parse(bufferedInputStream, metadata);
            Map<String, Object> metaMap = new HashMap<>();
            for (String name : metadata.names()) {
                metaMap.put(name, metadata.get(name));
            }
            return metaMap;
        }
    }

}
