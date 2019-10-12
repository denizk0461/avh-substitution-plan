package com.denizd.substitutionplan.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.denizd.substitutionplan.data.HelperFunctions
import com.denizd.substitutionplan.models.Substitution

internal class PersonalPlanFragment : PlanFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coursePreference = prefs.getString("courses", "") ?: ""
        val classPreference = prefs.getString("classes", "") ?: ""

        substitutionPlan?.observe(this, Observer<List<Substitution>> { substitutions ->
            planCardList.clear()
            binding.emptyPlanEmoticon.visibility = View.GONE
            binding.emptyPlanText.visibility = View.GONE
            binding.emptyPlanLayout.visibility = View.GONE
            isPersonalPlanEmpty = true
            binding.recyclerView.visibility = View.VISIBLE

            substitutions.filter { substItem ->
                HelperFunctions.checkPersonalSubstitutions(
                    substItem,
                    coursePreference,
                    classPreference,
                    true
                )
            }.forEach { substItem ->
                planCardList.add(substItem)
            }
            isPersonalPlanEmpty = (planCardList.size == 1 && planCardList[0].date.substring(0, 3) == "psa") || planCardList.isEmpty()
            binding.recyclerView.scheduleLayoutAnimation()
            mAdapter.setSubst(planCardList)

            handler.postDelayed({
                if (isPersonalPlanEmpty) {
                    binding.emptyPlanEmoticon.visibility = View.VISIBLE
                    binding.emptyPlanText.visibility = View.VISIBLE
                    binding.emptyPlanLayout.visibility = View.VISIBLE
                    binding.emptyPlanLayout.scheduleLayoutAnimation()
                }
            }, 64)
        })
    }
}