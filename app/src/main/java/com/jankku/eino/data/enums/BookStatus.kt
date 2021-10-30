package com.jankku.eino.data.enums

enum class BookStatus(val value: String) {
    ALL("all"),
    READING("reading"),
    COMPLETED("completed"),
    ON_HOLD("on-hold"),
    DROPPED("dropped"),
    PLANNED("planned");

    companion object {
        fun toArray(): Array<String> = values().map { it ->
            it.value.replaceFirstChar { it.uppercase() }
        }.toTypedArray()
    }
}