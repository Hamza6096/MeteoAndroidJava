package fr.android.nli.meteo;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class OWM implements ListProvider<OWM.Observation> {
    private static final String URL = "https://api.openweathermap.org/data/2.5/group?id=264371,2950159,3060972,2800866,3054643,2618425,2964574,658225,2267057,3196359,3117735,2988507,3067696,456172,3169070,2673730,588409,2761369,593116,756135&units=metric&lang=fr&mode=json";
    private static final String KEY = "3c511d187303722ef3dbf36b7cb22bb2";
    private static final String ICON_BASE_URL = "http://openweathermap.org/img/w/";
    private static final String ICON_EXTENSION = "png";

    @Override
    public String getURL() {
        return URL + "&appid=" + KEY;
    }

    @Override
    public ArrayList<Observation> toList(String strJSON) throws JSONException {
        // Crééer un ArrayList vide.
        ArrayList<Observation> observations = new ArrayList<>();
        // Récuperer la racine JSON.
        JSONObject root = new JSONObject(strJSON);
        // Récupérer le tableau des ville.
        JSONArray cities = root.getJSONArray("list");
        // Pour charque ville, récuperer les données.
        for (int i = 0; i < cities.length(); i++) {
            // Créer une nouvelle observation.
            Observation obs = new Observation();
            // Ajouter cette observation à la liste
            observations.add(obs);
            //Récuperer les données.
            JSONObject city = cities.getJSONObject(i);
            obs.city = city.getString("name");
            JSONObject weather = city.getJSONArray("weather").getJSONObject(0);
            obs.description = weather.getString("description");
            obs.iconURL = ICON_BASE_URL + weather.getString("icon") + '.' + ICON_EXTENSION;
            JSONObject main = city.getJSONObject("main");
            obs.min = (int) Math.round(main.getDouble("temp_min"));
            obs.max = (int) Math.round(main.getDouble("temp_max"));

        }
        return observations;
    }

    public static final class Observation {
        String city;
        int min;
        int max;
        String description;
        String iconURL;

        @NonNull
        @Override
        public String toString() {
            return city + " : " + min + "°C /" + max + "°C";
        }
    }


}
