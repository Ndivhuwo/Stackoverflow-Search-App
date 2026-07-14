package za.co.ndivhuwo.stackoverflow_search_app.domain

import java.io.IOException
import retrofit2.HttpException

sealed class AppError : Throwable() {
    data object Network : AppError()
    data object Server : AppError()
    data class Api(override val message: String) : AppError()
    data class Unknown(val throwable: Throwable) : AppError()

    fun getDisplayMessage(): String {
        return when (this) {
            is Network -> "No internet connection. Please check your network."
            is Server -> "Server is currently unavailable. Please try again later."
            is Api -> message
            is Unknown -> "An unexpected error occurred."
        }
    }

    companion object {
        fun fromThrowable(throwable: Throwable): AppError {
            return when (throwable) {
                is IOException -> Network
                is HttpException -> {
                    when (throwable.code()) {
                        in 500..599 -> Server
                        else -> Api(throwable.message() ?: "API Error")
                    }
                }
                is AppError -> throwable
                else -> Unknown(throwable)
            }
        }
    }
}
