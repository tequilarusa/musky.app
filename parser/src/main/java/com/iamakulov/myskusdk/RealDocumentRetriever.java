package com.iamakulov.myskusdk;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RealDocumentRetriever implements DocumentRetriever {
    @Override
    public CompletableFuture<Document> get(String url, Map<String, String> cookies) {
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        return asyncHttpClient
            .prepareGet(url)
            .execute()
            .toCompletableFuture()
            .thenApply(response -> {
                String responseBody = response.getResponseBody();
                return Jsoup.parse(responseBody);
            });
    }

    @Override
    public CompletableFuture<Document> post(String url, String body, Map<String, String> cookies) {
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        return asyncHttpClient
            .preparePost(url)
            .setBody(body)
            .execute()
            .toCompletableFuture()
            .thenApply(response -> {
                String responseBody = response.getResponseBody();
                return Jsoup.parse(responseBody);
            });
    }
}
