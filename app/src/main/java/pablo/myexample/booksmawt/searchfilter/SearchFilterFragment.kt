package pablo.myexample.booksmawt.searchfilter

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import kotlinx.android.synthetic.main.activity_base.*

import pablo.myexample.booksmawt.R
import java.util.*
import kotlin.collections.ArrayList

class SearchFilterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.search_filter_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rangSB = view.findViewById<CrystalRangeSeekbar>(R.id.search_filter_seekbar)
        val minTv = view.findViewById<TextView>(R.id.minimum)
        val maxTv = view.findViewById<TextView>(R.id.maximum)
        rangSB.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            minTv.text = minValue.toString()
            maxTv.text = maxValue.toString()
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    //in case user uses backpress key instead of buttons(apply or cancel),
    //bottom nav in activity becomes visible
    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.bottom_nav_view.visibility = View.VISIBLE
    }
}
