package com.denizd.substitutionplan

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.*
import kotlin.collections.ArrayList

open class PlanFragment : Fragment(R.layout.plan) {
    lateinit var recyclerView: RecyclerView
    lateinit var mAdapter: CardAdapter
    private lateinit var layoutManager: GridLayoutManager
    val planCardList = ArrayList<Subst>()
    lateinit var bottomSheetText: TextView
    lateinit var substViewModel: SubstViewModel
    lateinit var mContext: Context
    lateinit var prefs: SharedPreferences
    lateinit var edit: SharedPreferences.Editor

    lateinit var smileydown: TextView
    lateinit var smileydowntext: TextView
    lateinit var linearsmiley: LinearLayout
    var persPlanEmpty: Boolean = true
    val handler = Handler()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        edit = prefs.edit()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val tabletSize = resources.getBoolean(R.bool.isTablet)
        val grid = if (tabletSize) {
            when (newConfig.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 2
                else -> 3
            }
        } else {
            when (newConfig.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 1
                else -> 2
            }
        }

        layoutManager = GridLayoutManager(mContext, grid)
        recyclerView.layoutManager = layoutManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        bottomSheetText = view.rootView.findViewById(R.id.bottom_sheet_text)
        bottomSheetText.text = prefs.getString("informational", getString(R.string.noinfo))

        recyclerView = view.findViewById(R.id.linearRecycler)
        recyclerView.hasFixedSize()

        val tabletSize = resources.getBoolean(R.bool.isTablet)
        val grid = if (tabletSize) {
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 2
                else -> 3
            }
        } else {
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 1
                else -> 2
            }
        }

        layoutManager = GridLayoutManager(mContext, grid)
        recyclerView.layoutManager = layoutManager

        mAdapter = CardAdapter(planCardList)
        recyclerView.adapter = mAdapter

        if (prefs.getInt("firstTimeOpening", 0) == 2) {
            if (!prefs.getBoolean("notif", false)) {
                pullToRefresh.isRefreshing = true
                DataFetcher(true, false, false, mContext, activity!!.application, view.rootView).execute()
                edit.putInt("firstTimeOpening", 3).apply()
            }
        }

        if (prefs.getBoolean("autoRefresh", false)) {
            pullToRefresh.isRefreshing = true
            DataFetcher(true, false, false, mContext, activity!!.application, view.rootView).execute()
            bottomSheetText.text = prefs.getString("informational", getString(R.string.noinfo))
        }
        substViewModel = ViewModelProviders.of(this).get(SubstViewModel::class.java)

        pullToRefresh.setOnRefreshListener {
            pullToRefresh.isRefreshing = true
            DataFetcher(true, false, false, mContext, activity!!.application, view.rootView).execute()
            bottomSheetText.text = prefs.getString("informational", getString(R.string.noinfo))
        }
    }
}