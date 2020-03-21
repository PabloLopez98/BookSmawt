package pablo.myexample.booksmawt.editprofile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.content.ContentResolver
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.edit_profile_fragment.*
import pablo.myexample.booksmawt.Communicator
import pablo.myexample.booksmawt.Profile

import pablo.myexample.booksmawt.R
import pablo.myexample.booksmawt.databinding.EditProfileFragmentBinding

class EditProfile : Fragment() {

    private lateinit var userId: String
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var binding: EditProfileFragmentBinding
    private lateinit var model: Communicator
    private lateinit var imageUri: Uri
    private val cityArray = arrayListOf(
        "Los Angeles",
        "San Diego",
        "San Jose",
        "San Francisco",
        "Fresno",
        "Sacramento",
        "Long Beach",
        "Oakland",
        "Bakersfield",
        "Anaheim",
        "Santa Ana",
        "Riverside",
        "Stockton",
        "Chula Vista",
        "Irvine",
        "Fremont",
        "San Bernardino",
        "Modesto",
        "Fontana",
        "Oxnard",
        "Moreno Valley",
        "Huntington Beach",
        "Glendale",
        "Santa Clarita",
        "Garden Grove",
        "Oceanside",
        "Rancho Cucamonga",
        "Santa Rosa",
        "Ontario",
        "Lancaster",
        "Elk Grove",
        "Corona",
        "Palmdale",
        "Salinas",
        "Pomona",
        "Hayward",
        "Escondido",
        "Torrance",
        "Sunnyvale",
        "Orange",
        "Fullerton",
        "Pasadena",
        "Thousand Oaks",
        "Visalia",
        "Simi Valley",
        "Concord",
        "Roseville",
        "Victorville",
        "Santa Clara",
        "Vallejo",
        "Berkeley",
        "El Monte",
        "Downey",
        "Costa Mesa",
        "Inglewood",
        "Carlsbad",
        "San Buenaventura (Ventura)",
        "Fairfield",
        "West Covina",
        "Murrieta",
        "Richmond",
        "Norwalk",
        "Antioch",
        "Temecula",
        "Burbank",
        "Daly City",
        "Rialto",
        "Santa Maria",
        "El Cajon",
        "San Mateo",
        "Clovis",
        "Compton",
        "Jurupa Valley",
        "Vista",
        "South Gate",
        "Mission Viejo",
        "Vacaville",
        "Carson",
        "Hesperia",
        "Santa Monica",
        "Westminster",
        "Redding",
        "Santa Barbara",
        "Chico",
        "Newport Beach",
        "San Leandro",
        "San Marcos",
        "Whittier",
        "Hawthorne",
        "Citrus Heights",
        "Tracy",
        "Alhambra",
        "Livermore",
        "Buena Park",
        "Menifee",
        "Hemet",
        "Lakewood",
        "Merced",
        "Chino",
        "Indio",
        "Redwood City",
        "Lake Forest",
        "Napa",
        "Tustin",
        "Bellflower",
        "Mountain View",
        "Chino Hills",
        "Baldwin Park",
        "Alameda",
        "Upland",
        "San Ramon",
        "Folsom",
        "Pleasanton",
        "Union City",
        "Perris",
        "Manteca",
        "Lynwood",
        "Apple Valley",
        "Redlands",
        "Turlock",
        "Milpitas",
        "Redondo Beach",
        "Rancho Cordova",
        "Yorba Linda",
        "Palo Alto",
        "Davis",
        "Camarillo",
        "Walnut Creek",
        "Pittsburg",
        "South San Francisco",
        "Yuba City",
        "San Clemente",
        "Laguna Niguel",
        "Pico Rivera",
        "Montebello",
        "Lodi",
        "Madera",
        "Santa Cruz",
        "La Habra",
        "Encinitas",
        "Monterey Park",
        "Tulare",
        "Cupertino",
        "Gardena",
        "National City",
        "Rocklin",
        "Petaluma",
        "Huntington Park",
        "San Rafael",
        "La Mesa",
        "Arcadia",
        "Fountain Valley",
        "Diamond Bar",
        "Woodland",
        "Santee",
        "Lake Elsinore",
        "Porterville",
        "Paramount",
        "Eastvale",
        "Rosemead",
        "Hanford",
        "Highland",
        "Brentwood",
        "Novato",
        "Colton",
        "Cathedral City",
        "Delano",
        "Yucaipa",
        "Watsonville",
        "Placentia",
        "Glendora",
        "Gilroy",
        "Palm Desert",
        "Cerritos",
        "West Sacramento",
        "Aliso Viejo",
        "Poway",
        "La Mirada",
        "Rancho Santa Margarita",
        "Cypress",
        "Dublin",
        "Covina",
        "Azusa",
        "Palm Springs",
        "San Luis Obispo",
        "Ceres",
        "San Jacinto",
        "Lincoln",
        "Newark",
        "Lompoc",
        "El Centro",
        "Danville",
        "Bell Gardens",
        "Coachella",
        "Rancho Palos Verdes",
        "San Bruno",
        "Rohnert Park",
        "Brea",
        "La Puente",
        "Campbell",
        "San Gabriel",
        "Beaumont",
        "Morgan Hill",
        "Culver City",
        "Calexico",
        "Stanton",
        "La Quinta",
        "Pacifica",
        "Montclair",
        "Oakley",
        "Monrovia",
        "Los Banos",
        "Martinez"
    )

    private fun snackBar(str: String) {
        Snackbar.make(
            inside_edit_profile_layout,
            str,
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_profile_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(activity!!).get(Communicator::class.java)
        model.profileObj.observe(activity!!, Observer<Profile> { o ->
            binding.profileObj = o
            when {
                o.url.contentEquals("empty") -> {
                }
                else -> {
                    Picasso.get().load(o.url).fit().centerCrop()
                        .into(binding.imageViewTop)
                }
            }
        })

        val adapter = ArrayAdapter(context, android.R.layout.select_dialog_item, cityArray)
        binding.newLocationEt.setAdapter(adapter)

        binding.backArrowTl.setOnClickListener {
            backToProfile()
        }
        binding.selectImageButton.setOnClickListener {
            selectImage()
        }
        apply_button.setOnClickListener {
            checkEmptyFields()
        }
        userId = FirebaseAuth.getInstance().currentUser!!.uid
    }

    private fun checkEmptyFields() {
        when {
            TextUtils.isEmpty(binding.newNameEt.text.toString()) -> snackBar("New name input is empty")
            TextUtils.isEmpty(binding.newLocationEt.text.toString()) -> snackBar("New location input is empty")
            else -> {
                verifyAddress()
            }
        }
    }

    private fun verifyAddress() {
        when {
            binding.newLocationEt.text.toString() in cityArray -> {
                inspectImageView()
            }
            else -> {
                snackBar("New location cannot be verified")
            }
        }
    }

    private fun inspectImageView() {
        when {
            image_view_top.drawable == null -> snackBar("Image input is empty")
            else -> {
                uploadImageAndGetUrl()
            }
        }
    }

    private fun uploadImageAndGetUrl() {
        val endNode = "Profile." + getExtension()
        val storageRef = FirebaseStorage.getInstance().getReference().child("Users").child(userId).child(endNode)
        storageRef.putFile(imageUri).continueWithTask { task ->
            when {
                !task.isSuccessful -> {
                    snackBar("Edit Failed!. Try again.")
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    val downloadUrl = task.result.toString()
                    uploadToDatabase(
                        binding.newLocationEt.text.toString(),
                        binding.newNameEt.text.toString(),
                        downloadUrl
                    )
                }
                else -> {
                    snackBar("Edit Failed!. Try again.")
                }
            }
        }
    }

    private fun uploadToDatabase(location: String, name: String, url: String) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Profile")
            .setValue(Profile(name, location, url))
        backToProfile()
    }

    private fun getExtension(): String {
        val contentResolver = context?.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver?.getType(imageUri))
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == PICK_IMAGE_REQUEST && data != null && data.data != null && resultCode == RESULT_OK -> {
                imageUri = data.data
                Picasso.get().load(imageUri).fit().centerCrop().into(binding.imageViewTop);
            }
        }
    }

    private fun backToProfile() {
        activity!!.onBackPressed()
        //view!!.findNavController().navigate(R.id.action_editProfile_to_navigation_profile)
    }

}
