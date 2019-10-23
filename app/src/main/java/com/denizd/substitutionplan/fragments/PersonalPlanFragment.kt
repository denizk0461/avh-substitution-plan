package com.denizd.substitutionplan.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.denizd.substitutionplan.models.Substitution

internal class PersonalPlanFragment : PlanFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coursePreference = viewModel.getNonNullString("courses")
        val classPreference = viewModel.getNonNullString("classes")

        substitutionPlan?.observe(this, Observer<List<Substitution>> { substitutions ->
            val list = ArrayList<Substitution>()

            substitutions.filter { substItem ->
                viewModel.checkIfSubstitutionPersonal(
                    substItem,
                    classPreference,
                    coursePreference,
                    true
                )
            }.forEach { substItem ->
                list.add(substItem)
            }
            val isPersonalPlanEmpty = (list.size == 1 && list[0].date.substring(0, 3) == "psa") || list.isEmpty()
            if (isPersonalPlanEmpty) {
                list.add(viewModel.emptyPersonalSubstitution)
            }
            binding.recyclerView.scheduleLayoutAnimation()
            mAdapter.setSubst(list)
        })
    }
}