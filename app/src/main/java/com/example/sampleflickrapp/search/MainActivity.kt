package com.example.sampleflickrapp.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.res.Configuration
import android.graphics.Color
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import com.example.sampleflickrapp.extensions.injectViewModel
import com.example.sampleflickrapp.utils.ItemDivider
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.example.sampleflickrapp.data.ERROR
import com.example.sampleflickrapp.data.LIST
import com.example.sampleflickrapp.data.NO_DATA
import com.example.sampleflickrapp.data.Status
import com.example.sampleflickrapp.search.SearchAdapter
import com.example.sampleflickrapp.search.SearchVM
import com.example.sampleflickrapp.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.layout_no_results.*


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {


    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector() = dispatchingAndroidInjector
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var searchVM: SearchVM
    val adapter = SearchAdapter()


    private var oldQuery: String = "kittens"
    private var newQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidInjection.inject(this)

        searchVM = injectViewModel(viewModelFactory)


        val config = resources.configuration
        rvPhotos.layoutManager = GridLayoutManager(this,2,getOrientation(config),false)
        val divider = resources.getDimensionPixelSize(R.dimen.divider)
        rvPhotos.addItemDecoration(ItemDivider(Color.TRANSPARENT,divider,divider))

        rvPhotos.adapter = adapter

        etSearch.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && etSearch.text.toString().isNotEmpty()) {
                newQuery = etSearch.text.toString()
                etSearch.text?.clear()

                searchPhotos(newQuery)
            }
            true
        }

        ivSearch.setOnClickListener {
            if (etSearch.text.toString().isNotEmpty()) {
                newQuery = etSearch.text.toString()
                etSearch.text?.clear()

                searchPhotos(newQuery)
            }
        }



    }

    override fun onResume() {
        super.onResume()

        if (searchVM.oldQuery.isNotEmpty())
            oldQuery = searchVM.oldQuery

        searchPhotos()
    }


    private fun searchPhotos(query: String = oldQuery) {
        dismissKeyboard()
        val data  =searchVM.search(query)



        data?.pagedList?.observe(this, Observer {

            adapter.submitList(it)

        })

        data?.networkState?.observe(this, Observer {

            when (it.status) {
                Status.RUNNING -> {
                    toggleLoading(true)
                }
                Status.SUCCESS -> {
                    toggleLoading(false)
                    show(LIST)
                    oldQuery = newQuery
                }
                Status.FAILED -> {
                    toggleLoading(false)
                    show(ERROR)
                    searchVM.oldQuery  = ""
                    oldQuery = ""
                }
                Status.NO_DATA -> {
                    toggleLoading(false)
                    if (adapter.itemCount  == 0 || oldQuery != newQuery )
                            show(NO_DATA)

                }
            }
        })


    }


    @RecyclerView.Orientation
    private fun getOrientation(config: Configuration): Int {
        return when (config.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                LinearLayoutManager.VERTICAL
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                LinearLayoutManager.HORIZONTAL
            }
            else -> {
                throw AssertionError("This should not be the case.")
            }
        }
    }

    private fun dismissKeyboard() {
        etSearch.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
    }

    private fun toggleLoading(show: Boolean) {
        ivSearch.visibility = if(show) View.GONE else View.VISIBLE
        pbLoading.visibility = if(show) View.VISIBLE else View.GONE
    }

    private fun show(what:Int = LIST) {

        when (what) {
            LIST -> {
                llNoResults.visibility = View.GONE
                rvPhotos.visibility = View.VISIBLE
            }

            ERROR -> {
                setDetails(R.drawable.ic_error,R.string.error,R.string.try_later)
                llNoResults.visibility = View.VISIBLE
                rvPhotos.visibility = View.GONE
            }

            NO_DATA -> {
                setDetails()
                llNoResults.visibility = View.VISIBLE
                rvPhotos.visibility = View.GONE
            }
        }
    }

    private fun setDetails(@DrawableRes icon:Int = R.drawable.no_results,
                           @StringRes title:Int = R.string.no_results,
                           @StringRes message: Int = R.string.try_different) {
        ivIcon.setImageDrawable(ContextCompat.getDrawable(this,icon))
        ivTitle.text = resources.getString(title)
        ivMessage.text = resources.getString(message)
    }

    fun setTestViewModel(viewModel: SearchVM) {
        searchVM = viewModel
    }

}
