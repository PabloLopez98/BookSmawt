package pablo.myexample.booksmawt.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/*
TODO
-display messages list, and update as database updates
-send button, update database on opposite end
-update recyclerview with database updates
-delete option
-click book, send to preview
 */

class ChatFragment : Fragment() {

    private lateinit var thisUserObject: ChatProfile
    private lateinit var messagesList: ArrayList<LastMessage>
    private val baseRef = FirebaseDatabase.getInstance().reference.child("Chats")
    private lateinit var owner: ChatProfile
    private lateinit var buyer: ChatProfile
    private lateinit var adapter: ChatFragmentAdapter
    private val userId: String = FirebaseAuth.getInstance().currentUser!!.uid
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
        messagesList = ArrayList()
        model = ViewModelProvider(activity!!).get(Communicator::class.java)
        model.lastMessageObj.observe(activity!!, Observer<LastMessage> { o ->
            getData(o.chatId)
        })

        binding.chatFragSend.setOnClickListener {
            checkInput()
        }
        binding.chatFragBackArrow.setOnClickListener {
            toMessagesFragment()
        }
    }

    private fun toMessagesFragment() {
        activity!!.bottom_nav_view.visibility = View.VISIBLE
        view!!.findNavController().navigate(R.id.action_chatFragment_to_navigation_messages)
    }

    private fun getData(chatId: String) {
        baseRef.child(chatId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) = when {
                snapshot.exists() -> {
                    snapshot.children.forEach {
                        when (it.key) {
                            "Book" -> {
                                val bookObj = it.getValue(Book::class.java)
                                binding.bookObj = bookObj
                                Picasso.get().load(bookObj!!.urlList[0])
                                    .into(binding.chatFragImage)
                            }
                            "Buyer" -> {
                                buyer = it.getValue(ChatProfile::class.java)!!
                            }
                            "Owner" -> {
                                owner = it.getValue(ChatProfile::class.java)!!
                            }
                        }
                    }
                    displayNameOnTop()
                }
                else -> {/* NOTHING */
                }
            }
        })
    }

    private fun displayNameOnTop() {
        when (userId) {
            buyer.id -> {
                binding.chatFragOtherName.text = owner.name
                thisUserObject = owner
            }
            else -> {
                binding.chatFragOtherName.text = buyer.name
                thisUserObject = buyer
            }
        }
        getMessages()
    }

    private fun getMessages() {
        baseRef.child(buyer.chatId).child("Messages")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) = when {
                    snapshot.exists() -> {
                        snapshot.children.forEach {
                            val msg = it.getValue(LastMessage::class.java)!!
                            messagesList.add(msg)
                        }
                        setUpRecyclerView()
                    }
                    else -> {/* NOTHING */
                    }
                }
            })
    }

    private fun setUpRecyclerView() {
        messagesList.removeAt(0)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ChatFragmentAdapter(messagesList)
        recyclerView.adapter = adapter
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
        val date = LocalDateTime.now()
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT))
        val msg = binding.chatFragInput.text.toString()
        val message = LastMessage(
            userId,
            thisUserObject.chatId,
            thisUserObject.url,
            thisUserObject.name,
            date,
            msg
        )
        messagesList.add(message)
        adapter.notifyDataSetChanged()
        //update database
        baseRef.child(thisUserObject.chatId).child("Messages").push().setValue(message)
        val baseRefJr = FirebaseDatabase.getInstance().reference.child("Users")
        baseRefJr.child(owner.id).child("Chats").child(thisUserObject.chatId).setValue(message)
        baseRefJr.child(buyer.id).child("Chats").child(thisUserObject.chatId).setValue(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.bottom_nav_view.visibility = View.VISIBLE
    }

}
