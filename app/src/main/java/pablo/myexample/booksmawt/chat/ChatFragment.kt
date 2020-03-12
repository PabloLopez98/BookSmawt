package pablo.myexample.booksmawt.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.chat_fragment.*
import pablo.myexample.booksmawt.*
import pablo.myexample.booksmawt.databinding.ChatFragmentBinding
import pablo.myexample.booksmawt.list.ListFragmentAdapter
import java.text.SimpleDateFormat


class ChatFragment : Fragment() {

    private lateinit var messageObjList: ArrayList<Message>
    private lateinit var adapter: ChatFragmentAdapter
    private val userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var profileObj: Profile
    private lateinit var binding: ChatFragmentBinding
    private lateinit var model: Communicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProvider(activity!!).get(Communicator::class.java)
        model.bookObj.observe(activity!!, Observer<Book> { o ->
            binding.bookObj = o
            Picasso.get().load(o.urlList[0]).into(binding.chatFragImage)
        })

        getProfileData()
        setUpRecyclerView()

        binding.chatFragSend.setOnClickListener {
            checkInput()
        }
    }

    private fun getProfileData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Profile")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    when {
                        snapshot.exists() -> {
                            profileObj = snapshot.getValue(Profile::class.java)!!
                        }
                    }
                }
            })
    }

    private fun setUpRecyclerView() {
        messageObjList = ArrayList()
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ChatFragmentAdapter(messageObjList)
        recyclerView.adapter = adapter
    }

    private fun loadConversation() {

    }

    private fun checkInput() {
        when {
            binding.chatFragInput.text.isBlank() -> Toast.makeText(
                context,
                "Input is empty",
                Toast.LENGTH_SHORT
            ).show()
            else -> sendMessage()
        }
    }

    private fun sendMessage() {
        val message =
            Message(userId, profileObj.url, profileObj.name, "theDate", "This is the message!")
        messageObjList.add(message)
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.bottom_nav_view.visibility = View.VISIBLE
    }
}
