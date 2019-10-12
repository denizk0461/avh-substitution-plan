package com.denizd.substitutionplan.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.denizd.substitutionplan.adapters.FoodAdapter
import com.denizd.substitutionplan.database.FoodViewModel
import com.denizd.substitutionplan.databinding.FoodLayoutBinding
import com.denizd.substitutionplan.models.Food

internal class FoodFragment : Fragment() {

    private val foodArrayList = ArrayList<Food>()
    private val mAdapter = FoodAdapter(foodArrayList)
    private lateinit var mContext: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var foodViewModel: FoodViewModel

    private lateinit var binding: FoodLayoutBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FoodLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(mContext, 1)
            adapter = mAdapter
        }

        foodViewModel = ViewModelProviders.of(this).get(FoodViewModel::class.java)
        foodViewModel.allFoods?.observe(this, Observer<List<Food>> { foodList ->
            foodArrayList.clear()
            for (item in foodList) {
                foodArrayList.add(item)
            }
            binding.recyclerView.scheduleLayoutAnimation()
            mAdapter.setFood(foodArrayList)
        })

        if (prefs.getBoolean("autoRefresh", false)) {
            foodViewModel.refresh(swipeRefreshLayout = binding.swipeRefreshLayout, rootView = view.rootView)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            foodViewModel.refresh(swipeRefreshLayout = binding.swipeRefreshLayout, rootView = view.rootView)
        }
    }
}