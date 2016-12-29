package com.iamakulov.myskusdk.sdk_method_groups;

import com.iamakulov.myskusdk.DocumentRetriever;
import com.iamakulov.myskusdk.MyskuCallback;
import com.iamakulov.myskusdk.MyskuError;
import com.iamakulov.myskusdk.containers.*;
import com.iamakulov.myskusdk.helpers.ArticleContentHelpers;
import com.iamakulov.myskusdk.helpers.CommentHelpers;
import com.iamakulov.myskusdk.helpers.RequestHelpers;
import com.iamakulov.myskusdk.helpers.UrlHelpers;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleMethods {
    private DocumentRetriever retriever;
    private Map<String, String> cookies;

    public ArticleMethods(DocumentRetriever retriever, Map<String, String> cookies) {
        this.retriever = retriever;
        this.cookies = cookies;
    }

    private List<Comment> parseComments(Document document) {
        Element body = document.body();

        return CommentHelpers.parseCommentList(body.select("#content .comments").first());
    }

    public void getFullArticle(ArticleContent.Id articleId, MyskuCallback<FullArticle> callback) {
        retriever.get(UrlHelpers.getArticleUrlFromId(articleId), cookies)
            .thenAccept(document -> {
                ArticleContent content = ArticleContentHelpers.parseArticleContent(document.body().select(".topic").first(), document.location());
                List<Comment> comments = parseComments(document);

                FullArticle article = new FullArticleBuilder()
                    .setContent(content)
                    .setComments(comments)
                    .build();

                callback.onSuccess(article);
            })
            .exceptionally(throwable -> {
                callback.onError(new MyskuError(throwable));
                return null;
            });
    }

    public void addComment(ArticleContent.Id articleId, String body, Comment.Id replyTo, MyskuCallback<Comment> callback) {
        retriever.get(UrlHelpers.getArticleUrlFromId(articleId), cookies)
            .thenCompose(document -> {
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put("security_ls_key", extractLivestreetSecurityKey(document.html()));
                requestParams.put("reply", replyTo.id);
                requestParams.put("cmt_target_id", articleId.id);
                requestParams.put("form_moderator_id", ""); // Unimplemented. Should probably work without this in most cases
                requestParams.put("comment_text", body);
                String requestBody = RequestHelpers.composePostRequestBody(requestParams);

                String requestUrl = "http://mysku.ru/blog/ajaxaddcomment/?PHPSESSID=" + cookies.get("PHPSESSID");

                return retriever.post(requestUrl, requestBody, cookies);
            })
            // TODO: implement
            .thenAccept(document -> {
                callback.onSuccess(new CommentBuilder().build());
            })
            .exceptionally(throwable -> {
                callback.onError(new MyskuError(throwable));
                return null;
            });
    }

    private String extractLivestreetSecurityKey(String content) {
        Matcher matcher = Pattern.compile("LIVESTREET_SECURITY_KEY = '([^']+)';").matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new RuntimeException("Can’t find LIVESTREET_SECURITY_KEY");
        }
    }
}
