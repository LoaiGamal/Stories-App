package com.example.storiesapp.ui

import android.app.Application
import android.text.SpannableString
import android.text.SpannedString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storiesapp.data.Text
import org.zwobble.mammoth.DocumentConverter
import org.zwobble.mammoth.Result

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var _organizedList = MutableLiveData<List<Text>>()
    val organizedList: LiveData<List<Text>>
        get() = _organizedList

    private var isText: Boolean? = null
    private var searchedString: String? = null

    private val converter = DocumentConverter()
    private val result: Result<String>? =
        converter.extractRawText(application.assets.open("أجمل القصص القصيرة.docx"))
    private val rawText = result?.value

    // Split the document into sentences and filter it


    private fun splitDocumentIntoSentences(rawText: String?): List<String> {
        val lines = rawText!!.split("\n")
        return removeValuesViaIteration(lines.toMutableList())

    }

    private fun removeValuesViaIteration(listWithNullsAndEmpty: MutableList<String?>): List<String> {
        val iterator = listWithNullsAndEmpty.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (element.isNullOrEmpty()) {
                iterator.remove()
            }
        }
        listWithNullsAndEmpty.removeAt(0)
        return listWithNullsAndEmpty as List<String>
    }


    private fun convertStringListToTextList(list: List<String>): List<Text> {
        val newList = mutableListOf<Text>()
        val numberOfTitles = 9
        var numberOfBodies = 0
        for (i in list.indices) {
            if (list[i][0].isDigit()) {
                newList.add(Text.Title(i, list[i]))
            } else if (i % 2 == 1) {
                newList.add(Text.Headline((i - (numberOfTitles + numberOfBodies)), list[i]))
            } else {
                newList.add(Text.Body((i - (numberOfTitles + numberOfBodies + 1)), list[i]))
                numberOfBodies++
            }
        }
        return newList
    }

    fun getOrganizedList() {
        _organizedList.value = convertStringListToTextList(splitDocumentIntoSentences(rawText))
    }

    fun highlightText(index: Int, body: Text.Body) {
        _organizedList.value!![index].let {
            it as Text.Body
            it.body = body.body
            it.spannableString = body.spannableString
        }
    }

    fun makeSearch(sentence: String): Int {
        var indexOfSearchedSentence = -1

        indexOfSearchedSentence = organizedList.value!!.indexOfFirst {
            it is Text.Headline && it.headline.contains(sentence)
        }
        if (indexOfSearchedSentence > -1)
            return indexOfSearchedSentence

        indexOfSearchedSentence =
            organizedList.value!!.indexOfFirst { it is Text.Body && it.body.contains(sentence) }
        if (indexOfSearchedSentence > -1)
            return indexOfSearchedSentence

        return indexOfSearchedSentence
    }
}