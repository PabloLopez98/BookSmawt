package pablo.myexample.booksmawt.ui.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.add_fragment.*
import pablo.myexample.booksmawt.Book
import pablo.myexample.booksmawt.Profile

import pablo.myexample.booksmawt.R
import pablo.myexample.booksmawt.databinding.AddFragmentBinding

class AddFragment : Fragment() {

    private lateinit var imageUriA: Uri
    private lateinit var imageUriB: Uri
    private lateinit var imageUriC: Uri
    private lateinit var urlList: ArrayList<String>
    private lateinit var userId: String
    private val PICK_IMAGE_REQUEST_A = 1
    private val PICK_IMAGE_REQUEST_B = 2
    private val PICK_IMAGE_REQUEST_C = 3
    private lateinit var binding: AddFragmentBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun snackBar(str: String) {
        Snackbar.make(
            add_fragment_layout,
            str,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun getExtension(uri: Uri): String {
        val contentResolver = context?.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver?.getType(uri))
    }

    private fun uploadToDatabase(ownerObj: Profile) {
        val obj: Book = Book(
            urlList,
            binding.titleEt.text.toString(),
            binding.authorEt.text.toString(),
            binding.isbnEt.text.toString(),
            binding.priceEt.text.toString(),
            binding.detailsEt.text.toString(),
            ownerObj.location,
            ownerObj.url,
            ownerObj.name,
            userId
        )

        //upload under 'Cities'
        FirebaseDatabase.getInstance().getReference().child("Cities").child(ownerObj.location).child(binding.isbnEt.text.toString()).child(userId).setValue(obj)
        //upload under unique user
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Cities").child(ownerObj.location).push().setValue(obj)
        //wait a little while showing snackbar, navigate to list when done
    }

    private fun uploadImagesAndGetUrl() {

        arrayOf(imageUriA, imageUriB, imageUriC).forEach { item ->
            when {
                item != Uri.EMPTY -> {
                    val endNode
                            : String =
                        System.currentTimeMillis().toString() + "." + getExtension(item)
                    val storageRef =
                        FirebaseStorage.getInstance().getReference().child("Users").child(userId)
                            .child(binding.isbnEt.text.toString()).child(endNode)
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
                                val downloadUrl = task.result.toString()
                                urlList.add(downloadUrl)
                            }
                            else -> {
                                snackBar("Upload Failed!. Try again.")
                            }
                        }
                    }
                }
            }
        }

        fetchProfileOfOwner()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        urlList = ArrayList()
        imageUriA = Uri.EMPTY
        imageUriB = Uri.EMPTY
        imageUriC = Uri.EMPTY
        binding.apply {
            imageOne.setOnClickListener {
                selectImage(PICK_IMAGE_REQUEST_A)
            }
            imageTwo.setOnClickListener {
                selectImage(PICK_IMAGE_REQUEST_B)
            }
            imageThree.setOnClickListener {
                selectImage(PICK_IMAGE_REQUEST_C)
            }
            uploadButton.setOnClickListener {
                checkIfAnyInputIsEmpty()
            }
        }
    }

    private fun checkIfAnyInputIsEmpty() {
        when {
            (binding.imageOne.drawable == null && binding.imageTwo.drawable == null && binding.imageThree.drawable == null) -> {
                snackBar("Need at least one image")
            }
            TextUtils.isEmpty(binding.detailsEt.text.toString()) -> {
                snackBar("Details input is empty")
            }
            (TextUtils.isEmpty(binding.isbnEt.text.toString()) && binding.isbnEt.text.toString().length == 13) -> {
                snackBar("ISBN# input is empty")
            }
            TextUtils.isEmpty(binding.priceEt.text.toString()) -> {
                snackBar("Price input is empty")
            }
            TextUtils.isEmpty(binding.titleEt.text.toString()) -> {
                snackBar("Title input is empty")
            }
            TextUtils.isEmpty(binding.authorEt.text.toString()) -> {
                snackBar("Author input is empty")
            }
            else -> {
                uploadImagesAndGetUrl()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            data != null && data.data != null && resultCode == Activity.RESULT_OK -> when {
                requestCode == PICK_IMAGE_REQUEST_A -> {
                    imageUriA = data!!.data
                    Picasso.get().load(imageUriA).into(binding.imageOne)
                }
                requestCode == PICK_IMAGE_REQUEST_B -> {
                    imageUriB = data!!.data
                    Picasso.get().load(imageUriB).into(binding.imageTwo)
                }
                requestCode == PICK_IMAGE_REQUEST_C -> {
                    imageUriC = data!!.data
                    Picasso.get().load(imageUriC).into(binding.imageThree)
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
}
