package com.denizd.substitutionplan.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.adapters.SubstitutionAdapter
import com.denizd.substitutionplan.viewmodels.SubstViewModel
import com.denizd.substitutionplan.databinding.PlanBinding
import com.denizd.substitutionplan.models.Substitution
import com.google.android.material.snackbar.Snackbar
import kotlin.collections.ArrayList

internal open class PlanFragment : Fragment() {

    internal val mAdapter: SubstitutionAdapter by lazy {
        SubstitutionAdapter(ArrayList(), viewModel.substitutionPlanColours)
    }
    lateinit var viewModel: SubstViewModel
    internal var substitutionPlan: LiveData<List<Substitution>>? = null
    internal lateinit var binding: PlanBinding
    private lateinit var snackBarContainer: CoordinatorLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), viewModel.getGridColumnCount(newConfig))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snackBarContainer = view.rootView.findViewById(R.id.coordination) // TODO replace findViewById with ViewBinding

        viewModel = ViewModelProviders.of(this).get(SubstViewModel::class.java)
        substitutionPlan = if (viewModel.shouldUseAppSorting) {
            viewModel.allSubstitutionsSorted
        } else {
            viewModel.allSubstitutionsOriginal
        }

        binding.recyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), viewModel.getGridColumnCount(resources.configuration))
            adapter = mAdapter
        }

        // TODO figure something out
//        if (prefs.getInt("firstTimeOpening", 0) == 1) {
//            refreshAndDisplaySnackBar()
//            prefs.edit().putInt("firstTimeOpening", 2).apply()
//        }

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
                snackBar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorError)) // TODO check if requireContext() is dangerous ;(
            }
            snackBar.show()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}