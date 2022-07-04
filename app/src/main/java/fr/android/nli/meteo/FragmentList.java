package fr.android.nli.meteo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import fr.android.nli.meteo.databinding.FragmentListBinding;

public class FragmentList<P extends ListProvider<E>, E> extends Fragment {
    private VMListProvider<P, E> vm;

    public FragmentList() {
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
        fr.android.nli.meteo.databinding.FragmentListBinding binding = FragmentListBinding.inflate(inflater, container, false);
        // Récuperer le ListView depuis le layout via son id.
        ListView listView = binding.list;
        // Crééer un ArrayAdapter dédié.
        ArrayAdapter<E> adapter = vm.getProvider().getAdapter(this.getContext());
        // Associer le ListView à l'ArrayAdapter.
        listView.setAdapter(adapter);
        // Définir un écouteur de clics sur les items de la liste.
        listView.setOnItemClickListener((parent, view, position, id) -> vm.setPosition(position));
        // Observer la liste du provider du provider et peupler l'adapter.
        vm.getMldList(false).observe(getViewLifecycleOwner(), list -> {
            adapter.clear();
            adapter.addAll(list);
        });
        // Retourner le fragment inflaté
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflater le layout du menu
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Si clic sur Refresh, actualiser.
        if (item.getItemId() == R.id.action_refresh) {
            vm.getMldList(true);
            return true;
        }
        // SInon laisser le parent traiter le clic.
        return super.onOptionsItemSelected(item);
    }
}