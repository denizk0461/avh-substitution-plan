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

internal class FoodFragment : Fragment(R.layout.food_layout) {

    private val foodArrayList = ArrayList<Food>()
    private val mAdapter = FoodAdapter(foodArrayList)
    private lateinit var mContext: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodViewModel: FoodViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)

        recyclerView = view.findViewById(R.id.linear_food)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = GridLayoutManager(mContext, 1)
        recyclerView.adapter = mAdapter

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
            foodViewModel.refresh(swipeRefreshLayout = pullToRefresh, rootView = view.rootView)
        }

        pullToRefresh.setOnRefreshListener {
            foodViewModel.refresh(swipeRefreshLayout = pullToRefresh, rootView = view.rootView)
        }
    }
}