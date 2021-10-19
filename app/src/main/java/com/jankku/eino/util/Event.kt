package com.jankku.eino.util

sealed class Event {
    data class AddBookSuccessEvent(val message: String) : Event()
    data class AddBookErrorEvent(val message: String) : Event()
}
