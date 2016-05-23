package com.openweathermap;

import static com.jayway.restassured.http.ContentType.HTML;
import static com.jayway.restassured.http.ContentType.JSON;
import static com.jayway.restassured.http.ContentType.XML;
import static com.openweathermap.Cities.CITIES;

import com.jayway.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Random;

public class Common {

    public static final String BASE_API_URL = "http://api.openweathermap.org/data/2.5";
    public static final String API_KEY = "6f95c082f65aa559778111e7f0894efc";

    public static final String CURRENT_WEATHER_URL = BASE_API_URL+"/weather?appid="+API_KEY+"&";
    public static final String FORECAST5_WEATHER_URL = BASE_API_URL+"/forecast5?appid="+API_KEY+"&";

    public static final HashMap<String, ContentType> CONTENT_TYPES = new HashMap(){{
        put("json", JSON);
        put("xml", XML);
        put("html", HTML);
    }};

    public static HashMap getRandomCity(){
        return CITIES.get(new Random().nextInt(CITIES.size()));
    }

    public static String getCityNameFromMap(HashMap<String, Object> map){
        return map.get("name").toString();
    }

    public static Integer getCityIdFromMap(HashMap<String, Object> map){
        return (Integer) map.get("id");
    }

    public static String getCountryCodeFromMap(HashMap<String, Object> map){
        return map.get("country").toString();
    }

    public static String randomString()
    {
        return RandomStringUtils.randomAlphanumeric(20);
    }
}
