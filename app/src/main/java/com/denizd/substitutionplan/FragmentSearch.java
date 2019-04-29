package com.denizd.substitutionplan;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.material.snackbar.Snackbar.make;

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
                if (substs.get(i).getDate().toLowerCase().toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
                    search = true;
                }
                if (substs.get(i).getTime().toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
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
        }
    }

//    private class fetcher extends AsyncTask<Void, Void, Void> {
//
//        private int count = 0, iconColour;
//        String[] groupS, dateS, timeS, courseS, roomS, additionalS;
//        boolean attempt = false, search = false, first = false, npe = false, emptyIcon;
//        Elements rows;
//        Document doc;
//        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        private RecyclerView recyclerView;
//        private CardAdapter mAdapter;
//        private RecyclerView.LayoutManager layoutManager;
//        private ArrayList<Subst> planCardList;
//
//        protected fetcher() {}
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            if (!first) {
//                try {
//                    doc = Jsoup.connect("https://djd4rkn355.github.io/subst").get();
//                    rows = doc.select("tr");
//                    count = rows.size();
//                    attempt = true;
//
//                } catch (IOException e1) {
//                    npe = true;
//                }
//
//                groupS = new String[count];
//                dateS = new String[count];
//                timeS = new String[count];
//                courseS = new String[count];
//                roomS = new String[count];
//                additionalS = new String[count];
//
//                if (attempt) {
//                    for (int i = 0; i < count; i++) {
//                        Element row = rows.get(i);
//                        Elements cols = row.select("th");
//                        groupS[i] = cols.get(0).text();
//                        dateS[i] = cols.get(1).text();
//                        timeS[i] = cols.get(2).text();
//                        courseS[i] = cols.get(3).text();
//                        roomS[i] = cols.get(4).text();
//                        additionalS[i] = cols.get(5).text();
//                    }
//                }
//
//                if (prefs.getInt("themeInt", 0) == 0) {
//                    iconColour = Color.parseColor("#212121");
//                }
//                if (prefs.getInt("themeInt", 0) == 1) {
//                    iconColour = Color.parseColor("#e0e0e0");
//                }
//                first = true;
//            }
//
//            return null;
//        }
//
//        private int icons(String[] courseS, int i) {
//            emptyIcon = false;
//            if (courseS[i].toLowerCase().contains("deu") || courseS[i].toLowerCase().contains("dep") || courseS[i].toLowerCase().contains("daz")) {
//                return R.drawable.ic_german;
//            } else if (courseS[i].toLowerCase().contains("mat") || courseS[i].toLowerCase().contains("map")) {
//                return R.drawable.ic_maths;
//            } else if (courseS[i].toLowerCase().contains("eng") || courseS[i].toLowerCase().contains("enp") || courseS[i].toLowerCase().contains("ena")) {
//                return R.drawable.ic_english;
//            } else if (courseS[i].toLowerCase().contains("spo") || courseS[i].toLowerCase().contains("spp") || courseS[i].toLowerCase().contains("spth")) {
//                return R.drawable.ic_pe;
//            } else if (courseS[i].toLowerCase().contains("pol") || courseS[i].toLowerCase().contains("pop")) {
//                return R.drawable.ic_politics;
//            } else if (courseS[i].toLowerCase().contains("dar") || courseS[i].toLowerCase().contains("dap")) {
//                return R.drawable.ic_drama;
//            } else if (courseS[i].toLowerCase().contains("phy") || courseS[i].toLowerCase().contains("php")) {
//                return R.drawable.ic_physics;
//            } else if (courseS[i].toLowerCase().contains("bio") || courseS[i].toLowerCase().contains("bip") || courseS[i].toLowerCase().contains("nw")) {
//                return R.drawable.ic_biology;
//            } else if (courseS[i].toLowerCase().contains("che") || courseS[i].toLowerCase().contains("chp")) {
//                return R.drawable.ic_chemistry;
//            } else if (courseS[i].toLowerCase().contains("phi") || courseS[i].toLowerCase().contains("psp")) {
//                return R.drawable.ic_philosophy;
//            } else if (courseS[i].toLowerCase().contains("laa") || courseS[i].toLowerCase().contains("laf") || courseS[i].toLowerCase().contains("lat")) {
//                return R.drawable.ic_latin;
//            } else if (courseS[i].toLowerCase().contains("spa") || courseS[i].toLowerCase().contains("spf")) {
//                return R.drawable.ic_spanish;
//            } else if (courseS[i].toLowerCase().contains("fra") || courseS[i].toLowerCase().contains("frf") || courseS[i].toLowerCase().contains("frz")) {
//                return R.drawable.ic_french;
//            } else if (courseS[i].toLowerCase().contains("inf")) {
//                return R.drawable.ic_compsci;
//            } else if (courseS[i].toLowerCase().contains("ges")) {
//                return R.drawable.ic_history;
//            } else if (courseS[i].toLowerCase().contains("rel")) {
//                return R.drawable.ic_religion;
//            } else if (courseS[i].toLowerCase().contains("geg") || courseS[i].toLowerCase().contains("wuk")) {
//                return R.drawable.ic_geography;
//            } else if (courseS[i].toLowerCase().contains("kun")) {
//                return R.drawable.ic_arts;
//            } else if (courseS[i].toLowerCase().contains("mus")) {
//                return R.drawable.ic_music;
//            } else if (courseS[i].toLowerCase().contains("tue")) {
//                return R.drawable.ic_turkish;
//            } else if (courseS[i].toLowerCase().contains("chi")) {
//                return R.drawable.ic_chinese;
//            } else if (courseS[i].toLowerCase().contains("gll")) {
//                return R.drawable.ic_gll;
//            } else if (courseS[i].toLowerCase().contains("wat")) {
//                return R.drawable.ic_wat;
//            } else if (courseS[i].toLowerCase().contains("för")) {
//                return R.drawable.ic_help;
//            } else if (courseS[i].toLowerCase().contains("wp") || courseS[i].toLowerCase().contains("met")) {
//                return R.drawable.ic_pencil;
//            } else {
//                emptyIcon = true;
//                return R.drawable.ic_empty;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            if (attempt) {
//
//                try {
//
//                    planCardList = new ArrayList<>();
//                    recyclerView = getView().findViewById(R.id.linearRecyclerSearch);
//                    layoutManager = new LinearLayoutManager(getContext());
//                    recyclerView.setHasFixedSize(true);
//                    recyclerView.setLayoutManager(layoutManager);
//                    mAdapter = new CardAdapter(planCardList);
//                    recyclerView.setAdapter(mAdapter);
//                } catch (NullPointerException e) {
//                    return;
//                }
//                recyclerView.removeAllViews();
//                EditText searchfield = getView().findViewById(R.id.searchbar);
//                final Animation animationIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
//
//                for (int i = 0; i < count; i++) {
//                    if (groupS[i].toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
//                        search = true;
//                    } if (dateS[i].toLowerCase().toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
//                        search = true;
//                    } if (timeS[i].toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
//                        search = true;
//                    } if (courseS[i].toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
//                        search = true;
//                    } if (roomS[i].toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
//                        search = true;
//                    } if (additionalS[i].toLowerCase().contains(searchfield.getText().toString().toLowerCase())) {
//                        search = true;
//                    }
//
//                    if (search) {
//                        Drawable drawable = getResources().getDrawable(icons(courseS, i));
//                        if (!emptyIcon) {
//                            drawable.setTint(iconColour);
//                        }
//                        planCardList.add(new Subst(0, groupS[i], dateS[i], timeS[i], courseS[i], roomS[i], additionalS[i], 0));
//                        recyclerView.setAnimation(animationIn);
//                        search = false;
//                    }
//                }
//            } else if (npe) {
//                View contextView = getView().getRootView().findViewById(R.id.coordination);
//                Snackbar snackbar = make(contextView, getText(R.string.nointernet), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null);
//                snackbar.show();
//            }
//        }
//    }
}
