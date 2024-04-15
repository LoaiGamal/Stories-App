package com.example.storiesapp.ui

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.storiesapp.R
import com.example.storiesapp.data.Text
import org.zwobble.mammoth.DocumentConverter
import org.zwobble.mammoth.Result

class MainActivity : AppCompatActivity() {
    private var textSize = 0f
    private lateinit var textAdapter: TextAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val text: String = getString(R.string.most_beautiful_short_stories)
        val mSpannableString = SpannableString(text)
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

        val appTitleTxtView: TextView = findViewById((R.id.titleTxtView))
        appTitleTxtView.text = mSpannableString


        val converter = DocumentConverter()
        val result: Result<String>? =
            converter.extractRawText(assets.open("أجمل القصص القصيرة.docx"))
        val rawText = result?.value
        Log.i("Loai", rawText.toString())

        var lines = rawText!!.split("\n")
        Log.i("TAAAAAG", lines.size.toString())

        lines = removeValuesViaIteration(lines.toMutableList())
        Log.i("TAAAAAG", lines.size.toString())

        val organizedList = convertStringListToTextList(lines)
        Log.i("Converted", organizedList.size.toString())

        textAdapter = TextAdapter()
        textAdapter.submitList(organizedList)
        val textRecyclerView: RecyclerView = findViewById(R.id.textRecyclerView)
        textRecyclerView.adapter = textAdapter
        textAdapter.setOnTitleClickListener(object : TextAdapter.OnItemClickListener {
            override fun onTitleClicked(titleId: Int) {
                Log.i("Title ID", titleId.toString())
                val bodyID = organizedList.indexOfFirst { it is Text.Body && it.id == titleId }
                Log.i("Body Id", bodyID.toString())
                textRecyclerView.scrollToPosition(bodyID)
            }
        })


        val btn: Button = findViewById(R.id.btn)
//
        val appTitle: TextView = findViewById(R.id.titleTxtView)
        textSize = appTitle.textSize / 4
        Log.i("BeforeIncreasing", textSize.toString())
        btn.setOnClickListener {
            textSize += 4f
            Log.i("textSize", textSize.toString())
            appTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            Log.i("Increase", appTitle.textSize.toString())
            textAdapter.increaseTextSize()
        }
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
        var id = 0
        var titleID = 0
        var headlineID = 0
        var bodyID = 0
        list.forEach { line ->
            id++
            if (line[0].isDigit()) {
                newList.add(Text.Title(titleID, line))
                titleID++
            } else if (id % 2 != 1 && titleID != 0) {
                newList.add(Text.Headline(headlineID, line))
                headlineID++
            } else {
                newList.add(Text.Body(bodyID, line))
                bodyID++
            }
        }

        return newList
    }
}