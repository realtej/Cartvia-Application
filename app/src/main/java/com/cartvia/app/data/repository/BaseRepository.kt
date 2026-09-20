package com.cartvia.app.data.repository

import com.cartvia.app.data.remote.ApiErrorCode
import com.cartvia.app.data.remote.ApiEnvelope
import com.cartvia.app.util.ApiResult
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRepository {
    protected suspend fun <T> safeApiCall(call: suspend () -> ApiEnvelope<T>): ApiResult<T> {
        return try {
            val response = call()
            if (response.success && response.data != null) {
                ApiResult.Success(response.data)
            } else {
                ApiResult.Error(
                    code = mapErrorCode(response.errorCode),
                    message = response.message,
                    fieldErrors = response.fieldErrors
                )
            }
        } catch (e: HttpException) {
            ApiResult.NetworkError(e)
        } catch (e: IOException) {
            ApiResult.NetworkError(e)
        } catch (e: Exception) {
            ApiResult.Error(ApiErrorCode.INTERNAL_ERROR, e.message)
        }
    }

    private fun mapErrorCode(code: String?): ApiErrorCode {
        return try {
            if (code == null) ApiErrorCode.UNKNOWN
            else ApiErrorCode.valueOf(code)
        } catch (e: Exception) {
            ApiErrorCode.UNKNOWN
        }
    }
}
