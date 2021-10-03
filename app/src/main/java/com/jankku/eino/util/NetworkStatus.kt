package com.jankku.eino.util

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}