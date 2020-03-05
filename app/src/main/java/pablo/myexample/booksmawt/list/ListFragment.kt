package pablo.myexample.booksmawt.list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_base.*
import pablo.myexample.booksmawt.Book
import pablo.myexample.booksmawt.Profile

import pablo.myexample.booksmawt.R

class ListFragment : Fragment() {

    private lateinit var userId: String
    //private lateinit var adapter: ListFragmentAdapter
    private lateinit var bookList: ArrayList<Book>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        bookList = ArrayList()
        getData()
    }

    private fun getData() {
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Cities")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    when {
                        snapshot.exists() -> {
                            snapshot.children.forEach{
                                it.children.forEach{
                                    val bookObj : Book = it.getValue(Book::class.java)!!
                                    bookList.add(bookObj)
                                }
                            }
                        }
                    }
                }
            })
    }
}
