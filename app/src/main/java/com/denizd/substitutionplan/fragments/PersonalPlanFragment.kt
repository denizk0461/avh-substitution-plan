package com.denizd.substitutionplan.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.denizd.substitutionplan.data.MiscData
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.models.Subst

internal class PersonalPlanFragment : PlanFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        smileydown = view.findViewById(R.id.smileydown)
        smileydowntext = view.findViewById(R.id.smileydowntext)
        linearsmiley = view.findViewById(R.id.linearsmiley)

        val coursePreference = prefs.getString("courses", "") ?: ""
        val classPreference = prefs.getString("classes", "") ?: ""

        substViewModel.allSubst?.observe(this, Observer<List<Subst>> { substitutions ->
            planCardList.clear()
            smileydown.visibility = View.GONE
            smileydowntext.visibility = View.GONE
            linearsmiley.visibility = View.GONE
            persPlanEmpty = true
            recyclerView.visibility = View.VISIBLE

            substitutions.filter {
                MiscData.checkPersonalSubstitutions(
                    it,
                    coursePreference,
                    classPreference,
                    true
                )
            }.forEach { substItem ->
                planCardList.add(substItem)
            }
            persPlanEmpty = (planCardList.size == 1 && planCardList[0].date.substring(0, 3) == "psa") || planCardList.isEmpty()

            planCardList.sortWith(Comparator { lhs, rhs -> rhs.priority.compareTo(lhs.priority) })
            recyclerView.scheduleLayoutAnimation()
            mAdapter.setSubst(planCardList)

            handler.postDelayed({
                if (persPlanEmpty) {
                    smileydown.visibility = View.VISIBLE
                    smileydowntext.visibility = View.VISIBLE
                    linearsmiley.visibility = View.VISIBLE
                    linearsmiley.scheduleLayoutAnimation()
                }
            }, 64)
        })
    }

}