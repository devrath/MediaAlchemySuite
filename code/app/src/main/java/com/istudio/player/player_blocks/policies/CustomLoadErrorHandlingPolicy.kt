package com.istudio.player.player_blocks.policies

import android.util.Log
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import androidx.media3.datasource.HttpDataSource
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.C
import com.istudio.player.application.APP_TAG
import com.istudio.player.player_blocks.callbacks.PlaybackErrorHandler

@UnstableApi
class CustomLoadErrorHandlingPolicy(
    private val errorHandler: PlaybackErrorHandler
) : DefaultLoadErrorHandlingPolicy() {


    override fun getRetryDelayMsFor(loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo): Long {
        when (val exception = loadErrorInfo.exception) {
            is HttpDataSource.InvalidResponseCodeException -> {
                val responseCode = exception.responseCode
                return when (responseCode) {
                    404 -> retryWithBackoff(loadErrorInfo)
                    in 500..599 -> retryWithBackoff(loadErrorInfo)
                    else -> triggerFallback()
                }
            }

            is HttpDataSource.HttpDataSourceException -> {
                // Underlying IOException might indicate no connectivity
                if (isNetworkUnavailable(exception.cause)) {
                    Log.e(APP_TAG, "No connectivity detected.")
                    return noNetworkConnectivity()
                }
                return retryWithBackoff(loadErrorInfo)
            }

            else -> {
                if (isNetworkUnavailable(exception)) {
                    Log.e(APP_TAG, "No internet connection.")
                    return noNetworkConnectivity()
                }
                return retryWithBackoff(loadErrorInfo)
            }
        }
    }

    override fun getMinimumLoadableRetryCount(dataType: Int): Int = 3

    private fun retryWithBackoff(loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo): Long {
        val delay = when (loadErrorInfo.errorCount) {
            0 -> 1000L
            1 -> 2000L
            2 -> 4000L
            else -> return triggerFallback()
        }
        Log.w(APP_TAG, "Retrying load. Attempt=${loadErrorInfo.errorCount + 1}, delay=${delay}ms")
        return delay
    }

    private fun noNetworkConnectivity(): Long {
        errorHandler.noConnectivity?.invoke()
        return C.TIME_UNSET
    }

    private fun triggerFallback(): Long {
        Log.e(APP_TAG, "Triggering fallback after max retries or unrecoverable error.")
        errorHandler.onMaxRetryReached?.invoke()
        return C.TIME_UNSET
    }

    private fun isNetworkUnavailable(exception: Throwable?): Boolean {
        return exception is java.net.UnknownHostException ||
                exception is java.net.SocketTimeoutException ||
                exception is java.net.ConnectException ||
                exception is java.net.NoRouteToHostException
    }
}