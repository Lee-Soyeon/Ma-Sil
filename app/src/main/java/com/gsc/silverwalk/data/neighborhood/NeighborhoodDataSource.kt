package com.gsc.silverwalk.data.neighborhood

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gsc.silverwalk.ui.fragment.neighborhood.NeighborhoodItemInfo
import java.text.SimpleDateFormat
import java.util.*

class NeighborhoodDataSource {

    fun findAllNeighborhood() {
        val itemList: MutableList<NeighborhoodItemInfo> = mutableListOf()

        Firebase.firestore
            .collection("share")
            .get()
            .addOnSuccessListener {
                for (result in it) {
                    val caption = result.data.get("caption").toString()
                    val user_name = result.data.get("user_name").toString()

                    val timestamp = result.data.get("time") as Timestamp
                    val timeDate = timestamp.toDate()
                    val timeString = SimpleDateFormat("HH:mm a", Locale.US).format(timeDate)

                    FirebaseStorage.getInstance()
                        .getReferenceFromUrl(result.data.get("user_thumbnail_path").toString())
                        .downloadUrl
                        .addOnSuccessListener {thumbnailUri ->
                            val imagePaths =
                                result.data.get("image_paths") as List<String>
                            val itemImagePaths: MutableList<String> = mutableListOf()
                            val iter = imagePaths.iterator()
                            while(iter.hasNext()){

                            }
                        }
                        .addOnFailureListener {

                        }
                }
            }
            .addOnFailureListener {

            }
    }

}