package com.openweathermap;

import static com.openweathermap.Cities.CITY_NAME_WITH_ID;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Common {

    public static final String BASE_API_URL = "http://api.openweathermap.org/data/2.5";
    public static final String API_KEY = "6f95c082f65aa559778111e7f0894efc";

    public static final String CURRENT_WEATHER_URL = BASE_API_URL+"/weather?appid="+API_KEY+"&";
    public static final String FORECAST5_WEATHER_URL = BASE_API_URL+"/forecast5?appid="+API_KEY+"&";

    public static String getRandomCityName(){
        Object[] keys = CITY_NAME_WITH_ID.keySet().toArray();
        return keys[new Random().nextInt(keys.length)].toString();
    }

    public static HashMap<String, String> getRandomCityNameWithCode(){
        Object[] keys = CITY_NAME_WITH_ID.keySet().toArray();
        final String key = keys[new Random().nextInt(keys.length)].toString();
        final String value = CITY_NAME_WITH_ID.get(key);
        return new HashMap<String, String>(){{put(key, value);}};
    }

    public static String getCityNameFromMap(HashMap<String, String > map){
        String result="";
        for (Map.Entry<String, String > res: map.entrySet()){
            result = res.getKey();
        }
        return result;
    }

    public static Integer getCityCodeFromMap(HashMap<String, String > map){
        Integer result=0;
        for (Map.Entry<String, String > res: map.entrySet()){
            result = Integer.parseInt(res.getValue());
        }
        return result;
    }

    public static String randomString()
    {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
