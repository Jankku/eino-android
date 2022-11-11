package com.jankku.eino.util

import com.auth0.android.jwt.JWT

fun isJWTExpired(token: String) = JWT(token).isExpired(0)
fun isJWTNotExpired(token: String) = !(JWT(token).isExpired(0))
