package com.denizd.substitutionplan.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.denizd.substitutionplan.data.SubstUtil
import com.denizd.substitutionplan.models.Substitution

internal class GeneralPlanFragment : PlanFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        substitutionPlan?.observe(this, Observer<List<Substitution>> { substitutions ->
            mAdapter.setSubst(if (substitutions.isEmpty()) {
                SubstUtil.getEmptyGeneralSubstitution(mContext)
            } else {
                substitutions
            })
            binding.recyclerView.scheduleLayoutAnimation()
        })
    }
}