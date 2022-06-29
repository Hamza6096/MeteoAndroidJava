package fr.android.nli.meteo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import fr.android.nli.meteo.databinding.ActivityMeteoBinding;


public class ActivityMeteo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Appel du constructeur parent.
        super.onCreate(savedInstanceState);
        // Récuperer la classe auto-générée de binding du layout.
        fr.android.nli.meteo.databinding.ActivityMeteoBinding binding = ActivityMeteoBinding.inflate(this.getLayoutInflater());
        // Afficher le layout.
        this.setContentView(binding.getRoot());
        // Supporter l'ActionBar via une Toolbar.
        this.setSupportActionBar(binding.toolbar);
        //Récuperer l'instance Singleton de VMListProvider.
        VMListProvider<OWM, OWM.Observation> vm = new ViewModelProvider(this).get(VMListProvider.class);
        // Définir le provider dans le vm
        vm.setProvider(new OWM());



        //La premère fois, Charger une instance de FrangmentList dans le FrameLayout.
        if (savedInstanceState == null) {


            this
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new FragmentList())
                    .commit();
        }
    }
}