package com.denizd.substitutionplan;

import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.util.Date;
import java.util.List;

import static com.google.android.material.snackbar.Snackbar.make;

public class FragmentPlan extends Fragment {

    private RecyclerView recyclerView;
    private CardAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext()),
            linearLayoutManager = new LinearLayoutManager(getContext()),
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
    private ArrayList<Subst> planCardList;
    private TextView bottomSheetText;
    private SubstViewModel substViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plan, null);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = linearLayoutManager;
            recyclerView.setLayoutManager(layoutManager);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = gridLayoutManager;
            recyclerView.setLayoutManager(layoutManager); // TODO grid layout but change an integer value instead
        }
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
        recyclerView.setHasFixedSize(true);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = linearLayoutManager;
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = gridLayoutManager;
        }
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CardAdapter(planCardList);
        recyclerView.setAdapter(mAdapter);

        if (prefs.getInt("firstTimeOpening", 0) == 2) {
            if (!prefs.getBoolean("notif", false)) {
                pullToRefresh.setRefreshing(true);
                new DataFetcher(false, getContext(), getActivity().getApplication(), getView().getRootView()).execute();
                edit.putInt("firstTimeOpening", 3);
                edit.apply();
            }
        }

        if (prefs.getBoolean("autoRefresh", false)) {
            pullToRefresh.setRefreshing(true);
            new DataFetcher(false, getContext(), getActivity().getApplication(), getView().getRootView()).execute();
            bottomSheetText.setText(prefs.getString("informational", getString(R.string.noinfo)));
        }
        substViewModel = ViewModelProviders.of(getActivity()).get(SubstViewModel.class);
        substViewModel.getAllSubst().observe(this, new Observer<List<Subst>>() {
            @Override
            public void onChanged(List<Subst> substs) {
                mAdapter.setSubst(substs);
                recyclerView.scheduleLayoutAnimation();
                bottomSheetText.setText(prefs.getString("informational", getString(R.string.noinfo)));
            }
        });


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                new DataFetcher(true, getContext(), getActivity().getApplication(), getView().getRootView()).execute(); // TODO test for notification
                bottomSheetText.setText(prefs.getString("informational", getString(R.string.noinfo)));
            }
        });
    }

    private class fetcher extends AsyncTask<Void, Void, Void> {

        private int count = 0, pCount = 0, priority = 200;
        final String OLD_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz", NEW_FORMAT = "yyyy-MM-dd, HH:mm:ss";
        String newDateString, informational;
        String[] groupS, dateS, timeS, courseS, roomS, additionalS;
        boolean attempt = false, npe = false;
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

                        MiscData dg = new MiscData();
                        int drawable = dg.getIcon(courseS[i]);
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
            } catch (NullPointerException ignored) {}
        }


    }
}
