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
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.madapps.prefrences.EasyPrefrences
import com.rd.PageIndicatorView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.lang.IndexOutOfBoundsException



class NewsFragment : Fragment(R.layout.news) {

    private lateinit var mContext: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor
    private lateinit var easyPrefs: EasyPrefrences
    private lateinit var pageIndicatorView: PageIndicatorView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        edit = prefs.edit()
        easyPrefs = EasyPrefrences(mContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // TODO go on stackoverflow and get the necessary code lol
        super.onViewCreated(view, savedInstanceState)
        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)

        val viewPager2 = view.findViewById<ViewPager2>(R.id.viewPager2)
        val list = ArrayList<News>()
        val adapter = NewsAdapter(list)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager2.adapter = adapter

        pageIndicatorView = view.findViewById(R.id.indicator)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pageIndicatorView.selection = position
            }
        })

        pageIndicatorView.count = 0
        pageIndicatorView.selection = 0

        pullToRefresh.isRefreshing = true
        FetchNews(mContext, adapter, list, pageIndicatorView, viewPager2, pullToRefresh, view.rootView).execute()

        pullToRefresh.setOnRefreshListener {
            pullToRefresh.isRefreshing = true
            FetchNews(mContext, adapter, list, pageIndicatorView, viewPager2, pullToRefresh, view.rootView).execute()
        }
    }

    class FetchNews(context: Context, adapter: NewsAdapter, arrayList: ArrayList<News>, pageIndicatorView: PageIndicatorView, viewPager2: ViewPager2,
                         swipeRefreshLayout: SwipeRefreshLayout, rootView: View) : AsyncTask<Void, Void, Void>() {

        private val mContext = context
        private val mAdapter = adapter
        private val mArrayList = arrayList
        private val mPageIndicatorView = pageIndicatorView
        private val mViewPager2 = viewPager2
        private val frameLayout = rootView.findViewById<FrameLayout>(R.id.frameLayoutNews)
        private var foodList = ArrayList<String>()
        private lateinit var foodElements: Elements
        private lateinit var doc: Document
        private val pullToRefresh = swipeRefreshLayout
        private lateinit var progressBar: ProgressBar
        val mRootView = rootView
        private val fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        private val handler = Handler(Looper.getMainLooper())
        private var exceptionOccured = false
        private var size = 0

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                doc = Jsoup.connect("http://307.joomla.schule.bremen.de/index.php/schulleben/aktuelles").maxBodySize(0).get()
                val blog = doc.getElementsByClass("blog").first()
                val a = blog.getElementsByTag("h2")

                size = a.size

                for (i in 0 until a.size) {
//                    var searching = true
                    var content = ""
                    for (e in a[i].parent().parent().getElementsByTag("p")) {
                        if (e.text().isNotEmpty()) {
                            if (content.isEmpty()) {
                                content = e.text()
                            } else {
                                content += "\n\n" + e.text()
                            }
                        }
                    }
                    mArrayList.add(News(a[i].text(), content))
                }

            } catch (e: Exception) { exceptionOccured = true }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            if (!exceptionOccured) {
                mAdapter.setNews(mArrayList)
                frameLayout.scheduleLayoutAnimation()
                mPageIndicatorView.count = size
                mPageIndicatorView.selection = 0
                mViewPager2.setCurrentItem(0, true)
                pullToRefresh.isRefreshing = false

            } else {
                pullToRefresh.isRefreshing = false
                val snackbarview = mRootView.findViewById<View>(R.id.coordination)
                Snackbar.make(snackbarview, mContext.getText(R.string.nointernet), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()

            }
        }
    }
}