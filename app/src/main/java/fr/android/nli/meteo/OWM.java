package fr.android.nli.meteo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.android.nli.meteo.OWM.Observation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public final class OWM implements ListProvider<Observation> {
    private static final String URL = "https://api.openweathermap.org/data/2.5/group?id=264371,2950159,3060972,2800866,3054643,2618425,2964574,658225,2267057,3196359,3117735,2988507,3067696,456172,3169070,2673730,588409,2761369,593116,756135&units=metric&lang=fr&mode=json";
    private static final String KEY = "3c511d187303722ef3dbf36b7cb22bb2";
    private static final String ICON_BASE_URL = "https://openweathermap.org/img/w/";
    private static final String ICON_EXTENSION = "png";
    private static final String ERR_ICON_NOT_FOUND = "ERR_ICON_NOT_FOUND";

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
            obs.feelsLike = (int) Math.round(main.getDouble("feels_like"));
            obs.humidity = main.getInt("humidity");
            JSONObject wind = city.getJSONObject("wind");
            obs.windDirection = wind.getInt("deg");
            obs.windSpeed = (int) Math.round(wind.getDouble("speed") * 3.6); // m/s -> km/h
            // Downloader les icônes.
            try {
                obs.icon = BitmapFactory.decodeStream(new URL(obs.iconURL).openStream());
            } catch (IOException e) {
                Log.e("OWM.toList", ERR_ICON_NOT_FOUND);
            }

        }
        return observations;
    }

    @Override
    public ArrayAdapter<Observation> getAdapter(Context context) {
        return new ArrayAdapterObservation(context, R.layout.observation);
    }

    public static final class Observation {
        String city;
        int min;
        int max;
        int feelsLike;
        int humidity;
        int windSpeed;
        int windDirection;
        String description;
        String iconURL;
        Bitmap icon;

        @NonNull
        @Override
        public String toString() {
            return city + " : " + min + "°C /" + max + "°C";
        }
    }

    public static final class ArrayAdapterObservation extends ArrayAdapter<Observation> {
        private final int resource;

        public ArrayAdapterObservation(@NonNull Context context, int resource) {
            super(context, resource);
            // Sauvegarder la reference au layout
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            if (convertView == null) {
                // Cet item n'est pas recyclé, (donc inflater), l'utiliser te quel.
                Log.d("Adapter", "inflater");
                view = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            } else {
                // Cet item est recyclé, utiliser tel quel
                Log.d("Adapter", "recycle");
                view = convertView;
            }
            // Utiliser l'observation pour définir les champs du layout
            ((TextView) view.findViewById(R.id.item_text)).setText(getItem(position).toString());
            ((ImageView) view.findViewById(R.id.item_icon)).setImageBitmap(getItem(position).icon);
            // Si champs description disponible, le définir.
            TextView tvDescription = view.findViewById(R.id.item_description);
            if (tvDescription != null)
                tvDescription.setText(getItem(position).description);
            // Retourner le layout inflaté et renseigné.
            return view;
        }
    }
}
