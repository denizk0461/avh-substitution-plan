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

class PlanFragment : Fragment(R.layout.plan) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: CardAdapter
    private lateinit var layoutManager: GridLayoutManager
    private val planCardList = ArrayList<Subst>()
    private lateinit var bottomSheetText: TextView
    private lateinit var substViewModel: SubstViewModel
    private lateinit var mContext: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor
    private var personal = false

    private lateinit var smileydown: TextView
    private lateinit var smileydowntext: TextView
    private lateinit var linearsmiley: LinearLayout
    private var persPlanEmpty: Boolean = true
    private val handler = Handler()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        personal = arguments!!.getBoolean("ispersonal")
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

        if (personal) {
            smileydown = view.findViewById(R.id.smileydown)
            smileydowntext = view.findViewById(R.id.smileydowntext)
            linearsmiley = view.findViewById(R.id.linearsmiley)
        }

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
        substViewModel.allSubst?.observe(this, Observer<List<Subst>> {
            if (personal) {
                planCardList.clear()
                smileydown.visibility = View.GONE
                smileydowntext.visibility = View.GONE
                linearsmiley.visibility = View.GONE
                persPlanEmpty = true
                recyclerView.visibility = View.VISIBLE
                for (i in 0 until it.size) {
                    if (prefs.getString("courses", "").isEmpty() && prefs.getString("classes", "").isNotEmpty()) {
                        if (it[i].group.isNotEmpty() && !it[i].group.equals("")) {
                            if (prefs.getString("classes", "").contains(it[i].group) || it[i].group.contains(prefs.getString("classes", "").toString())) {
                                planCardList.add(it[i])
                                persPlanEmpty = false
                            }
                        }
                    } else if (prefs.getString("classes", "").isNotEmpty() && prefs.getString("courses", "").isNotEmpty()) {
                        if (!it[i].group.equals("") && !it[i].course.equals("")) {
                            if (prefs.getString("courses", "").contains(it[i].course)) {
                                if (prefs.getString("classes", "").contains(it[i].group) || it[i].group.contains(prefs.getString("classes", "").toString())) {
                                    planCardList.add(it[i])
                                    persPlanEmpty = false
                                }
                            }
                        }
                    }
                    planCardList.sortWith(Comparator { lhs, rhs -> Integer.compare(rhs.priority, lhs.priority) })
                    recyclerView.scheduleLayoutAnimation()
                    mAdapter.setSubst(planCardList)

                    handler.postDelayed({
                        if (persPlanEmpty) {
                            recyclerView.visibility = View.GONE
                            smileydown.visibility = View.VISIBLE
                            smileydowntext.visibility = View.VISIBLE
                            linearsmiley.visibility = View.VISIBLE
                            linearsmiley.scheduleLayoutAnimation()
                        }
                    }, 64)
                }
            } else {
                mAdapter.setSubst(it)
                recyclerView.scheduleLayoutAnimation()
            }
            bottomSheetText.text = prefs.getString("informational", getString(R.string.noinfo))
        })

        pullToRefresh.setOnRefreshListener {
            pullToRefresh.isRefreshing = true
            DataFetcher(true, false, false, mContext, activity!!.application, view.rootView).execute()
            bottomSheetText.text = prefs.getString("informational", getString(R.string.noinfo))
        }
    }
}