package com.iamakulov.myskusdk;

import com.iamakulov.myskusdk.sdk_method_groups.ArticleMethodsTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class TestHelpers {
    public static DocumentRetriever createCustomRetriever(final String fileName) {
        return new DocumentRetriever() {
            @Override
            public void get(String url, Map<String, String> cookies, MyskuCallback<Document> callback) {
                try {
                    callback.onSuccess(loadDocumentFromFile(fileName));
                } catch (Exception e) {
                    callback.onError(new MyskuError(e));
                }
            }

            @Override
            public void post(String url, String body, Map<String, String> cookies, MyskuCallback<Document> callback) {

            }
        };
    }

    public static DocumentRetriever createCustomRetrieverWithUrlCheck(final String expectedRequestUrl, final String fileName) {
        return new DocumentRetriever() {
            @Override
            public void get(String actualRequestUrl, Map<String, String> cookies, MyskuCallback<Document> callback) {
                assertEquals(expectedRequestUrl, actualRequestUrl);

                try {
                    callback.onSuccess(loadDocumentFromFile(fileName));
                } catch (Exception e) {
                    callback.onError(new MyskuError(e));
                }
            }

            @Override
            public void post(String url, String body, Map<String, String> cookies, MyskuCallback<Document> callback) {

            }
        };
    }

    private static Document loadDocumentFromFile(String fileName) throws URISyntaxException, IOException {
        URL resourceUrl = ArticleMethodsTest.class.getResource(fileName);
        Path resPath = Paths.get(resourceUrl.toURI());
        String html = new String(Files.readAllBytes(resPath), "UTF8");

        return Jsoup.parse(html);
    }
}
