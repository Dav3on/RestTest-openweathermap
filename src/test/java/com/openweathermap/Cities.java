package com.openweathermap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Cities {

    //List of city ID city.list.json.gz can be downloaded here http://bulk.openweathermap.org/sample/
    public static final ArrayList<HashMap> CITIES = new ArrayList<HashMap>(Arrays.asList(
            new HashMap() {{
                put("name", "Kiev");
                put("id", 703448);
                put("country", "UA");
                put("lon", 30.516666);
                put("lat", 50.433334);
            }},
            new HashMap() {{
                put("name", "Ternopil");
                put("id", 691650);
                put("country", "UA");
                put("lon", 25.60556);
                put("lat", 49.555889);
            }},
            new HashMap() {{
                put("name", "Kherson");
                put("id", 706448);
                put("country", "UA");
                put("lon", 32.617802);
                put("lat", 46.655811);
            }},
            new HashMap() {{
                put("name", "Lutsk");
                put("id", 702569);
                put("country", "UA");
                put("lon", 25.34244);
                put("lat", 50.759319);
            }},
            new HashMap() {{
                put("name", "Irpin");
                put("id", 707565);
                put("country", "UA");
                put("lon", 30.250549);
                put("lat", 50.521751);
            }}
    ));
}
