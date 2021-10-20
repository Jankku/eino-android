package com.jankku.eino.ui.book

import androidx.lifecycle.ViewModel
import com.jankku.eino.data.model.Book
import com.jankku.eino.data.model.DetailItem
import com.jankku.eino.util.utcToLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "BookDetailViewModel"

@HiltViewModel
class BookDetailViewModel @Inject constructor() : ViewModel() {
    val detailItemList = mutableListOf<DetailItem>()

    fun bookToDetailItemList(book: Book) {
        with(book) {
            with(detailItemList) {
                add(DetailItem("Title", title))
                add(DetailItem("Author", author))
                add(DetailItem("Publisher", publisher))
                add(DetailItem("ISBN", isbn))
                add(DetailItem("Pages", pages.toString()))
                add(DetailItem("Year", year.toString()))
                add(DetailItem("Status", status.replaceFirstChar { it.uppercase() }))
                add(DetailItem("Score", score.toString()))
                add(DetailItem("Start date", utcToLocal(start_date)))
                add(DetailItem("End date", utcToLocal(end_date)))
            }
        }
    }
}