package com.denizd.substitutionplan;

import android.animation.Animator;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Calendar;

public class FirstTime extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);
        setTheme(R.style.AppTheme0);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        } else {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        window.setBackgroundDrawable(getDrawable(R.drawable.white));
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(this, R.color.white));
        setTaskDescription(taskDesc);

        final Context context = this;

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final SharedPreferences.Editor edit = prefs.edit();

        final ExtendedFloatingActionButton fab = findViewById(R.id.efab);

        final Intent start = new Intent(this, MainActivity.class);

        final EditText name = findViewById(R.id.txtName);
        final EditText grade = findViewById(R.id.txtClasses);
        final EditText courses = findViewById(R.id.txtCourses);
        final CheckBox notif = findViewById(R.id.cbNotif);
        final CheckBox dark = findViewById(R.id.cbDark);
        final CheckBox pers = findViewById(R.id.cbPersonalised);
        final ImageButton helpClasses = findViewById(R.id.chipHelpClasses);
        final ImageButton helpCourses = findViewById(R.id.chipHelpCourses);

        helpCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog;
                alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogCustomLight);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.simple_dialog, null);
                TextView title = dialogView.findViewById(R.id.textviewtitle);
                title.setText(R.string.helpCoursesTitle);
                TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                dialogText.setText(getString(R.string.helpCourses));
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        helpClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog;
                alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogCustomLight);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.simple_dialog, null);
                TextView title = dialogView.findViewById(R.id.textviewtitle);
                title.setText(R.string.helpClassesTitle);
                TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                dialogText.setText(getString(R.string.helpClasses));
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setClickable(false);

                edit.putString("firstTimeDev", String.valueOf(Calendar.getInstance().getTime()));
                edit.putString("username", name.getText().toString());
                edit.putString("classes", grade.getText().toString());
                edit.putString("courses", courses.getText().toString());
                if (dark.isChecked()) {
                    edit.putInt("themeInt", 1);
                } else {
                    edit.putInt("themeInt", 0);
                }
                if (notif.isChecked()) {
                    edit.putBoolean("notif", true);
                } else {
                    edit.putBoolean("notif", false);
                }
                if (pers.isChecked()) {
                    edit.putBoolean("defaultPersonalised", true);
                } else {
                    edit.putBoolean("defaultPersonalised", false);
                }

                edit.putBoolean("firstTime", false);
                edit.apply();

                final CoordinatorLayout cLayout = findViewById(R.id.coordinatorLayout);
                final LinearLayout linearInflation = findViewById(R.id.linearInflation);

                int x = fab.getRight() - fab.getWidth() / 2;
                int y = fab.getBottom() - fab.getHeight() / 2;

                int startRadius = 0;
                int endRadius = (int) Math.hypot(cLayout.getWidth(), cLayout.getHeight());

                inflater.inflate(R.layout.welcome_screen, linearInflation, true);

                Animator anim = ViewAnimationUtils.createCircularReveal(linearInflation, x, y, startRadius, endRadius);
                anim.setDuration(600);

                final LinearLayout colour = findViewById(R.id.colourLayout);

                linearInflation.setVisibility(View.VISIBLE);
                anim.start();

                final Animation animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_short);

                final Handler handlerZero = new Handler();
                handlerZero.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        colour.startAnimation(animationOut);
                    }
                }, 400);

                animationOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        colour.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                final Handler handlerOne = new Handler();
                handlerOne.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ImageView mImgCheck = findViewById(R.id.imageView);
                        ((Animatable) mImgCheck.getDrawable()).start();
                    }
                }, 1000);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(start);
                        finish();
                    }
                }, 2000);

            }
        });
    }
}
