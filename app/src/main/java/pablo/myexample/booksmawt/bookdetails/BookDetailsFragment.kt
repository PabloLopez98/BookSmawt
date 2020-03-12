package pablo.myexample.booksmawt.bookdetails

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageClickListener
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.book_details_fragment.*
import pablo.myexample.booksmawt.Book
import pablo.myexample.booksmawt.Communicator
import pablo.myexample.booksmawt.Profile

import pablo.myexample.booksmawt.R
import pablo.myexample.booksmawt.databinding.BookDetailsFragmentBinding

class BookDetailsFragment : Fragment() {

    private lateinit var book: Book
    private lateinit var userId: String
    private lateinit var binding: BookDetailsFragmentBinding
    private lateinit var model: Communicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.book_details_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = FirebaseAuth.getInstance().currentUser!!.uid
        model = ViewModelProvider(activity!!).get(Communicator::class.java)
        model.bookObj.observe(activity!!, Observer<Book> { o ->
            binding.bookObj = o
            book = o
        })

        binding.apply {
            val images = book.urlList
            val imageListener = ImageListener { position, imageView ->
                Picasso.get().load(images[position]).into(imageView)
            }
            bookDetailsCarousel.setImageListener(imageListener)
            bookDetailsCarousel.pageCount = images.size
            bookDetailsCarousel.setImageClickListener(ImageClickListener { position ->
                val dialog = Dialog(context)
                val theView: View = layoutInflater.inflate(R.layout.image_layout, null)
                dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(theView)
                val imageViewDisplayArea = theView.findViewById<ImageView>(R.id.image_display)
                val imageClicked = images[position]
                Picasso.get().load(imageClicked).into(imageViewDisplayArea)
                dialog.show()
            })
            backArrowDetails.setOnClickListener {
                backToSearch()
            }
            Picasso.get().load(book.urlOfOwner).into(bookDetailsImage)
            when {
                userId == book.idOfOwner -> {
                    book_details_button.text = "Edit Upload"
                }
            }

            bookDetailsButton.setOnClickListener {
                when {
                    bookDetailsButton.text == "Edit Upload" -> {
                        toEditBookUpload()
                    }
                    else -> {
                        toChatFragment()
                    }
                }
            }
        }
    }

    private fun toChatFragment(){
        model.passBookObj(book)
        view!!.findNavController().navigate(R.id.action_bookDetails_to_chatFragment)
    }

    private fun toEditBookUpload() {
        model.passBookObj(book)
        view!!.findNavController().navigate(R.id.action_bookDetails_to_editAddedBookFragment)
    }

    private fun backToSearch() {
        view!!.findNavController().navigate(R.id.action_bookDetails_to_navigation_search)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
