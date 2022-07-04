package fr.android.nli.meteo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

final public class Util {
    public static boolean isConnected(final Context context) {
        // Récuperer un ConnectivityManager.
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        // Sinon, récupérer les connexions réseaux disponibles.
        Network [] networks = cm.getAllNetworks();
        // Parmi ces connexions, rechercher une connexion internet active.
        for (Network network : networks) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(network);
            // Si connexion internet, retourner true
            if (nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
                return true;
        }
        // Aucune connexion internet
        return false;
    }
}
