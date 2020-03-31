package pablo.myexample.booksmawt.searchfilter

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import pablo.myexample.booksmawt.Communicator
import pablo.myexample.booksmawt.Filter
import pablo.myexample.booksmawt.R
import pablo.myexample.booksmawt.databinding.SearchFilterFragmentBinding


class SearchFilterFragment : Fragment() {

    private lateinit var formalAddress: String
    private lateinit var model: Communicator
    private lateinit var binding: SearchFilterFragmentBinding
    private val cityArray = arrayListOf(
        "Santa Fe Springs",
        "Los Angeles",
        "San Diego",
        "San Jose",
        "San Francisco",
        "Fresno",
        "Sacramento",
        "Long Beach",
        "Oakland",
        "Bakersfield",
        "Anaheim",
        "Santa Ana",
        "Riverside",
        "Stockton",
        "Chula Vista",
        "Irvine",
        "Fremont",
        "San Bernardino",
        "Modesto",
        "Fontana",
        "Oxnard",
        "Moreno Valley",
        "Huntington Beach",
        "Glendale",
        "Santa Clarita",
        "Garden Grove",
        "Oceanside",
        "Rancho Cucamonga",
        "Santa Rosa",
        "Ontario",
        "Lancaster",
        "Elk Grove",
        "Corona",
        "Palmdale",
        "Salinas",
        "Pomona",
        "Hayward",
        "Escondido",
        "Torrance",
        "Sunnyvale",
        "Orange",
        "Fullerton",
        "Pasadena",
        "Thousand Oaks",
        "Visalia",
        "Simi Valley",
        "Concord",
        "Roseville",
        "Victorville",
        "Santa Clara",
        "Vallejo",
        "Berkeley",
        "El Monte",
        "Downey",
        "Costa Mesa",
        "Inglewood",
        "Carlsbad",
        "San Buenaventura (Ventura)",
        "Fairfield",
        "West Covina",
        "Murrieta",
        "Richmond",
        "Norwalk",
        "Antioch",
        "Temecula",
        "Burbank",
        "Daly City",
        "Rialto",
        "Santa Maria",
        "El Cajon",
        "San Mateo",
        "Clovis",
        "Compton",
        "Jurupa Valley",
        "Vista",
        "South Gate",
        "Mission Viejo",
        "Vacaville",
        "Carson",
        "Hesperia",
        "Santa Monica",
        "Westminster",
        "Redding",
        "Santa Barbara",
        "Chico",
        "Newport Beach",
        "San Leandro",
        "San Marcos",
        "Whittier",
        "Hawthorne",
        "Citrus Heights",
        "Tracy",
        "Alhambra",
        "Livermore",
        "Buena Park",
        "Menifee",
        "Hemet",
        "Lakewood",
        "Merced",
        "Chino",
        "Indio",
        "Redwood City",
        "Lake Forest",
        "Napa",
        "Tustin",
        "Bellflower",
        "Mountain View",
        "Chino Hills",
        "Baldwin Park",
        "Alameda",
        "Upland",
        "San Ramon",
        "Folsom",
        "Pleasanton",
        "Union City",
        "Perris",
        "Manteca",
        "Lynwood",
        "Apple Valley",
        "Redlands",
        "Turlock",
        "Milpitas",
        "Redondo Beach",
        "Rancho Cordova",
        "Yorba Linda",
        "Palo Alto",
        "Davis",
        "Camarillo",
        "Walnut Creek",
        "Pittsburg",
        "South San Francisco",
        "Yuba City",
        "San Clemente",
        "Laguna Niguel",
        "Pico Rivera",
        "Montebello",
        "Lodi",
        "Madera",
        "Santa Cruz",
        "La Habra",
        "Encinitas",
        "Monterey Park",
        "Tulare",
        "Cupertino",
        "Gardena",
        "National City",
        "Rocklin",
        "Petaluma",
        "Huntington Park",
        "San Rafael",
        "La Mesa",
        "Arcadia",
        "Fountain Valley",
        "Diamond Bar",
        "Woodland",
        "Santee",
        "Lake Elsinore",
        "Porterville",
        "Paramount",
        "Eastvale",
        "Rosemead",
        "Hanford",
        "Highland",
        "Brentwood",
        "Novato",
        "Colton",
        "Cathedral City",
        "Delano",
        "Yucaipa",
        "Watsonville",
        "Placentia",
        "Glendora",
        "Gilroy",
        "Palm Desert",
        "Cerritos",
        "West Sacramento",
        "Aliso Viejo",
        "Poway",
        "La Mirada",
        "Rancho Santa Margarita",
        "Cypress",
        "Dublin",
        "Covina",
        "Azusa",
        "Palm Springs",
        "San Luis Obispo",
        "Ceres",
        "San Jacinto",
        "Lincoln",
        "Newark",
        "Lompoc",
        "El Centro",
        "Danville",
        "Bell Gardens",
        "Coachella",
        "Rancho Palos Verdes",
        "San Bruno",
        "Rohnert Park",
        "Brea",
        "La Puente",
        "Campbell",
        "San Gabriel",
        "Beaumont",
        "Morgan Hill",
        "Culver City",
        "Calexico",
        "Stanton",
        "La Quinta",
        "Pacifica",
        "Montclair",
        "Oakley",
        "Monrovia",
        "Los Banos",
        "Martinez"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.search_filter_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formalAddress = ""
        model = ViewModelProvider(activity!!).get(Communicator::class.java)
        binding.apply {
            searchFilterSeekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
                minimum.text = minValue.toString()
                maximum.text = maxValue.toString()
            }
            backArrowFilter.setOnClickListener {
                backToSearch()
            }
            cancelButton.setOnClickListener {
                backToSearch()
            }
            applyButton.setOnClickListener {
                checkLocation()
            }
            verifyLocationButton.setOnClickListener {
                verifyLocation()
            }
            val adapter = ArrayAdapter(context, android.R.layout.select_dialog_item, cityArray)
            locationEt.setAdapter(adapter)
        }
    }

    private fun verifyLocation() {
        val location = binding.locationEt.text.toString()
        binding.locationEt.setText("verifying...")
        when {
            location.isNotEmpty() -> {
                val url =
                    "https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&key=" + R.string.api_key
                val queue = Volley.newRequestQueue(context)
                val stringRequest = JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener<JSONObject?> { response ->
                        try {
                            val jsonArray = response!!.getJSONArray("results")
                            val jsonObject = jsonArray.getJSONObject(0)
                            formalAddress = jsonObject.getString("formatted_address")
                            val newFA = formalAddress.replace(",", "").split(" ")
                            val theLength: Int = newFA.size
                            if (newFA[theLength - 1] == "USA" && newFA[theLength - 2].length == 2) {
                                binding.locationEt.setText(formalAddress)
                                binding.okFlag.visibility = View.VISIBLE
                            } else {
                                binding.locationEt.setText("Cannot Verify")
                                binding.okFlag.visibility = View.INVISIBLE
                                formalAddress = ""
                            }
                        } catch (e: Exception) {
                            binding.locationEt.setText("Cannot Verify")
                            binding.okFlag.visibility = View.INVISIBLE
                            formalAddress = ""
                        }
                    },
                    Response.ErrorListener {
                        binding.locationEt.setText("Cannot Verify")
                        binding.okFlag.visibility = View.INVISIBLE
                        formalAddress = ""
                    })
                queue.add(stringRequest)
            }
        }
    }

    private fun checkLocation() {
        when {
            formalAddress != "" -> {
                collectDataAndPassObj()
            }
            else -> {
                Toast.makeText(context, "Cannot Verify Location", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun collectDataAndPassObj() {
        val min: String = binding.minimum.text.toString()
        val max: String = binding.maximum.text.toString()
        val filterObj = Filter(formalAddress, min, max)
        model.passFilterObj(filterObj)
        backToSearch()
    }

    private fun backToSearch() {
        activity!!.onBackPressed()
        //view!!.findNavController().navigate(R.id.action_searchFilterFragment_to_navigation_search)
    }

}
