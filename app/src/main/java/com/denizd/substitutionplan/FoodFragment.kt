package com.denizd.substitutionplan

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.madapps.prefrences.EasyPrefrences

class FoodFragment : Fragment(R.layout.food_layout) {

    public val foodArrayList = ArrayList<Food>()
    private val mAdapter = FoodAdapter(foodArrayList)
    private lateinit var mContext: Context
    private lateinit var prefs: SharedPreferences
//    private lateinit var edit: SharedPreferences.Editor
    private lateinit var easyPrefs: EasyPrefrences
    private lateinit var foodListPopulation: ArrayList<String>
    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
//        edit = prefs.edit()
        easyPrefs = EasyPrefrences(mContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        foodListPopulation = ArrayList(easyPrefs.getListString("foodListPrefs"))

        try {
            recyclerView = view.findViewById(R.id.linear_food)
            recyclerView.hasFixedSize()
            recyclerView.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL,false)
            recyclerView.adapter = mAdapter

            try {
                recyclerView.removeAllViews()
            } catch (ignored: NullPointerException) {}

            for (i in 0 until foodListPopulation.size) {
                foodArrayList.add(Food(foodListPopulation[i]))
                mAdapter.setFood(foodArrayList)
                recyclerView.scheduleLayoutAnimation()
            }
        } catch (ignored: NullPointerException) {}

        if (prefs.getBoolean("autoRefresh", false)) {
            pullToRefresh.isRefreshing = true
            DataFetcher(false, true, false, mContext, activity!!.application, view.rootView).execute()
        }

        pullToRefresh.setOnRefreshListener {
            pullToRefresh.isRefreshing = true
            DataFetcher(false, true, false, mContext, activity!!.application, view.rootView).execute()
        }

    }
}