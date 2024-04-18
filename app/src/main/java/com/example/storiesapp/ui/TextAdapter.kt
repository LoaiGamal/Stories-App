package com.example.storiesapp.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.TypedValue
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.storiesapp.R
import com.example.storiesapp.data.Text


var textSize = 14f
const val DEFINITION = 0

class TextAdapter() :
    ListAdapter<Text, RecyclerView.ViewHolder>(TextDiffCallback()) {

    private var listener: OnItemClickListener? = null
    private var textHighlightListener: TextHighlightListener? = null
    private var textColor: String? = null
    var spannableString: SpannableString? = null

    companion object {
        private const val VIEW_TYPE_TITLE = 0
        private const val VIEW_TYPE_HEADLINE = 1
        private const val VIEW_TYPE_BODY = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TITLE -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_title, parent, false)
                TitleViewHolder(view, listener!!)
            }

            VIEW_TYPE_HEADLINE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_headline, parent, false)
                HeadlineViewHolder(view)
            }

            VIEW_TYPE_BODY -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_body, parent, false)
                BodyViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (holder) {
            is TitleViewHolder -> holder.bind(item as Text.Title)
            is HeadlineViewHolder -> holder.bind(item as Text.Headline)
            is BodyViewHolder -> holder.bind(item as Text.Body, textColor)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Text.Title -> VIEW_TYPE_TITLE
            is Text.Headline -> VIEW_TYPE_HEADLINE
            is Text.Body -> VIEW_TYPE_BODY
        }
    }

    fun setOnTitleClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setTextHighlightListener(listener: TextHighlightListener) {
        this.textHighlightListener = listener
    }

    // View holders

    class TitleViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)

        init {
            itemView.setOnClickListener {
                listener.onTitleClicked(adapterPosition)
            }
        }

        fun bind(item: Text.Title) {
            titleTextView.text = item.title
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            val mSpannableString = SpannableString(titleTextView.text)
            mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
            titleTextView.text = mSpannableString
        }
    }

    inner class HeadlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headlineTextView: TextView = itemView.findViewById(R.id.headlineTextView)

        fun bind(item: Text.Headline) {
            if (item.spannableString == null)
                headlineTextView.text = item.headline
            else
                headlineTextView.text = item.spannableString
            headlineTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            headlineTextView.setTextColor(Color.parseColor("#0000FF"))
            getSelectedText(headlineTextView, adapterPosition, item.spannableString)
        }
    }

    inner class BodyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bodyTextView: AppCompatTextView = itemView.findViewById(R.id.bodyTextView)

        fun bind(item: Text.Body, color: String?) {
            Log.d("LOGGER", "bodyTextView is selectable = ${bodyTextView.isTextSelectable}")
            Log.d("LOGGER", "item body ${item.body}")

            if (item.spannableString == null)
                bodyTextView.text = item.body
            else
                bodyTextView.text = item.spannableString
            bodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            if (color != null) {
                bodyTextView.setTextColor(Color.parseColor(color))
            }
            getSelectedText(bodyTextView, adapterPosition, item.spannableString)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun increaseTextSize() {
        textSize += 4f
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun decreaseTextSize() {
        if (textSize - 4 <= 0f)
            return
        textSize -= 4f
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun pickTextColor(color: String) {
        textColor = color
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onTitleClicked(titleId: Int)
    }

    interface TextHighlightListener {
        fun onTextHighlighted(text: SpannableString, index: Int)
    }

    fun getSelectedText(view: TextView, adapterPosition: Int, spannableString: SpannableString?) {
        Log.d("LOGGER", "getSelectedText")
        if (view.customSelectionActionModeCallback != null) {
            view.customSelectionActionModeCallback = null
            Log.d("LOGGER", "customSelectionActionModeCallback = null")
        }

        view.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                Log.d("LOGGER", "onPrepareActionMode")
                return true
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                // Called when action mode is first created. The menu supplied
                // will be used to generate action buttons for the action mode

                // Here is an example MenuItem
                menu.add(0, DEFINITION, 0, "تظليل")
                Log.d("LOGGER", "onCreateActionMode")
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                Log.d("LOGGER", "onDestroyActionMode")
                // Called when an action mode is about to be exited and
                // destroyed
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                Log.d("LOGGER", "onActionItemClicked - item id = ${item.itemId}")
                var min = 0
                var max = view.text.length
                when (item.itemId) {
                    DEFINITION -> {

                        if (view.isFocused) {
                            Log.d("LOGGER", "onActionItemClicked - view is focused")
                            val selStart = view.selectionStart
                            val selEnd = view.selectionEnd

                            min = Math.max(0, Math.min(selStart, selEnd))
                            max = Math.max(0, Math.max(selStart, selEnd))
                        }

                        Log.d("LOGGERHIGH", "min: ${min}")
                        Log.d("LOGGERHIGH", "max: ${max}")

                        val span = spannableString ?: SpannableString(view.text)
                        val highlightedText = highlightText(span, min, max)
                        view.text = highlightedText


                        Log.d("LOGGERHIGH", "${currentList[adapterPosition]}")
                        textHighlightListener?.onTextHighlighted(highlightedText, adapterPosition)
                        notifyDataSetChanged()

                        mode.finish()
                        return true
                    }

                    else -> return false
                }
            }
        })
    }

    fun highlightText(text: SpannableString, min: Int, max: Int): SpannableString {
        text.setSpan(BackgroundColorSpan(Color.YELLOW), min, max, 0)
        return text
    }
}

class TextDiffCallback : DiffUtil.ItemCallback<Text>() {
    override fun areItemsTheSame(oldItem: Text, newItem: Text): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Text, newItem: Text): Boolean {
        return oldItem == newItem
    }

}

