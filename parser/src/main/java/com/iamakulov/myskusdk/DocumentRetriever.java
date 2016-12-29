package com.iamakulov.myskusdk;

import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DocumentRetriever {
    CompletableFuture<Document> get(String url, Map<String, String> cookies);

    CompletableFuture<Document> post(String url, String body, Map<String, String> cookies);
}
