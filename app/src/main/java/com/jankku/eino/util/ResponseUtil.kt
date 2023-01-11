package com.jankku.eino.util

import com.jankku.eino.network.response.ApiError
import com.jankku.eino.network.response.ApiErrorResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

fun <T> handleResponse(response: Response<T>, moshi: Moshi): Flow<Result<T>> {
    return if (response.isSuccessful) {
        flowOf(Result.Success(response.body() as T))
    } else {
        val error = response.getError(moshi)
        flowOf(Result.Error(error.message))
    }
}

fun <T> Response<T>.getError(moshi: Moshi): ApiError {
    return try {
        val error = this.errorBody()!!.string()
        val errorList = moshi.adapter(ApiErrorResponse::class.java).fromJson(error)
        errorList!!.errors[0]
    } catch (e: Exception) {
        ApiError("Error", e.message ?: e.localizedMessage)
    }
}

fun buildErrorResponse(request: Request, exception: Exception) =
    okhttp3.Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(400)
        .body((exception.message ?: exception.localizedMessage).toResponseBody())
        .message(exception.message ?: exception.localizedMessage)
        .build()

