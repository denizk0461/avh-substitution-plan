package com.denizd.substitutionplan

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.madapps.prefrences.EasyPrefrences
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.lang.IndexOutOfBoundsException

class FoodFragment : Fragment(R.layout.food_layout) {

    private val foodArrayList = ArrayList<Food>()
    private val mAdapter = FoodAdapter(foodArrayList)
    private lateinit var mContext: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor
    private lateinit var easyPrefs: EasyPrefrences
    lateinit var foodListPopulation: ArrayList<String>
    lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        edit = prefs.edit()
        easyPrefs = EasyPrefrences(mContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        foodListPopulation = ArrayList(easyPrefs.getListString("foodListPrefs"))

        try {
            recyclerView = view.findViewById(R.id.linear_food)
            recyclerView.hasFixedSize()
            recyclerView.layoutManager = GridLayoutManager(mContext, 1) // , RecyclerView.VERTICAL,false
            recyclerView.adapter = mAdapter

            try {
                recyclerView.removeAllViews()
            } catch (ignored: NullPointerException) {}

            for (i in 0 until foodListPopulation.size) {
                foodArrayList.add(Food(foodListPopulation[i]))
                mAdapter.setFood(foodArrayList)
                recyclerView.scheduleLayoutAnimation()
            }
        } catch (ignored: NullPointerException) {}

        if (prefs.getBoolean("autoRefresh", false)) {
            pullToRefresh.isRefreshing = true
            RepopulateFood(mContext, recyclerView, easyPrefs, mAdapter, foodArrayList, pullToRefresh, view.rootView).execute()
        }

        pullToRefresh.setOnRefreshListener {
            pullToRefresh.isRefreshing = true
            RepopulateFood(mContext, recyclerView, easyPrefs, mAdapter, foodArrayList, pullToRefresh, view.rootView).execute()
        }
        /* bad approach - I should be using the DataFetcher.kt class, but that requires figuring out how to access the RecyclerView and its adapter from
        * another class - the best approach would be to store the data in the database instead of using EasyPreferences, however, I'm not doing that now */
    }

    class RepopulateFood(context: Context, recyclerView: RecyclerView, easyPrefs: EasyPrefrences,
                         adapter: FoodAdapter, foodArrayList: ArrayList<Food>,
                         swipeRefreshLayout: SwipeRefreshLayout, rootView: View) : AsyncTask<Void, Void, Void>() {

        private val mContext = context
        private val mRecyclerView = recyclerView
        private val mEasyPrefs = easyPrefs
        private val mAdapter = adapter
        private val mFoodArrayList = foodArrayList
        private var foodList = ArrayList<String>()
        private lateinit var foodElements: Elements
        private lateinit var docFood: Document
        private val pullToRefresh = swipeRefreshLayout
        private lateinit var progressBar: ProgressBar
        val mRootView = rootView
        private val fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        private val handler = Handler(Looper.getMainLooper())
        private var exceptionOccurred = false

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                docFood = Jsoup.connect("https://djd4rkn355.github.io/food.html").get()
                foodElements = docFood.select("th")

                progressBar = mRootView.findViewById(R.id.progressBar)

                progressBar.max = foodElements.size

                var foodInt = 0
                while (foodInt in 0 until foodElements.size) {
                    try { // what a mess this is!
                        if (foodElements[foodInt].text().contains("Montag") ||
                                foodElements[foodInt].text().contains("Dienstag") ||
                                foodElements[foodInt].text().contains("Mittwoch") ||
                                foodElements[foodInt].text().contains("Donnerstag") ||
                                foodElements[foodInt].text().contains("Freitag")) {

                            if (foodElements[foodInt + 3].text().contains("Montag") ||
                                    foodElements[foodInt + 3].text().contains("Dienstag") ||
                                    foodElements[foodInt + 3].text().contains("Mittwoch") ||
                                    foodElements[foodInt + 3].text().contains("Donnerstag") ||
                                    foodElements[foodInt + 3].text().contains("Freitag") ||
                                    foodElements[foodInt + 3].text().contains("von")) {
                                foodList.add(foodElements[foodInt].text() + "\n"
                                        + foodElements[foodInt + 1].text() + "\n"
                                        + foodElements[foodInt + 2].text())
                                foodInt += 2
                            } else if (foodElements[foodInt + 2].text().contains("Montag") ||
                                    foodElements[foodInt + 2].text().contains("Dienstag") ||
                                    foodElements[foodInt + 2].text().contains("Mittwoch") ||
                                    foodElements[foodInt + 2].text().contains("Donnerstag") ||
                                    foodElements[foodInt + 2].text().contains("Freitag") ||
                                    foodElements[foodInt + 2].text().contains("von")) {
                                foodList.add(foodElements[foodInt].text() + "\n"
                                        + foodElements[foodInt + 1].text())
                                foodInt += 1
                            }

                        } else {
                            foodList.add(foodElements[foodInt].text())
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        try {
                            foodList.add(foodElements[foodInt].text() + "\n"
                                    + foodElements[foodInt + 1].text() + "\n"
                                    + foodElements[foodInt + 2].text())
                            break
                        } catch (e1: IndexOutOfBoundsException) {
                            foodList.add(foodElements[foodInt].text() + "\n"
                                    + foodElements[foodInt + 1].text())
                            break
                        }
                    }
                    foodInt++
                    progressBar.progress = foodInt
                }

                progressBar.progress = 100

                mEasyPrefs.putListString("foodListPrefs", foodList)
            } catch (e: Exception) { exceptionOccurred = true }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            if (!exceptionOccurred) {
                try {
                    mRecyclerView.removeAllViews()
                    mFoodArrayList.removeAll(mFoodArrayList)
                } catch (ignored: NullPointerException) {}

                val foodListPopulation = ArrayList(mEasyPrefs.getListString("foodListPrefs"))

                for (i in 0 until foodListPopulation.size) {
                    mFoodArrayList.add(Food(foodListPopulation[i]))
                    mAdapter.setFood(mFoodArrayList)
                }

                mRecyclerView.scheduleLayoutAnimation()
                pullToRefresh.isRefreshing = false

                handler.postDelayed({ progressBar.startAnimation(fadeOut) }, 200)
                fadeOut.setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationStart(arg0: Animation) {}
                    override fun onAnimationRepeat(arg0: Animation) {}
                    override fun onAnimationEnd(arg0: Animation) {
                        progressBar.progress = 0
                    }
                })
            } else {
                pullToRefresh.isRefreshing = false
                val snackbarview = mRootView.findViewById<View>(R.id.coordination)
                Snackbar.make(snackbarview, mContext.getText(R.string.nointernet), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()

            }
        }
    }
}