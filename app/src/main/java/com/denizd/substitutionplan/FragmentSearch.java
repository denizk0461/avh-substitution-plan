package com.denizd.substitutionplan;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearch extends Fragment {

    ConstraintLayout constraintLayout;

    private RecyclerView recyclerView;
    private CardAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Subst> planCardList;
    private SubstViewModel substViewModel;
    private boolean search = false;
    private EditText searchfield;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search, container, false);
        constraintLayout = rootView.findViewById(R.id.fragment_container);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar progressBar = getView().getRootView().findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        planCardList = new ArrayList<>();
        recyclerView = getView().findViewById(R.id.linearRecyclerSearch);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CardAdapter(planCardList);
        recyclerView.setAdapter(mAdapter);

        searchfield = getView().findViewById(R.id.searchbar);
        searchfield.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                substViewModel = ViewModelProviders.of(getActivity()).get(SubstViewModel.class);
                substViewModel.getAllSubst().observe(getActivity(), new Observer<List<Subst>>() {
                    @Override
                    public void onChanged(List<Subst> substs) {
                        planCardList.removeAll(substs);
                        new search(substs).searchSth();
                    }
                });

            }
        });

    }

    private class search extends AsyncTask<Void, Void, Void> {

        private List<Subst> substs;

        protected search(List<Subst> substs) {
            this.substs = substs;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        protected void searchSth() {
            for (int i = 0; i < substs.size(); i++) {
                if (substs.get(i).getGroup().toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
                    search = true;
                }
                if (substs.get(i).getCourse().toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
                    search = true;
                }
                if (substs.get(i).getRoom().toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
                    search = true;
                }
                if (substs.get(i).getAdditional().toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
                    search = true;
                }

                if (search) {
                    planCardList.add(substs.get(i));
                    search = false;
                }
            }
            recyclerView.scheduleLayoutAnimation();
        }
    }
}
