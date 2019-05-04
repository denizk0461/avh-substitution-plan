package com.denizd.substitutionplan;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.madapps.prefrences.EasyPrefrences;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.google.android.material.snackbar.Snackbar.make;

public class FragmentFood extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Food> foodArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.food_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SwipeRefreshLayout pullToRefresh = getView().findViewById(R.id.pullToRefreshFood);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor edit = prefs.edit();
        final LayoutAnimationController animationIn = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        EasyPrefrences easyPrefs = new EasyPrefrences(getContext());
        ArrayList<String> foodListPopulation = new ArrayList<>(easyPrefs.getListString("foodListPrefs"));
        try {
            foodArrayList = new ArrayList<>();
            recyclerView = getView().findViewById(R.id.linear_food);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new FoodAdapter(foodArrayList);
            recyclerView.setAdapter(mAdapter);

//            try {
//                recyclerView.removeAllViews();
//            } catch (NullPointerException e) {
//
//            }

            for (int i = 0; i < foodListPopulation.size(); i++) {
                foodArrayList.add(new Food(foodListPopulation.get(i)));
                mAdapter.setFood(foodArrayList);
                recyclerView.scheduleLayoutAnimation();
            }
        } catch (NullPointerException e) {

        }

        if (prefs.getBoolean("autoRefresh", false)) {
            pullToRefresh.setRefreshing(true);
            new fetcher().execute();
        }

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                new fetcher().execute();
            }
        });
    }

    private class fetcher extends AsyncTask<Void, Void, Void> {

        boolean attempt = false, npe = false;
        Elements foodElements;
        Document docFood;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor edit = prefs.edit();
        final SwipeRefreshLayout pullToRefresh = getView().findViewById(R.id.pullToRefreshFood);

        protected fetcher() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            final ProgressBar progressBar = getView().getRootView().findViewById(R.id.progressBar);
            try {
                docFood = Jsoup.connect("https://djd4rkn355.github.io/food.html").get();
                foodElements = docFood.select("th");

                attempt = true;

                progressBar.setProgress(0);
                progressBar.setMax(foodElements.size());
//                progressBar.incrementProgressBy(paragraphs[0].size());
            } catch (IOException e1) {
                npe = true;
            } catch (NullPointerException e1) {
                npe = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (attempt) {


                try {

                    final ProgressBar progressBar = getView().getRootView().findViewById(R.id.progressBar);
                    EasyPrefrences easyPrefs = new EasyPrefrences(getContext());

                    ArrayList<String> foodList = new ArrayList<>();

                    try {
                        recyclerView.removeAllViews();
                        foodArrayList.removeAll(foodArrayList);
                    } catch (NullPointerException e) {

                    }

                    for (int foodInt = 0; foodInt < foodElements.size(); foodInt++) {

                        try {
                            if (foodElements.get(foodInt).text().contains("Montag") ||
                                    foodElements.get(foodInt).text().contains("Dienstag") ||
                                    foodElements.get(foodInt).text().contains("Mittwoch") ||
                                    foodElements.get(foodInt).text().contains("Donnerstag") ||
                                    foodElements.get(foodInt).text().contains("Freitag")) {

                                if (foodElements.get(foodInt + 3).text().contains("Montag") ||
                                        foodElements.get(foodInt + 3).text().contains("Dienstag") ||
                                        foodElements.get(foodInt + 3).text().contains("Mittwoch") ||
                                        foodElements.get(foodInt + 3).text().contains("Donnerstag") ||
                                        foodElements.get(foodInt + 3).text().contains("Freitag") ||
                                        foodElements.get(foodInt + 3).text().contains("von")) {
                                    foodList.add(foodElements.get(foodInt).text() + "\n" + foodElements.get(foodInt + 1).text() + "\n" + foodElements.get(foodInt + 2).text());
                                    foodInt += 2;
                                } else if (foodElements.get(foodInt + 2).text().contains("Montag") ||
                                        foodElements.get(foodInt + 2).text().contains("Dienstag") ||
                                        foodElements.get(foodInt + 2).text().contains("Mittwoch") ||
                                        foodElements.get(foodInt + 2).text().contains("Donnerstag") ||
                                        foodElements.get(foodInt + 2).text().contains("Freitag") ||
                                        foodElements.get(foodInt + 2).text().contains("von")) {
                                    foodList.add(foodElements.get(foodInt).text() + "\n" + foodElements.get(foodInt + 1).text());
                                    foodInt += 1;
                                }

                            } else {
                                foodList.add(foodElements.get(foodInt).text());
                            }
                        } catch (IndexOutOfBoundsException e) {
                            try {
                                foodList.add(foodElements.get(foodInt).text() + "\n" + foodElements.get(foodInt + 1).text() + "\n" + foodElements.get(foodInt + 2).text());
                                break;
                            } catch (IndexOutOfBoundsException e1) {
                                foodList.add(foodElements.get(foodInt).text() + "\n" + foodElements.get(foodInt + 1).text());
                                break;
                            }
                        }
                        progressBar.incrementProgressBy(1);
                    }

                    easyPrefs.putListString("foodListPrefs", foodList);

                    ArrayList<String> foodListPopulation = new ArrayList<>(easyPrefs.getListString("foodListPrefs"));

                    final LayoutAnimationController animationIn = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
                    final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

                    for (int i = 0; i < foodListPopulation.size(); i++) {
                        foodArrayList.add(new Food(foodListPopulation.get(i)));
                        mAdapter.setFood(foodArrayList);
                        recyclerView.scheduleLayoutAnimation();
                        progressBar.incrementProgressBy(1);
                    }

                    progressBar.setProgress(1000);

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
                    try {
                        pullToRefresh.setRefreshing(false);
                    } catch (NullPointerException e) {

                    }

                } catch (NullPointerException e) {
//                    pullToRefresh.setRefreshing(false);
                }
            } else if (npe) {
                pullToRefresh.setRefreshing(false);
                View contextView = getView().getRootView().findViewById(R.id.coordination);
                Snackbar snackbar = make(contextView, getText(R.string.nointernet), Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                snackbar.show();
            }

        }
    }
}
