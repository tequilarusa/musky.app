package com.iamakulov.myskusdk.sdk_method_groups;

import com.iamakulov.myskusdk.*;
import com.iamakulov.myskusdk.containers.ArticlePreview;
import com.iamakulov.myskusdk.containers.Category;
import com.iamakulov.myskusdk.containers.User;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class ArticlesMethodsTest {
    private List<ArticlePreview> articlesResult;

    @Test
    public void shouldGetRatedArticles() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);
        int customPage = 5;

        DocumentRetriever retriever = TestHelpers.createCustomRetrieverWithUrlCheck(
                "http://mysku.ru/index/page" + (customPage + 1) + "/",
                "articles_list.html"
        );
        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(retriever);

        sdk.articles().getRatedArticles(customPage, new MyskuCallback<List<ArticlePreview>>() {
            @Override
            public void onSuccess(List<ArticlePreview> result) {
                articlesResult = result;
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

        checkArticles(articlesResult);
    }

    @Test
    public void shouldGetRecentArticles() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);
        int customPage = 5;

        DocumentRetriever retriever = TestHelpers.createCustomRetrieverWithUrlCheck(
                "http://mysku.ru/blog/all/page" + (customPage + 1) + "/",
                "articles_list.html"
        );
        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(retriever);

        sdk.articles().getRecentArticles(customPage, new MyskuCallback<List<ArticlePreview>>() {
            @Override
            public void onSuccess(List<ArticlePreview> result) {
                articlesResult = result;
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

        checkArticles(articlesResult);
    }

    @Test
    public void shouldGetTopArticles() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);
        int customPage = 5;

        DocumentRetriever retriever = TestHelpers.createCustomRetrieverWithUrlCheck(
                "http://mysku.ru/top/topic/7d/page" + (customPage + 1) + "/",
                "articles_list.html"
        );
        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(retriever);

        sdk.articles().getTopArticles(customPage, MyskuSdk.TopArticlesInterval.WEEK, new MyskuCallback<List<ArticlePreview>>() {
            @Override
            public void onSuccess(List<ArticlePreview> result) {
                articlesResult = result;
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

        checkArticles(articlesResult);
    }

    @Test
    public void shouldGetArticlesByCategory() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);
        int customPage = 5;

        DocumentRetriever retriever = TestHelpers.createCustomRetrieverWithUrlCheck(
                "http://mysku.ru/blog/china-stores/page" + (customPage + 1) + "/",
                "articles_list.html"
        );
        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(retriever);

        sdk.articles().getArticlesByCategory(new Category.Id("blog/china-stores"), customPage, new MyskuCallback<List<ArticlePreview>>() {
            @Override
            public void onSuccess(List<ArticlePreview> result) {
                articlesResult = result;
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

        checkArticles(articlesResult);
    }

    @Test
    public void shouldGetArticlesCreatedByUser() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);
        int customPage = 5;

        DocumentRetriever retriever = TestHelpers.createCustomRetrieverWithUrlCheck(
                "http://mysku.ru/my/ruslan96/page" + (customPage + 1) + "/",
                "articles_list.html"
        );
        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(retriever);

        sdk.articles().getArticlesCreatedByUser(new User.Id("ruslan96"), customPage, new MyskuCallback<List<ArticlePreview>>() {
            @Override
            public void onSuccess(List<ArticlePreview> result) {
                articlesResult = result;
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

        checkArticles(articlesResult);
    }

    @Test
    public void shouldGetArticlesFavoritedByUser() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);
        int customPage = 5;

        DocumentRetriever retriever = TestHelpers.createCustomRetrieverWithUrlCheck(
                "http://mysku.ru/profile/ruslan96/favourites/page" + (customPage + 1) + "/",
                "articles_list.html"
        );
        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(retriever);

        sdk.articles().getArticlesFavoritedByUser(new User.Id("ruslan96"), customPage, new MyskuCallback<List<ArticlePreview>>() {
            @Override
            public void onSuccess(List<ArticlePreview> result) {
                articlesResult = result;
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

        checkArticles(articlesResult);
    }

    @Test
    public void searchArticles() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);
        int customPage = 5;

        DocumentRetriever retriever = TestHelpers.createCustomRetrieverWithUrlCheck(
                "http://mysku.ru/search/topics/page" + (customPage + 1) + "/?q=phone",
                "articles_list.html"
        );
        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(retriever);

        sdk.articles().searchArticles("phone", customPage, new MyskuCallback<List<ArticlePreview>>() {
            @Override
            public void onSuccess(List<ArticlePreview> result) {
                articlesResult = result;
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

        checkArticles(articlesResult);
    }

    private void checkArticles(List<ArticlePreview> articles) {
        assertEquals(10, articles.size());
        assertEquals("Мини ручная барсеточка «А-ля, 90-е»", articles.get(0).getContent().getTitle());
        assertEquals("3", articles.get(0).getCommentCount());

        assertEquals("QCY Q26, те же грабли", articles.get(2).getContent().getTitle());
        assertEquals("24", articles.get(2).getCommentCount());
    }
}