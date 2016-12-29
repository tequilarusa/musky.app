package com.iamakulov.myskusdk.helpers;

import java.util.Map;
import java.util.stream.Collectors;

public class RequestHelpers {
    public static String composePostRequestBody(Map<String, String> keyValues) {
        return keyValues
            .entrySet()
            .stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining());
    }
}
