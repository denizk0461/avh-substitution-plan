package com.denizd.substitutionplan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.adapters.FoodAdapter
import com.denizd.substitutionplan.viewmodels.FoodViewModel
import com.denizd.substitutionplan.databinding.FoodLayoutBinding
import com.denizd.substitutionplan.models.Food
import com.google.android.material.snackbar.Snackbar

internal class FoodFragment : Fragment() {

    private val mAdapter = FoodAdapter(ArrayList())
    private lateinit var viewModel: FoodViewModel

    private lateinit var binding: FoodLayoutBinding
    private lateinit var snackBarContainer: CoordinatorLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FoodLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snackBarContainer = view.rootView.findViewById(R.id.coordination) // TODO replace findViewById with ViewBinding

        binding.recyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = mAdapter
        }

        viewModel = ViewModelProviders.of(this).get(FoodViewModel::class.java)
        viewModel.allFoodItems?.observe(this, Observer<List<Food>> { foodList ->
            binding.recyclerView.scheduleLayoutAnimation()
            mAdapter.setFood(if (foodList.isEmpty()) {
                viewModel.emptyFoodMenu
            } else {
                foodList
            })
        })

        if (viewModel.shouldAutoRefresh) {
            refreshAndDisplaySnackBar()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshAndDisplaySnackBar()
        }
    }

    private fun refreshAndDisplaySnackBar() {
        viewModel.refresh { result, error ->
            val snackBar = Snackbar.make(snackBarContainer, result, Snackbar.LENGTH_LONG)
            if (error) {
                snackBar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorError))
            }
            snackBar.show()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}