package com.example.storiesapp.data

import android.text.SpannableString

sealed interface Text {
    data class Title(val id: Int, val title: String): Text
    data class Headline(var id: Int, var headline: String, var spannableString: SpannableString? = null): Text
    data class Body (var id: Int, var body: String, var spannableString: SpannableString? = null): Text
}