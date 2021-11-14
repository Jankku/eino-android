package com.jankku.eino.data.enums

enum class Sort(val value: String) {
    ASCENDING("ascending"),
    DESCENDING("descending");

    companion object {
        fun toArray(): Array<String> = values().map { it ->
            it.value.replaceFirstChar { it.uppercase() }
        }.toTypedArray()
    }
}