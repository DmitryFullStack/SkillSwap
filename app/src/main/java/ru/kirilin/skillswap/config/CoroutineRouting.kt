package ru.kirilin.skillswap.config

import kotlinx.coroutines.*

inline fun CoroutineScope.launchIO(
    crossinline action: suspend () -> Unit,
    crossinline onError: suspend (Throwable) -> Unit,
    errorDispatcher: CoroutineDispatcher = Dispatchers.Main
): Job {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        launch(errorDispatcher) {
            onError(throwable)
        }
    }
    return this.launch(coroutineExceptionHandler + Dispatchers.IO){
        action()
    }

}

suspend inline fun <T> withIO(noinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO, block)
}

suspend inline fun <T> withMain(noinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Main, block)
}