package pablo.myexample.booksmawt.addedbook

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageClickListener
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_base.*
import pablo.myexample.booksmawt.*

import pablo.myexample.booksmawt.databinding.AddedBookFragmentBinding

class AddedBookFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var binding: AddedBookFragmentBinding
    private lateinit var model: Communicator
    private var bookInUse: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.added_book_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        model = ViewModelProvider(activity!!).get(Communicator::class.java)
        model.bookObj.observe(activity!!, Observer<Book> { o ->
            binding.bookObj = o
            val images = o.urlList
            val imageListener = ImageListener { position, imageView ->
                Picasso.get().load(images.get(position)).rotate(90F).into(imageView)
            }
            val carouselView = binding.addedBookCarousel
            carouselView.setImageListener(imageListener)
            carouselView.pageCount = images.size
            carouselView.setImageClickListener { position ->
                val dialog = Dialog(context)
                val theView: View = layoutInflater.inflate(R.layout.image_layout, null)
                dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(theView)
                val imageViewDisplayArea = theView.findViewById<ImageView>(R.id.image_display)
                val imageClicked = images[position]
                Picasso.get().load(imageClicked)
                    .rotate(90F).into(imageViewDisplayArea)
                dialog.show()
            }
        })
        binding.addedBookArrow.setOnClickListener {
            toListFragment()
        }
        binding.addedBookDeleteButton.setOnClickListener {
            dialogToDelete()
        }
        binding.addedBookEditButton.setOnClickListener {
            editBook()
        }
        checkIfBookInUse()
    }

    private fun checkIfBookInUse(){
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Chats").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                when {
                    snapshot.exists() -> {
                        snapshot.children.forEach {
                            val lastMsg = it.getValue(LastMessage::class.java)
                            val theisbn = lastMsg!!.theisbn
                            val thebisbn = binding.bookObj!!.isbn
                            if(theisbn == thebisbn){
                                bookInUse = true
                            }
                        }
                    }
                }
            }
        })
    }

    private fun editBook() {
        model.passBookObj(binding.bookObj!!)
        toEditAddedBookFragment()
    }

    private fun toEditAddedBookFragment(){
        when (bookInUse) {
            false -> {
                view!!.findNavController().navigate(R.id.action_addedBookFragment_to_editAddedBookFragment)
            }
            else -> {
                okDialog()
            }
        }
    }

    private fun okDialog() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        dialogBuilder.setMessage("Cannot edit while buyers are communicating with you about the book.")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss()}
        val alert = dialogBuilder.create()
        alert.setTitle("Sorry")
        alert.show()
    }

    private fun dialogToDelete() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        dialogBuilder.setMessage("Are you sure you want to delete this book upload?")
            .setPositiveButton("Yes", {dialog, id -> deleteBook()})
            .setNegativeButton("No", {dialog, id -> dialog.dismiss()})
        val alert = dialogBuilder.create()
        alert.setTitle("Warning")
        alert.show()
    }

    private fun deleteBook() {
        val len = binding.bookObj!!.urlList.size + 1
        for (i in 1 until len) {
            var endNode = "$i.jpg"
            FirebaseStorage.getInstance().reference.child("Users").child(userId).child(binding.bookObj!!.isbn).child(endNode).delete()
        }
        FirebaseDatabase.getInstance().reference.child("Cities").child(binding.bookObj!!.location).child(binding.bookObj!!.isbn).child(userId).removeValue()
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Cities").child(binding.bookObj!!.location).child(binding.bookObj!!.isbn).removeValue()
        toListFragment()
    }

    private fun toListFragment() {
        //activity!!.onBackPressed()
        view!!.findNavController().navigate(R.id.action_addedBookFragment_to_navigation_list)
    }

}
