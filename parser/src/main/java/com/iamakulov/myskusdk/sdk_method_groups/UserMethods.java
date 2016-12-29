package com.iamakulov.myskusdk.sdk_method_groups;

import com.iamakulov.myskusdk.DocumentRetriever;
import com.iamakulov.myskusdk.MyskuCallback;
import com.iamakulov.myskusdk.MyskuError;
import com.iamakulov.myskusdk.MyskuSdk;
import com.iamakulov.myskusdk.containers.*;
import com.iamakulov.myskusdk.helpers.UrlHelpers;
import com.iamakulov.myskusdk.helpers.UserHelpers;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserMethods {
    private DocumentRetriever retriever;
    private Map<String, String> cookies;

    public UserMethods(DocumentRetriever retriever, Map<String, String> cookies) {
        this.retriever = retriever;
        this.cookies = cookies;
    }

    void getUserDetails(User.Id userId, MyskuCallback<UserDetails> callback) {
        retriever.get(UrlHelpers.getUserUrlFromId(userId), cookies)
            .thenAccept(document -> {
                Element body = document.body();

                String autodetectedCountry = getRowDataByTitle(body, "Автоопределение страны:").text();
                List<User> friends = extractUsersFromLinksList(getRowDataByTitle(body, "Друзья:"));
                String karma = body.select(".vote_block .total").text();
                String karmaVoteCount = body.select(".vote_block .count").text();
                String lastVisit = getRowDataByTitle(body, "Последний визит:").text();

                List<Category> participation = getRowDataByTitle(body, "Состоит в:")
                    .select("a")
                    .stream()
                    .map(e -> new CategoryBuilder()
                        .setId(UrlHelpers.getCategoryIdFromUrl(e.attr("href")))
                        .setName(e.text())
                        .build()
                    )
                    .collect(Collectors.toList());

                Element personalTable = body.select(".title")
                    .stream()
                    .filter(element -> element.text().equals("Личное"))
                    .collect(Collectors.toList())
                    .get(0)
                    .select("+ table")
                    .get(0);

                Map<String, String> personalData = personalTable.select("tr")
                    .stream()
                    .collect(Collectors.toMap(
                        element -> element.select("td:first-child").text(),
                        element -> element.select("td:last-child").text()
                    ));

                String realName = body.select(".nickname .note").text();
                String registered = getRowDataByTitle(body, "Зарегистрирован:").text();
                List<User> subscribers = extractUsersFromLinksList(getRowDataByTitle(body, "Читатели:"));
                List<User> subscriptions = extractUsersFromLinksList(getRowDataByTitle(body, "Подписан на:"));
                String username = body.select(".nickname").text()
                    .replace(realName, "");


                UserDetails userDetails = new UserDetailsBuilder()
                    .setAutodetectedCountry(autodetectedCountry)
                    .setFriends(friends)
                    .setKarma(karma)
                    .setKarmaVoteCount(karmaVoteCount)
                    .setLastVisit(lastVisit)
                    .setParticipation(participation)
                    .setPersonalData(personalData)
                    .setRealName(realName)
                    .setRegistered(registered)
                    .setSubscribers(subscribers)
                    .setSubscriptions(subscriptions)
                    .setUser(UserHelpers.createUserFromUsername(username))
                    .build();

                callback.onSuccess(userDetails);
            })
            .exceptionally(throwable -> {
                callback.onError(new MyskuError(throwable));
                return null;
            });
    }

    private List<User> extractUsersFromLinksList(Element container) {
        return container
            .select("a")
            .stream()
            .map(Element::text)
            .map(UserHelpers::createUserFromUsername)
            .collect(Collectors.toList());
    }

    private Element getRowDataByTitle(Element rowsContainer, String title) {
        return rowsContainer.select("tr")
            .stream()
            .filter(row -> row.select("td:first-child").text().equals(title))
            .collect(Collectors.toList())
            .get(0)
            .select("td:last-child")
            .first();
    }

    void getUserComments(User.Id userId, MyskuCallback<List<Comment>> callback) {
        getUserComments(userId, 0, callback);
    }

    void getUserComments(User.Id userId, int page, MyskuCallback<List<Comment>> callback) {
        retriever.get(UrlHelpers.getUserCommentsUrlFromId(userId, page), cookies)
            .thenAccept(document -> {
                List<Comment> comments = document.body().select("#content .comment")
                    .stream()
                    .map(element -> {
                        String username = element.select(".author").text();
                        String authorThumbnail = element.select(".avatar").attr("href");
                        String body = element.select(".text").text();
                        String date = element.select(".date").text();
                        Comment.Id id = new Comment.Id(
                            element.select(".imglink").attr("href").replaceAll("^.*#comment", "")
                        );
                        String rating = element.select(".voting .total").text();

                        return new CommentBuilder()
                            .setAuthor(UserHelpers.createUserFromUsername(username))
                            .setAuthorThumbnail(authorThumbnail)
                            .setBody(body)
                            .setDate(date)
                            .setId(id)
                            .setRating(rating)
                            .setReplies(new ArrayList<>())
                            .build();
                    })
                    .collect(Collectors.toList());

                callback.onSuccess(comments);
            })
            .exceptionally(throwable -> {
                callback.onError(new MyskuError(throwable));
                return null;
            });
    }

    void getUserRating(User.Id userId, MyskuCallback<String> callback) {
        getUserRating(userId, MyskuSdk.UserRatingInterval.THIS_MONTH, callback);
    }

    void getUserRating(User.Id userId, MyskuSdk.UserRatingInterval interval, MyskuCallback<String> callback) {
        // TODO: ?
        throw new Error("UserMethods#getUserRating isn’t implemented");
    }

    void increaseUserKarma(User.Id userId, MyskuCallback<UserDetails> callback) {

    }

    void decreaseUserKarma(User.Id userId, MyskuCallback<UserDetails> callback) {

    }

    void addFriend(User.Id userId, MyskuCallback<UserDetails> callback) {

    }
}
