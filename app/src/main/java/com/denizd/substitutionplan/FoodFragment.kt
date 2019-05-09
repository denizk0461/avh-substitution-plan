package com.denizd.substitutionplan

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

    private lateinit var foodArrayList: ArrayList<Food>
    private lateinit var recyclerView: RecyclerView
    private val mAdapter = FoodAdapter(foodArrayList)

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.food_layout, null)
//    } // TODO do I need this?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pullToRefresh = getView()?.findViewById(R.id.pullToRefresh) as SwipeRefreshLayout
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity) as SharedPreferences
//        val edit = prefs.edit() as SharedPreferences.Editor
        val easyPrefs = EasyPrefrences(context!!)
        val foodListPopulation = ArrayList<String>(easyPrefs.getListString("foodListPrefs"))

        try {
            recyclerView = getView()?.findViewById(R.id.linear_food) as RecyclerView
            recyclerView.hasFixedSize()
            recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL ,false)
            recyclerView.adapter = mAdapter

            try {
                recyclerView.removeAllViews()
            } catch (e: NullPointerException) {}

            for (i in 0..foodListPopulation.size) {
                foodArrayList.add(Food(foodListPopulation[i]))
                mAdapter.setFood(foodArrayList)
                recyclerView.scheduleLayoutAnimation()
            }
        } catch (e: NullPointerException) {}

        if (prefs.getBoolean("autoRefresh", false)) {
            pullToRefresh.isRefreshing = true
            // coroutines
        }

        pullToRefresh.setOnRefreshListener {
            pullToRefresh.isRefreshing = true
            // coroutines
        }

    }
}