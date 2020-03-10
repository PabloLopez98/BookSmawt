package pablo.myexample.booksmawt.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_base.*
import pablo.myexample.booksmawt.*
import pablo.myexample.booksmawt.databinding.SearchFragmentBinding

class SearchFragment : Fragment() {

    private lateinit var min: String
    private lateinit var max: String
    private lateinit var loc: String
    private lateinit var orderBy: String
    private lateinit var model: Communicator
    private lateinit var bookList: ArrayList<Book>
    private lateinit var recyclerviewAdapter: SearchFragmentAdapter
    private lateinit var binding: SearchFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProvider(activity!!).get(Communicator::class.java)

        binding.filterButton.setOnClickListener {
            activity!!.bottom_nav_view.visibility = View.INVISIBLE
            view.findNavController().navigate(R.id.action_navigation_search_to_searchFilterFragment)
        }

        val options = arrayListOf("Most Expensive", "Least Expensive")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                orderBy = options[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.searchEt.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    checkISBN()
                    true
                }
                else -> {
                    false
                }
            }
        }

        setUpFilter()
        loadProfileImage()
        setupRecyclerview()
    }

    private fun setUpFilter() {
        model.filterObj.observe(activity!!, Observer<Filter> { o ->
            min = o.min
            max = o.max
            loc = o.location
            binding.locationTv.text = loc
            binding.priceRange.text = "$$min - $$max"
        })
    }

    private fun loadProfileImage() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Profile")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    when {
                        snapshot.exists() -> {
                            val profileObj = snapshot.getValue(Profile::class.java)
                            when {
                                profileObj!!.url.contentEquals("empty") -> {
                                }
                                else -> {
                                    Picasso.get().load(profileObj.url)
                                        .into(binding.profileImageView)
                                }
                            }
                        }
                    }
                }
            })
    }

    private fun setupRecyclerview() {
        bookList = ArrayList()
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerviewAdapter = SearchFragmentAdapter(bookList, { book -> bookItemClicked(book) })
        recyclerView.adapter = recyclerviewAdapter
    }

    private fun checkVariables() {
        when{
            binding.locationTv.text == "City N/A" -> {Toast.makeText(context, "No search filter", Toast.LENGTH_LONG).show()}
            else -> {searchBook()}
        }
    }

    private fun searchBook(){
        Toast.makeText(context, "Searching...", Toast.LENGTH_LONG).show()
    }

    private fun checkISBN() {
        when {
            TextUtils.isEmpty(binding.searchEt.text.toString()) -> {
                Toast.makeText(context, "Search is empty", Toast.LENGTH_SHORT).show()
            }
            binding.searchEt.text.toString().length != 13 -> {
                Toast.makeText(context, "ISBN has to be 13 digits", Toast.LENGTH_LONG).show()
            }
            else -> {
                hideKeyboard()
                checkVariables()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.rootView.windowToken, 0)
    }

    private fun bookItemClicked(book: Book) {
        model.passBookObj(book)
        toBookDetails()
    }

    private fun toBookDetails() {
        activity!!.bottom_nav_view.visibility = View.INVISIBLE
        view!!.findNavController().navigate(R.id.action_navigation_search_to_bookDetails)
    }
}
