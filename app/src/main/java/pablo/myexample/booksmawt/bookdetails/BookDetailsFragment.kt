package pablo.myexample.booksmawt.bookdetails

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView

import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageClickListener
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_base.*

import pablo.myexample.booksmawt.R

class BookDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.book_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val images = intArrayOf(R.drawable.demo_cover, R.drawable.demo_cover_two)
        val imageListener = ImageListener { position, imageView -> imageView.setImageResource(images[position]) }
        val carouselView = view.findViewById<CarouselView>(R.id.book_details_carousel)
        carouselView.setImageListener(imageListener)
        carouselView.pageCount = images.size
        carouselView.setImageClickListener(ImageClickListener { position ->

            val dialog = Dialog(context)
            val theView: View = layoutInflater.inflate(R.layout.image_layout, null)
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(theView)
            val imageViewDisplayArea = theView.findViewById<ImageView>(R.id.image_display)
            val imageClicked = images[position]
            imageViewDisplayArea.setImageResource(imageClicked)
            dialog.show()

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.bottom_nav_view.visibility = View.VISIBLE
    }

}
