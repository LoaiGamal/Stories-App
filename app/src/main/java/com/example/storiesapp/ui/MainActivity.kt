package com.example.storiesapp.ui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storiesapp.R
import com.example.storiesapp.data.Text
import com.example.storiesapp.databinding.RgbLayoutDialogBinding
import com.example.storiesapp.databinding.SearchDialogBinding
import org.zwobble.mammoth.DocumentConverter
import org.zwobble.mammoth.Result

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    private var textSize = 0f
    private lateinit var textAdapter: TextAdapter
    private var oldBackgroundColors: List<String> = listOf("255", "255", "255", "")
    private var oldTextColors: List<String> = listOf("0", "0", "0", "")
    private var isText: Boolean? = null
    private var searchedString: String? = null

    private val rgbLayoutDialogBinding: RgbLayoutDialogBinding by lazy {
        RgbLayoutDialogBinding.inflate(layoutInflater)
    }

    private val searchLayoutDialogBinding: SearchDialogBinding by lazy {
        SearchDialogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.getOrganizedList()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set the app title
        val text: String = getString(R.string.most_beautiful_short_stories)
        val mSpannableString = SpannableString(text)
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

        val appTitleTxtView: TextView = findViewById((R.id.titleTxtView))
        appTitleTxtView.text = mSpannableString


        // Initialize recycler view adapter and set the listeners
        textAdapter = TextAdapter()
        viewModel.organizedList.observe(this) {
            textAdapter.submitList(it)
        }
        val textRecyclerView: RecyclerView = findViewById(R.id.textRecyclerView)
        textRecyclerView.adapter = textAdapter
        textRecyclerView.layoutManager = LinearLayoutManager(this)
        textAdapter.setOnTitleClickListener(object : TextAdapter.OnItemClickListener {
            override fun onTitleClicked(titleId: Int) {
                Log.i("LOGGER", "Title ID: ${titleId.toString()}")
                val bodyID =
                    viewModel.organizedList.value!!.indexOfFirst { it is Text.Body && it.id == titleId }
                Log.i("LOGGER", "Body Id: ${bodyID.toString()}")
                textRecyclerView.smoothScrollToPosition(bodyID)
            }
        })


        textAdapter.setTextHighlightListener(object : TextAdapter.TextHighlightListener {
            override fun onTextHighlighted(text: SpannableString, index: Int) {
                if (viewModel.organizedList.value!![index] is Text.Body) {
                    var temp = viewModel.organizedList.value!![index] as Text.Body
                    temp = Text.Body(index, temp.body, text)
                    Log.d(
                        "LOGGER",
                        "before add: ${viewModel.organizedList.value!![index].toString()}"
                    )
                    viewModel.highlightText(index, temp)
//                    Log.d("LOGGER", "after add: ${organizedList[index].toString()}")
                }
            }
        })


        val appTitle: TextView = findViewById(R.id.titleTxtView)
        textSize = appTitle.textSize / 4
        Log.i("LOGGER", "Before Increasing: ${textSize.toString()}")

        // Increase text size
        val increaseTextSizeBtn: ImageButton = findViewById(R.id.textSizeIncrease)
        increaseTextSizeBtn.setOnClickListener {
            textSize += 4f
            appTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            textAdapter.increaseTextSize()
        }


        // Decrease text size
        val decreaseTextSizeBtn: ImageButton = findViewById(R.id.textSizeDecrease)
        decreaseTextSizeBtn.setOnClickListener {
            textAdapter.decreaseTextSize()
            if (textSize - 4f <= 0f)
                appTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            else {
                textSize -= 4f
                appTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            }
        }

        // Set the rgb dialog for change the text and background colors
        val rgbDialog = Dialog(this).apply {
            setContentView(rgbLayoutDialogBinding.root)
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
        }
        setOnSeekbar(
            "R",
            rgbLayoutDialogBinding.redLayout.typeTxt,
            rgbLayoutDialogBinding.redLayout.seekBar,
            rgbLayoutDialogBinding.redLayout.colorValueTxt
        )
        setOnSeekbar(
            "G",
            rgbLayoutDialogBinding.greenLayout.typeTxt,
            rgbLayoutDialogBinding.greenLayout.seekBar,
            rgbLayoutDialogBinding.greenLayout.colorValueTxt
        )
        setOnSeekbar(
            "B",
            rgbLayoutDialogBinding.blueLayout.typeTxt,
            rgbLayoutDialogBinding.blueLayout.seekBar,
            rgbLayoutDialogBinding.blueLayout.colorValueTxt
        )
        rgbLayoutDialogBinding.cancelBtn.setOnClickListener {
            if (isText!!) {
                rgbLayoutDialogBinding.redLayout.seekBar.progress = oldTextColors[0].toInt()
                rgbLayoutDialogBinding.greenLayout.seekBar.progress = oldTextColors[1].toInt()
                rgbLayoutDialogBinding.blueLayout.seekBar.progress = oldTextColors[2].toInt()
            } else {
                rgbLayoutDialogBinding.redLayout.seekBar.progress = oldBackgroundColors[0].toInt()
                rgbLayoutDialogBinding.greenLayout.seekBar.progress = oldBackgroundColors[1].toInt()
                rgbLayoutDialogBinding.blueLayout.seekBar.progress = oldBackgroundColors[2].toInt()
            }
            rgbDialog.dismiss()
            Log.i("Cancel", oldBackgroundColors.toString())
        }
        rgbLayoutDialogBinding.pickBtn.setOnClickListener {
            if (!isText!!) {
                oldBackgroundColors = setRGBColor("Background")
                val screenBackground: View = findViewById(R.id.main)
                screenBackground.setBackgroundColor(Color.parseColor(oldBackgroundColors[3]))
            } else if (isText!!) {
                oldTextColors = setRGBColor("Text")
                textAdapter.pickTextColor(oldTextColors[3])
            }
            rgbDialog.dismiss()
        }

        // Change text color
        val changingTextColorBtn: Button = findViewById(R.id.changeTextColor)
        changingTextColorBtn.setOnClickListener {
            isText = true
            rgbLayoutDialogBinding.redLayout.seekBar.progress = oldTextColors[0].toInt()
            rgbLayoutDialogBinding.greenLayout.seekBar.progress = oldTextColors[1].toInt()
            rgbLayoutDialogBinding.blueLayout.seekBar.progress = oldTextColors[2].toInt()
            rgbDialog.show()
        }


        // Change background color
        val backgroundChangingColorBtn: ImageButton = findViewById(R.id.changeBackgroundColor)
        backgroundChangingColorBtn.setOnClickListener {
            isText = false
            rgbLayoutDialogBinding.redLayout.seekBar.progress = oldBackgroundColors[0].toInt()
            rgbLayoutDialogBinding.greenLayout.seekBar.progress = oldBackgroundColors[1].toInt()
            rgbLayoutDialogBinding.blueLayout.seekBar.progress = oldBackgroundColors[2].toInt()
            rgbDialog.show()
        }

        // Set the search dialog
        val searchDialog = Dialog(this).apply {
            setContentView(searchLayoutDialogBinding.root)
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
        }

        searchLayoutDialogBinding.cancelBtn.setOnClickListener {
            searchDialog.dismiss()
        }

        searchLayoutDialogBinding.pickBtn.setOnClickListener {
            searchedString = getSearchedString()
            if (searchedString == null) {
                Toast.makeText(this, "من فضلك أكتب نص تريد البحث عنه!", Toast.LENGTH_LONG).show()
            } else {
                val sentenceSearchedIndex = viewModel.makeSearch(searchedString!!)
                if (sentenceSearchedIndex == -1)
                    Toast.makeText(this, "هذه العبارة لم يتم العثور عليها", Toast.LENGTH_SHORT)
                        .show()
                else {
                    textRecyclerView.smoothScrollToPosition(sentenceSearchedIndex)
                }

                searchLayoutDialogBinding.searchEditText.setText("")
            }
            searchDialog.dismiss()
        }

        // Searching
        val searchBtn: ImageButton = findViewById(R.id.searchBtn)
        searchBtn.setOnClickListener {
            searchDialog.show()
        }

    }


    private fun setOnSeekbar(
        type: String,
        typeTxt: TextView,
        seekBar: SeekBar,
        colorTxt: TextView
    ) {
        when (type) {
            "R" -> typeTxt.setBackgroundResource(R.color.red)
            "G" -> typeTxt.setBackgroundResource(R.color.green)
            "B" -> typeTxt.setBackgroundResource(R.color.blue)
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                colorTxt.text = seekBar.progress.toString()
                if (!isText!!) {
                    setRGBColor("Background")
                } else if (isText!!) {
                    setRGBColor("Text")
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        colorTxt.text = seekBar.progress.toString()
    }

    private fun setRGBColor(viewType: String): List<String> {

        val list = mutableListOf<String>()
        val hex = String.format(
            "#%02x%02x%02x",
            rgbLayoutDialogBinding.redLayout.seekBar.progress,
            rgbLayoutDialogBinding.greenLayout.seekBar.progress,
            rgbLayoutDialogBinding.blueLayout.seekBar.progress
        )

        if (viewType == "Background") {
            rgbLayoutDialogBinding.colorView.setBackgroundColor(Color.parseColor(hex))
        } else if (viewType == "Text") {
            rgbLayoutDialogBinding.colorView.setTextColor(Color.parseColor(hex))
        }
        list.add(rgbLayoutDialogBinding.redLayout.seekBar.progress.toString())
        list.add(rgbLayoutDialogBinding.greenLayout.seekBar.progress.toString())
        list.add(rgbLayoutDialogBinding.blueLayout.seekBar.progress.toString())
        list.add(hex)
        return list
    }

    private fun getSearchedString(): String? {
        val editTextInput: String?
        if (searchLayoutDialogBinding.searchEditText.text.toString() == "")
            return null
        editTextInput = searchLayoutDialogBinding.searchEditText.text.toString()
        return editTextInput
    }


}