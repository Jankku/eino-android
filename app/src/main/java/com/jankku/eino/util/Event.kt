package com.jankku.eino.util

sealed class Event {
    data class AddBookSuccessEvent(val message: String) : Event()
    data class AddBookErrorEvent(val message: String) : Event()
    data class EditBookSuccessEvent(val message: String) : Event()
    data class EditBookErrorEvent(val message: String) : Event()
    data class DeleteBookSuccessEvent(val message: String) : Event()
    data class DeleteBookErrorEvent(val message: String) : Event()
    data class GetBookErrorEvent(val message: String) : Event()
}
