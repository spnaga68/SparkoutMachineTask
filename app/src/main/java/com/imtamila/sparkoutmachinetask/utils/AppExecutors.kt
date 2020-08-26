package com.imtamila.sparkoutmachinetask.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

open class AppExecutors(
    private val diskIO: Executor,
    private val networkIO: Executor,
    private val mainThread: Executor
) {
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppExecutors? = null

        fun getInstance(): AppExecutors {
            return instance ?: synchronized(this) {
                instance ?: AppExecutors(
                    Executors.newSingleThreadExecutor(),
                    Executors.newFixedThreadPool(3),
                    MainThreadExecutor()
                ).also { instance = it }
            }
        }
    }

    fun diskIO(): Executor {
        return diskIO
    }

    fun networkIO(): Executor {
        return networkIO
    }

    fun mainThread(): Executor {
        return mainThread
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun runOnIoThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}