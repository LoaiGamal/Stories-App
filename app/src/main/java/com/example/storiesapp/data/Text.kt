package com.example.storiesapp.data

sealed interface Text {
    data class Title(val id: Int, val title: String): Text
    data class Headline(val id: Int, val headline: String): Text
    data class Body (val id: Int, val body: String): Text
}