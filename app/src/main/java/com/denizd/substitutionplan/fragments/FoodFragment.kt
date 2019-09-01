package com.denizd.substitutionplan.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.denizd.substitutionplan.*
import com.denizd.substitutionplan.adapters.FoodAdapter
import com.denizd.substitutionplan.database.FoodViewModel
import com.denizd.substitutionplan.models.Food
import com.denizd.substitutionplan.data.DataFetcher

internal class FoodFragment : Fragment(R.layout.food_layout) {

    private val foodArrayList = ArrayList<Food>()
    private val mAdapter = FoodAdapter(foodArrayList)
    private lateinit var mContext: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodViewModel: FoodViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        edit = prefs.edit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)

        try {
            recyclerView = view.findViewById(R.id.linear_food)
            recyclerView.hasFixedSize()
            recyclerView.layoutManager = GridLayoutManager(mContext, 1) // , RecyclerView.VERTICAL,false
            recyclerView.adapter = mAdapter

            try {
                recyclerView.removeAllViews()
            } catch (ignored: NullPointerException) {}

        } catch (ignored: NullPointerException) {}

        foodViewModel = ViewModelProviders.of(this).get(FoodViewModel::class.java)
        foodViewModel.allFoods?.observe(this, Observer<List<Food>> { foodList ->
            foodArrayList.clear()
            for (item in foodList) {
                foodArrayList.add(item)
            }
            recyclerView.scheduleLayoutAnimation()
            mAdapter.setFood(foodArrayList)
        })

        if (prefs.getBoolean("autoRefresh", false)) {
            pullToRefresh.isRefreshing = true
            DataFetcher(
                false,
                true,
                false,
                mContext,
                activity!!.application,
                view.rootView
            ).execute()
        }

        pullToRefresh.setOnRefreshListener {
            pullToRefresh.isRefreshing = true
            DataFetcher(
                false,
                true,
                false,
                mContext,
                activity!!.application,
                view.rootView
            ).execute()
        }
    }
}