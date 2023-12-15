package com.amangarg.searchview.coroutines.flow.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun <T> CoroutineScope.debounce(
    waitPeriodInMs: Long = 300L,
    context: CoroutineContext,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = launch(context) {
            delay(waitPeriodInMs)
            destinationFunction(param)
        }
    }
}
