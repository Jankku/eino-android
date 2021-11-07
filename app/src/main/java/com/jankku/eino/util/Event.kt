package com.jankku.eino.util

sealed class Event {
    data class GetItemError(val message: String) : Event()
    data class GetItemListError(val message: String) : Event()
    data class AddItemSuccessEvent(val message: String) : Event()
    data class AddItemErrorEvent(val message: String) : Event()
    data class EditItemSuccess(val message: String) : Event()
    data class EditItemError(val message: String) : Event()
    data class DeleteItemSuccess(val message: String) : Event()
    data class DeleteItemError(val message: String) : Event()
    data class LogoutSuccessEvent(val message: String) : Event()
    data class LogoutErrorEvent(val message: String) : Event()
}
