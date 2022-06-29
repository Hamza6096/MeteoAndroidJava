package fr.android.nli.meteo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class VMListProvider <P extends ListProvider<E>, E> extends AndroidViewModel {
    private final Application application;
    private static final String ERROR_WRONG_SERVER_RESPONSE = "ERROR_WRONG_SERVER_RESPONSE";
    private static final String ERROR_WRONG_JSON_RESPONSE = "ERROR_WRONG_JSON_RESPONSE";
    private final MutableLiveData<P> LDProvider = new MutableLiveData<>();
    private MutableLiveData<ArrayList<E>> LDList;

    public VMListProvider(@NonNull Application application) {
        super(application);
        // Récupérer la référence à la l'application.
        this.application = application;
    }

    public MutableLiveData<ArrayList<E>> getLDList() {
        // Si pas encore ou plus d'ArrayList, instancier et requêter le serveur du provider.
        if (LDList == null) {
            LDList = new MutableLiveData<>();
            new AsyncTaskProvider().execute(LDProvider.getValue());
        }

        // Retourner l'ArrayList.
        return LDList;
    }

    public P getProvider() {
        return LDProvider.getValue();
    }

    public void setProvider(P provider) {
        LDProvider.setValue(provider);
    }

    /**
     * L'API level 30 a marqué comme déprécié AsyncTask (Java) mais pas CoroutineScope (Kotlin) alors que cette dernière est une stricte copie de la première.
     * C'est une démarche marketing pour promouvoir Kotlin en poussant les developpeurs Java a utiliser java.util.concurrent nettement moins abordable.
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskProvider extends AsyncTask<P, Void, ArrayList<E>> {

        @Override
        protected ArrayList<E> doInBackground(P... providers) {
            // Requeter le ListProvider.
            P provider = providers[0];
            // Préparer un inputStream
            InputStream is;
            // Requeter le serveur du ListProvider.
            try {
                is = new URL(provider.getURL()).openStream();
            } catch (IOException e) {
                Log.e("ListProvider", ERROR_WRONG_SERVER_RESPONSE);
                return null;
            }
            //Lire la réponse ligne à ligne
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine())
                sb.append(scanner.nextLine());
            //Exploiter la réponse et récuperer un ArrayList
            ArrayList<E> list;
            try {
                list = provider.toList(sb.toString());
            } catch (Exception e) {
                Log.e("ListProvider", ERROR_WRONG_JSON_RESPONSE);
                return null;
            }
            // Retourner l'ArrayList.
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<E> list) {
            Log.d("list", list.toString());
            // Définir l'ArrayList LiveData à partir de l'ArrayList reçu.
            LDList.setValue(list);
        }
    }
}
