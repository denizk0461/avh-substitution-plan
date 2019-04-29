package com.denizd.substitutionplan;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.android.device.DeviceName;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.google.android.material.snackbar.Snackbar.make;

public class MainActivity extends AppCompatActivity {

    BottomSheetBehavior bottomSheetBehaviour;
    private String manufacturer;
    private String marketName;

    protected void job() {
        ComponentName componentName = new ComponentName(this, ScheduledJobService.class);
        JobInfo info = new JobInfo.Builder(42, componentName)
                .setRequiresCharging(false)
                .setPersisted(true)
                .setPeriodic(900000)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final SharedPreferences.Editor edit = prefs.edit();
        final Intent firstTime = new Intent(this, FirstTime.class);
        if (prefs.getBoolean("firstTime", true)) {
//        if (true) {
            startActivity(firstTime);
            finish();
        }

        edit.putInt("launchDev", prefs.getInt("launchDev", 0) + 1);
        edit.apply();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final AppBarLayout appbarlayout = findViewById(R.id.appbarlayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final TextView toolbarTxt = findViewById(R.id.toolbarTxt);

        final LinearLayout bottomSheetRoot = findViewById(R.id.bottom_sheet);
        final LinearLayout bottomSheet = findViewById(R.id.bottom_sheet_linear);
        final TextView bottomSheetHeader = findViewById(R.id.bottom_sheet_header);
        final TextView bottomSheetText = findViewById(R.id.bottom_sheet_text);
        final View bottomNavDivider = findViewById(R.id.bottom_nav_divider);
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetRoot);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        final BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        final Chip chip = findViewById(R.id.chip);

        final CoordinatorLayout fragmentContainer = findViewById(R.id.fragment_container);
        fragmentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        final LinearLayout bottomSheetCloser = findViewById(R.id.bottom_sheet_closer);
        bottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (bottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) { // 2 = expanded, 4 = collapsed
                    bottomSheetCloser.setVisibility(View.VISIBLE);
                }
                if (bottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED) { // 2 = expanded, 4 = collapsed
                    bottomSheetCloser.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        if (!prefs.getBoolean("autoRefresh", false) && prefs.getInt("firstTimeOpening", 0) > 1) {
            try {
                final String OLD_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz", NEW_FORMAT = "yyyy-MM-dd, HH:mm:ss";
                String newDateString;
                Date d = new Date(prefs.getString("time", ""));
                SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
                sdf.applyPattern(NEW_FORMAT);
                newDateString = sdf.format(d);
                View contextView = findViewById(R.id.coordination);
                Snackbar snackbar = make(contextView, getText(R.string.lastupdated) + ": " + newDateString, Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                snackbar.show();
            } catch (IllegalArgumentException e) {

            }
        }

        final Context context = this;

        // TODO Huawei dialog

        if (prefs.getBoolean("huaweiDeviceDialog", true)) {
//        if (true) {
            DeviceName.with(this).request(new DeviceName.Callback() {
                @Override
                public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                    manufacturer = info.manufacturer;
                    marketName = info.marketName;
                if (manufacturer.contains("Huawei") ||
                        manufacturer.contains("Honor") ||
                        manufacturer.contains("Xiaomi")) {
//                    if (true) {

                        AlertDialog.Builder alertDialog;
                        View dialogView = LayoutInflater.from(context).inflate(R.layout.secret_dialog, null);
                        TextView secretText = dialogView.findViewById(R.id.secrettext);
                        if (prefs.getInt("themeInt", 0) == 1) {
                            secretText.setTextColor(getResources().getColor(R.color.hintdark));
                            alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogCustomDark);
                        } else {
                            secretText.setTextColor(getResources().getColor(R.color.hint));
                            alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogCustomLight);
                        }
                        alertDialog.setTitle(R.string.chinaTitle);
                        TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                        dialogText.setText(R.string.chinaDialog);
                        secretText.setText(R.string.orbuy);
                        Button searchbtn = dialogView.findViewById(R.id.searchbtn);
                        searchbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                intent.putExtra(SearchManager.QUERY, marketName + " disable power optimisation");
                                startActivity(intent);
                            }
                        });
                        alertDialog.setView(dialogView);
                        alertDialog.show();

                        edit.putBoolean("huaweiDeviceDialog", false);
                        edit.apply();
                    }
                }
            });
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
        } else {
            View contextView = findViewById(R.id.coordination);
            Snackbar.make(contextView, getText(R.string.nointernet), Snackbar.LENGTH_LONG).show();
        }

        bottomSheetCloser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetCloser.setVisibility(View.GONE);
                bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        // theme change
        if (prefs.getInt("themeInt", 0) == 0) {
            setTheme(R.style.AppTheme0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
            } else {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
                window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            }

            toolbarTxt.setTextColor(ContextCompat.getColor(this, R.color.accent));
            bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.chip_state_list));
            chip.setTextColor(ContextCompat.getColor(this, R.color.lessdark));
            bottomSheet.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            bottomSheetHeader.setTextColor(ContextCompat.getColor(this, R.color.lessdark));
            bottomSheetText.setTextColor(ContextCompat.getColor(this, R.color.lessdark));
            bottomNavDivider.setBackgroundColor(ContextCompat.getColor(this, R.color.lightgrey));

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(this, R.color.white));
            setTaskDescription(taskDesc);
        }
        if (prefs.getInt("themeInt", 0) == 1) {
            setTheme(R.style.AppTheme0Dark);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.dark));
            toolbarTxt.setTextColor(ContextCompat.getColor(this, R.color.accent));
            bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.dark));
            window.setNavigationBarColor(getResources().getColor(R.color.background));
            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.chip_state_list_dark));
            chip.setTextColor(ContextCompat.getColor(this, R.color.lightgrey));
            bottomSheet.setBackgroundColor(ContextCompat.getColor(this, R.color.dark));
            bottomSheetHeader.setTextColor(ContextCompat.getColor(this, R.color.lightgrey));
            bottomSheetText.setTextColor(ContextCompat.getColor(this, R.color.lightgrey));
            bottomNavDivider.setBackgroundColor(ContextCompat.getColor(this, R.color.darkdivider));

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.background));
            setTaskDescription(taskDesc);
        }

        if (prefs.getBoolean("greeting", false)) {
            if (!prefs.getString("username", "").isEmpty()) {
                final Animation animationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.chip_slide_in);
                final Animation animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.chip_slide_out);
                final Random generator = new Random();
                Resources res = getResources();
                String greetings[] = res.getStringArray(R.array.greeting8_array);
                Calendar rightNow = Calendar.getInstance();
                int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);

                switch (generator.nextInt(9)) {
                    case 0:
                        chip.setText(getString(R.string.greeting0, prefs.getString("username", "")));
                        break;
                    case 1:
                        chip.setText(getString(R.string.greeting1, prefs.getString("username", "")));
                        break;
                    case 2:
                        chip.setText(getString(R.string.greeting2, prefs.getString("username", "")));
                        break;
                    case 3:
                        chip.setText(getString(R.string.greeting3, prefs.getString("username", "")));
                        break;
                    case 4:
                        chip.setText(getString(R.string.greeting4, prefs.getString("username", "")));
                        break;
                    case 5:
                        chip.setText(getString(R.string.greeting5, prefs.getString("username", "")));
                        break;
                    case 6:
                        chip.setText(getString(R.string.greeting6, prefs.getString("username", "")));
                        break;
                    case 7:
                        chip.setText(getString(R.string.greeting7, prefs.getString("username", "")));
                        break;
                    case 8:
                        if (currentHour < 11) {
                            chip.setText(greetings[0] + prefs.getString("username", "") + ".");
                        } else if (currentHour > 10 && currentHour < 18) {
                            chip.setText(greetings[1] + prefs.getString("username", "") + ".");
                        } else if (currentHour > 17) {
                            chip.setText(greetings[2] + prefs.getString("username", "") + ".");
                        }
                        break;
                }


                final Handler chipIn = new Handler();
                chipIn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chip.setVisibility(View.VISIBLE);
                        chip.startAnimation(animationIn);
                    }
                }, 500);

                final Handler chipOut = new Handler();
                chipOut.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chip.startAnimation(animationOut);
                    }
                }, 3000);

                animationOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation arg0) {}
                    @Override
                    public void onAnimationRepeat(Animation arg0) {}
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        chip.setVisibility(View.GONE);
                    }
                });
            }
        }

        if (prefs.getInt("firstTimeOpening", 0) < 2) {
//            if (true) {
            final Handler bsbIn = new Handler();
            bsbIn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }, 600);

            final Handler bsbOut = new Handler();
            bsbOut.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }, 2000);

            edit.putInt("firstTimeOpening", prefs.getInt("firstTimeOpening", 0) + 1);
            edit.apply();
        }

        if (prefs.getBoolean("openInfo", false)) {
            final Handler bsbIn = new Handler();
            bsbIn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }, 600);
        }


        if (prefs.getBoolean("defaultPersonalised", false)) {
            loadFragment(new FragmentPersonal());
            bottomNav.setSelectedItemId(R.id.personal);
            if (!prefs.getString("username", "").isEmpty()) {
                if (Character.toString(prefs.getString("username", "").charAt(prefs.getString("username", "").length() - 1)).toLowerCase().equals("s")
                        || Character.toString(prefs.getString("username", "").charAt(prefs.getString("username", "").length() - 1)).toLowerCase().equals("z")) {
                    toolbarTxt.setText(prefs.getString("username", "") + getString(R.string.nosplan));
                } else {
                    toolbarTxt.setText(prefs.getString("username", "") + getString(R.string.splan));
                }
            } else {
                toolbarTxt.setText(getString(R.string.personalplan));
            }
        } else if (!prefs.getBoolean("defaultPersonalised", false)) {
            loadFragment(new FragmentPlan());
            bottomNav.setSelectedItemId(R.id.plan);
            toolbarTxt.setText(getString(R.string.app_name));
        }

        job();

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.plan:
                        fragment = new FragmentPlan();
                        toolbarTxt.setText(getString(R.string.app_name));
                        break;
                    case R.id.personal:
                        fragment = new FragmentPersonal();
                        if (!prefs.getString("username", "").isEmpty()) {
                            if (Character.toString(prefs.getString("username", "").charAt(prefs.getString("username", "").length() - 1)).toLowerCase().equals("s")
                                    || Character.toString(prefs.getString("username", "").charAt(prefs.getString("username", "").length() - 1)).toLowerCase().equals("x")
                                    || Character.toString(prefs.getString("username", "").charAt(prefs.getString("username", "").length() - 1)).toLowerCase().equals("z")) {
                                toolbarTxt.setText(prefs.getString("username", "") + getString(R.string.nosplan));
                            } else {
                                toolbarTxt.setText(prefs.getString("username", "") + getString(R.string.splan));
                            }
                        } else {
                            toolbarTxt.setText(getString(R.string.personalplan));
                        }
                        break;
//                    case R.id.search:
//                        fragment = new FragmentSearch();
//                        toolbarTxt.setText(getString(R.string.search));
//                        break;
                    case R.id.menu:
                        fragment = new FragmentFood();
                        toolbarTxt.setText(getString(R.string.foodmenu));
                        break;
                    case R.id.settings:
                        fragment = new FragmentSettings();
                        toolbarTxt.setText(getString(R.string.settings));
                        break;
                }
                appbarlayout.setExpanded(true);
                bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return loadFragment(fragment);
            }

        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
//                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out) // TODO add proper animations
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}