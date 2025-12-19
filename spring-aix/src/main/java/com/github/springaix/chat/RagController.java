package com.github.springaix.chat;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RagController {

    private final VectorStore vectorStore;

    @PostMapping("/rag/upload")
    public void uploadFile(MultipartFile file) {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(file.getResource());
        // 2. 生成文档
        List<Document> documents = tikaDocumentReader.read();
        // 3. 存入Elasticsearch
        vectorStore.add(documents);
    }

    @PostMapping("/rag/similaritySearch")
    public List<Document> search(@RequestParam String query) {
        return vectorStore.similaritySearch(query);
    }

}
