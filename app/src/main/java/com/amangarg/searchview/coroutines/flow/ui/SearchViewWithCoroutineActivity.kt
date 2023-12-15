package com.amangarg.searchview.coroutines.flow.ui

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.amangarg.searchview.coroutines.flow.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchViewWithCoroutineActivity: AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_with_search_view)
        job = Job()
        findViewById<SearchView>(R.id.sv_search).setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    loadCitiesDebounced(newText)
                    return true
                }
            }
        )
    }

    val loadCitiesDebounced = debounce<String>(400L, Dispatchers.IO) {
        getDataFromAPI(it)
    }

    fun <T> CoroutineScope.debounce(
        waitMs: Long = 300L,
        context: CoroutineContext,
        destinationFunction: (T) -> Unit
    ): (T) -> Unit {
        var debounceJob: Job? = null
        return { param: T ->
            debounceJob?.cancel()
            debounceJob = launch(context) {
                delay(waitMs)
                destinationFunction(param)
            }
        }
    }

    private fun getDataFromAPI(text: String): String {
        if (text.trim().isBlank()) {
            return ""
        }
        launch(Dispatchers.IO) {
            // perform API call here
            delay(2000)
            return text
        }
    }
}
