package pablo.myexample.booksmawt.editaddedbook


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_added_book.*
import pablo.myexample.booksmawt.Book
import pablo.myexample.booksmawt.Communicator
import pablo.myexample.booksmawt.Profile
import pablo.myexample.booksmawt.R

import pablo.myexample.booksmawt.databinding.FragmentEditAddedBookBinding

class EditAddedBookFragment : Fragment() {

    /*
    delete old info and place new one inside under Cities and Users
     */

    private lateinit var obj: Book
    private lateinit var userId: String
    private lateinit var urlList: ArrayList<String>
    private lateinit var imageUriA: Uri
    private lateinit var imageUriB: Uri
    private lateinit var imageUriC: Uri
    private val PICK_IMAGE_REQUEST_A = 1
    private val PICK_IMAGE_REQUEST_B = 2
    private val PICK_IMAGE_REQUEST_C = 3
    private lateinit var model: Communicator
    private lateinit var binding: FragmentEditAddedBookBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_added_book, container, false)
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            data != null && data.data != null && resultCode == Activity.RESULT_OK -> when {
                requestCode == PICK_IMAGE_REQUEST_A -> {
                    imageUriA = data!!.data
                    Picasso.get().load(imageUriA).into(binding.imageA)
                }
                requestCode == PICK_IMAGE_REQUEST_B -> {
                    imageUriB = data!!.data
                    Picasso.get().load(imageUriB).into(binding.imageB)
                }
                requestCode == PICK_IMAGE_REQUEST_C -> {
                    imageUriC = data!!.data
                    Picasso.get().load(imageUriC).into(binding.imageC)
                }
            }
        }
    }

    private fun selectImage(digit: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, digit)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        urlList = ArrayList()
        model = ViewModelProvider(activity!!).get(Communicator::class.java)
        model.bookObj.observe(activity!!, Observer<Book> { o ->
            binding.bookObj = o
            for ((index, value) in binding.bookObj!!.urlList.withIndex()) {
                when (index) {
                    0 -> {
                        Picasso.get().load(value).into(binding.imageA)
                    }
                    1 -> {
                        Picasso.get().load(value).into(binding.imageB)
                    }
                    2 -> {
                        Picasso.get().load(value).into(binding.imageC)
                    }
                }
            }
        })
        imageUriA = Uri.EMPTY
        imageUriB = Uri.EMPTY
        imageUriC = Uri.EMPTY
        binding.apply {
            imageA.setOnClickListener {
                selectImage(PICK_IMAGE_REQUEST_A)
            }
            imageB.setOnClickListener {
                selectImage(PICK_IMAGE_REQUEST_B)
            }
            imageC.setOnClickListener {
                selectImage(PICK_IMAGE_REQUEST_C)
            }
            binding.backArrow.setOnClickListener {
                view!!.findNavController()
                    .navigate(R.id.action_editAddedBookFragment_to_addedBookFragment)
            }
            updateButton.setOnClickListener {
                checkIfAnyInputIsEmpty()
            }
        }
    }

    interface MyCallBack {
        fun onCallBack(str: String, int: Int)
    }

    private fun getExtension(uri: Uri): String {
        val contentResolver = context?.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver?.getType(uri))
    }

    private fun uploadImagesAndGetUrl(cb: MyCallBack) {
        var theLength = 3//number of imageUri
        arrayOf(imageUriA, imageUriB, imageUriC).forEach { item ->
            when {
                item != Uri.EMPTY -> {
                    val endNode: String =
                        System.currentTimeMillis().toString() + "." + getExtension(item)
                    val storageRef =
                        FirebaseStorage.getInstance().getReference().child("Users").child(userId)
                            .child(binding.isbnEdit.text.toString()).child(endNode)
                    storageRef.putFile(item).continueWithTask { task ->
                        when {
                            !task.isSuccessful -> {
                                snackBar("Upload Failed!. Try again.")
                            }
                        }
                        storageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        when {
                            task.isSuccessful -> {
                                cb.onCallBack(task.result.toString(), theLength)
                            }
                            else -> {
                                snackBar("Upload Failed!. Try again.")
                            }
                        }
                    }
                }
                else -> {
                    theLength--
                }
            }
        }
    }

    private fun checkIfAnyInputIsEmpty() {
        when {
            //images will never be empty
            TextUtils.isEmpty(binding.detailEdit.text.toString()) -> {
                snackBar("Details input is empty")
            }
            TextUtils.isEmpty(binding.isbnEdit.text.toString()) -> {
                snackBar("ISBN# input is empty")
            }
            binding.isbnEdit.text.toString().length != 13 -> {
                snackBar("ISBN# must be 13 digits")
            }
            TextUtils.isEmpty(binding.priceEdit.text.toString()) -> {
                snackBar("Price input is empty")
            }
            TextUtils.isEmpty(binding.titleEdit.text.toString()) -> {
                snackBar("Title input is empty")
            }
            TextUtils.isEmpty(binding.authorEdit.text.toString()) -> {
                snackBar("Author input is empty")
            }
            else -> {
                uploadImagesAndGetUrl(object : EditAddedBookFragment.MyCallBack {
                    override fun onCallBack(str: String, int: Int) {
                        urlList.add(str)
                        when {
                            (urlList.size == int) -> {
                                fetchProfileOfOwner()
                            }
                        }
                    }
                }
                )
            }
        }
    }

    private fun fetchProfileOfOwner() {
        val mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
            .child("Profile")
        mRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                when {
                    snapshot.exists() -> {
                        val obj: Profile = snapshot.getValue(Profile::class.java)!!
                        uploadToDatabase(obj)
                    }
                }
            }
        })
    }

    private fun uploadToDatabase(ownerObj: Profile) {
        obj = Book(
            urlList,
            binding.titleEdit.text.toString(),
            binding.authorEdit.text.toString(),
            binding.isbnEdit.text.toString(),
            binding.priceEdit.text.toString(),
            binding.detailEdit.text.toString(),
            ownerObj.location,
            ownerObj.url,
            ownerObj.name,
            userId
        )

        //upload under 'Cities'
        FirebaseDatabase.getInstance().reference.child("Cities").child(ownerObj.location)
            .child(binding.isbnEdit.text.toString()).child(userId).setValue(obj)

        //upload under unique user
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Cities")
            .child(ownerObj.location).child(binding.isbnEdit.text.toString()).setValue(obj)

        //wait a little while showing snackbar, navigate to book details fragment(for owner) when done
        model.passBookObj(obj)
        backToAddedBookFragment()
    }

    private fun backToAddedBookFragment() {
        model.passBookObj(obj)
        view!!.findNavController().navigate(R.id.action_editAddedBookFragment_to_addedBookFragment)
    }

    private fun snackBar(str: String) {
        Snackbar.make(
            edit_upload_layout,
            str,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
