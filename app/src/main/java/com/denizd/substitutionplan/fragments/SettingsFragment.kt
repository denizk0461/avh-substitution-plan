package com.denizd.substitutionplan.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denizd.substitutionplan.*
import com.denizd.substitutionplan.adapters.ColourPickerAdapter
import com.denizd.substitutionplan.adapters.CourseColourAdapter
import com.denizd.substitutionplan.adapters.RingtoneAdapter
import com.denizd.substitutionplan.data.SubstUtil
import com.denizd.substitutionplan.databinding.ContentSettingsBinding
import com.denizd.substitutionplan.models.Colour
import com.denizd.substitutionplan.viewmodels.SettingsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.collections.ArrayList

internal class SettingsFragment : Fragment(), View.OnClickListener, View.OnLongClickListener,
        CompoundButton.OnCheckedChangeListener, CourseColourAdapter.OnCourseColourClickListener,
        RingtoneAdapter.OnRingtoneClickListener, ColourPickerAdapter.OnColourClickListener {

    private lateinit var mContext: Context
    private val customTabsIntent = CustomTabsIntent.Builder().build() as CustomTabsIntent
    private var customiseColoursButtonLongPressed = false
    private var versionCount = 0
    private var currentCourseSelectedInColourPicker = ""

    private lateinit var colourRecycler: RecyclerView
    private lateinit var ringtoneDialog: AlertDialog
    private lateinit var colourPickerDialog: AlertDialog

    private lateinit var binding: ContentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var snackBarContainer: CoordinatorLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ContentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this)[SettingsViewModel::class.java]
        snackBarContainer = view.rootView.findViewById(R.id.coordination) // TODO replace findViewById with ViewBinding

        binding.apply {
            val thisFragment = this@SettingsFragment

            // Set text
            setRingtoneText()

            textAppVersionDesc.text = BuildConfig.VERSION_NAME

            // Set on click listeners
            buttonCustomiseColours.setOnClickListener(thisFragment)
            buttonNotificationsHelp.setOnClickListener(thisFragment)
            buttonCustomiseRingtone.setOnClickListener(thisFragment)

            buttonGroupHelp.setOnClickListener(thisFragment)
            buttonCoursesHelp.setOnClickListener(thisFragment)
            buttonOrderHelp.setOnClickListener(thisFragment)

            buttonForcedRefresh.setOnClickListener(thisFragment)
            buttonVisitWebsite.setOnClickListener(thisFragment)
            buttonLicences.setOnClickListener(thisFragment)
            buttonVersion.setOnClickListener(thisFragment)

            // Set on long click listeners
            buttonCustomiseColours.setOnLongClickListener(thisFragment)

            buttonOrderHelp.setOnLongClickListener(thisFragment)

            buttonForcedRefresh.setOnLongClickListener(thisFragment)

            // Set on checked change listeners
            switchGreeting.setOnCheckedChangeListener(thisFragment)
            switchNotifications.setOnCheckedChangeListener(thisFragment)
            switchPersonalisedPlan.setOnCheckedChangeListener(thisFragment)
            switchAutoRefresh.setOnCheckedChangeListener(thisFragment)

            // Set checked
            switchGreeting.isChecked = viewModel.shouldGreet
            switchNotifications.isChecked = viewModel.shouldReceiveNotifications

            switchPersonalisedPlan.isChecked = viewModel.shouldOpenPersonalisedPlan
            switchAutoRefresh.isChecked = viewModel.shouldAutoRefresh

            // Set edit text (+ listeners)
            edittextName.setText(viewModel.username)
            edittextName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    viewModel.username = edittextName.text.toString().trim()
                }
            })

            edittextGroup.setText(viewModel.group)
            edittextGroup.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    viewModel.group = edittextGroup.text.toString()
                }
            })

            edittextCourses.setText(viewModel.courses)
            edittextCourses.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    viewModel.courses = edittextCourses.text.toString()
                }
            })

            // Set spinners (+ listeners)
            val darkModeList = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                R.array.themes_pre_q
            } else {
                R.array.themes
            }
            val darkModeAdapter = ArrayAdapter.createFromResource(
                    mContext,
                    darkModeList,
                    R.layout.dropdown_item
            )
            autocompleteDarkMode.setAdapter(darkModeAdapter)
            autocompleteDarkMode.setText(autocompleteDarkMode.adapter.getItem(viewModel.currentTheme).toString(), false)
            autocompleteDarkMode.setOnItemClickListener { _, _, position, _ ->
                viewModel.currentTheme = position
                when (position) {
                    0 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        if (Build.VERSION.SDK_INT in 23..28) {
                            @SuppressLint("InlinedApi")
                            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            activity?.window?.navigationBarColor = ContextCompat.getColor(mContext, R.color.colorBackground)
                        }
                    }
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) // only accessible on API 28+
                    else -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                            activity?.window?.navigationBarColor = ContextCompat.getColor(mContext, R.color.colorBackground)
                        }
                    }
                }
                view.rootView.findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId = if (viewModel.shouldOpenPersonalisedPlan) {
                    R.id.personal
                } else {
                    R.id.plan
                }
            }

            val selectedOrderingOption = if (viewModel.shouldUseAppSorting) 0 else 1
            val orderingArrayAdapter = ArrayAdapter.createFromResource(
                    mContext,
                    R.array.ordering_systems,
                    R.layout.dropdown_item
            )
            autocompleteOrder.setAdapter(orderingArrayAdapter)
            autocompleteOrder.setText(autocompleteOrder.adapter.getItem(selectedOrderingOption).toString(), false)
            autocompleteOrder.setOnItemClickListener { _, _, position, _ ->
                viewModel.shouldUseAppSorting = position == 0
            }
        }
    }

    // Functions for handling touch events in this fragment
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_customise_colours -> createColourDialog()
            R.id.button_notifications_help -> createDialog(mContext.getString(R.string.no_notifications_title), mContext.getString(R.string.no_notifications_dialog_text))
            R.id.button_customise_ringtone -> createRingtoneDialog()
            R.id.button_group_help -> createDialog(getString(R.string.grade_help_dialog_title), getString(
                    R.string.grade_help_dialog_text
            ))
            R.id.button_courses_help -> createDialog(getString(R.string.courses_help_dialog_title), getString(R.string.courses_help_dialog_text))
            R.id.button_order_help -> createDialog(getString(R.string.ordering_systems_dialog_title), getString(R.string.ordering_systems_dialog_text))
            R.id.button_forced_refresh -> {
                viewModel.forceRefresh { result, error ->
                    val snackBar = Snackbar.make(snackBarContainer, result, Snackbar.LENGTH_LONG)
                    if (error) {
                        snackBar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorError))
                    }
                    snackBar.show()
                }
            }
            R.id.button_visit_website -> {
                try {
                    customTabsIntent.launchUrl(mContext, Uri.parse("http://307.joomla.schule.bremen.de"))
                } catch (e: ActivityNotFoundException) {
                    makeToast(getString(R.string.chrome_not_found))
                }
            }
            R.id.button_licences -> {
                createDialog(mContext.getString(R.string.licences_title), licences)
            }
            R.id.button_version -> {
                if (versionCount == 7) {
                    makeToast(getString(R.string.congratulations_for_nothing))
                }
                versionCount += 1
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        return when (v?.id) {
            R.id.button_customise_colours -> {
                if (!customiseColoursButtonLongPressed) {
                    binding.textCustomiseColoursTitle.text = getString(R.string.made_by_deniz)
                    binding.textCustomiseColoursDesc.text = getString(R.string.thanks_for_using)
                } else {
                    binding.textCustomiseColoursTitle.text = getString(R.string.customise_colours_title)
                    binding.textCustomiseColoursDesc.text = getString(R.string.customise_colours_desc)
                }
                customiseColoursButtonLongPressed = !customiseColoursButtonLongPressed
                true
            }
            R.id.button_order_help -> {
                debugMenu()
                true
            }
            R.id.button_forced_refresh -> {
                viewModel.clearTimes()
                makeToast(mContext.getString(R.string.force_refresh_cleared_times))
                true
            }
            else -> false
        }
    }

    override fun onCheckedChanged(v: CompoundButton?, isChecked: Boolean) {
        if (v?.isPressed == true) {
            when (v.id) {
                R.id.switch_greeting -> {
                    viewModel.shouldGreet = isChecked
                }
                R.id.switch_notifications -> {
                    viewModel.shouldReceiveNotifications = isChecked
                    if (isChecked) {
                        subscribeToTopic(SubstUtil.FB_TOPIC_ANDROID)
                    } else {
                        unsubscribeFromTopic(SubstUtil.FB_TOPIC_ANDROID)
                    }
                }
                R.id.switch_personalised_plan -> {
                    viewModel.shouldOpenPersonalisedPlan = isChecked
                }
                R.id.switch_auto_refresh -> {
                    viewModel.shouldAutoRefresh = isChecked
                }
            }
        }
    }

    // Functions for handling touch events for recycler view elements
    override fun onCourseClick(position: Int, title: String, titleNoLang: String) {
        currentCourseSelectedInColourPicker = titleNoLang

        val colourPickerBuilder = AlertDialog.Builder(mContext)
        val dialogView = View.inflate(mContext, R.layout.recycler_dialog, null)
        val titleText = dialogView.findViewById<TextView>(R.id.empty_textviewtitle)
        titleText.text = title
        val colourRecycler = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
        colourRecycler.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(mContext, 1)
            adapter = ColourPickerAdapter(SubstUtil.getColourArray(mContext).toList(), this@SettingsFragment)
        }
        colourPickerDialog = colourPickerBuilder.setView(dialogView).create()
        colourPickerDialog.show()
    }

    override fun onColourClick(position: Int, colourNoLang: String) {
        viewModel.setAndApplyString("card$currentCourseSelectedInColourPicker", colourNoLang)
        val recyclerViewState = colourRecycler.layoutManager?.onSaveInstanceState()
        colourRecycler.adapter = CourseColourAdapter(getColourList(), this)
        colourRecycler.layoutManager?.onRestoreInstanceState(recyclerViewState)
        colourPickerDialog.dismiss()
    }

    override fun onRingtoneClick(position: Int, name: String, uri: String) {
        viewModel.setAndApplyString("ringtoneName", name)
        viewModel.setAndApplyString("ringtoneUri", uri)
        setRingtoneText()
        ringtoneDialog.dismiss()
    }

    // Functions for handling dialog creation
    // TODO put this in view model layer
    // TODO possibly replace with DialogFragment
    private fun createDialog(title: String, text: String) {
        val alertDialog = AlertDialog.Builder(mContext)
        val dialogView = View.inflate(mContext, R.layout.simple_dialog, null)
        dialogView.findViewById<TextView>(R.id.textviewtitle).text = title
        dialogView.findViewById<TextView>(R.id.dialogtext).text = text
        alertDialog.setView(dialogView).show()
    }

    private fun createColourDialog() {
        val colourCustomisationDialogBuilder = AlertDialog.Builder(mContext)

        val dialogView = View.inflate(mContext, R.layout.recycler_dialog, null)
        val titleText = dialogView.findViewById<TextView>(R.id.empty_textviewtitle)
        titleText.text = getString(R.string.customise_colours_title)
        colourRecycler = dialogView.findViewById(R.id.recyclerView)
        colourRecycler.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(mContext, 1)
            adapter = CourseColourAdapter(getColourList(), this@SettingsFragment)
        }
        colourCustomisationDialogBuilder.setView(dialogView)
        val colourCustomisationDialog = colourCustomisationDialogBuilder.create()
        colourCustomisationDialog.show()
    }

    private fun createRingtoneDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            viewModel.getNotificationChannel()
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, mContext.applicationContext.packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, "general")
            }
            startActivity(intent)
        } else {
            val ringtoneCustomiserBuilder = AlertDialog.Builder(mContext)

            val dialogView = View.inflate(mContext, R.layout.recycler_dialog, null)
            val titleText = dialogView.findViewById<TextView>(R.id.empty_textviewtitle)
            titleText.text = getString(R.string.pick_ringtone_dialog_title)

            dialogView.findViewById<RecyclerView>(R.id.recyclerView).apply {
                hasFixedSize()
                layoutManager = GridLayoutManager(mContext, 1)
                if (!viewModel.ringtonesInitialised) {
                    viewModel.getRingtones {
                        adapter = RingtoneAdapter(viewModel.ringtones, this@SettingsFragment)
                    }
                }
            }

            ringtoneDialog = ringtoneCustomiserBuilder.setView(dialogView).create()
            ringtoneDialog.show()
        }
    }

    private fun debugMenu(): Boolean {
        val alertDialog = AlertDialog.Builder(mContext)
        val dialogView = View.inflate(mContext, R.layout.edittext_dialog, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.dialog_edittext)
        val dialogButton = dialogView.findViewById<MaterialButton>(R.id.dialog_button)

        dialogView.findViewById<TextView>(R.id.textviewtitle).text = getString(
                R.string.experimental_menu_dialog_title
        )
        dialogView.findViewById<TextView>(R.id.dialogtext).text = getString(
                R.string.experimental_menu_dialog_text
        )
        dialogButton.setOnClickListener {
            with (dialogEditText.text.toString()) {
                when {
                    this == "_statistics" -> {
                        val alertDialogDev = AlertDialog.Builder(mContext)
                        val devDialogView = View.inflate(mContext, R.layout.diagnostics_dialog, null)
                        val devDialogText = devDialogView.findViewById<TextView>(R.id.dialogtext)
                        val resetLaunchBtn = devDialogView.findViewById<MaterialButton>(R.id.btnResetLaunch)
                        val resetNotificationBtn = devDialogView.findViewById<MaterialButton>(R.id.btnResetNotif)

                        devDialogView.findViewById<TextView>(R.id.textviewtitle).text = getString(R.string.statistics_dialog_title)
                        devDialogText.text = getDiagnosticsText()

                        resetLaunchBtn.setOnClickListener {
                            viewModel.setAndApplyInt("launchDev", 0)
                            devDialogText.text = getDiagnosticsText()
                        }

                        resetNotificationBtn.setOnClickListener {
                            viewModel.setAndApplyInt("pingFB", 0)
                            devDialogText.text = getDiagnosticsText()
                        }

                        alertDialogDev.setView(devDialogView)
                        alertDialogDev.show()
                    }
                    this == "_login" -> {
                        viewModel.setAndApplyBool("successful_login", false)
                        makeToast("Login flag cleared")
                    }
                    this == "_firsttime" -> {
                        viewModel.setAndApplyBool("firstTime", true)
                        makeToast("First time flag cleared")
                    }
                    this == "_testurls" -> {
                        val currentTest = !viewModel.getBool("testUrls", false)
                        viewModel.setAndApplyBool("testUrls", currentTest)
                        if (viewModel.getString("custom_test_url") != "") {
                            viewModel.setAndApplyString("custom_test_url", "")
                            makeToast("Cleared custom URL")
                        }
                        makeToast("Test URLs set to $currentTest")
                    }
                    length > 4 && substring(0, 4) == "_url" -> {
                        viewModel.setAndApplyString("custom_test_url", substring(4, length))
                        makeToast("Successfully entered custom URL")
                    }
                    length == 4 && substring(0, 4) == "_url" -> {
                        viewModel.setAndApplyString("custom_test_url", "")
                        makeToast("Cleared custom URL")
                    }
                    this == "_devchannel" -> {
                        val subscribedToDevelopmentChannel = viewModel.getBool("subscribedToFBDebugChannel")
                        val subbedText = if (subscribedToDevelopmentChannel) {
                            unsubscribeFromTopic(SubstUtil.FB_TOPIC_DEVELOPMENT)
                            "Unsubscribed from"
                        } else {
                            subscribeToTopic(SubstUtil.FB_TOPIC_DEVELOPMENT)
                            "Subscribed to"
                        }
                        viewModel.setAndApplyBool("subscribedToFBDebugChannel", !subscribedToDevelopmentChannel)
                        makeToast("$subbedText Firebase development channel")
                    }
                    this == "_ioschannel" -> {
                        val subscribedToIosChannel = viewModel.getBool("subscribedToiOSChannel")
                        val subbed = if (subscribedToIosChannel) {
                            unsubscribeFromTopic(SubstUtil.FB_TOPIC_IOS)
                            "Unsubscribed from"
                        } else {
                            subscribeToTopic(SubstUtil.FB_TOPIC_IOS)
                            "Subscribed to"
                        }
                        viewModel.setAndApplyBool("subscribedToiOSChannel", !subscribedToIosChannel)
                        makeToast("$subbed iOS channel")
                    }
                    this == "_viewstacktrace" -> {
                        createDialog("Debug Stack Trace", viewModel.getString("debug_recent_exception"))
                    }
//                    this == "_write" -> SubstUtil.writePrefsToXml(prefs, mContext, requireActivity())
//                    this == "_read" -> {
//                        if (!SubstUtil.readPrefsFromXml(prefs, mContext)) {
//                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 42)
//                        } else {
//                            TODO("Figure this out or remove it outright as it is outdated")
//                        }
//                    }
                    else -> makeToast(getString(R.string.invalid_code))
                }
            }
        }
        alertDialog.setView(dialogView)
        alertDialog.show()
        return true
    }

    // Functions for retrieving data
    private fun getColourList(): ArrayList<Colour> {
        val colours = ArrayList<Colour>()
        val coursesNoLang = SubstUtil.languageIndependentCourses
        val courses = arrayOf(
                getString(R.string.course_deu),
                getString(R.string.course_eng),
                getString(R.string.course_fra),
                getString(R.string.course_spa),
                getString(R.string.course_lat),
                getString(R.string.course_tue),
                getString(R.string.course_chi),
                getString(R.string.course_kun),
                getString(R.string.course_mus),
                getString(R.string.course_dar),
                getString(R.string.course_geg),
                getString(R.string.course_ges),
                getString(R.string.course_pol),
                getString(R.string.course_phi),
                getString(R.string.course_rel),
                getString(R.string.course_mat),
                getString(R.string.course_bio),
                getString(R.string.course_che),
                getString(R.string.course_phy),
                getString(R.string.course_inf),
                getString(R.string.course_spo),
                getString(R.string.course_gll),
                getString(R.string.course_wat),
                getString(R.string.course_foer),
                getString(R.string.course_wp)
        )
        val coursesIcons = intArrayOf(
                R.drawable.ic_german,
                R.drawable.ic_english,
                R.drawable.ic_french,
                R.drawable.ic_spanish,
                R.drawable.ic_latin,
                R.drawable.ic_turkish,
                R.drawable.ic_chinese,
                R.drawable.ic_arts,
                R.drawable.ic_music,
                R.drawable.ic_drama,
                R.drawable.ic_geography,
                R.drawable.ic_history,
                R.drawable.ic_politics,
                R.drawable.ic_philosophy,
                R.drawable.ic_religion,
                R.drawable.ic_maths,
                R.drawable.ic_biology,
                R.drawable.ic_chemistry,
                R.drawable.ic_physics,
                R.drawable.ic_compsci,
                R.drawable.ic_pe,
                R.drawable.ic_gll,
                R.drawable.ic_wat,
                R.drawable.ic_help,
                R.drawable.ic_met
        )

        for (i in coursesNoLang.indices) {
            colours.add(
                Colour(
                    courses[i],
                    coursesNoLang[i],
                    coursesIcons[i],
                    SubstUtil.getColourForString(viewModel.getString("card${coursesNoLang[i]}", ""), mContext)
                )
            )
        }
        return colours
    }

    // Functions for handling other things
    private fun setRingtoneText() {
        binding.textCustomiseRingtoneDesc.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.getString(R.string.pick_ringtone_desc_o)
        } else {
            val tone = if ((viewModel.getString("ringtoneName")).isNotEmpty()) {
                viewModel.getString("ringtoneName")
            } else {
                mContext.getString(R.string.default_ringtone)
            }
            mContext.getString(R.string.pick_ringtone_desc, tone)
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }

    private fun getDiagnosticsText(): String {
        return "First Launch: ${viewModel.getString("firstTimeDev", "")}" +
                "\n\nApp launched: ${viewModel.getInt("launchDev", 0)}" +
                "\n\nFirebase ping service fired: ${viewModel.getInt("pingFB", 0)}" +
                "\n\nDevice Name: ${Build.DEVICE}" +
                "\n\nDevice Model: ${Build.MODEL}" +
                "\n\nAndroid Version: ${Build.VERSION.RELEASE}" +
                "\n\nSubscribed to notification channel: ${viewModel.getBool("notif", false)}" +
                "\n\nSubscribed to iOS channel: ${viewModel.getBool("subscribedToiOSChannel", false)}" +
                "\n\nSubscribed to dev channel: ${viewModel.getBool("subscribedToFBDebugChannel", false)}"
    }

    private fun subscribeToTopic(topic: String) = FirebaseMessaging.getInstance().subscribeToTopic(topic)
    private fun unsubscribeFromTopic(topic: String) = FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)

    // Below this point follow string literals that I didn't bother putting in /res/values/strings

    // TODO check if this is displayed properly
    private val licences = """
        Libraries:
         • jsoup HTML parser © 2009-2018 Jonathan Hedley, licensed under the open source MIT Licence
         
        Font:
         • Manrope © 2018-2019 Michael Sharanda, licensed under the SIL Open Font Licence 1.1
         
        Icons:
         • Authors: bqlqn, fjstudio, Freepik, Smashicons
         • © 2013-2019 Freepik Company S.L., licensed under Creative Commons BY 3.0
         """.trimIndent()
}