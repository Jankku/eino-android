package com.jankku.eino.util

sealed class Event {
    data class GetItemError(val message: String) : Event()
    data class GetItemListError(val message: String) : Event()
    data class AddItemSuccess(val message: String) : Event()
    data class AddItemError(val message: String) : Event()
    data class EditItemSuccess(val message: String) : Event()
    data class EditItemError(val message: String) : Event()
    data class DeleteItemSuccess(val message: String) : Event()
    data class DeleteItemError(val message: String) : Event()
    data class LogoutSuccess(val message: String) : Event()
    data class LogoutError(val message: String) : Event()
    data class SearchError(val message: String) : Event()
    data class ProfileError(val message: String) : Event()
    data class DeleteAccountSuccess(val message: String) : Event()
    data class DeleteAccountError(val message: String) : Event()
}
