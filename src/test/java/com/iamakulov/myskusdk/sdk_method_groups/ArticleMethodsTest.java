package com.iamakulov.myskusdk.sdk_method_groups;

import com.iamakulov.myskusdk.*;
import com.iamakulov.myskusdk.containers.*;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ArticleMethodsTest {
    private FullArticle articleResult;

    @Test
    public void shouldParseGenericArticleContent() throws Exception {
        loadArticleFromFile("normal_article.html");

        ArticleContent articleContent = articleResult.getContent();
        assertEquals("Mikeoff", articleContent.getAuthor().getUsername());
        assertTrue("Body should have content", articleContent.getBody().length() > 0);
        assertArrayEquals(
            new String[]{"AliExpress", "Кабели", "Пункт №18"},
            articleContent.getCategories()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList())
                .toArray()
        );
        assertEquals("23 октября 2016, 14:29", articleContent.getDate());
        assertEquals("$2.37", articleContent.getPrice());
        assertArrayEquals(
            new String[]{},
            articleContent.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList())
                .toArray()
        );
        assertEquals("Обзор только кабеля. Без переходника на USB-C (!)", articleContent.getTitle());
        assertEquals("1925", articleContent.getViewCount());
    }

    @Test
    public void shouldParseNormalArticleContent() throws Exception {
        loadArticleFromFile("normal_article.html");

        NormalArticleContent content = (NormalArticleContent) articleResult.getContent();
        assertEquals("+14", content.getRating());
        assertEquals("+1", content.getGoingToPurchase());
        assertEquals("http://go.mysku.ru/?r=http%3A%2F%2Fwww.aliexpress.com%2Fstore%2Fproduct%2F1M-1-8A-Charging-Cord-Flat-Retractable-Micro-USB-Data-Sync-Charger-Cable-Wire-Micro-USB%2F712974_32670602869.html&key=ms&ww=46003", content.getMarketLink());
    }

    @Test
    public void shouldParseCouponArticleContent() throws Exception {
        loadArticleFromFile("coupon_article.html");

        CouponArticleContent content = (CouponArticleContent) articleResult.getContent();
        assertEquals("0", content.getRating());
        assertEquals("bgsolar", content.getCouponCode());
        assertEquals("31.12.2016", content.getCouponExpirationDate());
        assertEquals("http://go.mysku.ru/?r=http%3A%2F%2Fwww.banggood.com%2FSolar-Igniter-Take-Firearms-Emergency-Tool-Gear-Camping-p-1034979.html&key=ms&ww=48129", content.getMarketLink());
    }

    @Test
    public void shouldParseComments() throws Exception {
        loadArticleFromFile("normal_article.html");

        List<Comment> commentList = articleResult.getComments();
        assertEquals(8, commentList.size());
        assertEquals(
            21,
            // Flat list of all comments
            commentList.stream()
                .flatMap(ArticleMethodsTest::flattenComments)
                .collect(Collectors.toList())
                .size()
        );

        Comment comment = commentList.get(0);
        assertEquals("ptolomay", comment.getAuthor().getUsername());
        assertEquals("http://assets.mysku-st.ru/templates/skin/mysku.v3/images/avatar_48x48.jpg", comment.getAuthorThumbnail());
        assertEquals("У меня похожая сама разобралась на части.", comment.getBody());
        assertEquals("23 октября 2016, 14:34", comment.getDate());
        assertEquals("1674964", comment.getId().id);
        assertEquals("0", comment.getRating());
        assertEquals(1, comment.getReplies().size());
        assertEquals("Mikeoff", comment.getReplies().get(0).getAuthor().getUsername());
    }

    private void loadArticleFromFile(String fileName) throws InterruptedException {
        final CountDownLatch lock = new CountDownLatch(1);

        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(TestHelpers.createCustomRetriever(fileName));
        sdk.article().getFullArticle(new ArticleContent.Id(""), new MyskuCallback<FullArticle>() {
            @Override
            public void onSuccess(FullArticle result) {
                articleResult = result;
                lock.countDown();
            }

            @Override
            public void onError(MyskuError error) {
                error.getException().printStackTrace();
                assertTrue("The method should not be called", false);

                lock.countDown();
            }
        });

        lock.await(); // `CompletableFuture` in the retriever is async, so wait until it’s resolved.
    }

    // http://stackoverflow.com/questions/32656888/recursive-use-of-stream-flatmap
    private static Stream<Comment> flattenComments(Comment comment) {
        return Stream.concat(
            Stream.of(comment),
            comment.getReplies().stream().flatMap(ArticleMethodsTest::flattenComments)
        );
    }
}