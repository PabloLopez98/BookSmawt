package pablo.myexample.booksmawt.searchfilter

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_base.*

import pablo.myexample.booksmawt.R

class SearchFilterFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFilterFragment()
    }

    private lateinit var viewModel: SearchFilterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_filter_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchFilterViewModel::class.java)
        // TODO: Use the ViewModel
    }

    //in case user uses backpress key instead of buttons(apply or cancel), bottom nav in activity becomes visible
    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.bottom_nav_view.visibility = View.VISIBLE
    }
}
