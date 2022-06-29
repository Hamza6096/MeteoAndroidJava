package fr.android.nli.meteo;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Scanner;

import fr.android.nli.meteo.OWM.Observation;
import fr.android.nli.meteo.databinding.FragmentListBinding;

public class FragmentList<P extends ListProvider<E>, E> extends Fragment {
    private VMListProvider<P, E> vm;

    public FragmentList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Récupérer l'instance Singleton de VMListProvider.
        vm = new ViewModelProvider(requireActivity()).get(VMListProvider.class);

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
        // Observer la liste du provider du provider et peupler l'adapter.
        vm .getLDList().observe(getViewLifecycleOwner(), list -> {
            adapter.clear();
                adapter.addAll(list);
        });
        // Retourner le fragment inflaté
        return binding.getRoot();
    }


}