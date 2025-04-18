package com.github.springdeepl.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final VectorStore vectorStore;
    private final TikaParserService parserService;

    public String processDocument(MultipartFile file) {

        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();

        // 1. 使用Tika解析文件内容及元数据
        String text = null;
        try (InputStream inputStream = file.getInputStream()) {
            text = parserService.parseContent(inputStream);
        } catch (Exception e) {
            log.error("Error processing file: {}", fileName, e);
            throw new RuntimeException("Error processing file", e);
        }
        if (text == null || text.isEmpty()) {
            log.error("File content is empty: {}", fileName);
            throw new RuntimeException("File content is empty");
        }
        // 2. 生成文档
        Document document =
            Document.builder().text(text).metadata(Map.of("fileName", fileName, "fileType", fileType)).build();

        // 3. 存入Elasticsearch
        vectorStore.add(List.of(document));

        return document.getId();
    }

}
