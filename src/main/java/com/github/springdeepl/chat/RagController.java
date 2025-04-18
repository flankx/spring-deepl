package com.github.springdeepl.chat;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.springdeepl.service.VectorStoreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RagController {

    private final VectorStoreService vectorStoreService;
    private final VectorStore vectorStore;

    @PostMapping("/rag/upload")
    public String uploadFile(MultipartFile file) {
        return vectorStoreService.processDocument(file);
    }

    @PostMapping("/rag/similaritySearch")
    public List<Document> search(@RequestParam String query) {
        return vectorStore.similaritySearch(query);
    }

}
