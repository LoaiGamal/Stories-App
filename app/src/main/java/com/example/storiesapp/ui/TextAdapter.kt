package com.example.storiesapp.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.storiesapp.R
import com.example.storiesapp.data.Text


var textSize = 14f

class TextAdapter():
    ListAdapter<Text, RecyclerView.ViewHolder>(TextDiffCallback()) {

    private var listener: OnItemClickListener? = null
    private var textColor: String? = null
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

    // View holders

    class TitleViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
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

    class HeadlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headlineTextView: TextView = itemView.findViewById(R.id.headlineTextView)

        fun bind(item: Text.Headline) {
            headlineTextView.text = item.headline
            headlineTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            headlineTextView.setTextColor(Color.parseColor("#0000FF"))
        }
    }

    class BodyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bodyTextView: TextView = itemView.findViewById(R.id.bodyTextView)
        fun bind(item: Text.Body, color: String?) {
            bodyTextView.text = item.body
            bodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            if (color != null){
                bodyTextView.setTextColor(Color.parseColor(color))
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_TITLE = 0
        private const val VIEW_TYPE_HEADLINE = 1
        private const val VIEW_TYPE_BODY = 2
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

    fun pickTextColor(color: String){
        textColor = color
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onTitleClicked(titleId: Int)
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