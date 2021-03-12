package com.gsc.silverwalk.ui.neighborhood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.fragment_achievement.*
import kotlinx.android.synthetic.main.fragment_neighborhood.*

class NeighborhoodFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_neighborhood, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Need To Implement Infinite Scroll

        addNeighborhood()
    }

    fun addNeighborhood() {
        val neighborhoodItem = layoutInflater.inflate(
            R.layout.linearlayout_neighborhood_item,
            neighborhood_root_linearlayout,
            false)

        /*
        // while(iterator.hasNext())
        // while(iterator.hasNext())
        var routeImage = ImageView(context)
        routeImage.setImageURI(0)
        neighborhoodItem.findViewById<LinearLayout>(
            R.id.neighborhood_item_image_linearlayout).addView(routeImage)

        neighborhoodItem.findViewById<ImageView>(
            R.id.neighborhood_item_user_image).setImageURI(0)
        neighborhoodItem.findViewById<TextView>(
            R.id.neighborhood_item_user_text).setText("")

        neighborhoodItem.findViewById<TextView>(
            R.id.neighborhood_display_1_text).setText("")
        */

        neighborhood_root_linearlayout.addView(neighborhoodItem)
    }
}