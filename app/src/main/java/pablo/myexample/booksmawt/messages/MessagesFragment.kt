package pablo.myexample.booksmawt.messages

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_base.*
import pablo.myexample.booksmawt.Book
import pablo.myexample.booksmawt.Communicator
import pablo.myexample.booksmawt.LastMessage

import pablo.myexample.booksmawt.R
import pablo.myexample.booksmawt.chat.ChatFragmentAdapter
import pablo.myexample.booksmawt.list.ListFragmentAdapter
import java.lang.Exception

class MessagesFragment : Fragment() {

    private lateinit var listener: ChildEventListener
    private lateinit var getMsgRef: DatabaseReference
    private lateinit var lastMessagesChatIdList: ArrayList<String>
    private lateinit var model: Communicator
    private lateinit var adapter: MessagesFragmentAdapter
    private lateinit var lastMessagesList: ArrayList<LastMessage>
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onDestroyView() {
        super.onDestroyView()
        getMsgRef.removeEventListener(listener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.messages_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity!!.bottom_nav_view.visibility == View.INVISIBLE) {
            activity!!.bottom_nav_view.visibility = View.VISIBLE
        }
        model = ViewModelProvider(activity!!).get(Communicator::class.java)
        lastMessagesChatIdList = ArrayList()
        lastMessagesList = ArrayList()
        setUpRecyclerView()
        getLastMessages()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun getLastMessages() {
        getMsgRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Chats")
        listener = getMsgRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatId = p0.key.toString()
                val position: Int = lastMessagesChatIdList.indexOf(chatId)
                lastMessagesList[position] = p0.getValue(LastMessage::class.java)!!
                adapter.notifyItemChanged(position)
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                when {
                    p0.exists() -> {
                        lastMessagesList.add(p0.getValue(LastMessage::class.java)!!)
                        lastMessagesChatIdList.add(p0.key.toString())
                    }
                }
                adapter.notifyDataSetChanged()

                try {
                    view!!.findViewById<ConstraintLayout>(R.id.progress_circle_messages)
                        .visibility = View.INVISIBLE
                } catch (e: Exception) {
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
        try {
            view!!.findViewById<ConstraintLayout>(R.id.progress_circle_messages).visibility =
                View.INVISIBLE
        } catch (e: Exception) {
        }
    }

    private fun setUpRecyclerView() {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.messages_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MessagesFragmentAdapter(lastMessagesList) { obj -> bookItemClicked(obj) }
        recyclerView.adapter = adapter
    }

    private fun bookItemClicked(obj: LastMessage) {
        model.passLastMessageObj(obj)
        toChatFragment()
    }

    private fun toChatFragment() {
        activity!!.bottom_nav_view.visibility = View.INVISIBLE
        view!!.findNavController().navigate(R.id.action_navigation_messages_to_chatFragment)
    }
}
