package com.denizd.substitutionplan.fragments

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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.denizd.substitutionplan.*
import com.denizd.substitutionplan.adapters.CardAdapter
import com.denizd.substitutionplan.database.SubstViewModel
import com.denizd.substitutionplan.models.Subst
import com.denizd.substitutionplan.data.DataFetcher
import kotlin.collections.ArrayList

internal open class PlanFragment : Fragment(R.layout.plan) {
    lateinit var recyclerView: RecyclerView
    lateinit var mAdapter: CardAdapter
    private lateinit var layoutManager: GridLayoutManager
    var planCardList = ArrayList<Subst>()
    lateinit var substViewModel: SubstViewModel
    private lateinit var mContext: Context
    lateinit var prefs: SharedPreferences

    lateinit var smileydown: TextView
    lateinit var smileydowntext: TextView
    lateinit var linearsmiley: LinearLayout
    var persPlanEmpty: Boolean = true
    val handler = Handler()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        layoutManager = GridLayoutManager(mContext, getGridColumnCount(newConfig))
        recyclerView.layoutManager = layoutManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)

        recyclerView = view.findViewById(R.id.linearRecycler)
        recyclerView.hasFixedSize()
        layoutManager = GridLayoutManager(mContext, getGridColumnCount(resources.configuration))
        recyclerView.layoutManager = layoutManager

        mAdapter = CardAdapter(planCardList)
        recyclerView.adapter = mAdapter

        if (prefs.getInt("firstTimeOpening", 0) == 1) {
            startFetch(view, pullToRefresh, true)
            prefs.edit().putInt("firstTimeOpening", 2).apply()
        }

        if (prefs.getBoolean("autoRefresh", false)) {
            startFetch(view, pullToRefresh, false)
        }
        substViewModel = ViewModelProviders.of(this).get(SubstViewModel::class.java)

        pullToRefresh.setOnRefreshListener {
            startFetch(view, pullToRefresh, false)
        }
    }

    private fun startFetch(view: View, pullToRefresh: SwipeRefreshLayout, refreshMenu: Boolean) {
        pullToRefresh.isRefreshing = true
        DataFetcher(
            isPlan = true,
            isMenu = refreshMenu,
            isJobService = false,
            context = mContext,
            application = activity!!.application,
            parentView = view.rootView
        ).execute()
    }

    private fun getGridColumnCount(config: Configuration): Int {
        val tabletSize = resources.getBoolean(R.bool.isTablet)
        return if (tabletSize) {
            when (config.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 2
                else -> 3
            }
        } else {
            when (config.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 1
                else -> 2
            }
        }
    }
}