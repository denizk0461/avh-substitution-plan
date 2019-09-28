package com.denizd.substitutionplan.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.denizd.substitutionplan.data.HelperFunctions
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.models.Substitution

internal class PersonalPlanFragment : PlanFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        personalPlanEmptyEmoticon = view.findViewById(R.id.smileydown)
        personalPlanEmptyText = view.findViewById(R.id.smileydowntext)
        personalPlanEmptyLayout = view.findViewById(R.id.linearsmiley)

        val coursePreference = prefs.getString("courses", "") ?: ""
        val classPreference = prefs.getString("classes", "") ?: ""

        substitutionPlan?.observe(this, Observer<List<Substitution>> { substitutions ->
            planCardList.clear()
            personalPlanEmptyEmoticon.visibility = View.GONE
            personalPlanEmptyText.visibility = View.GONE
            personalPlanEmptyLayout.visibility = View.GONE
            isPersonalPlanEmpty = true
            recyclerView.visibility = View.VISIBLE

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
            recyclerView.scheduleLayoutAnimation()
            mAdapter.setSubst(planCardList)

            handler.postDelayed({
                if (isPersonalPlanEmpty) {
                    personalPlanEmptyEmoticon.visibility = View.VISIBLE
                    personalPlanEmptyText.visibility = View.VISIBLE
                    personalPlanEmptyLayout.visibility = View.VISIBLE
                    personalPlanEmptyLayout.scheduleLayoutAnimation()
                }
            }, 64)
        })
    }
}