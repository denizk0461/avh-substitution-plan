package com.denizd.substitutionplan.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.adapters.SubstitutionAdapter
import com.denizd.substitutionplan.database.SubstViewModel
import com.denizd.substitutionplan.databinding.PlanBinding
import com.denizd.substitutionplan.models.Substitution
import kotlin.collections.ArrayList

internal open class PlanFragment : Fragment() {
    internal lateinit var mAdapter: SubstitutionAdapter
    internal var planCardList = ArrayList<Substitution>()
    private lateinit var substViewModel: SubstViewModel
    private lateinit var mContext: Context
    internal lateinit var prefs: SharedPreferences

    internal var isPersonalPlanEmpty: Boolean = true
    internal val handler = Handler()
    internal var substitutionPlan: LiveData<List<Substitution>>? = null

    internal lateinit var binding: PlanBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.recyclerView.layoutManager = GridLayoutManager(mContext, getGridColumnCount(newConfig))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        substViewModel = ViewModelProviders.of(this).get(SubstViewModel::class.java)
        substitutionPlan = if (prefs.getBoolean("app_specific_sorting", true)) {
            substViewModel.allSubstitutionsSorted
        } else {
            substViewModel.allSubstitutionsOriginal
        }

        mAdapter = SubstitutionAdapter(planCardList, prefs)
        binding.recyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(mContext, getGridColumnCount(resources.configuration))
            adapter = mAdapter
        }

        if (prefs.getInt("firstTimeOpening", 0) == 1) {
            substViewModel.refresh(swipeRefreshLayout = binding.swipeRefreshLayout, rootView = view.rootView, refreshMenu = true)
            prefs.edit().putInt("firstTimeOpening", 2).apply()
        }

        if (prefs.getBoolean("autoRefresh", false)) {
            substViewModel.refresh(swipeRefreshLayout = binding.swipeRefreshLayout, rootView = view.rootView, refreshMenu = false)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            substViewModel.refresh(swipeRefreshLayout = binding.swipeRefreshLayout, rootView = view.rootView, refreshMenu = false)
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