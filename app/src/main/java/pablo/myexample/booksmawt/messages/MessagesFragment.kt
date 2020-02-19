package pablo.myexample.booksmawt.messages

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_base.*

import pablo.myexample.booksmawt.R

class MessagesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.messages_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<LinearLayout>(R.id.linearlayoutofmessages).setOnClickListener {
            //hide bottom nav by getting view from activity
            activity!!.bottom_nav_view.visibility = View.INVISIBLE
            //call fragment switch action
            view.findNavController().navigate(R.id.action_navigation_messages_to_chatFragment)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
