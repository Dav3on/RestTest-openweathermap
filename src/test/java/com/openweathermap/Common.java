package com.openweathermap;

import static com.jayway.restassured.http.ContentType.HTML;
import static com.jayway.restassured.http.ContentType.JSON;
import static com.jayway.restassured.http.ContentType.XML;

import com.jayway.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;

public class Common {

    public static final String BASE_API_URL = "http://api.openweathermap.org/data/2.5";
    public static final Integer DEFAULT_PORT = 80;
    public static final String API_KEY = "6f95c082f65aa559778111e7f0894efc";

    public static final HashMap<String, ContentType> CONTENT_TYPES = new HashMap<String, ContentType>(){{
        put("", JSON);
        put("json", JSON);
        put("xml", XML);
        put("html", HTML);
    }};

    public static String randomString()
    {
        return RandomStringUtils.randomAlphanumeric(20);
    }
}
