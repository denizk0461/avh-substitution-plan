package com.denizd.substitutionplan

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer

class GeneralPlanFragment : PlanFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        substViewModel.allSubst?.observe(this, Observer<List<Subst>> {
            mAdapter.setSubst(it)
            recyclerView.scheduleLayoutAnimation()
            bottomSheetText.text = prefs.getString("informational", getString(R.string.noinfo))
        })
    }
}