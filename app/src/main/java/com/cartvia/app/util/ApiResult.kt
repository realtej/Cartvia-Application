package com.cartvia.app.util

import com.cartvia.app.data.remote.ApiErrorCode
import com.cartvia.app.data.remote.FieldError

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(
        val code: ApiErrorCode,
        val message: String?,
        val fieldErrors: List<FieldError>? = null
    ) : ApiResult<Nothing>()
    data class NetworkError(val exception: Throwable) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

fun <T> ApiResult<T>.onError(action: (ApiErrorCode, String?, List<FieldError>?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(code, message, fieldErrors)
    return this
}
