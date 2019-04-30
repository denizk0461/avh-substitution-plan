package com.denizd.substitutionplan;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.device.DeviceName;

import java.text.DecimalFormat;
import java.util.Random;

public class FragmentSettings extends Fragment {
    CoordinatorLayout coordinatorLayout;
    private int cs = 7;
    private String manufacturer, name, model;

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
        builder.setToolbarColor(getResources().getColor(R.color.white));
        final CustomTabsIntent customTabsIntent = builder.build();

        final EditText txtName = getView().findViewById(R.id.txtName);
        final Switch greeting = getView().findViewById(R.id.switchDisableGreeting);
        final Switch darkmode = getView().findViewById(R.id.switchDark);

        final EditText txtClasses = getView().findViewById(R.id.txtClasses);
        final EditText txtCourses = getView().findViewById(R.id.txtCourses);
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

        final TextView notif1 = getView().findViewById(R.id.txtNotifications);
        final TextView auto1 = getView().findViewById(R.id.txtAutoRefresh);
        final TextView notif2 = getView().findViewById(R.id.txtNotifiedAbout);
        final TextView auto2 = getView().findViewById(R.id.txtAutoRefreshTwo);

        helpCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog;
                if (prefs.getInt("themeInt", 0) == 1) {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                } else {
                    alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                }
                alertDialog.setTitle(getString(R.string.helpCoursesTitle));
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dialog, null);
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
                alertDialog.setTitle(getString(R.string.helpClassesTitle));
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dialog, null);
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

        // TODO check whether Huawei devices actually support background services

//        DeviceName.with(getContext()).request(new DeviceName.Callback() {
//            @Override
//            public void onFinished(DeviceName.DeviceInfo info, Exception error) {
//                manufacturer = info.manufacturer;
//                if (manufacturer.contains("Huawei") ||
//                        manufacturer.contains("Honor")) {
////                if (true) {
//                    notifswitch.setChecked(false);
//                    autoRefresh.setChecked(true);
//                    notifswitch.setEnabled(false);
//                    autoRefresh.setEnabled(false);
//
//                    @ColorInt int color;
//                    if (prefs.getInt("themeInt", 0) == 1) {
//                        TypedValue typedValue = new TypedValue();
//                        Resources.Theme theme = getContext().getTheme();
//                        theme.resolveAttribute(R.attr.colorHintDark, typedValue, true);
//                        color = typedValue.data;
//                        notif2.setTextColor(color);
//                        auto2.setTextColor(color);
//                    } else {
//                        TypedValue typedValue = new TypedValue();
//                        Resources.Theme theme = getContext().getTheme();
//                        theme.resolveAttribute(R.attr.colorHint, typedValue, true);
//                        color = typedValue.data;
//                    }
//
//                    notif1.setTextColor(color);
//                    auto1.setTextColor(color);
//                    edit.putBoolean("notif", false);
//                    edit.putBoolean("autoRefresh", true);
//                    edit.apply();
//                    notif2.setText(R.string.chinaNotif);
//                    auto2.setText(R.string.chinaAuto);
//                }
//            }
//        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customTabsIntent.launchUrl(getContext(), Uri.parse("http://307.joomla.schule.bremen.de"));
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
                alertDialog.setTitle("Licences");
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dialog, null);
                TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                dialogText.setText("Libraries:\n • Android Device Names © 2015 Jared Rummler, licensed under the Apache License, Version 2.0" +
                        "\n • EasyPreferences © 2018 Mukesh Solanki, licensed under the MIT License" +
                        "\n • jsoup HTML parser © 2009-2018 Jonathan Hedley, licensed under the open source MIT License" +
                        "\n\nFont:\n • Manrope © 2018-2019 Michael Sharanda, licensed under the SIL Open Font License 1.1" +
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
                alertDialog.setTitle("Terms & Conditions");
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dialog, null);
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
                alertDialog.setTitle("Privacy Policy");
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.simple_dialog, null);
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
                    Toast.makeText(getContext(), getString(R.string.donttell), Toast.LENGTH_LONG).show();
                    cs = 7;
                    customTabsIntent.launchUrl(getContext(), Uri.parse("http://www.flussufer.de/gerd/person.htm"));
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
                alertDialog.setTitle(getString(R.string.experimentalmenu));
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.edittext_dialog, null);
                TextView dialogText = dialogView.findViewById(R.id.dialogtext);
                dialogText.setText(getString(R.string.fortest));
                final EditText dialogEditText = dialogView.findViewById(R.id.dialog_edittext);
                final Button dialogButton = dialogView.findViewById(R.id.dialog_button);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialogEditText.getText().toString().equals("@DEV_DIAGNOSTICS")) {
                            AlertDialog.Builder alertDialogDev;
                            if (prefs.getInt("themeInt", 0) == 1) {
                                alertDialogDev = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                            } else {
                                alertDialogDev = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                            }
                            alertDialogDev.setTitle(getString(R.string.diagnosticsmenu));
                            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.diagnostics_dialog, null);
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

                        } else if (dialogEditText.getText().toString().equals("2018-04-20")) {
                            Toast.makeText(getActivity(), "◢ ◤",
                                    Toast.LENGTH_LONG).show();

                        } else if (dialogEditText.getText().toString().equals("Q1 Matchmaker")) {
                            final Random gen = new Random();
                            AlertDialog.Builder alertDialogDev;
                            if (prefs.getInt("themeInt", 0) == 1) {
                                alertDialogDev = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
                            } else {
                                alertDialogDev = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomLight);
                            }
                            alertDialogDev.setTitle(getString(R.string.dating));
                            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dating_dialog, null);

                            Button datingBtn = dialogView.findViewById(R.id.dating_btn);
                            final EditText boyT = dialogView.findViewById(R.id.dating_txt1);
                            final EditText girlT = dialogView.findViewById(R.id.dating_txt2);
                            final CheckBox boy = dialogView.findViewById(R.id.cb1);
                            final CheckBox girl = dialogView.findViewById(R.id.cb2);
                            final TextView percentage = dialogView.findViewById(R.id.dating_txtp);
                            datingBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        double boyP = 0, girlP = 0, perc = 0;
                                        if (boyT.getText().toString().isEmpty() || boy.isChecked()) {
                                            boyT.setText(names.boy[gen.nextInt(names.boy.length)]);
                                        }
                                        if (girlT.getText().toString().isEmpty() || girl.isChecked()) {
                                            girlT.setText(names.girl[gen.nextInt(names.girl.length)]);
                                        }
                                        for (int i = 0; i < boyT.getText().length(); i++) {
                                            boyP++;
                                        }
                                        for (int i = 0; i < girlT.getText().length(); i++) {
                                            girlP++;
                                        }
                                        if (girlP < boyP) {
                                            perc = (girlP / boyP) * 100;
                                        }
                                        if (girlP > boyP) {
                                            perc = (boyP / girlP) * 100;
                                        }
                                        char multiplyTempBoy = boyT.getText().charAt(boyT.getText().length() - 1);
                                        char multiplyTempGirl = girlT.getText().charAt(girlT.getText().length() - 1);
                                        double multiply = Character.getNumericValue(multiplyTempBoy) + Character.getNumericValue(multiplyTempGirl);
                                        multiply = multiply / 1.2;
                                        if (perc == 0) {
                                            perc += 30;
                                        }
                                        DecimalFormat df = new DecimalFormat("#.##");
                                        if (perc * (multiply / 40) > 100) {
                                            percentage.setText("100.00%");
                                        } else {
                                            percentage.setText(df.format(perc * (multiply / 40)) + "%");
                                        }
                                    } catch (NullPointerException e) {
                                        Toast.makeText(getActivity(), getString(R.string.error),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            alertDialogDev.setView(dialogView);
                            alertDialogDev.show();

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.nothinghappened),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
                return true;
            }
        });
    }

    private void setDiagnosticsText(TextView dialogText, SharedPreferences prefs) {
        dialogText.setText("First Launch: " + prefs.getString("firstTimeDev", "") +
                "\n\nApp launched: " + prefs.getInt("launchDev", 0) +
                "\n\nNotification Service fired: " + prefs.getInt("notificationTestNumberDev", 0) +
                "\n\nDevice Name: " + name +
                "\n\nDevice Model: " + model +
                "\n\nAndroid Version: " + Build.VERSION.RELEASE);
    }
}
