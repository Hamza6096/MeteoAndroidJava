package fr.android.nli.meteo;

import android.content.Context;
import android.widget.ArrayAdapter;

import org.json.JSONException;

import java.util.ArrayList;

public interface ListProvider<E> {

    default String getURL() {
        return null;
    }

    default ArrayList<E> toList(final String str) throws JSONException {
        return null;
    }

    default ArrayAdapter<E> getAdapter(Context context) {
        return null;
    }
}
