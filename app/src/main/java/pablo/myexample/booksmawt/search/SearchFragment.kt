package pablo.myexample.booksmawt.search


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_base.*
import pablo.myexample.booksmawt.BookDetails.BookDetails
import pablo.myexample.booksmawt.R


class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageButton>(R.id.filter_button).setOnClickListener {
            //hide bottom nav by getting view from activity
            activity!!.bottom_nav_view.visibility = View.INVISIBLE
            //call fragment switch action
            view.findNavController().navigate(R.id.action_navigation_search_to_searchFilterFragment)
        }
        view.findViewById<ConstraintLayout>(R.id.constraint_layout).setOnClickListener {
            activity!!.bottom_nav_view.visibility = View.INVISIBLE
            view.findNavController().navigate(R.id.action_navigation_search_to_bookDetails)
        }
    }

}
