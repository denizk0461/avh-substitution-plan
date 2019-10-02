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
import com.denizd.substitutionplan.data.Topic
import com.denizd.substitutionplan.models.Colour
import com.denizd.substitutionplan.models.Ringtone
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.collections.ArrayList

internal class SettingsFragment : Fragment(R.layout.content_settings), View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, ColourAdapter.OnClickListener,
    RingtoneAdapter.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var prefs: SharedPreferences
    private val builder = CustomTabsIntent.Builder()
    private val customTabsIntent = builder.build() as CustomTabsIntent
    private var window: Window? = null
    private lateinit var colourRecycler: RecyclerView
    private lateinit var ringtoneCustomiserDialog: AlertDialog
    private lateinit var currentRingtone: TextView
    private var longPressed = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        window = activity?.window
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameEditText = view.findViewById<TextInputEditText>(R.id.txtName)
        val gradeEditText = view.findViewById<TextInputEditText>(R.id.txtClasses)
        val courseEditText = view.findViewById<TextInputEditText>(R.id.txtCourses)

        val greetingSwitch = view.findViewById<Switch>(R.id.switchDisableGreeting)
        val notificationSwitch = view.findViewById<Switch>(R.id.switchNotifications)
        val defaultPlanSwitch = view.findViewById<Switch>(R.id.switchDefaultPlan)
        val autoRefreshSwitch = view.findViewById<Switch>(R.id.switchAutoRefresh)
        val versionNumberText = view.findViewById<TextView>(R.id.txtVersionTwo)
        val helpGradeButton = view.findViewById<ImageButton>(R.id.chipHelpClasses)
        val helpCoursesButton = view.findViewById<ImageButton>(R.id.chipHelpCourses)
        val colourCustomisationButton = view.findViewById<LinearLayout>(R.id.btnCustomiseColours)
        val versionButton = view.findViewById<LinearLayout>(R.id.btnVersion)
        currentRingtone = view.findViewById(R.id.txtCustomiseRingtone2)
        setRingtoneText()
        val forceRefreshButton = view.findViewById<LinearLayout>(R.id.btnForceRefresh)

        val colourTextTitle = view.findViewById<TextView>(R.id.txtCustomiseColours1)
        val colourTextDesc = view.findViewById<TextView>(R.id.txtCustomiseColours2)

        greetingSwitch.setOnCheckedChangeListener(this)
        notificationSwitch.setOnCheckedChangeListener(this)
        defaultPlanSwitch.setOnCheckedChangeListener(this)
        autoRefreshSwitch.setOnCheckedChangeListener(this)
        helpCoursesButton.setOnClickListener(this)
        helpGradeButton.setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.chip_help_ordering).setOnClickListener(this)
        colourCustomisationButton.setOnClickListener(this)
        colourCustomisationButton.setOnLongClickListener {
            if (!longPressed) {
                colourTextTitle.text = getString(R.string.made_by_deniz)
                colourTextDesc.text = getString(R.string.thanks_for_using)
            } else {
                colourTextTitle.text = getString(R.string.customise_colours_title)
                colourTextDesc.text = getString(R.string.customise_colours_desc)
            }
            longPressed = !longPressed
            true
        }
        view.findViewById<LinearLayout>(R.id.btnNoNotif).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.btnCustomiseRingtone).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.btnWebsite).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.btnLicences).setOnClickListener(this)
        versionButton.setOnClickListener(this)
        forceRefreshButton.setOnClickListener(this)
        forceRefreshButton.setOnLongClickListener {
            prefs.edit().putString("timeNew", "").putString("newFoodTime", "").apply()
            makeToast(mContext.getString(R.string.force_refresh_cleared_times))
            true
        }

        val darkModeDropDown = view.findViewById<AutoCompleteTextView>(R.id.darkModeDropDownText)
        val darkModeList = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            R.array.themes_pre_q
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
            prefs.edit().putInt("themeInt", position).apply()

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

        val orderingDropDownText = view.findViewById<AutoCompleteTextView>(R.id.order_drop_down_text)

        val orderingArrayAdapter = ArrayAdapter.createFromResource(
            mContext,
            R.array.ordering_systems,
            R.layout.dropdown_item
        )
        orderingArrayAdapter.setDropDownViewResource(R.layout.dropdown_item)
        orderingDropDownText.setAdapter(orderingArrayAdapter)

        val selectedOrderingOption = if (prefs.getBoolean("app_specific_sorting", true)) 0 else 1
        orderingDropDownText.setText(orderingDropDownText.adapter.getItem(selectedOrderingOption).toString(), false)

        orderingDropDownText.setOnItemClickListener { _, _, position, _ ->
            prefs.edit().putBoolean("app_specific_sorting", position == 0).apply()
        }

        greetingSwitch.isChecked = prefs.getBoolean("greeting", true)

        notificationSwitch.isChecked = prefs.getBoolean("notif", false)
        defaultPlanSwitch.isChecked = prefs.getBoolean("defaultPersonalised", false)
        autoRefreshSwitch.isChecked = prefs.getBoolean("autoRefresh", false)

        versionNumberText.text = BuildConfig.VERSION_NAME

        nameEditText.setText(prefs.getString("username", ""))
        nameEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                prefs.edit().putString("username", nameEditText.text.toString().trim()).apply()
            }
        })

        gradeEditText.setText(prefs.getString("classes", ""))
        gradeEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                prefs.edit().putString("classes", gradeEditText.text.toString()).apply()
            }
        })

        courseEditText.setText(prefs.getString("courses", ""))
        courseEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                prefs.edit().putString("courses", courseEditText.text.toString()).apply()
            }
        })

        versionButton.setOnLongClickListener {
            debugMenu()
        }
    }

    private fun createDialog(title: String, text: String) {
        val alertDialog = AlertDialog.Builder(mContext,
            R.style.AlertDialog
        )
        val dialogView = View.inflate(mContext, R.layout.simple_dialog, null)
        dialogView.findViewById<TextView>(R.id.textviewtitle).text = title
        dialogView.findViewById<TextView>(R.id.dialogtext).text = text
        alertDialog.setView(dialogView).show()
    }

    private fun createColourDialog() {
        val colourCustomisationDialogBuilder = AlertDialog.Builder(mContext, R.style.AlertDialog)

        val dialogView = View.inflate(mContext, R.layout.recycler_dialog, null)
        val titleText = dialogView.findViewById<TextView>(R.id.empty_textviewtitle)
        titleText.text = getString(R.string.customise_colours_title)
        colourRecycler = dialogView.findViewById(R.id.recyclerView)
        colourRecycler.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(mContext, 1)
            adapter = ColourAdapter(getColourList(), this@SettingsFragment)
        }
        colourCustomisationDialogBuilder.setView(dialogView)
        val colourCustomisationDialog = colourCustomisationDialogBuilder.create()
        colourCustomisationDialog.show()
    }

    private fun getColourList(): ArrayList<Colour> {
        val colours = ArrayList<Colour>()
        val coursesNoLang = HelperFunctions.languageIndependentCourses
        val courses = arrayOf(getString(R.string.course_deu), getString(
            R.string.course_eng
        ), getString(R.string.course_fra), getString(R.string.course_spa), getString(
            R.string.course_lat
        ), getString(R.string.course_tue), getString(R.string.course_chi), getString(
            R.string.course_kun
        ), getString(R.string.course_mus), getString(R.string.course_dar), getString(
            R.string.course_geg
        ), getString(R.string.course_ges), getString(R.string.course_pol), getString(
            R.string.course_phi
        ), getString(R.string.course_rel), getString(R.string.course_mat), getString(
            R.string.course_bio
        ), getString(R.string.course_che), getString(R.string.course_phy), getString(
            R.string.course_inf
        ), getString(R.string.course_spo), getString(R.string.course_gll), getString(
            R.string.course_wat
        ), getString(R.string.course_foer), getString(R.string.course_wp))
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

            val dialogView = View.inflate(mContext, R.layout.recycler_dialog, null)
            val titleText = dialogView.findViewById<TextView>(R.id.empty_textviewtitle)
            titleText.text = getString(R.string.pick_ringtone_dialog_title)

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

    private fun setRingtoneText() {
        currentRingtone.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.getString(R.string.pick_ringtone_desc_o)
        } else {
            val tone = if ((prefs.getString("ringtoneName", "") ?: "").isNotEmpty()) {
                prefs.getString("ringtoneName", "")
            } else {
                mContext.getString(R.string.default_ringtone)
            }
            mContext.getString(R.string.pick_ringtone_desc, tone)
        }
    }

    private fun debugMenu(): Boolean {
        val alertDialog = AlertDialog.Builder(mContext, R.style.AlertDialog)
        val dialogView = View.inflate(mContext, R.layout.edittext_dialog, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.dialog_edittext)
        val dialogButton = dialogView.findViewById<Button>(R.id.dialog_button)

        dialogView.findViewById<TextView>(R.id.textviewtitle).text = getString(
            R.string.experimental_menu_dialog_title
        )
        dialogView.findViewById<TextView>(R.id.dialogtext).text = getString(
            R.string.experimental_menu_dialog_text
        )
        dialogButton.setOnClickListener {
            when (dialogEditText.text.toString()) {
                "_STATISTICS" -> {
                    val alertDialogDev = AlertDialog.Builder(mContext, R.style.AlertDialog)
                    val devDialogView = View.inflate(mContext, R.layout.diagnostics_dialog, null)
                    val devDialogText = devDialogView.findViewById<TextView>(R.id.dialogtext)
                    val resetLaunchBtn = devDialogView.findViewById<Button>(R.id.btnResetLaunch)
                    val resetNotificationBtn = devDialogView.findViewById<Button>(R.id.btnResetNotif)

                    devDialogView.findViewById<TextView>(R.id.textviewtitle).text = getString(R.string.statistics_dialog_title)
                    devDialogText.text = getDiagnosticsText()

                    resetLaunchBtn.setOnClickListener {
                        prefs.edit().putInt("launchDev", 0).apply()
                        devDialogText.text = getDiagnosticsText()
                    }

                    resetNotificationBtn.setOnClickListener {
                        prefs.edit().putInt("pingFB", 0).apply()
                        devDialogText.text = getDiagnosticsText()
                    }

                    alertDialogDev.setView(devDialogView)
                    alertDialogDev.show()
                }
                "2019-06-06" -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Jc2xfYuLWgE")) // 'Freak'
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setPackage("com.google.android.youtube")
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        makeToast(getString(R.string.youtube_not_found))
                    }
                }
                "_LOGIN" -> {
                    prefs.edit().putBoolean("successful_login", false).apply()
                    makeToast("Login flag cleared")
                }
                "_FIRSTTIME" -> {
                    prefs.edit().putBoolean("firstTime", true).apply()
                    makeToast("First time flag cleared")
                }
                "_TESTURLS" -> {
                    val currentTest = !prefs.getBoolean("testUrls", false)
                    prefs.edit().putBoolean("testUrls", currentTest).apply()
                    makeToast("Test URLs set to $currentTest")
                }
                "_DEVCHANNEL" -> {
                    val subbed = if (prefs.getBoolean("subscribedToFBDebugChannel", false)) {
                        unsubscribeFromTopic(Topic.DEVELOPMENT)
                        "Unsubscribed from"
                    } else {
                        subscribeToTopic(Topic.DEVELOPMENT)
                        "Subscribed to"
                    }
                    prefs.edit().putBoolean("subscribedToFBDebugChannel", !prefs.getBoolean("subscribedToFBDebugChannel", false)).apply()
                    makeToast("$subbed Firebase development channel")
                }
                "_IOSCHANNEL" -> {
                    val subbed = if (prefs.getBoolean("subscribedToiOSChannel", false)) {
                        unsubscribeFromTopic(Topic.IOS)
                        "Unsubscribed from"
                    } else {
                        subscribeToTopic(Topic.IOS)
                        "Subscribed to"
                    }
                    prefs.edit().putBoolean("subscribedToiOSChannel", !prefs.getBoolean("subscribedToiOSChannel", false)).apply()
                    makeToast("$subbed iOS channel")
                }
                "_WRITE" -> HelperFunctions.writePrefsToXml(prefs, mContext, activity!!)
                "_READ" -> HelperFunctions.readPrefsFromXml(prefs, mContext, activity!!)
                else -> makeToast(getString(R.string.invalid_code))
            }
        }
        alertDialog.setView(dialogView)
        alertDialog.show()
        return true
    }

    private fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }

    private fun getDiagnosticsText(): String {
        return "First Launch: ${prefs.getString("firstTimeDev", "")}" +
                "\n\nApp launched: ${prefs.getInt("launchDev", 0)}" +
                "\n\nFirebase ping service fired: ${prefs.getInt("pingFB", 0)}" +
                "\n\nDevice Name: ${Build.DEVICE}" +
                "\n\nDevice Model: ${Build.MODEL}" +
                "\n\nAndroid Version: ${Build.VERSION.RELEASE}" +
                "\n\nSubscribed to notification channel: ${prefs.getBoolean("notif", false)}" +
                "\n\nSubscribed to iOS channel: ${prefs.getBoolean("subscribedToiOSChannel", false)}" +
                "\n\nSubscribed to dev channel: ${prefs.getBoolean("subscribedToFBDebugChannel", false)}"
    }

    private fun subscribeToTopic(topic: Topic) = FirebaseMessaging.getInstance().subscribeToTopic(topic.tag)
    private fun unsubscribeFromTopic(topic: Topic) = FirebaseMessaging.getInstance().unsubscribeFromTopic(topic.tag)

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.chipHelpCourses -> createDialog(getString(R.string.courses_help_dialog_title), getString(R.string.courses_help_dialog_text))
            R.id.chipHelpClasses -> createDialog(getString(R.string.grade_help_dialog_title), getString(
                R.string.grade_help_dialog_text
            ))
            R.id.chip_help_ordering -> createDialog(getString(R.string.ordering_systems_dialog_title), getString(R.string.ordering_systems_dialog_text))
            R.id.btnCustomiseColours -> createColourDialog()
            R.id.btnNoNotif -> createDialog(mContext.getString(R.string.no_notifications_title), mContext.getString(R.string.no_notifications_dialog_text))
            R.id.btnCustomiseRingtone -> createRingtoneDialog()
            R.id.btnWebsite -> {
                try {
                    customTabsIntent.launchUrl(mContext, Uri.parse("http://307.joomla.schule.bremen.de"))
                } catch (e: ActivityNotFoundException) {
                    makeToast(getString(R.string.chrome_not_found))
                }
            }
            R.id.btnLicences -> {
                createDialog(mContext.getString(R.string.licences_title), licences)
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
    }

    override fun onRingtoneClick(position: Int, name: String, uri: String) {
        prefs.edit().apply {
            putString("ringtoneName", name)
            putString("ringtoneUri", uri)
        }.apply()
        setRingtoneText()
        ringtoneCustomiserDialog.dismiss()
    }

    /**
     * OnClick method for colour customisation dialog
     * @param position      the position of the clicked item
     * @param title         the title of the clicked course
     * @param titleNoLang   the language-independent string used for storing the course's colour in
     *                      Shared Preferences
     */
    override fun onClick(position: Int, title: String, titleNoLang: String) {
        val colourPickerBuilder = AlertDialog.Builder(mContext, R.style.AlertDialog)
        val pickerDialogView = View.inflate(mContext, R.layout.empty_dialog, null)
        val pickerTitleText = pickerDialogView.findViewById<TextView>(R.id.empty_textviewtitle)
        val pickerLayout = pickerDialogView.findViewById<LinearLayout>(R.id.empty_linearlayout)
        pickerTitleText.text = title

        val picker = View.inflate(mContext, R.layout.bg_colour_picker, null)
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
                prefs.edit().putString("card$titleNoLang", colours[i2]).apply()
                val recyclerViewState = colourRecycler.layoutManager?.onSaveInstanceState()
                colourRecycler.adapter = ColourAdapter(getColourList(), this)
                colourRecycler.layoutManager?.onRestoreInstanceState(recyclerViewState)
                colourPickerDialog.dismiss()
            }
        }
        colourPickerDialog.show()
    }

    override fun onCheckedChanged(v: CompoundButton?, isChecked: Boolean) {
        if (v?.isPressed == true) {
            when (v.id) {
                R.id.switchDisableGreeting -> {
                    prefs.edit().putBoolean("greeting", isChecked).apply()
                }
                R.id.switchNotifications -> {
                    prefs.edit().putBoolean("notif", isChecked).apply()
                    if (isChecked) {
                        subscribeToTopic(Topic.ANDROID)
                    } else {
                        unsubscribeFromTopic(Topic.ANDROID)
                    }
                }
                R.id.switchDefaultPlan -> {
                    prefs.edit().putBoolean("defaultPersonalised", isChecked).apply()
                }
                R.id.switchAutoRefresh -> {
                    prefs.edit().putBoolean("autoRefresh", isChecked).apply()
                }
            }
        }
    }

    /// Below this point follow string literals that I didn't bother putting in /res/values

    private val licences = "Libraries:" +
            "\n • jsoup HTML parser © 2009-2018 Jonathan Hedley, licensed under the open source MIT Licence" +
            "\n\nFont:" +
            "\n • Manrope © 2018-2019 Michael Sharanda, licensed under the SIL Open Font Licence 1.1" +
            "\n\nIcons:" +
            "\n • bqlqn\n • fjstudio\n • Freepik\n • Smashicons" +
            "\n • © 2013-2019 Freepik Company S.L., licensed under Creative Commons BY 3.0"
}