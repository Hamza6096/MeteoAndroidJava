package fr.android.nli.meteo;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import fr.android.nli.meteo.databinding.ActivityMeteoBinding;
import fr.android.nli.meteo.databinding.FragmentListBinding;

public class FragmentList extends Fragment {
    private static final ListProvider<OWM.Observation> owm = new OWM();
    private ArrayAdapter<String> adapter;

    public FragmentList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Récuperer la classe auto-générée du layout.
        fr.android.nli.meteo.databinding.FragmentListBinding binding = FragmentListBinding.inflate(inflater, container, false);
        // Récuperer le ListView depuis le layout via son id.
        ListView listView = binding.list;
        // Crééer un ArrayAdapter de String.
        adapter = new ArrayAdapter<>(this.getContext(), R.layout.item);
        // Associer le ListView à l'ArrayAdapter.
        listView.setAdapter(adapter);
        // Requêter le serveur
        new AsyncTaskProvider().execute(owm.getURL());

        // Fournir le tableau a l'ArrayAdapter
        //adapter.addAll(cities);

        // Retourner le fragment inflaté
        return binding.getRoot();
    }

    /**
     * L'API level 30 a marqué comme déprécié AsyncTask (Java) mais pas CoroutineScope (Kotlin) alors que cette dernière est une stricte copie de la première.
     * C'est une démarche marketing pour promouvoir Kotlin en poussant les developpeurs Java a utiliser java.util.concurrent nettement moins abordable.
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskProvider extends AsyncTask<String, Void, ArrayList<OWM.Observation>> {

        @Override
        protected ArrayList<OWM.Observation> doInBackground(String... urls) {
            // Requeter le serveur OWM
            InputStream is;
            try {
                is = new URL(urls[0]).openStream();
            } catch (IOException e) {
                Log.e("FragmentList", "Réponse serveur incorrect");
                return null;
            }
            //Lire la réponse ligne à ligne
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine())
                sb.append(scanner.nextLine());
            //Exploiter la réponse et récuperer une liste.
            ArrayList<OWM.Observation> observations;
            try {
                observations = owm.toList(sb.toString());
            } catch (Exception e) {
                Log.e("FragmentList", "Réponse serveur incorrect");
                return null;
            }
            return observations;
        }

        @Override
        protected void onPostExecute(ArrayList<OWM.Observation> observations) {
            Log.d("getContent", observations.toString());
            for (OWM.Observation obs : observations )
            adapter.add(obs.toString());
        }
    }
}