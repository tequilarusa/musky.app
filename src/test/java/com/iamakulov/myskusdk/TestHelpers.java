package com.iamakulov.myskusdk;

import com.iamakulov.myskusdk.sdk_method_groups.ArticleMethodsTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

public class TestHelpers {
    public static DocumentRetriever createCustomRetriever(String fileName) {
        return new DocumentRetriever() {
            @Override
            public CompletableFuture<Document> get(String url, Map<String, String> cookies) {
                return loadDocumentFromFile(fileName);
            }

            @Override
            public CompletableFuture<Document> post(String url, String body, Map<String, String> cookies) {
                return null;
            }
        };
    }

    public static DocumentRetriever createCustomRetrieverWithUrlCheck(String expectedRequestUrl, String fileName) {
        return new DocumentRetriever() {
            @Override
            public CompletableFuture<Document> get(String actualRequestUrl, Map<String, String> cookies) {
                assertEquals(expectedRequestUrl, actualRequestUrl);
                return loadDocumentFromFile(fileName);
            }

            @Override
            public CompletableFuture<Document> post(String url, String body, Map<String, String> cookies) {
                return null;
            }
        };
    }

    private static CompletableFuture<Document> loadDocumentFromFile(String fileName) {
        try {
            URL resourceUrl = ArticleMethodsTest.class.getResource(fileName);
            Path resPath = Paths.get(resourceUrl.toURI());
            String html = new String(Files.readAllBytes(resPath), "UTF8");
            Document document = Jsoup.parse(html);

            CompletableFuture<Document> future = new CompletableFuture<>();
            future.complete(document);
            return future;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
