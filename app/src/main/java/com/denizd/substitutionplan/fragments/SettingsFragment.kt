package com.denizd.substitutionplan.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denizd.substitutionplan.*
import com.denizd.substitutionplan.adapters.ColourAdapter
import com.denizd.substitutionplan.adapters.RingtoneAdapter
import com.denizd.substitutionplan.data.DataFetcher
import com.denizd.substitutionplan.data.HelperFunctions
import com.denizd.substitutionplan.models.Colour
import com.denizd.substitutionplan.models.Ringtone
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.collections.ArrayList

internal class SettingsFragment : Fragment(R.layout.content_settings), View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
    ColourAdapter.OnClickListener,
    RingtoneAdapter.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor
    private val builder = CustomTabsIntent.Builder()
    private val customTabsIntent = builder.build() as CustomTabsIntent
    private var cs: Int = 7
    private var window: Window? = null
    private lateinit var colourRecycler: RecyclerView
    private lateinit var ringtoneCustomiserDialog: AlertDialog
    private lateinit var currentRingtone: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        edit = prefs.edit()

        window = activity?.window
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtName = view.findViewById<TextInputEditText>(R.id.txtName)
        val txtClasses = view.findViewById<TextInputEditText>(R.id.txtClasses)
        val txtCourses = view.findViewById<TextInputEditText>(R.id.txtCourses)

        val switchDisableGreeting = view.findViewById<Switch>(R.id.switchDisableGreeting)
//        val switchDark = view.findViewById<Switch>(R.id.switchDark)
        val switchNotifications = view.findViewById<Switch>(R.id.switchNotifications)
        val switchDefaultPlan = view.findViewById<Switch>(R.id.switchDefaultPlan)
        val switchAutoRefresh = view.findViewById<Switch>(R.id.switchAutoRefresh)
        val versionNumber = view.findViewById<TextView>(R.id.txtVersionTwo)
        val helpCourses = view.findViewById<ImageButton>(R.id.chipHelpCourses)
        val helpClasses = view.findViewById<ImageButton>(R.id.chipHelpClasses)
        val btnCustomiseColours = view.findViewById<LinearLayout>(R.id.btnCustomiseColours)
        val btnVersion = view.findViewById<LinearLayout>(R.id.btnVersion)
        currentRingtone = view.findViewById(R.id.txtCustomiseRingtone2)
        setRingtoneText()
        val btnForceRefresh = view.findViewById<LinearLayout>(R.id.btnForceRefresh)

        switchDisableGreeting.setOnCheckedChangeListener(this)
//        switchDark.setOnCheckedChangeListener(this)
        switchNotifications.setOnCheckedChangeListener(this)
        switchDefaultPlan.setOnCheckedChangeListener(this)
        switchAutoRefresh.setOnCheckedChangeListener(this)
        helpCourses.setOnClickListener(this)
        helpClasses.setOnClickListener(this)
        btnCustomiseColours.setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.btnNoNotif).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.btnCustomiseRingtone).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.btnWebsite).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.btnLicences).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.btnTerms).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.btnPrivacyP).setOnClickListener(this)
        btnVersion.setOnClickListener(this)
        btnForceRefresh.setOnClickListener(this)
        btnForceRefresh.setOnLongClickListener {
            edit.putString("timeNew", "").putString("newFoodTime", "").apply()
            makeToast(mContext.getString(R.string.forcedRefreshTimes))
            true
        }

        val darkModeDropDown = view.findViewById<AutoCompleteTextView>(R.id.darkModeDropDownText)
        val darkModeList = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            R.array.themesPreQ
        } else {
            R.array.themes
        }

        val darkModeAdapter = ArrayAdapter.createFromResource(
            mContext,
            darkModeList,
            R.layout.dropdown_item
        )
        darkModeAdapter.setDropDownViewResource(R.layout.dropdown_item)
        darkModeDropDown.setAdapter(darkModeAdapter)

        darkModeDropDown.setText(darkModeDropDown.adapter.getItem(prefs.getInt("themeInt", 0)).toString(), false)

        darkModeDropDown.setOnItemClickListener { _, _, position, _ ->
            edit.putInt("themeInt", position).apply()

            val bottomNav = view.rootView.findViewById<BottomNavigationView>(R.id.bottom_nav)
            when (position) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    if (Build.VERSION.SDK_INT in 23..28) {
                        @SuppressLint("InlinedApi")
                        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        window?.navigationBarColor = ContextCompat.getColor(mContext, R.color.colorBackground)
                    }
                }
                2 -> { // only accessible on Android 10 (and above)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        window?.navigationBarColor = ContextCompat.getColor(mContext, R.color.colorBackground)
                    }
                }
            }
            if (prefs.getBoolean("defaultPersonalised", false)) {
                bottomNav.selectedItemId = R.id.personal
            } else {
                bottomNav.selectedItemId = R.id.plan
            }
        }

        switchDisableGreeting.isChecked = prefs.getBoolean("greeting", true)

        switchNotifications.isChecked = prefs.getBoolean("notif", false)
        switchDefaultPlan.isChecked = prefs.getBoolean("defaultPersonalised", false)
        switchAutoRefresh.isChecked = prefs.getBoolean("autoRefresh", false)

        versionNumber.text = BuildConfig.VERSION_NAME

        txtName.setText(prefs.getString("username", ""))
        txtName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                edit.putString("username", txtName.text.toString().trim()).apply()
            }
        })

        txtClasses.setText(prefs.getString("classes", ""))
        txtClasses.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                edit.putString("classes", txtClasses.text.toString()).apply()
            }
        })

        txtCourses.setText(prefs.getString("courses", ""))
        txtCourses.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                edit.putString("courses", txtCourses.text.toString()).apply()
            }
        })

        btnVersion.setOnLongClickListener {
            makeToast(getString(R.string.madeBy))
            debugMenu()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.chipHelpCourses -> createDialog(getString(R.string.enterCoursesHelpTitle), getString(
                R.string.enterCoursesHelp
            ))
            R.id.chipHelpClasses -> createDialog(getString(R.string.enterGradeHelpTitle), getString(
                R.string.enterGradeHelp
            ))
            R.id.btnCustomiseColours -> createColourDialog()
            R.id.btnNoNotif -> createDialog(mContext.getString(R.string.notReceivingNotifications1), mContext.getString(R.string.notReceivingNotificationsHelp))
            R.id.btnCustomiseRingtone -> createRingtoneDialog()
            R.id.btnWebsite -> {
                try {
                    customTabsIntent.launchUrl(mContext, Uri.parse("http://307.joomla.schule.bremen.de"))
                } catch (e: ActivityNotFoundException) {
                    makeToast(getString(R.string.chromeCompatibleNotFound))
                }
            }
            R.id.btnLicences -> {
                createDialog("Licences", "Libraries:\n • jsoup HTML parser © 2009-2018 Jonathan Hedley, licensed under the open source MIT Licence" +
                        "\n\nFont:\n • Manrope © 2018-2019 Michael Sharanda, licensed under the SIL Open Font Licence 1.1" +
                        "\n\nIcons:\n • bqlqn\n • fjstudio\n • Freepik\n • Smashicons\n • © 2013-2019 Freepik Company S.L., licensed under Creative Commons BY 3.0" +
                        "\n\nAdditional help:\n • Leon Becker\n • Alex Lick\n • Batuhan Özcan\n • Erich Kerkesner")
            }
            R.id.btnTerms -> {
                createDialog("Terms & Conditions", "These terms automatically apply to anyone using this app - therefore, please make sure to read them carefully before using the app. Copying or modifying parts of the app or the entire app, as well as creating derivative versions, is prohibited without permission. This app is intellectual property of the developer.\n" +
                        "\nThe developer reserves the right to make changes to the app at any given time and for any reason. The user will be informed of any changes made accordingly.\n" +
                        "\nTampering with the end device, in the form of rooting or otherwise modifying the operating system may result in malfunction of the software. In this case, the developer does not provide support, as the user is responsible for keeping the device free of software that may compromise its security.\n" +
                        "\nFull functionality of the app relies on a steady internet connection. The developer does not take responsibility for malfunction of these services, nor for any errors caused by the end device. The user is responsible for ensuring that their device is fully updated and fully functioning.\n" +
                        "\nAs the app relies on third-parties, information provided in-app may be delayed or incorrect at times. The developer accepts no liability for any information mistakenly provided in the app.\n" +
                        "\nThe developer may wish to update the app at any point. Should such action arise, then the user is advised to update the app to ensure proper functionality. However, the developer does not promise that the app will be continuously updated, and may stop providing the app at any point. Unless told otherwise, upon any termination, (a) the rights and licenses granted to the user in these terms will end; (b) the user must stop using the app, and (if needed) delete it from their device.\n" +
                        "\nChanges to these Terms & Conditions\n" +
                        "\nThe developer may update these Terms & Conditions from time to time. Thus, the user is advised to review this page periodically for any changes. Any changes are effective immediately after they are posted on this page.\n" +
                        "\nContact Us\n" +
                        "\nIn case of questions or suggestions regarding these Terms & Conditions, the user is advised to contact the developer of this app. Contact information is provided through the Play Store.\n" +
                        "\n---\n" +
                        "\nThese Terms & Conditions were modified upon the template provided by App Privacy Policy Generator. This service is in no way affiliated with AvH Plan or the developer.")
            }
            R.id.btnPrivacyP -> {
                createDialog("Privacy Policy", "The app \"Alexander-von-Humboldt-Plan\", hereby abbreviated to AvH Plan, is provided as a free service by the developer for use as is.\n" +
                        "\nThis page is used to inform visitors and users regarding policies with the collection, use, and disclosure of personal information, if they choose to make use of the service.\n" +
                        "\nAvH Plan does not collect, store, share, or in any other way use personal information retrieved from its users. Any information asked for within the app is only used to improve the user experience and as such, is stored locally and cannot be accessed by third-parties or be used to identify the user.\n" +
                        "\nInformation Collection and Use\n" +
                        "\nAt this moment, AvH Plan does not require personally identifiable information for its core functionality. Certain features do require entering information that may be personally identifiable, however, this data will be retained on the user's device and is therefore not accessible outside the app\n" +
                        "\nLog Data\n" +
                        "\nIn the case of an error in the app, device-specific data may be collected and stored on your phone. This data is called Log Data. This Log Data may include information, such as your device's Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilising the service, the time and date when the last usage occured, and other statistics. This data can not be used to personally identify any user.\n" +
                        "\nCookies\n" +
                        "\nCookies are files with small amounts of data that are commonly used as anonymous unique identifiers on the web. AvH Plan strives to not make use of cookies for its functionality, however, as its functionality requires access to third-party-websites, cookies may be unintentionally collected by the device's web browser. AvH Plan does not make use of these cookies in any way.\n" +
                        "\nService Providers\n" +
                        "\nNo third-parties are employed for providing any functionality or services of the AvH Plan-app and as such, all functionality is accessible without directly sharing personal data with third-parties.\n" +
                        "\nSecurity\n" +
                        "\nWhile absolute security cannot be ensured due to the internet functionality the app employs, no information is shared over any transmission methods.\n" +
                        "\nLinks to Other Sites\n" +
                        "\nThis Service may contain links to other sites, which are not operated by the developer of AvH Plan. The user is advised to review the privacy policy of any webpage they may access through the app. No responsibility for the content of these third-party websites is provided by the app developer.\n" +
                        "\nChildren’s Privacy\n" +
                        "\nThe service does not address anyone under the age of 13. As the app does not collect any information from its users, no data is collected from users that may be under the age of 13.\n" +
                        "\nChanges to This Privacy Policy\n" +
                        "\nThis privacy policy may be updated as the AvH Plan app is updated with new features. As such, the user is advised to review this page periodically for any changes. Any changes that may be made are effective immediately upon publishing.\n" +
                        "\nContact Us\n" +
                        "\nIn case of questions or suggestions regarding this privacy policy, the user is advised to contact the developer of this app. Contact information is provided through the Play Store.\n" +
                        "\n---\n" +
                        "\nThis privacy policy was modified upon the template provided by privacypolicytemplate.net and App Privacy Policy Generator. These services are in no way affiliated with AvH Plan or the developer.")
            }
            R.id.btnVersion -> {
                if (cs > 0) {
                    cs--
                } else {
                    try {
                        cs = 7
                        customTabsIntent.launchUrl(mContext, Uri.parse("http://www.flussufer.de/gerd/person.htm"))
                        makeToast(getString(R.string.dontTellHim))
                    } catch (e: ActivityNotFoundException) {
                        makeToast(getString(R.string.chromeCompatibleNotFound))
                    }
                }
            }
            R.id.btnForceRefresh -> {
                DataFetcher(
                    isPlan = true,
                    isMenu = true,
                    isJobService = false,
                    context = mContext,
                    application = activity!!.application,
                    parentView = view!!.rootView,
                    forced = true
                ).execute()
            }
        }
        edit.apply()
    }

    override fun onCheckedChanged(v: CompoundButton?, isChecked: Boolean) {
        if (v?.isPressed == true) {
            when (v.id) {
                R.id.switchDisableGreeting -> {
                    edit.putBoolean("greeting", isChecked)
                }
                R.id.switchNotifications -> {
                    edit.putBoolean("notif", isChecked)
                    if (isChecked) {
                        subToTopic("android")
                    } else {
                        unsubFromTopic("android")
                    }
                }
                R.id.switchDefaultPlan -> {
                    edit.putBoolean("defaultPersonalised", isChecked)
                }
                R.id.switchAutoRefresh -> {
                    edit.putBoolean("autoRefresh", isChecked)
                }
            }
            edit.apply()
        }
    }

    private fun createDialog(title: String, text: String) {
        val alertDialog = AlertDialog.Builder(mContext,
            R.style.AlertDialog
        )
        val dialogView = LayoutInflater.from(mContext).inflate(R.layout.simple_dialog, null)
        dialogView.findViewById<TextView>(R.id.textviewtitle).text = title
        dialogView.findViewById<TextView>(R.id.dialogtext).text = text
        alertDialog.setView(dialogView).show()
    }

    private fun createColourDialog() {
        val colourCustomiserBuilder = AlertDialog.Builder(mContext, R.style.AlertDialog)

        val dialogView = LayoutInflater.from(mContext).inflate(R.layout.recycler_dialog, null)
        val titleText = dialogView.findViewById<TextView>(R.id.empty_textviewtitle)
        titleText.text = getString(R.string.customiseColoursTitle)
        colourRecycler = dialogView.findViewById(R.id.recyclerView)
        colourRecycler.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(mContext, 1)
            adapter = ColourAdapter(getColourList(), this@SettingsFragment)
        }
        colourCustomiserBuilder.setView(dialogView)
        val colourCustomiserDialog = colourCustomiserBuilder.create()
        colourCustomiserDialog.show()
    }

    private fun getColourList(): ArrayList<Colour> {
        val colours = ArrayList<Colour>()
        val coursesNoLang = HelperFunctions.languageIndependentCourses
        val courses = arrayOf(getString(R.string.courseDeu), getString(
            R.string.courseEng
        ), getString(R.string.courseFra), getString(R.string.courseSpa), getString(
            R.string.courseLat
        ), getString(R.string.courseTue), getString(R.string.courseChi), getString(
            R.string.courseKun
        ), getString(R.string.courseMus), getString(R.string.courseDar), getString(
            R.string.courseGeg
        ), getString(R.string.courseGes), getString(R.string.coursePol), getString(
            R.string.coursePhi
        ), getString(R.string.courseRel), getString(R.string.courseMat), getString(
            R.string.courseBio
        ), getString(R.string.courseChe), getString(R.string.coursePhy), getString(
            R.string.courseInf
        ), getString(R.string.courseSpo), getString(R.string.courseGll), getString(
            R.string.courseWat
        ), getString(R.string.courseFor), getString(R.string.courseWp))
        val coursesIcons = intArrayOf(R.drawable.ic_german, R.drawable.ic_english, R.drawable.ic_french,
            R.drawable.ic_spanish, R.drawable.ic_latin, R.drawable.ic_turkish, R.drawable.ic_chinese,
            R.drawable.ic_arts, R.drawable.ic_music, R.drawable.ic_drama, R.drawable.ic_geography,
            R.drawable.ic_history, R.drawable.ic_politics, R.drawable.ic_philosophy, R.drawable.ic_religion,
            R.drawable.ic_maths, R.drawable.ic_biology, R.drawable.ic_chemistry, R.drawable.ic_physics,
            R.drawable.ic_compsci, R.drawable.ic_pe, R.drawable.ic_gll, R.drawable.ic_wat,
            R.drawable.ic_help, R.drawable.ic_pencil
        )

        for (i in coursesNoLang.indices) {
            colours.add(
                Colour(
                    courses[i],
                    coursesNoLang[i],
                    coursesIcons[i],
                    HelperFunctions.getColourForString(prefs.getString("card${coursesNoLang[i]}", "") ?: "")
                )
            )
        }
        return colours
    }

    private fun createRingtoneDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            HelperFunctions.getNotificationChannel(mContext, prefs)
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, mContext.applicationContext.packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, "general")
            }
            startActivity(intent)
        } else {
            val ringtoneCustomiserBuilder = AlertDialog.Builder(mContext, R.style.AlertDialog)

            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.recycler_dialog, null)
            val titleText = dialogView.findViewById<TextView>(R.id.empty_textviewtitle)
            titleText.text = getString(R.string.pickRingtone)

            val recycler = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
            recycler.apply {
                hasFixedSize()
                layoutManager = GridLayoutManager(mContext, 1)
            }

            ringtoneCustomiserDialog = ringtoneCustomiserBuilder.setView(dialogView).create()
            ringtoneCustomiserDialog.show()

            recycler.postDelayed({
                val ringtones = getRingtones().toList()
                recycler.adapter = RingtoneAdapter(ringtones, this)
            }, 100)
        }
    }

    private fun getRingtones(): ArrayList<Ringtone> {
        lateinit var ringtoneCursor: Cursor
        val ringtoneManager = RingtoneManager(activity).apply {
            setType(RingtoneManager.TYPE_NOTIFICATION)
            ringtoneCursor = cursor
        }
        val alarms = ArrayList<Ringtone>()

        while (!ringtoneCursor.isAfterLast && ringtoneCursor.moveToNext()) {
            val position = ringtoneCursor.position
            alarms.add(
                Ringtone(
                    ringtoneManager.getRingtone(position).getTitle(mContext),
                    ringtoneManager.getRingtoneUri(position).toString()
                )
            )
        }
        return alarms
    }

    override fun onRingtoneClick(position: Int, name: String, uri: String) {
        prefs.edit().apply {
            putString("ringtoneName", name)
            putString("ringtoneUri", uri)
        }.apply()
        setRingtoneText()
        ringtoneCustomiserDialog.dismiss()
    }

    private fun setRingtoneText() {
        currentRingtone.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.getString(R.string.deviceSettingsRedirect)
        } else {
            val tone = if ((prefs.getString("ringtoneName", "") ?: "").isNotEmpty()) {
                prefs.getString("ringtoneName", "")
            } else {
                mContext.getString(R.string.defaultRingtone)
            }
            mContext.getString(R.string.setRingtone2, tone)
        }
    }

    override fun onClick(position: Int, title: String, titleNoLang: String) {
        val colourPickerBuilder = AlertDialog.Builder(mContext, R.style.AlertDialog)
        val pickerDialogView = LayoutInflater.from(mContext).inflate(R.layout.empty_dialog, null)
        val pickerTitleText = pickerDialogView.findViewById<TextView>(R.id.empty_textviewtitle)
        val pickerLayout = pickerDialogView.findViewById<LinearLayout>(R.id.empty_linearlayout)
        pickerTitleText.text = title

        val picker = LayoutInflater.from(mContext).inflate(R.layout.bg_colour_picker, null)
        pickerLayout.addView(picker)
        colourPickerBuilder.setView(pickerDialogView)
        val colourPickerDialog: AlertDialog = colourPickerBuilder.create()

        val buttons = intArrayOf(R.id.def, R.id.red, R.id.orange, R.id.yellow, R.id.green,
            R.id.teal, R.id.cyan, R.id.blue, R.id.purple, R.id.pink, R.id.brown, R.id.grey,
            R.id.pureWhite, R.id.salmon, R.id.tangerine, R.id.banana, R.id.flora, R.id.spindrift,
            R.id.sky, R.id.orchid, R.id.lavender, R.id.carnation, R.id.brown2, R.id.pureBlack)
        val colours = HelperFunctions.colourNames

        for (i2 in buttons.indices) {
            picker.findViewById<MaterialButton>(buttons[i2]).setOnClickListener {
//                prefs.edit().putInt("bg$titleNoLang", colourIntegers[i2]).apply()
                prefs.edit().putString("card$titleNoLang", colours[i2]).apply()
                val recyclerViewState = colourRecycler.layoutManager?.onSaveInstanceState()
                colourRecycler.adapter = ColourAdapter(getColourList(), this)
                colourRecycler.layoutManager?.onRestoreInstanceState(recyclerViewState)
                colourPickerDialog.dismiss()
            }
        }
        colourPickerDialog.show()
    }

    private fun debugMenu(): Boolean {
        val alertDialog = AlertDialog.Builder(mContext,
            R.style.AlertDialog
        )
        val dialogView = LayoutInflater.from(mContext).inflate(R.layout.edittext_dialog, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.dialog_edittext)
        val dialogButton = dialogView.findViewById<Button>(R.id.dialog_button)

        dialogView.findViewById<TextView>(R.id.textviewtitle).text = getString(
            R.string.experimentalMenu
        )
        dialogView.findViewById<TextView>(R.id.dialogtext).text = getString(
            R.string.menuForTests
        )
        dialogButton.setOnClickListener {
            when (dialogEditText.text.toString()) {
                "_DIAGNOSTICS" -> {
                    val alertDialogDev = AlertDialog.Builder(mContext, R.style.AlertDialog)
                    val devDialogView = LayoutInflater.from(mContext).inflate(R.layout.diagnostics_dialog, null)
                    val devDialogText = devDialogView.findViewById<TextView>(R.id.dialogtext)
                    val resetLaunchBtn = devDialogView.findViewById<Button>(R.id.btnResetLaunch)
                    val resetNotificationBtn = devDialogView.findViewById<Button>(R.id.btnResetNotif)

                    devDialogView.findViewById<TextView>(R.id.textviewtitle).text = getString(
                        R.string.diagnosticsMenu
                    )
                    devDialogText.text = getDiagnosticsText(prefs)

                    resetLaunchBtn.setOnClickListener {
                        edit.putInt("launchDev", 0).apply()
                        devDialogText.text = getDiagnosticsText(prefs)
                    }

                    resetNotificationBtn.setOnClickListener {
                        edit.putInt("pingFB", 0).apply()
                        devDialogText.text = getDiagnosticsText(prefs)
                    }

                    alertDialogDev.setView(devDialogView)
                    alertDialogDev.show()
                }
                "2018-04-20" -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Jc2xfYuLWgE")) // Freak
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setPackage("com.google.android.youtube")
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        makeToast(getString(R.string.youtubeNotFound))
                    }
                }
                "_FIRSTTIME" -> {
                    edit.putBoolean("firstTime", true).apply()
                    makeToast("First time flag cleared")
                }
                "_TESTURLS" -> {
                    val currentTest = !prefs.getBoolean("testUrls", false)
                    edit.putBoolean("testUrls", currentTest).apply()
                    makeToast("Test URLs set to $currentTest")
                }
                "_DEVCHANNEL" -> {
                    val subbed = if (prefs.getBoolean("subscribedToFBDebugChannel", false)) {
                        unsubFromTopic("debug")
                        "Unsubscribed from"
                    } else {
                        subToTopic("debug")
                        "Subscribed to"
                    }
                    edit.putBoolean("subscribedToFBDebugChannel", !prefs.getBoolean("subscribedToFBDebugChannel", false)).apply()
                    makeToast("$subbed Firebase development channel")
                }
                "_IOSCHANNEL" -> {
                    val subbed = if (prefs.getBoolean("subscribedToiOSChannel", false)) {
                        unsubFromTopic("ios")
                        "Unsubscribed from"
                    } else {
                        subToTopic("ios")
                        "Subscribed to"
                    }
                    edit.putBoolean("subscribedToiOSChannel", !prefs.getBoolean("subscribedToiOSChannel", false)).apply()
                    makeToast("$subbed iOS channel")
                }
                "_WRITE" -> HelperFunctions.writePrefsToXml(prefs, mContext, activity!!)
                "_READ" -> HelperFunctions.readPrefsFromXml(prefs, mContext, activity!!)
                else -> makeToast(getString(R.string.invalidCode))
            }
        }
        alertDialog.setView(dialogView)
        alertDialog.show()
        return true
    }

    private fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }

    private fun getDiagnosticsText(prefs: SharedPreferences): String {
        return "First Launch: ${prefs.getString("firstTimeDev", "")}" +
                "\n\nApp launched: ${prefs.getInt("launchDev", 0)}" +
                "\n\nFirebase ping service fired: ${prefs.getInt("pingFB", 0)}" +
                "\n\nDevice Name: ${Build.DEVICE}" +
                "\n\nDevice Model: ${Build.MODEL}" +
                "\n\nAndroid Version: ${Build.VERSION.RELEASE}" +
                "\n\nSubscribed to notification channel: ${prefs.getBoolean("notif", false)}" +
                "\n\nSubscribed to dev channel: ${prefs.getBoolean("subscribedToFBDebugChannel", false)}"
    }

    private fun subToTopic(topic: String) = FirebaseMessaging.getInstance().subscribeToTopic("substitutions-$topic")
    private fun unsubFromTopic(topic: String) = FirebaseMessaging.getInstance().unsubscribeFromTopic("substitutions-$topic")
}