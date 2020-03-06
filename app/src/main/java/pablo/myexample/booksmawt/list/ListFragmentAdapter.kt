package pablo.myexample.booksmawt.list

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_preview_cardview.view.*
import pablo.myexample.booksmawt.Book
import pablo.myexample.booksmawt.R

class ListFragmentAdapter(val bookList: ArrayList<Book>, private val clickListener: (Book) -> Unit) : RecyclerView.Adapter<ListFragmentAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_preview_cardview, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(bookList[position], clickListener)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return bookList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(obj: Book, clickListener: (Book) -> Unit) {
            Picasso.get().load(obj.urlList[0]).into(itemView.image)
            itemView.title.text = obj.title
            itemView.author.text = obj.author
            itemView.isbn.text = obj.isbn
            itemView.price.text = obj.price
            itemView.location.text = obj.location
            itemView.setOnClickListener { clickListener(obj) }
        }
    }

}