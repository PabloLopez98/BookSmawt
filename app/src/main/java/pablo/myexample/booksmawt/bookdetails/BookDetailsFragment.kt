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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageClickListener
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.book_details_fragment.*
import pablo.myexample.booksmawt.*
import pablo.myexample.booksmawt.chat.ChatFragment

import pablo.myexample.booksmawt.databinding.BookDetailsFragmentBinding

class BookDetailsFragment : Fragment() {

    private lateinit var buyer: ChatProfile
    private lateinit var profile: Profile
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

        getProfile()

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
                        createChatStructure()
                    }
                }
            }
        }
    }

    private fun getProfile() {
        val mRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
            .child("Profile")
        mRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                when {
                    snapshot.exists() -> {
                        profile = snapshot.getValue(Profile::class.java)!!
                    }
                }
            }
        })
    }

    private fun createChatStructure() {
        //chat room id and base reference
        val chatId = System.currentTimeMillis().toString()
        val chatRef = FirebaseDatabase.getInstance().reference.child("Chats").child(chatId)
        //ChatProfile Objects
        val owner = ChatProfile(chatId, book.nameOfOwner, book.urlOfOwner, book.idOfOwner)
        buyer = ChatProfile(chatId, profile.name, profile.url, userId)
        //Under Users
        val lastMessageObjRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Chats")
                .child(chatId)
        val lastMessageObj = LastMessage(chatId, book.urlOfOwner, book.nameOfOwner, "N/A", "N/A")
        lastMessageObjRef.setValue(lastMessageObj)
        //Under Chats
        chatRef.child("Owner").setValue(owner)
        chatRef.child("Buyer").setValue(buyer)
        chatRef.child("Book").setValue(book)
        chatRef.child("Messages").push().setValue(lastMessageObj)
        //then go to chat frag
        toChatFragment()
    }

    private fun toChatFragment() {
        model.passBuyerObj(buyer)
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
