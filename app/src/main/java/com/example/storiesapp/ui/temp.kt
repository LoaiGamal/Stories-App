//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.storiesapp.R
//import com.example.storiesapp.data.Text
//
//class TextAdapter(private val items: List<Text>) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    companion object {
//        private const val VIEW_TYPE_TITLE = 0
//        private const val VIEW_TYPE_HEADLINE = 1
//        private const val VIEW_TYPE_BODY = 2
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            VIEW_TYPE_TITLE -> {
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_title, parent, false)
//                TitleViewHolder(view)
//            }
//            VIEW_TYPE_HEADLINE -> {
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_headline, parent, false)
//                HeadlineViewHolder(view)
//            }
//            VIEW_TYPE_BODY -> {
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_body, parent, false)
//                BodyViewHolder(view)
//            }
//            else -> throw IllegalArgumentException("Invalid view type")
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val item = items[position]
//        when (holder) {
//            is TitleViewHolder -> holder.bind(item as Text.Title)
//            is HeadlineViewHolder -> holder.bind(item as Text.Headline)
//            is BodyViewHolder -> holder.bind(item as Text.Body)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when (items[position]) {
//            is Text.Title -> VIEW_TYPE_TITLE
//            is Text.Headline -> VIEW_TYPE_HEADLINE
//            is Text.Body -> VIEW_TYPE_BODY
//        }
//    }
//
//    // View holders
//
//    class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
//
//        fun bind(item: Text.Title) {
//            titleTextView.text = item.title
//        }
//    }
//
//    class HeadlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val headlineTextView: TextView = itemView.findViewById(R.id.headlineTextView)
//
//        fun bind(item: Text.Headline) {
//            headlineTextView.text = item.headline
//        }
//    }
//
//    class BodyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val bodyTextView: TextView = itemView.findViewById(R.id.bodyTextView)
//
//        fun bind(item: Text.Body) {
//            bodyTextView.text = item.body
//        }
//    }
//}