package com.openweathermap;

public class Common {
    public static final String BASE_API_URL = "http://api.openweathermap.org/data/2.5";
    public static final String API_KEY = "6f95c082f65aa559778111e7f0894efc";

    public static final String CURRENT_WEATHER_URL = BASE_API_URL+"/current?appid="+API_KEY+"&";
    public static final String FORECAST5_WEATHER_URL = BASE_API_URL+"/forecast5?appid="+API_KEY+"&";
}
