package com.denizd.substitutionplan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.madapps.prefrences.EasyPrefrences;

import androidx.core.app.NotificationCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ScheduledJobService extends JobService {

    private boolean jobCancelled = false;
    Context context = this;
    private SubstViewModel substViewModel;

    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor edit = prefs.edit();

        substViewModel = new SubstViewModel(getApplication());
//        substViewModel = ViewModelProviders.of().get(SubstViewModel.class);
//        substViewModel.getAllSubst().observe(this, new Observer<List<Subst>>() {
//            @Override
//            public void onChanged(List<Subst> substs) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (jobCancelled) {
                            return;
                        }
                        if (prefs.getBoolean("notif", false)) {
                            edit.putInt("notificationTestNumberDev", prefs.getInt("notificationTestNumberDev", 0) + 1);
                            edit.apply();
                            URL url;
                            URLConnection connection = null;
                            try {
                                url = new URL("https://djd4rkn355.github.io/subst");
                                connection = url.openConnection();
                            } catch (MalformedURLException e) {}
                            catch (IOException e) {}
                            try {
                                if ((!connection.getHeaderField("Last-Modified").equals(prefs.getString("time", "")))) {
//                                if (true) {
                                    new fetcher(context).execute();
                                    edit.putString("time", connection.getHeaderField("Last-Modified"));
                                    edit.apply();
                                }
                            } catch (NullPointerException e1) {
                            }
                        }
                        jobFinished(params, false);
                    }
                }).start();
//            }
//        });
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return true;
    }

    private class fetcher extends AsyncTask<Void, Void, Void> {

        private int count = 0, columns = 0, pCount = 0, priority = 200;
        Context mContext;
        String notifText = "", foodInfo = "", informational = "";
        String[] groupS, dateS, timeS, courseS, roomS, additionalS;
        boolean attempt = false, emptyIcon;
        URL url;
        URLConnection connection;
        String modified;
        Elements rows, paragraphs;
        Elements[] paragraphsFood = new Elements[2];
        Elements foodElements;
        Element[] dateFood = new Element[2], blockFood = new Element[2];
        Document doc, docFood;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "general";
        CharSequence channelName = getText(R.string.general);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final SharedPreferences.Editor edit = prefs.edit();

        protected fetcher(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel = new NotificationChannel(channelId, channelName, importance);
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);
                manager.createNotificationChannel(channel);
            }

            try {
                doc = Jsoup.connect("https://djd4rkn355.github.io/subst").get();
                url = new URL("https://djd4rkn355.github.io/subst");
                connection = url.openConnection();
                rows = doc.select("tr");
                count = rows.size();

                docFood = Jsoup.connect("https://djd4rkn355.github.io/food.html").get();
                foodElements = docFood.select("th");

                paragraphs = doc.select("p");
                pCount = paragraphs.size();

                for (int i = 0; i < pCount; i++) {
                    if (i == 0) {
                        informational = paragraphs.get(i).text();
                    } else {
                        informational += "\n\n" + paragraphs.get(i).text();
                    }
                }

                edit.putString("informational", informational);
                edit.apply();

                attempt = true;
                modified = connection.getHeaderField("Last-Modified");
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            groupS = new String[count];
            dateS = new String[count];
            timeS = new String[count];
            courseS = new String[count];
            roomS = new String[count];
            additionalS = new String[count];

            if (attempt) {

                EasyPrefrences easyPrefs = new EasyPrefrences(context);
                ArrayList<String> foodList = new ArrayList<>();
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
                }

                easyPrefs.putListString("foodListPrefs", foodList);

                if (substViewModel != null) {
                    substViewModel.deleteAllSubst();
                }

                    for (int i = 0; i < count; i++) {
                        Element row = rows.get(i);
                        Elements cols = row.select("th");
                        groupS[i] = cols.get(0).text();
                        dateS[i] = cols.get(1).text();
                        timeS[i] = cols.get(2).text();
                        courseS[i] = cols.get(3).text();
                        roomS[i] = cols.get(4).text();
                        additionalS[i] = cols.get(5).text();

                        int drawable = icons(courseS[i]);
                        Subst subst = new Subst(drawable, groupS[i], dateS[i], timeS[i], courseS[i], roomS[i], additionalS[i], priority);
                        priority--;
                        substViewModel.insert(subst);

                        // for juniors
                        if (prefs.getString("courses", "").isEmpty() && !prefs.getString("classes", "").isEmpty()) {
                            if (!groupS[i].isEmpty() && !groupS[i].equals("")) {
                                if (prefs.getString("classes", "").contains(groupS[i]) || groupS[i].contains(prefs.getString("classes", ""))) {
                                    if (!notifText.isEmpty()) {
                                        notifText += ", ";
                                    }
                                    notifText += courseS[i] + ": " + additionalS[i];
                                }
                            }
                        }

                        // for seniors
                        else if (!prefs.getString("courses", "").isEmpty() && !prefs.getString("classes", "").isEmpty()) {
                            if (!groupS[i].equals("") && !courseS[i].equals("")) {
                                if (prefs.getString("courses", "").contains(courseS[i])) {
                                    if (prefs.getString("classes", "").contains(groupS[i]) || groupS[i].contains(prefs.getString("classes", ""))) {
                                        if (!notifText.isEmpty()) {
                                            notifText += ", ";
                                        }
                                        notifText += courseS[i] + ": " + additionalS[i];
                                    }
                                }
                            }
                        }
                    }
                    Intent openApp = new Intent(getApplicationContext(), MainActivity.class);
                    openApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent openAppPending = PendingIntent.getActivity(mContext, 0, openApp, 0);

                RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification);
                notificationLayout.setTextViewText(R.id.notification_title, getString(R.string.subst));
                notificationLayout.setTextViewText(R.id.notification_textview, notifText);

                    if (!notifText.isEmpty()) {
                        Notification notification = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notification = new NotificationCompat.Builder(mContext)
//                                    .setContentTitle(getString(R.string.subst))
//                                    .setStyle(new NotificationCompat.BigTextStyle()
//                                            .bigText(notifText))
                                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                    .setCustomContentView(notificationLayout)
                                    .setSmallIcon(R.drawable.ic_avh)
                                    .setChannelId(channelId)
                                    .setContentIntent(openAppPending)
                                    .setAutoCancel(true)
                                    .build();
                        } else {
                            notification = new NotificationCompat.Builder(mContext)
//                                    .setContentTitle(getString(R.string.subst))
//                                    .setStyle(new NotificationCompat.BigTextStyle()
//                                            .bigText(notifText))
                                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                    .setCustomContentView(notificationLayout)
                                    .setSmallIcon(R.drawable.ic_avh)
                                    .setContentIntent(openAppPending)
                                    .setAutoCancel(true)
                                    .build();
                        }
                        manager.notify(1, notification);
                    }

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
        protected void onPostExecute(Void result) {}
    }
}
