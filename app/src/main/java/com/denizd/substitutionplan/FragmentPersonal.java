package com.denizd.substitutionplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.google.android.material.snackbar.Snackbar.make;

public class FragmentPersonal extends Fragment {

    private RecyclerView recyclerView;
    private CardAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Subst> planCardList;
    private TextView bottomSheetText;
    private SubstViewModel substViewModel;
    private boolean persPlanEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plan, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SwipeRefreshLayout pullToRefresh = getView().findViewById(R.id.pullToRefresh);
        bottomSheetText = getView().getRootView().findViewById(R.id.bottom_sheet_text);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor edit = prefs.edit();

        bottomSheetText.setText(prefs.getString("informational", getString(R.string.noinfo)));

        planCardList = new ArrayList<>();
        recyclerView = getView().findViewById(R.id.linearRecycler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CardAdapter(planCardList);
        recyclerView.setAdapter(mAdapter);

        final TextView smileydown = getView().findViewById(R.id.smileydown);
        final TextView smileydowntext = getView().findViewById(R.id.smileydowntext);
        final LinearLayout linearsmiley = getView().findViewById(R.id.linearsmiley);

        if (prefs.getInt("firstTimeOpening", 0) == 2) {
            if (!prefs.getBoolean("notif", false)) {
                pullToRefresh.setRefreshing(true);
                new fetcher().execute();
                edit.putInt("firstTimeOpening", 3);
                edit.apply();
            }
        }

        if (prefs.getBoolean("autoRefresh", false)) {
            pullToRefresh.setRefreshing(true);
            new fetcher().execute();
            bottomSheetText.setText(prefs.getString("informational", getString(R.string.noinfo)));
        }
        substViewModel = ViewModelProviders.of(getActivity()).get(SubstViewModel.class);
        substViewModel.getAllSubst().observe(this, new Observer<List<Subst>>() {
            @Override
            public void onChanged(List<Subst> substs) {
                try {
                    if (planCardList != null) {
                        planCardList.clear();
                    }
                    smileydown.setVisibility(View.GONE);
                    smileydowntext.setVisibility(View.GONE);
                    linearsmiley.setVisibility(View.GONE);
                    persPlanEmpty = true;
                    recyclerView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < substs.size(); i++) {

                        if (prefs.getString("courses", "").isEmpty() && !prefs.getString("classes", "").isEmpty()) {
                            if (!substs.get(i).getGroup().isEmpty() && !substs.get(i).getGroup().equals("")) {
                                if (prefs.getString("classes", "").contains(substs.get(i).getGroup()) || substs.get(i).getGroup().contains(prefs.getString("classes", ""))) {
                                    planCardList.add(substs.get(i));
                                    persPlanEmpty = false;
                                }
                            }
                        }

                        // for seniors
                        else if (!prefs.getString("courses", "").isEmpty() && !prefs.getString("classes", "").isEmpty()) {
                            if (!substs.get(i).getGroup().equals("") && !substs.get(i).getCourse().equals("")) {
                                if (prefs.getString("courses", "").contains(substs.get(i).getCourse())) {
                                    if (prefs.getString("classes", "").contains(substs.get(i).getGroup()) || substs.get(i).getGroup().contains(prefs.getString("classes", ""))) {
                                        planCardList.add(substs.get(i));
                                        persPlanEmpty = false;
                                    }
                                }
                            }
                        }
                    }
                    recyclerView.getAdapter().notifyDataSetChanged(); // TODO check whether this is necessary
                    Collections.sort(planCardList, new Comparator<Subst>() {
                        @Override
                        public int compare(Subst lhs, Subst rhs) {
                            return Integer.compare(rhs.getPriority(), lhs.getPriority());
                        }
                    });
                    recyclerView.scheduleLayoutAnimation();
                    mAdapter.setSubst(planCardList);
                    bottomSheetText.setText(prefs.getString("informational", getString(R.string.noinfo)));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (persPlanEmpty) {
                                recyclerView.setVisibility(View.GONE);
                                smileydown.setVisibility(View.VISIBLE);
                                smileydowntext.setVisibility(View.VISIBLE);
                                linearsmiley.setVisibility(View.VISIBLE);
                                linearsmiley.scheduleLayoutAnimation();
                            }
                        }
                    }, 64);
                } catch (NullPointerException e) {

                }
            }
        });

//        pullToRefresh.setRefreshing(true);
//        new fetcher().execute();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                new fetcher().execute();
                bottomSheetText.setText(prefs.getString("informational", getString(R.string.noinfo)));
            }
        });
    }

    private class fetcher extends AsyncTask<Void, Void, Void> {

        private int count = 0, pCount = 0, priority = 200;
        final String OLD_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz", NEW_FORMAT = "yyyy-MM-dd, HH:mm:ss";
        String newDateString, informational;
        String[] groupS, dateS, timeS, courseS, roomS, additionalS;
        boolean attempt = false, npe = false, emptyIcon;
        URL url;
        URLConnection connection;
        String modified;
        Elements rows, paragraphs;
        Document doc;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor edit = prefs.edit();
        final SwipeRefreshLayout pullToRefresh = getView().findViewById(R.id.pullToRefresh);
        ProgressBar progressBar;
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d;

        protected fetcher() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                doc = Jsoup.connect("https://djd4rkn355.github.io/subst").get();
                url = new URL("https://djd4rkn355.github.io/subst");
                connection = url.openConnection();
                rows = doc.select("tr");
                count = rows.size();
                modified = connection.getHeaderField("Last-Modified");
                d = new Date(connection.getHeaderField("Last-Modified"));
                edit.putString("time", connection.getHeaderField("Last-Modified"));
                paragraphs = doc.select("p");
                pCount = paragraphs.size();
                attempt = true;

                for (int i = 0; i < pCount; i++) {
                    if (i == 0) {
                        informational = paragraphs.get(i).text();
                    } else {
                        informational += "\n\n" + paragraphs.get(i).text();
                    }
                }
                edit.putString("informational", informational);
                edit.apply();

                groupS = new String[count];
                dateS = new String[count];
                timeS = new String[count];
                courseS = new String[count];
                roomS = new String[count];
                additionalS = new String[count];

                progressBar = getView().getRootView().findViewById(R.id.progressBar);
                progressBar.setProgress(0);
                progressBar.setMax(count);

                substViewModel.deleteAllSubst();

                if (attempt) {
                    for (int i = 0; i < count; i++) {
                        Element row = rows.get(i);
                        Elements cols = row.select("th");
                        groupS[i] = cols.get(0).text();
                        dateS[i] = cols.get(1).text();
                        timeS[i] = cols.get(2).text();
                        courseS[i] = cols.get(3).text();
                        roomS[i] = cols.get(4).text();
                        additionalS[i] = cols.get(5).text();
                        progressBar.incrementProgressBy(1);

                        int drawable = icons(courseS[i]);
                        Subst subst = new Subst(drawable, groupS[i], dateS[i], timeS[i], courseS[i], roomS[i], additionalS[i], priority);
                        substViewModel.insert(subst);
                        priority--;
                    }
                }

            } catch (IOException e1) {
                npe = true;
            } catch (NullPointerException e1) {
                npe = true;
            }

            return null;
        }

        private int icons(String course) {
            emptyIcon = false;
            if (course.toLowerCase().contains("deu") || course.toLowerCase().contains("dep") || course.toLowerCase().contains("daz")) {
                return R.drawable.ic_german;
            } else if (course.toLowerCase().contains("mat") || course.toLowerCase().contains("map")) {
                return R.drawable.ic_maths;
            } else if (course.toLowerCase().contains("eng") || course.toLowerCase().contains("enp") || course.toLowerCase().contains("ena")) {
                return R.drawable.ic_english;
            } else if (course.toLowerCase().contains("spo") || course.toLowerCase().contains("spp") || course.toLowerCase().contains("spth")) {
                return R.drawable.ic_pe;
            } else if (course.toLowerCase().contains("pol") || course.toLowerCase().contains("pop")) {
                return R.drawable.ic_politics;
            } else if (course.toLowerCase().contains("dar") || course.toLowerCase().contains("dap")) {
                return R.drawable.ic_drama;
            } else if (course.toLowerCase().contains("phy") || course.toLowerCase().contains("php")) {
                return R.drawable.ic_physics;
            } else if (course.toLowerCase().contains("bio") || course.toLowerCase().contains("bip") || course.toLowerCase().contains("nw")) {
                return R.drawable.ic_biology;
            } else if (course.toLowerCase().contains("che") || course.toLowerCase().contains("chp")) {
                return R.drawable.ic_chemistry;
            } else if (course.toLowerCase().contains("phi") || course.toLowerCase().contains("psp")) {
                return R.drawable.ic_philosophy;
            } else if (course.toLowerCase().contains("laa") || course.toLowerCase().contains("laf") || course.toLowerCase().contains("lat")) {
                return R.drawable.ic_latin;
            } else if (course.toLowerCase().contains("spa") || course.toLowerCase().contains("spf")) {
                return R.drawable.ic_spanish;
            } else if (course.toLowerCase().contains("fra") || course.toLowerCase().contains("frf") || course.toLowerCase().contains("frz")) {
                return R.drawable.ic_french;
            } else if (course.toLowerCase().contains("inf")) {
                return R.drawable.ic_compsci;
            } else if (course.toLowerCase().contains("ges")) {
                return R.drawable.ic_history;
            } else if (course.toLowerCase().contains("rel")) {
                return R.drawable.ic_religion;
            } else if (course.toLowerCase().contains("geg") || course.toLowerCase().contains("wuk")) {
                return R.drawable.ic_geography;
            } else if (course.toLowerCase().contains("kun")) {
                return R.drawable.ic_arts;
            } else if (course.toLowerCase().contains("mus")) {
                return R.drawable.ic_music;
            } else if (course.toLowerCase().contains("tue")) {
                return R.drawable.ic_turkish;
            } else if (course.toLowerCase().contains("chi")) {
                return R.drawable.ic_chinese;
            } else if (course.toLowerCase().contains("gll")) {
                return R.drawable.ic_gll;
            } else if (course.toLowerCase().contains("wat")) {
                return R.drawable.ic_wat;
            } else if (course.toLowerCase().contains("för")) {
                return R.drawable.ic_help;
            } else if (course.toLowerCase().contains("wp") || course.toLowerCase().contains("met")) {
                return R.drawable.ic_pencil;
            } else {
                emptyIcon = true;
                return R.drawable.ic_empty;
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (attempt) {
                    final Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.startAnimation(fadeOut);
                        }
                    }, 200);

                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation arg0) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation arg0) {
                        }

                        @Override
                        public void onAnimationEnd(Animation arg0) {
                            progressBar.setProgress(0);
                        }
                    });

                    sdf.applyPattern(NEW_FORMAT);
                    newDateString = sdf.format(d);
                    View contextView = getView().getRootView().findViewById(R.id.coordination);
                    Snackbar snackbar = make(contextView, getText(R.string.lastupdated) + ": " + newDateString, Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    snackbar.show();

                    pullToRefresh.setRefreshing(false);
                } else if (npe) {
                    pullToRefresh.setRefreshing(false);
                    View contextView = getView().getRootView().findViewById(R.id.coordination);
                    Snackbar snackbar = make(contextView, getText(R.string.nointernet), Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    snackbar.show();
                }
            } catch (NullPointerException e) {

            }
        }


    }
}
