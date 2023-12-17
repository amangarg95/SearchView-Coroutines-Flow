package com.amangarg.searchview.coroutines.flow.ui

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.amangarg.searchview.coroutines.flow.utils.DefaultDispatcherProvider
import com.amangarg.searchview.coroutines.flow.utils.debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import com.amangarg.searchview.coroutines.flow.databinding.ActivityCoroutineWithSearchViewBinding
import com.amangarg.searchview.coroutines.flow.utils.getNameList
import kotlinx.coroutines.SupervisorJob

class SearchViewWithCoroutineActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var searchJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + searchJob

    private lateinit var binding: ActivityCoroutineWithSearchViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoroutineWithSearchViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        searchJob = Job()

        binding.svSearchName.requestFocus()
        binding.svSearchName.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    loadNameDebounce(newText)
                    return true
                }
            }
        )
    }

    val loadNameDebounce = debounce<String>(
        waitPeriodInMs = 300L,
        context = DefaultDispatcherProvider().io
    ) {
        getMatchedName(it)
    }

    private fun getMatchedName(name: String) {
        if (name.trim().isBlank()) {
            return
        }
        launch(DefaultDispatcherProvider().io) {
            // perform API call here
            delay(1000)
            val data = getResult(name)

            launch(DefaultDispatcherProvider().main) {
                binding.tvData.text = data
            }
        }
    }

    /**
     * Simulation of network data
     */
    private fun getResult(name: String): String {
        if (getNameList().contains(name)) {
            return name
        }
        return "Name is not available"
    }

    override fun onDestroy() {
        searchJob.cancel()
        super.onDestroy()
    }
}
