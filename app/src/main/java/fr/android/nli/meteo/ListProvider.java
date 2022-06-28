package fr.android.nli.meteo;

import org.json.JSONException;

import java.util.ArrayList;

public interface ListProvider<E> {

    default String getURL() {
        return null;
    }
    default ArrayList<E> toList(final String str) throws JSONException {
        return null;
    }
}
