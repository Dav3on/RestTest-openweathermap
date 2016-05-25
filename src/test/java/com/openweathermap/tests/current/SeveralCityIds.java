package com.openweathermap.tests.current;

import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.openweathermap.Cities.CITIES;
import static com.openweathermap.Common.API_KEY;
import static com.openweathermap.Common.BASE_API_URL;
import static com.openweathermap.Common.getRandomCity;

//http://openweathermap.org/current#severalid
public class SeveralCityIds {
    public final String endpointURL = BASE_API_URL+"/group?appid="+API_KEY+"&";
    ArrayList<HashMap> arr = new ArrayList<HashMap>();
    String arrayOfIds;

    @Before
    public void setUp()
    {
        Integer counter = new Random().nextInt(CITIES.size())+1;

        for (int i=0; i<counter; i++){
            arr.add(getRandomCity());
        }

        arrayOfIds = getStringOfIds();
    }

    public String getStringOfIds(){
        String temp="";
        for (HashMap map: this.arr){
            temp += map.get("id").toString()+",";
        }
        return temp;
    }

    @After
    public void tearDown() {
        arr = null;
        arrayOfIds = null;
    }
}
