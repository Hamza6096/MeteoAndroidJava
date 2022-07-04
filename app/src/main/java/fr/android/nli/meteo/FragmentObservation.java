package fr.android.nli.meteo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.android.nli.meteo.databinding.FragmentObservationBinding;

public class FragmentObservation extends Fragment {
    private VMListProvider<OWM, OWM.Observation> vm;

    public FragmentObservation() {
        // Required empty public constructor
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Récupérer l'instance Singleton de VMListProvider.
        vm = new ViewModelProvider(requireActivity()).get(VMListProvider.class);
        // Indiquer que ce fragment a un menu.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Récuperer la classe auto-générée du layout.
        FragmentObservationBinding binding = FragmentObservationBinding.inflate(inflater, container, false);
        //Récuperer l'observation sur laqquelle l'utilisateur a cliqué
        OWM.Observation obs = vm.getItem();
        // Définir les valeurs champs du layout
        assert obs != null;
        binding.obsCity.setText(obs.city);
        binding.obsDescription.setText(obs.description);
        binding.obsTemp.setText(getString(R.string.obs_temps, obs.min, obs.max, obs.feelsLike));
        binding.obsWind.setText(getString(R.string.obs_wind, obs.windDirection, obs.windSpeed));
        binding.obsHumidity.setText(getString(R.string.obs_humidity, obs.humidity));
        // Retourner le fragment inflaté
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflater le layout du menu
        inflater.inflate(R.menu.menu_observation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Si clic sur Refresh, actualiser.
        if (item.getItemId() == R.id.action_share) {
            // Récuperer l'observation en actuelle.
            OWM.Observation obs = vm.getItem();
            // Crééer un Intent ét définir ces caractéristiques.
            assert obs != null;// signifie que obs ne peut pas être null
            Intent intent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_SUBJECT, "Weather in " + obs.city)
                    .putExtra(Intent.EXTRA_TEXT, obs.toString());
            // Générer la liste des activités correspondant à l'Intente et démarrer celle choisie pas l'utilisateur
            startActivity(Intent.createChooser(intent, "null"));
            return true;
        }
        // SInon laisser le parent traiter le clic.
        return super.onOptionsItemSelected(item);
    }
}