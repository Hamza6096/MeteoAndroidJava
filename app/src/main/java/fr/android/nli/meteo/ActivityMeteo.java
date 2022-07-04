package fr.android.nli.meteo;

import android.os.Bundle;

import fr.android.nli.meteo.OWM.Observation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import fr.android.nli.meteo.databinding.ActivityMeteoBinding;


public class ActivityMeteo extends AppCompatActivity {

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Appel du constructeur parent.
        super.onCreate(savedInstanceState);
        // Récuperer la classe auto-générée de binding du layout.
        ActivityMeteoBinding binding = ActivityMeteoBinding.inflate(this.getLayoutInflater());
        // Afficher le layout.
        this.setContentView(binding.getRoot());
        // Supporter l'ActionBar via une Toolbar.
        this.setSupportActionBar(binding.toolbar);
        //Récuperer l'instance Singleton de VMListProvider.
        VMListProvider<OWM, Observation> vm = new ViewModelProvider(this).get(VMListProvider.class);
        // Définir le provider dans le vm
        vm.setProvider(new OWM());
        // Préparer la snackbar du chargement
        Snackbar loadingBar = Snackbar.make(binding.container, R.string.info_loading, Snackbar.LENGTH_INDEFINITE);
        // Observer l'état du ViewModel pour réagir aux chargement
        vm.getMldState().observe(this, state -> {
            switch (state) {
                case VMListProvider.STATE_NO_INTERNET:
                    // Afficher le SnackBar du chargement
                    Snackbar.make(binding.container, R.string.info_no_internet, Snackbar.LENGTH_LONG).show();
                    break;
                case VMListProvider.STATE_LOADING_STARTS:
                    // Afficher le SnackBar du chargement
                    loadingBar.show();
                    break;
                case VMListProvider.STATE_LOADING_ENDS:
                    // Masquer le SnackBar du chargement.
                    loadingBar.dismiss();
                    break;
                case VMListProvider.STATE_CLICK_ON_INTEM:
                    // Remplacer le FragmentList par le FragmentObservation dans le Framelayout.
                    this
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new FragmentObservation())
                            .addToBackStack(null)
                            .commit();
                    break;
                case VMListProvider.STATE_DONE:
                    // Ne rien faire, présent pour éviter une boucle sans fin
                    return;
            }
            vm.setStateDone();
        });
        //La premère fois, Charger une instance de FrangmentList dans le FrameLayout.
        if (savedInstanceState == null) {
            this
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new FragmentList<OWM, OWM.Observation>())
                    .commit();
        }
    }
}