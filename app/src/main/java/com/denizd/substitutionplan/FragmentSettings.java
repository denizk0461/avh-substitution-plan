package com.denizd.substitutionplan;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jaredrummler.android.device.DeviceName;

import java.text.DecimalFormat;
import java.util.Random;

public class FragmentSettings extends Fragment implements View.OnClickListener {
    CoordinatorLayout coordinatorLayout;
    private int cs = 7, i = 0;
    private String name, model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_settings, container, false);
        coordinatorLayout = rootView.findViewById(R.id.fragment_container);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar progressBar = getView().getRootView().findViewById(R.id.progressBar);
        progressBar.setProgress(0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor edit = prefs.edit();

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.background));
        final CustomTabsIntent customTabsIntent = builder.build();

        final TextInputEditText txtName = getView().findViewById(R.id.txtName);
        final Switch greeting = getView().findViewById(R.id.switchDisableGreeting);
        final Switch darkmode = getView().findViewById(R.id.switchDark);
        final Switch showinfo = getView().findViewById(R.id.switchHideInfo);
        final LinearLayout customiseColours = getView().findViewById(R.id.btnCustomiseColours);

        final TextInputEditText txtClasses = getView().findViewById(R.id.txtClasses);
        final TextInputEditText txtCourses = getView().findViewById(R.id.txtCourses);
        final ImageButton helpClasses = getView().findViewById(R.id.chipHelpClasses);
        final ImageButton helpCourses = getView().findViewById(R.id.chipHelpCourses);

        final Switch notifswitch = getView().findViewById(R.id.switchNotifications);

        final Switch defaultPlan = getView().findViewById(R.id.switchDefaultPlan);
        final Switch openInfo = getView().findViewById(R.id.switchOpenInfo);
        final Switch autoRefresh = getView().findViewById(R.id.switchAutoRefresh);
        final LinearLayout website = getView().findViewById(R.id.btnWebsite);
        final LinearLayout licences = getView().findViewById(R.id.btnLicences);
        final TextView versionNumber = getView().findViewById(R.id.txtVersionTwo);

        final LinearLayout tac = getView().findViewById(R.id.btnTerms);
        final LinearLayout privacy = getView().findViewById(R.id.btnPrivacyP);
        final LinearLayout version = getView().findViewById(R.id.btnVersion);

        helpCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog;
                if (prefs.getInt("themeInt", 0) == 1) {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                } else {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                }
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dialog, null);
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
                if (prefs.getInt("themeInt", 0) == 1) {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                } else {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                }
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dialog, null);
                TextView title = dialogView.findViewById(R.id.textviewtitle);
                title.setText(R.string.helpClassesTitle);
                TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                dialogText.setText(getString(R.string.helpClasses));
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        txtName.setText(prefs.getString("username", ""));
        txtName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit.putString("username", txtName.getText().toString());
                edit.apply();
            }
        });

        if (!prefs.getBoolean("greeting", false)) {
            greeting.setChecked(false);
        }
        if (prefs.getBoolean("greeting", false)) {
            greeting.setChecked(true);
        }

        greeting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit.putBoolean("greeting", true);
                }
                if (!isChecked) {
                    edit.putBoolean("greeting", false);
                }
                edit.apply();
            }
        });

        if (prefs.getInt("themeInt", 0) == 0) {
            darkmode.setChecked(false);
        }
        if (prefs.getInt("themeInt", 0) == 1) {
            darkmode.setChecked(true);
        }

        darkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit.putInt("themeInt", 1);
                }
                if (!isChecked) {
                    edit.putInt("themeInt", 0);
                }
                edit.apply();
            }
        });

        if (prefs.getBoolean("showinfotab", true)) {
            showinfo.setChecked(true);
        } else {
            showinfo.setChecked(false);
        }

        showinfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit.putBoolean("showinfotab", true);
                } else if (!isChecked) {
                    edit.putBoolean("showinfotab", false);
                }
                edit.apply();
            }
        });

        final String[] coursesNoLang = {"German", "English", "French", "Spanish", "Latin", "Turkish", "Chinese", "Arts", "Music", "Theatre",
                "Geography", "History", "Politics", "Philosophy", "Religion",
                "Maths", "Biology", "Chemistry", "Physics", "CompSci",
                "PhysEd", "GLL", "WAT", "Forder", "WP"};
        final String[] courses = {getString(R.string.courseDeu), getString(R.string.courseEng), getString(R.string.courseFra),
                getString(R.string.courseSpa), getString(R.string.courseLat), getString(R.string.courseTue),
                getString(R.string.courseChi), getString(R.string.courseKun), getString(R.string.courseMus),
                getString(R.string.courseDar),
                getString(R.string.courseGeg), getString(R.string.courseGes), getString(R.string.coursePol),
                getString(R.string.coursePhi), getString(R.string.courseRel),
                getString(R.string.courseMat), getString(R.string.courseBio), getString(R.string.courseChe),
                getString(R.string.coursePhy), getString(R.string.courseInf),
                getString(R.string.courseSpo), getString(R.string.courseGll), getString(R.string.courseWat),
                getString(R.string.courseFor), getString(R.string.courseWp)};
        final int[] coursesIcons = {R.drawable.ic_german, R.drawable.ic_english,
                R.drawable.ic_french, R.drawable.ic_spanish, R.drawable.ic_latin, R.drawable.ic_turkish,
                R.drawable.ic_chinese, R.drawable.ic_arts, R.drawable.ic_music, R.drawable.ic_drama,
                R.drawable.ic_geography, R.drawable.ic_history, R.drawable.ic_politics, R.drawable.ic_philosophy,
                R.drawable.ic_religion,
                R.drawable.ic_maths, R.drawable.ic_biology, R.drawable.ic_chemistry, R.drawable.ic_physics,
                R.drawable.ic_compsci,
                R.drawable.ic_pe, R.drawable.ic_gll, R.drawable.ic_wat, R.drawable.ic_help, R.drawable.ic_pencil};
        customiseColours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder colourCustomiserBuilder;
                if (prefs.getInt("themeInt", 0) == 1) {
                    colourCustomiserBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                } else {
                    colourCustomiserBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                }
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.empty_dialog, null);
                TextView titleText = dialogView.findViewById(R.id.empty_textviewtitle);
                titleText.setText(R.string.customisecolor1);
                LinearLayout emptyLayout = dialogView.findViewById(R.id.empty_linearlayout);
                for (i = 0; i < coursesNoLang.length; i++) {
                    View item = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
                    TextView itemText = item.findViewById(R.id.item_text);
                    ImageView itemImage = item.findViewById(R.id.item_image);
                    LinearLayout layout = item.findViewById(R.id.item_layout);
                    itemText.setText(courses[i]);
                    itemImage.setImageDrawable(getResources().getDrawable(coursesIcons[i]));

                    final String title = courses[i];
                    final String titleNoLang = coursesNoLang[i];

                    if (prefs.getInt("col" + titleNoLang, 0) != 0) {
                        itemText.setTextColor(ContextCompat.getColor(getContext(), prefs.getInt("col" + titleNoLang, 0)));
                        itemImage.getDrawable().setTint(ContextCompat.getColor(getContext(), prefs.getInt("col" + titleNoLang, 0)));
                    }

                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder colourPickerBuilder;
                            if (prefs.getInt("themeInt", 0) == 1) {
                                colourPickerBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                            } else {
                                colourPickerBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                            }
                            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.empty_dialog, null);
                            TextView titleText = dialogView.findViewById(R.id.empty_textviewtitle);
                            titleText.setText(title);
                            LinearLayout emptyLayout = dialogView.findViewById(R.id.empty_linearlayout);
                            View picker = LayoutInflater.from(getContext()).inflate(R.layout.colour_picker, null);
                            emptyLayout.addView(picker);
                            colourPickerBuilder.setView(dialogView);
                            final AlertDialog colourPickerDialog = colourPickerBuilder.create();

                            final MaterialButton[] buttons = {picker.findViewById(R.id.colourRed), picker.findViewById(R.id.colourPink),
                                    picker.findViewById(R.id.colourPurple), picker.findViewById(R.id.colourDeepPurple),
                                    picker.findViewById(R.id.colourLavender), picker.findViewById(R.id.colourIndigo),
                                    picker.findViewById(R.id.colourBlue), picker.findViewById(R.id.colourLightBlue),
                                    picker.findViewById(R.id.colourTeal), picker.findViewById(R.id.colourGreen),
                                    picker.findViewById(R.id.colourYellow), picker.findViewById(R.id.colourOrange),
                                    picker.findViewById(R.id.colourNeonRed), picker.findViewById(R.id.colourNeonPink),
                                    picker.findViewById(R.id.colourNeonPurple), picker.findViewById(R.id.colourNeonDeepPurple),
                                    picker.findViewById(R.id.colourNeonBlue), picker.findViewById(R.id.colourNeonGreen),
                                    picker.findViewById(R.id.colourNeonYellow), picker.findViewById(R.id.colourNeonOrange),
                                    picker.findViewById(R.id.colourNone)};
                            final int[] colours = {R.color.red, R.color.pink, R.color.purple, R.color.deeppurple,
                                    R.color.lavender, R.color.indigo, R.color.blue, R.color.lightblue,
                                    R.color.teal, R.color.green, R.color.yellow, R.color.orange,
                                    R.color.neonred, R.color.neonpink, R.color.neonpurple, R.color.neondeeppurple,
                                    R.color.neonblue, R.color.neongreen, R.color.neonyellow, R.color.neonorange, 0};
                            for (int i2 = 0; i2 < buttons.length; i2++) {
                                final int colour = colours[i2];
                                buttons[i2].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        edit.putInt("col" + titleNoLang, colour);
                                        edit.apply();
                                        colourPickerDialog.dismiss();
                                    }
                                });
                            }

                            colourPickerDialog.show();
                        }
                    });
                    emptyLayout.addView(item);
                }
                View button = LayoutInflater.from(getContext()).inflate(R.layout.forever_alone_button, null);
                MaterialButton buttonclearallcolours = button.findViewById(R.id.buttonclearallcolours);
                emptyLayout.addView(button);
                colourCustomiserBuilder.setView(dialogView);
                final AlertDialog colourCustomiserDialog = colourCustomiserBuilder.create();
                buttonclearallcolours.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (String courses : coursesNoLang) {
                            edit.putInt("col" + courses, 0);
                        }
                        edit.apply();
                        Toast.makeText(getActivity(), getString(R.string.allcolourscleared),
                                Toast.LENGTH_LONG).show();
                        colourCustomiserDialog.cancel();
                    }
                });
                colourCustomiserDialog.show();
            }
        });



        // ------

        txtClasses.setText(prefs.getString("classes", ""));
        txtCourses.setText(prefs.getString("courses", ""));

        txtClasses.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit.putString("classes", txtClasses.getText().toString());
                edit.apply();
            }
        });

        txtCourses.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit.putString("courses", txtCourses.getText().toString());
                edit.apply();
            }
        });

        if (!prefs.getBoolean("notif", false)) {
            notifswitch.setChecked(false);
        }
        if (prefs.getBoolean("notif", false)) {
            notifswitch.setChecked(true);
        }

        notifswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit.putBoolean("notif", true);
                }
                if (!isChecked) {
                    edit.putBoolean("notif", false);
                }
                edit.apply();
            }
        });

        if (!prefs.getBoolean("defaultPersonalised", false)) {
            defaultPlan.setChecked(false);
        }
        if (prefs.getBoolean("defaultPersonalised", false)) {
            defaultPlan.setChecked(true);
        }

        defaultPlan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit.putBoolean("defaultPersonalised", true);
                }
                if (!isChecked) {
                    edit.putBoolean("defaultPersonalised", false);
                }
                edit.apply();
            }
        });

        if (!prefs.getBoolean("openInfo", false)) {
            openInfo.setChecked(false);
        }
        if (prefs.getBoolean("openInfo", false)) {
            openInfo.setChecked(true);
        }

        openInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit.putBoolean("openInfo", true);
                }
                if (!isChecked) {
                    edit.putBoolean("openInfo", false);
                }
                edit.apply();
            }
        });

        if (!prefs.getBoolean("autoRefresh", false)) {
            autoRefresh.setChecked(false);
        }
        if (prefs.getBoolean("autoRefresh", false)) {
            autoRefresh.setChecked(true);
        }

        autoRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit.putBoolean("autoRefresh", true);
                }
                if (!isChecked) {
                    edit.putBoolean("autoRefresh", false);
                }
                edit.apply();
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    customTabsIntent.launchUrl(getContext(), Uri.parse("http://307.joomla.schule.bremen.de"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), getString(R.string.chromecompatible), Toast.LENGTH_LONG).show();
                }
            }
        });

        licences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog;
                if (prefs.getInt("themeInt", 0) == 1) {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                } else {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                }
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dialog, null);
                TextView title = dialogView.findViewById(R.id.textviewtitle);
                title.setText("Licences");
                TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                dialogText.setText("Libraries:\n • Android Device Names © 2015 Jared Rummler, licensed under the Apache Licence, Version 2.0" +
                        "\n • EasyPreferences © 2018 Mukesh Solanki, licensed under the MIT Licence" +
                        "\n • jsoup HTML parser © 2009-2018 Jonathan Hedley, licensed under the open source MIT Licence" +
                        "\n\nFont:\n • Manrope © 2018-2019 Michael Sharanda, licensed under the SIL Open Font Licence 1.1" +
                        "\n\nIcons:\n • bqlqn\n • fjstudio\n • Freepik\n • Smashicons\n • © 2013-2019 Freepik Company S.L., licensed under Creative Commons BY 3.0" +
                        "\n\nMarketing & Publishing:\n • Leon Becker\n • Alex Lick\n • Batuhan Özcan\n • Erich Kerkesner");
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        tac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog;
                if (prefs.getInt("themeInt", 0) == 1) {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                } else {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                }
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dialog, null);
                TextView title = dialogView.findViewById(R.id.textviewtitle);
                title.setText("Terms & Conditions");
                TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                dialogText.setText("These terms automatically apply to anyone using this app - therefore, please make sure to read them carefully before using the app. Copying or modifying parts of the app or the entire app, as well as creating derivative versions, is prohibited without permission. This app is intellectual property of the developer.\n" +
                        "\n" +
                        "The developer reserves the right to make changes to the app at any given time and for any reason. The user will be informed of any changes made accordingly.\n" +
                        "\n" +
                        "Tampering with the end device, in the form of rooting or otherwise modifying the operating system may result in malfunction of the software. In this case, the developer does not provide support, as the user is responsible for keeping the device free of software that may compromise its security.\n" +
                        "\n" +
                        "Full functionality of the app relies on a steady internet connection. The developer does not take responsibility for malfunction of these services, nor for any errors caused by the end device. The user is responsible for ensuring that their device is fully updated and fully functioning.\n" +
                        "\n" +
                        "As the app relies on third-parties, information provided in-app may be delayed or incorrect at times. The developer accepts no liability for any information mistakenly provided in the app.\n" +
                        "\n" +
                        "The developer may wish to update the app at any point. Should such action arise, then the user is advised to update the app to ensure proper functionality. However, the developer does not promise that the app will be continuously updated, and may stop providing the app at any point. Unless told otherwise, upon any termination, (a) the rights and licenses granted to the user in these terms will end; (b) the user must stop using the app, and (if needed) delete it from their device.\n" +
                        "\n" +
                        "Changes to these Terms & Conditions\n" +
                        "\n" +
                        "The developer may update these Terms & Conditions from time to time. Thus, the user is advised to review this page periodically for any changes. Any changes are effective immediately after they are posted on this page.\n" +
                        "\n" +
                        "Contact Us\n" +
                        "\n" +
                        "In case of questions or suggestions regarding these Terms & Conditions, the user is advised to contact the developer of this app. Contact information is provided through the Play Store.\n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "These Terms & Conditions were modified upon the template provided by App Privacy Policy Generator. This service is in no way affiliated with AvH Plan or the developer.");
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog;
                if (prefs.getInt("themeInt", 0) == 1) {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                } else {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                }
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dialog, null);
                TextView title = dialogView.findViewById(R.id.textviewtitle);
                title.setText("Privacy Policy");
                TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                dialogText.setText("The app \"Alexander-von-Humboldt-Plan\", hereby abbreviated to AvH Plan, is provided as a free service by the developer for use as is.\n" +
                        "\n" +
                        "This page is used to inform visitors and users regarding policies with the collection, use, and disclosure of personal information, if they choose to make use of the service.\n" +
                        "\n" +
                        "AvH Plan does not collect, store, share, or in any other way use personal information retrieved from its users. Any information asked for within the app is only used to improve the user experience and as such, is stored locally and cannot be accessed by third-parties or be used to identify the user.\n" +
                        "\n" +
                        "Information Collection and Use\n" +
                        "\n" +
                        "At this moment, AvH Plan does not require personally identifiable information for its core functionality. Certain features do require entering information that may be personally identifiable, however, this data will be retained on the user's device and is therefore not accessible outside the app\n" +
                        "\n" +
                        "Log Data\n" +
                        "\n" +
                        "In the case of an error in the app, device-specific data may be collected and stored on your phone. This data is called Log Data. This Log Data may include information, such as your device's Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilising the service, the time and date when the last usage occured, and other statistics. This data can not be used to personally identify any user.\n" +
                        "\n" +
                        "Cookies\n" +
                        "\n" +
                        "Cookies are files with small amounts of data that are commonly used as anonymous unique identifiers on the web. AvH Plan strives to not make use of cookies for its functionality, however, as its functionality requires access to third-party-websites, cookies may be unintentionally collected by the device's web browser. AvH Plan does not make use of these cookies in any way.\n" +
                        "\n" +
                        "Service Providers\n" +
                        "\n" +
                        "No third-parties are employed for providing any functionality or services of the AvH Plan-app and as such, all functionality is accessible without directly sharing personal data with third-parties.\n" +
                        "\n" +
                        "Security\n" +
                        "\n" +
                        "While absolute security cannot be ensured due to the internet functionality the app employs, no information is shared over any transmission methods.\n" +
                        "\n" +
                        "Links to Other Sites\n" +
                        "\n" +
                        "This Service may contain links to other sites, which are not operated by the developer of AvH Plan. The user is advised to review the privacy policy of any webpage they may access through the app. No responsibility for the content of these third-party websites is provided by the app developer.\n" +
                        "\n" +
                        "Children’s Privacy\n" +
                        "\n" +
                        "The service does not address anyone under the age of 13. As the app does not collect any information from its users, no data is collected from users that may be under the age of 13.\n" +
                        "\n" +
                        "Changes to This Privacy Policy\n" +
                        "\n" +
                        "This privacy policy may be updated as the AvH Plan app is updated with new features. As such, the user is advised to review this page periodically for any changes. Any changes that may be made are effective immediately upon publishing.\n" +
                        "\n" +
                        "Contact Us\n" +
                        "\n" +
                        "In case of questions or suggestions regarding this privacy policy, the user is advised to contact the developer of this app. Contact information is provided through the Play Store.\n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "This privacy policy was modified upon the template provided by privacypolicytemplate.net and App Privacy Policy Generator. These services are in no way affiliated with AvH Plan or the developer.");
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cs > 0) {
                    cs--;
                } else {
                    try {
                        cs = 7;
                        customTabsIntent.launchUrl(getContext(), Uri.parse("http://www.flussufer.de/gerd/person.htm"));
                        Toast.makeText(getContext(), getString(R.string.donttell), Toast.LENGTH_LONG).show();
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), getString(R.string.chromecompatible), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



//        if (BuildConfig.DEBUG) {
//            versionNumber.setText(BuildConfig.VERSION_NAME + "-DEV_BUILD");
//        } else {
            versionNumber.setText(BuildConfig.VERSION_NAME);
//        }

        final LinearLayout hiddenBtn = getView().findViewById(R.id.btnHiddenENP);
        hiddenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), getString(R.string.bestclass),
                        Toast.LENGTH_LONG).show();
            }
        });
        hiddenBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DeviceName.with(getContext()).request(new DeviceName.Callback() {
                    @Override
                    public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                        name = info.marketName;            // "Galaxy S8+"
                        model = info.model;                // "SM-G955W"
                    }
                });
                AlertDialog.Builder alertDialog;
                if (prefs.getInt("themeInt", 0) == 1) {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                } else {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                }
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.edittext_dialog, null);
                TextView title = dialogView.findViewById(R.id.textviewtitle);
                title.setText(R.string.experimentalmenu);
                TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                dialogText.setText(getString(R.string.fortest));
                final EditText dialogEditText = dialogView.findViewById(R.id.dialog_edittext);
                final Button dialogButton = dialogView.findViewById(R.id.dialog_button);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (dialogEditText.getText().toString()) {
                            case "@DIAGNOSTICS": {
                                AlertDialog.Builder alertDialogDev;
                                if (prefs.getInt("themeInt", 0) == 1) {
                                    alertDialogDev = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                                } else {
                                    alertDialogDev = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                                }
                                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.diagnostics_dialog, null);
                                TextView title = dialogView.findViewById(R.id.textviewtitle);
                                title.setText(R.string.diagnosticsmenu);
                                final TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                                setDiagnosticsText(dialogText, prefs);
                                Button launch = dialogView.findViewById(R.id.btnResetLaunch);
                                Button notif = dialogView.findViewById(R.id.btnResetNotif);
                                launch.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        edit.putInt("launchDev", 0);
                                        edit.apply();
                                        setDiagnosticsText(dialogText, prefs);
                                    }
                                });
                                notif.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        edit.putInt("notificationTestNumberDev", 0);
                                        edit.apply();
                                        setDiagnosticsText(dialogText, prefs);
                                    }
                                });


                                alertDialogDev.setView(dialogView);
                                alertDialogDev.show();

                                break;
                            }
                            case "2018-04-20":
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=u8tdT5pAE34")); // SOS
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setPackage("com.google.android.youtube");
                                    startActivity(intent);
                                    Toast.makeText(getActivity(), getEmojiByUnicode(0x1F494),
                                            Toast.LENGTH_LONG).show();
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(getActivity(), R.string.noyoutube, Toast.LENGTH_LONG).show();
                                }
                                break;
                            case "Q1 Matchmaker": {
                                final Random gen = new Random();
                                AlertDialog.Builder alertDialogDev;
                                if (prefs.getInt("themeInt", 0) == 1) {
                                    alertDialogDev = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                                } else {
                                    alertDialogDev = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                                }
                                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dating_dialog, null);
                                TextView title = dialogView.findViewById(R.id.textviewtitle);
                                title.setText(R.string.dating);

                                Button datingBtn = dialogView.findViewById(R.id.dating_btn);
                                final TextInputEditText boyT = dialogView.findViewById(R.id.dating_txt1);
                                final TextInputEditText girlT = dialogView.findViewById(R.id.dating_txt2);
                                final CheckBox boy = dialogView.findViewById(R.id.cb1);
                                final CheckBox girl = dialogView.findViewById(R.id.cb2);
                                final TextView percentage = dialogView.findViewById(R.id.dating_txtp);
                                datingBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        try {
//                                            double boyP = 0, girlP = 0, perc = 0;
//                                            if (boyT.getText().toString().isEmpty() || boy.isChecked()) {
//                                                boyT.setText(MiscData.boy[gen.nextInt(MiscData.boy.length)]);
//                                            }
//                                            if (girlT.getText().toString().isEmpty() || girl.isChecked()) {
//                                                girlT.setText(MiscData.girl[gen.nextInt(MiscData.girl.length)]);
//                                            }
//                                            for (int i = 0; i < boyT.getText().length(); i++) {
//                                                boyP++;
//                                            }
//                                            for (int i = 0; i < girlT.getText().length(); i++) {
//                                                girlP++;
//                                            }
//                                            if (girlP < boyP) {
//                                                perc = (girlP / boyP) * 100;
//                                            }
//                                            if (girlP > boyP) {
//                                                perc = (boyP / girlP) * 100;
//                                            }
//                                            char multiplyTempBoy = boyT.getText().charAt(boyT.getText().length() - 1);
//                                            char multiplyTempGirl = girlT.getText().charAt(girlT.getText().length() - 1);
//                                            double multiply = Character.getNumericValue(multiplyTempBoy) + Character.getNumericValue(multiplyTempGirl);
//                                            multiply = multiply / 1.2;
//                                            if (perc == 0) {
//                                                perc += 30;
//                                            }
//                                            DecimalFormat df = new DecimalFormat("#.##");
//                                            if (perc * (multiply / 40) > 100) {
//                                                percentage.setText("100.00%");
//                                            } else {
//                                                percentage.setText(df.format(perc * (multiply / 40)) + "%");
//                                            }
//                                        } catch (NullPointerException e) {
//                                            Toast.makeText(getActivity(), getString(R.string.error),
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
                                            Toast.makeText(getActivity(), "Deprecated",
                                                    Toast.LENGTH_SHORT).show();
                                    }
                                });
                                alertDialogDev.setView(dialogView);
                                alertDialogDev.show();

                                break;
                            }
                            case "@NOTIFICATION": {
                                edit.putString("time", "");
                                edit.apply();
                                Toast.makeText(getActivity(), "Notification time cleared",
                                        Toast.LENGTH_LONG).show();
                                break;
                            }
                            default:
                                Toast.makeText(getActivity(), getString(R.string.nothinghappened),
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
                return true;
            }
        });
    }

    private String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private void setDiagnosticsText(TextView dialogText, SharedPreferences prefs) {
        dialogText.setText("First Launch: " + prefs.getString("firstTimeDev", "") +
                "\n\nApp launched: " + prefs.getInt("launchDev", 0) +
                "\n\nNotification Service fired: " + prefs.getInt("notificationTestNumberDev", 0) +
                "\n\nDevice Name: " + name +
                "\n\nDevice Model: " + model +
                "\n\nAndroid Version: " + Build.VERSION.RELEASE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
