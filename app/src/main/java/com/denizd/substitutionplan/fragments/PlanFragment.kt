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
import com.denizd.substitutionplan.adapters.SubstitutionAdapter
import com.denizd.substitutionplan.database.SubstViewModel
import com.denizd.substitutionplan.models.Substitution
import kotlin.collections.ArrayList

internal open class PlanFragment : Fragment(R.layout.plan) {
    lateinit var recyclerView: RecyclerView
    lateinit var mAdapter: SubstitutionAdapter
    var planCardList = ArrayList<Substitution>()
    lateinit var substViewModel: SubstViewModel
    private lateinit var mContext: Context
    lateinit var prefs: SharedPreferences

    lateinit var personalPlanEmptyEmoticon: TextView
    lateinit var personalPlanEmptyText: TextView
    lateinit var personalPlanEmptyLayout: LinearLayout
    var isPersonalPlanEmpty: Boolean = true
    val handler = Handler()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recyclerView.layoutManager = GridLayoutManager(mContext, getGridColumnCount(newConfig))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        substViewModel = ViewModelProviders.of(this).get(SubstViewModel::class.java)

        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        recyclerView = view.findViewById(R.id.linearRecycler)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = GridLayoutManager(mContext, getGridColumnCount(resources.configuration))
        mAdapter = SubstitutionAdapter(planCardList, prefs)
        recyclerView.adapter = mAdapter

        if (prefs.getInt("firstTimeOpening", 0) == 1) {
            substViewModel.refresh(swipeRefreshLayout = pullToRefresh, rootView = view.rootView, refreshMenu = true)
            prefs.edit().putInt("firstTimeOpening", 2).apply()
        }

        if (prefs.getBoolean("autoRefresh", false)) {
            substViewModel.refresh(swipeRefreshLayout = pullToRefresh, rootView = view.rootView, refreshMenu = false)
        }

        pullToRefresh.setOnRefreshListener {
            substViewModel.refresh(swipeRefreshLayout = pullToRefresh, rootView = view.rootView, refreshMenu = false)
        }
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