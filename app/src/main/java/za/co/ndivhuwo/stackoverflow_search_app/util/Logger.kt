package za.co.ndivhuwo.stackoverflow_search_app.util

import timber.log.Timber

/**
 * Structured logger for the application.
 * Follows Kotlin best practices for observability and tracing.
 */
object AppLogger {

    fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Timber.tag(tag).e(throwable, message)
    }

    fun w(tag: String, message: String) {
        Timber.tag(tag).w(message)
    }

    /**
     * Logs an API request or response for tracing.
     */
    fun trace(tag: String, event: String, details: Map<String, Any?>) {
        val structuredMessage = buildString {
            append("[TRACE] $event | ")
            details.forEach { (key, value) ->
                append("$key=$value; ")
            }
        }
        Timber.tag(tag).v(structuredMessage)
    }
}
