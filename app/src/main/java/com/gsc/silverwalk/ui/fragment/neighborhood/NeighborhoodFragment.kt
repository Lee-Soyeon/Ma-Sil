package com.gsc.silverwalk.ui.fragment.neighborhood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.fragment_neighborhood.*
import kotlinx.android.synthetic.main.linearlayout_neighborhood_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class NeighborhoodFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater
            .inflate(R.layout.fragment_neighborhood, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Need To Implement Infinite Scroll

        addNeighborhood()
    }

    fun addNeighborhood() {

        Firebase.firestore
            .collection("share")
            .get()
            .addOnSuccessListener {
                if (isVisible) {
                    for (result in it) {
                        val neighborhoodItem = layoutInflater.inflate(
                            R.layout.linearlayout_neighborhood_item,
                            neighborhood_root_linearlayout,
                            false
                        )

                        neighborhoodItem.neighborhood_item_image_text.setText(
                            result.data.get("caption").toString()
                        )

                        neighborhoodItem.neighborhood_item_user_text.setText(
                            result.data.get("user_name").toString()
                        )

                        val timestamp = result.data.get("time") as Timestamp
                        val timeDate = timestamp.toDate()
                        neighborhoodItem.neighborhood_item_time_text.setText(
                            SimpleDateFormat("HH:mm a", Locale.US).format(timeDate)
                        )

                        FirebaseStorage.getInstance()
                            .getReferenceFromUrl(result.data.get("user_thumbnail_path").toString())
                            .downloadUrl
                            .addOnSuccessListener {
                                if (isVisible) {
                                    Glide.with(this).load(it).into(
                                        neighborhoodItem.neighborhood_item_user_image
                                    )
                                }
                            }
                            .addOnFailureListener {

                            }

                        val imagePaths =
                            result.data.get("image_paths") as List<String>
                        val iter = imagePaths.iterator()
                        while (iter.hasNext()) {
                            FirebaseStorage.getInstance()
                                .getReferenceFromUrl(iter.next())
                                .downloadUrl
                                .addOnSuccessListener {
                                    if (isVisible) {
                                        val neighborhoodImageItem = layoutInflater.inflate(
                                            R.layout.cardview_neighborhood_item_image,
                                            neighborhoodItem.neighborhood_item_image_linearlayout,
                                            false
                                        )

                                        Glide.with(this).load(it).into(
                                            neighborhoodImageItem
                                                .findViewById(R.id.neighborhood_item_image)
                                        )

                                        neighborhoodItem.neighborhood_item_image_linearlayout
                                            .addView(neighborhoodImageItem)
                                    }
                                }
                                .addOnFailureListener {

                                }
                        }

                        neighborhood_root_linearlayout.addView(neighborhoodItem)
                    }
                }
            }
            .addOnFailureListener {

            }
    }
}