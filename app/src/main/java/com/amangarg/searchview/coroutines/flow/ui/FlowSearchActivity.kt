package com.amangarg.searchview.coroutines.flow.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amangarg.searchview.coroutines.flow.databinding.ActivityFlowSearchBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.amangarg.searchview.coroutines.flow.utils.DefaultDispatcherProvider
import com.amangarg.searchview.coroutines.flow.utils.getNameList
import com.amangarg.searchview.coroutines.flow.utils.getQueryTextChangeStateFlow
import kotlin.coroutines.CoroutineContext

class FlowSearchActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityFlowSearchBinding

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowSearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        job = Job()
        setUpSearchStateFlow()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun setUpSearchStateFlow() {
        launch {
            binding.svSearchName.getQueryTextChangeStateFlow()
                .debounce(300)
                .filter { query ->
                    if (query.isEmpty()) {
                        binding.tvData.text = ""
                        return@filter false
                    } else {
                        return@filter true
                    }
                }
                .flowOn(DefaultDispatcherProvider().main)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    dataFromNetwork(query)
                        .catch {
                            emitAll(flowOf(""))
                        }
                }
                .flowOn(DefaultDispatcherProvider().default)
                .collect { result ->
                    binding.tvData.text = result
                }
        }
    }

    /**
     * Simulation of network data
     */
    private fun dataFromNetwork(query: String): Flow<String> {
        return flow {
            delay(2000)
            if (getNameList().contains(query)) {
                emit(query)
            }
            emit("Name is not available")
        }
    }

}
