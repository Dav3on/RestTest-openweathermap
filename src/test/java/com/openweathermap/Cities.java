package com.openweathermap;

import java.util.HashMap;

public class Cities {

    //List of city ID city.list.json.gz can be downloaded here http://bulk.openweathermap.org/sample/
    public static final HashMap<String, String> CITY_NAME_WITH_ID = new HashMap<String, String>(){{
        put("Kiev", "703448");
        put("Ternopil", "691650");
        put("Kherson", "706448");
        put("Lutsk", "702569");
        put("Irpin", "707565");
    }};
}
