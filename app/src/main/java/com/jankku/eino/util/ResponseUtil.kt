package com.jankku.eino.util

import com.jankku.eino.network.response.ApiError
import com.jankku.eino.network.response.ApiErrorResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
        ApiError("unknown", "Unknown error")
    }
}

