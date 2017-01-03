package com.iamakulov.myskusdk.sdk_method_groups;

import com.iamakulov.myskusdk.*;
import com.iamakulov.myskusdk.containers.*;
import com.iamakulov.myskusdk.helpers.UrlHelpers;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class UserMethodsTest {
    private UserDetails userDetails;
    private List<Comment> userComments;

    @Test
    public void shouldGetUserDetails() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        DocumentRetriever retriever = TestHelpers.createCustomRetrieverWithUrlCheck(
            "http://mysku.ru/profile/vbudennyj/",
            "user_details.html"
        );
        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(retriever);

        sdk.user().getUserDetails(new User.Id("vbudennyj"), new MyskuCallback<UserDetails>() {
            @Override
            public void onSuccess(UserDetails result) {
                userDetails = result;
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

        assertEquals("UA / UA", userDetails.getAutodetectedCountry());
        assertArrayEquals(
            new String[]{
                "nso77", "Kirtsun", "rubyfox", "Zloi",
                "kirich", "Viper-X", "CarlCori", "valendar",
                "KoOz", "koijl", "angelina198737"
            },
            userDetails.getFriends()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList())
                .toArray()
        );
        assertEquals("+157.70", userDetails.getKarma());
        assertEquals("54", userDetails.getKarmaVoteCount());
        assertEquals("25 декабря 2016, 19:54", userDetails.getLastVisit());
        assertArrayEquals(
            new Category[]{
                createCategory("AliExpress", "http://mysku.ru/blog/aliexpress/"),
                createCategory("Скидки и распродажи", "http://mysku.ru/blog/discounts/"),
                createCategory("Магазины Китая", "http://mysku.ru/blog/china-stores/"),
            },
            userDetails.getParticipation().toArray()
        );

        Map<String, String> personalDataMap = new HashMap<>();
        personalDataMap.put("Пол", "мужской");
        personalDataMap.put("Местоположение", "\tУкраина");
        personalDataMap.put("О себе", "skype - vbudennyj");
        assertEquals(
            personalDataMap,
            userDetails.getPersonalData()
        );

        assertEquals("30 сентября 2013, 22:16", userDetails.getRegistered());
        assertArrayEquals(
            new String[]{"Caimanchik"},
            userDetails.getSubscribers()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList())
                .toArray()
        );
        assertEquals("vbudennyj", userDetails.getUsername());
    }

    @Test
    public void shouldGetUserDetails2() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        DocumentRetriever retriever = TestHelpers.createCustomRetrieverWithUrlCheck(
            "http://mysku.ru/profile/Samodelkin/",
            "user_details_2.html"
        );
        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(retriever);

        sdk.user().getUserDetails(new User.Id("Samodelkin"), new MyskuCallback<UserDetails>() {
            @Override
            public void onSuccess(UserDetails result) {
                userDetails = result;
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

        assertEquals("Evgeny", userDetails.getRealName());
        assertArrayEquals(
            new String[]{"nso77", "Alesh", "Polkan-Irk", "severinv"},
            userDetails.getSubscribers()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList())
                .toArray()
        );
        assertArrayEquals(
            new String[]{"Zoolog", "WOLFRIEND"},
            userDetails.getSubscriptions()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList())
                .toArray()
        );
    }

    @Test
    public void shouldGetUserComments() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);
        int customPage = 5;

        DocumentRetriever retriever = TestHelpers.createCustomRetrieverWithUrlCheck(
            "http://mysku.ru/my/Samodelkin/comment/page" + customPage + "/",
            "user_details.html"
        );
        MyskuSdk sdk = MyskuSdkFactory.createSdkWithCustomRetriever(retriever);

        sdk.user().getUserComments(new User.Id("Samodelkin"), new MyskuCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> result) {
                userComments = result;
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

        assertEquals(20, userComments.size());

        Comment comment = userComments.get(0);
        assertEquals("Samodelkin", comment.getAuthor().getUsername());
        assertEquals("http://img.mysku-st.ru/uploads/images/04/23/32/2015/09/28/avatar_48x48.jpg?155439", comment.getAuthorThumbnail());
        assertEquals("Дома посмотрю версию, а где можно скачать свежую?", comment.getBody());
        assertEquals("03 октября 2016, 06:51", comment.getDate());
        assertEquals("0", comment.getRating());
        assertEquals(0, comment.getReplies().size());
    }

    private Category createCategory(String name, String url) {
        return new CategoryBuilder()
            .setId(UrlHelpers.getCategoryIdFromUrl(url))
            .setName(name)
            .build();
    }
}