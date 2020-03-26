package pablo.myexample.booksmawt.chat

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.chat_fragment.*
import pablo.myexample.booksmawt.*
import pablo.myexample.booksmawt.R
import pablo.myexample.booksmawt.databinding.ChatFragmentBinding
import pablo.myexample.booksmawt.list.ListFragmentAdapter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.properties.Delegates

class ChatFragment : Fragment() {

    private var onePersonLeft: Boolean = false
    private lateinit var lastMsg: LastMessage
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
            lastMsg = o
            getData(o.chatId)
        })

        binding.chatFragSend.setOnClickListener {
            //hide keyboard first
            hideKeyboard()
            checkInput()
        }
        binding.chatFragBackArrow.setOnClickListener {
            toMessagesFragment()
        }
        binding.linearLayout7.setOnClickListener {
            toPreviewFragment()
        }
        binding.chatFragDelete.setOnClickListener {
            deleteDialog()
        }
    }

    private fun hideKeyboard() {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0);
    }

    private fun deleteDialog() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        dialogBuilder.setMessage("Are you sure you want to delete this conversation")
            .setPositiveButton("Yes", { dialog, id -> deleteConversation() })
            .setNegativeButton("No", { dialog, id -> dialog.dismiss() })
        val alert = dialogBuilder.create()
        alert.setTitle("Warning")
        alert.show()
    }

    private fun deleteConversation() {
        val baseRef = FirebaseDatabase.getInstance().reference
        if (onePersonLeft) {//if last person, delete last msg node, and conversation permanently
            baseRef.child("Chats").child(owner.chatId).removeValue()
            baseRef.child("Users").child(userId).child("Chats").child(owner.chatId).removeValue()
        } else {//two people left, then delete last msg node, and tell database there is one person left
            baseRef.child("Chats").child(owner.chatId).child("OnePersonLeft").setValue("true")
            baseRef.child("Users").child(userId).child("Chats").child(owner.chatId).removeValue()
        }
        toMessagesFragment()
    }

    private fun toPreviewFragment() {
        model.passBookObj(binding.bookObj!!)
        model.passLastMessageObj(lastMsg)
        activity!!.bottom_nav_view.visibility = View.INVISIBLE
        view!!.findNavController().navigate(R.id.action_chatFragment_to_previewFragment)
    }

    private fun toMessagesFragment() {
        //activity!!.onBackPressed()
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
                                Picasso.get().load(bookObj!!.urlList[0]).fit().centerCrop()
                                    .into(binding.chatFragImage)
                            }
                            "Buyer" -> {
                                buyer = it.getValue(ChatProfile::class.java)!!
                            }
                            "Owner" -> {
                                owner = it.getValue(ChatProfile::class.java)!!
                            }
                            "OnePersonLeft" -> {
                                onePersonLeft = true
                            }
                        }
                    }
                    displayNameOnTop()
                }
                else -> {
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
        setUpRecyclerView()
        getMessages()
    }

    private fun getMessages() {
        baseRef.child(buyer.chatId).child("Messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    when {
                        p0.exists() -> {
                            when {
                                p0.getValue(LastMessage::class.java)!!.date != "N/A" -> messagesList.add(
                                    p0.getValue(LastMessage::class.java)!!
                                )
                            }
                        }
                        else -> {
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

            })
    }

    private fun setUpRecyclerView() {
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
            msg,
            binding.chatFragIsbn.text.toString()
        )

        //update database conversation
        if (userId == owner.id) {
            message.imageUrl = owner.url
            baseRef.child(thisUserObject.chatId).child("Messages").push().setValue(message)
        } else {
            message.imageUrl = buyer.url
            baseRef.child(thisUserObject.chatId).child("Messages").push().setValue(message)
        }
        val baseRefJr = FirebaseDatabase.getInstance().reference.child("Users")

        //send push notification only if there are two people in conversation
        if (onePersonLeft == false) {
            //decide which id user is and send push notification to correct recipient
            when (userId) {
                owner.id -> {
                    SendPush().sendFCMPush(
                        context!!,
                        buyer.id,
                        binding.chatFragInput.text.toString(),
                        owner.name
                    )
                }
                else -> {
                    SendPush().sendFCMPush(
                        context!!,
                        owner.id,
                        binding.chatFragInput.text.toString(),
                        buyer.name
                    )
                }
            }
            //Change the name to match opposite end user, and not have same name as profile name
            message.name = buyer.name
            message.imageUrl = buyer.url
            baseRefJr.child(owner.id).child("Chats").child(thisUserObject.chatId).setValue(message)
            message.name = owner.name
            message.imageUrl = owner.url
            baseRefJr.child(buyer.id).child("Chats").child(thisUserObject.chatId).setValue(message)
        } else {
            //setting last msg under 'Users'
            when (userId) {
                owner.id -> {
                    //dont forget to replace name with other persons name and url
                    message.name = buyer.name
                    message.imageUrl = buyer.url
                    baseRefJr.child(owner.id).child("Chats").child(thisUserObject.chatId)
                        .setValue(message)
                }
                else -> {
                    //dont forget to replace name with other persons name and url
                    message.name = owner.name
                    message.imageUrl = owner.url
                    baseRefJr.child(buyer.id).child("Chats").child(thisUserObject.chatId)
                        .setValue(message)
                }
            }
        }
        binding.chatFragInput.setText("")
    }
}
