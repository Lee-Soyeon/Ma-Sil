package com.gsc.silverwalk.ui.fragment.neighborhood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.fragment_neighborhood.*
import kotlinx.android.synthetic.main.linearlayout_neighborhood_item.view.*

class NeighborhoodFragment : Fragment() {

    private lateinit var neighborhoodViewModel: NeighborhoodViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_neighborhood, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        neighborhoodViewModel = ViewModelProvider(this, NeighborhoodViewModelFactory())
            .get(NeighborhoodViewModel::class.java)

        neighborhoodViewModel.neighborhoodForm.observe(viewLifecycleOwner, Observer {
            val neighborhoodForm = it ?: return@Observer

            val iter = neighborhoodForm.items?.iterator()
            while(iter!!.hasNext()){
                val item = iter.next()

                val neighborhoodItem = layoutInflater.inflate(
                    R.layout.linearlayout_neighborhood_item,
                    neighborhood_root_linearlayout,
                    false
                )

                neighborhoodItem.neighborhood_item_image_text.setText(item.caption)
                neighborhoodItem.neighborhood_item_user_text.setText(item.user_name)
                neighborhoodItem.neighborhood_item_time_text.setText(item.time)
                Glide.with(this).load(item.user_thumbnail).into(
                    neighborhoodItem.neighborhood_item_user_image)

                val imageIter = item.imagePaths?.iterator()
                while(imageIter!!.hasNext()){
                    val neighborhoodImageItem = layoutInflater.inflate(
                        R.layout.cardview_neighborhood_item_image,
                        neighborhoodItem.neighborhood_item_image_linearlayout,
                        false
                    )

                    Glide.with(this).load(imageIter.next()).into(
                        neighborhoodImageItem
                            .findViewById(R.id.neighborhood_item_image))

                    neighborhoodItem.neighborhood_item_image_linearlayout
                        .addView(neighborhoodImageItem)
                }

                neighborhood_root_linearlayout.addView(neighborhoodItem)
            }
        })
    }
}