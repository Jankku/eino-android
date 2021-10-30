package com.jankku.eino.util

sealed class Event {
    // Book
    data class AddBookSuccessEvent(val message: String) : Event()
    data class AddBookErrorEvent(val message: String) : Event()
    data class EditBookSuccessEvent(val message: String) : Event()
    data class EditBookErrorEvent(val message: String) : Event()
    data class DeleteBookSuccessEvent(val message: String) : Event()
    data class DeleteBookErrorEvent(val message: String) : Event()
    data class GetBookErrorEvent(val message: String) : Event()

    // Movie
    data class AddMovieSuccessEvent(val message: String) : Event()
    data class AddMovieErrorEvent(val message: String) : Event()
    data class EditMovieSuccessEvent(val message: String) : Event()
    data class EditMovieErrorEvent(val message: String) : Event()
    data class DeleteMovieSuccessEvent(val message: String) : Event()
    data class DeleteMovieErrorEvent(val message: String) : Event()
    data class GetMovieErrorEvent(val message: String) : Event()
}
