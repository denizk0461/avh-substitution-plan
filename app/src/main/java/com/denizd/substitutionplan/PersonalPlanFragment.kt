package com.denizd.substitutionplan

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer

class PersonalPlanFragment : PlanFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        smileydown = view.findViewById(R.id.smileydown)
        smileydowntext = view.findViewById(R.id.smileydowntext)
        linearsmiley = view.findViewById(R.id.linearsmiley)

        substViewModel.allSubst?.observe(this, Observer<List<Subst>> {
            planCardList.clear()
            smileydown.visibility = View.GONE
            smileydowntext.visibility = View.GONE
            linearsmiley.visibility = View.GONE
            persPlanEmpty = true
            recyclerView.visibility = View.VISIBLE
            for (i in 0 until it.size) {
                if (it[i].date.isNotEmpty() && it[i].date.substring(0, 3) == "psa") {
                    planCardList.add(it[i])
                } else if ((prefs.getString("courses", "") ?: "").isEmpty() && (prefs.getString("classes", "") ?: "").isNotEmpty()) {
                    if (it[i].group.isNotEmpty() && it[i].group != "") {
                        if ((prefs.getString("classes", "") ?: "").contains(it[i].group) || it[i].group.contains((prefs.getString("classes", "") ?: "").toString())) {
                            planCardList.add(it[i])
                            persPlanEmpty = false
                        }
                    }
                } else if ((prefs.getString("classes", "") ?: "").isNotEmpty() && (prefs.getString("courses", "") ?: "").isNotEmpty()) {
                    if (it[i].group != "" && it[i].course != "") {
                        if ((prefs.getString("courses", "") ?: "").contains(it[i].course)) {
                            if ((prefs.getString("classes", "") ?: "").contains(it[i].group) || it[i].group.contains((prefs.getString("classes", "") ?: "").toString())) {
                                planCardList.add(it[i])
                                persPlanEmpty = false
                            }
                        }
                    }
                }
            }
            planCardList.sortWith(Comparator { lhs, rhs -> Integer.compare(rhs.priority, lhs.priority) })
            recyclerView.scheduleLayoutAnimation()
            mAdapter.setSubst(planCardList)

            handler.postDelayed({
                if (persPlanEmpty) {
//                    recyclerView.visibility = View.GONE
                    smileydown.visibility = View.VISIBLE
                    smileydowntext.visibility = View.VISIBLE
                    linearsmiley.visibility = View.VISIBLE
                    linearsmiley.scheduleLayoutAnimation()
                }
            }, 64)
        })
    }

}