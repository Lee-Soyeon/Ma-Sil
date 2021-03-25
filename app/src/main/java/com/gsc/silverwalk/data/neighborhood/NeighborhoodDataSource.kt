package com.gsc.silverwalk.data.neighborhood

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.ui.fragment.neighborhood.NeighborhoodForm
import com.gsc.silverwalk.ui.fragment.neighborhood.NeighborhoodItemInfo
import java.text.SimpleDateFormat
import java.util.*

class NeighborhoodDataSource {

    fun findAllNeighborhood(result: (Result<NeighborhoodForm>) -> Unit) {
        val itemList: MutableList<NeighborhoodItemInfo> = mutableListOf()

        Firebase.firestore
            .collection("share")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val caption = document.data.get("caption").toString()
                    val user_name = document.data.get("user_name").toString()

                    val timestamp = document.data.get("time") as Timestamp
                    val timeDate = timestamp.toDate()
                    val timeString =
                        SimpleDateFormat("HH:mm a", Locale.US).format(timeDate)

                    FirebaseStorage.getInstance()
                        .getReferenceFromUrl(document.data.get("user_thumbnail_path").toString())
                        .downloadUrl
                        .addOnSuccessListener { thumbnailUri ->
                            val imagePaths =
                                document.data.get("image_paths") as List<String>
                            val itemImagePaths: MutableList<String> = mutableListOf()
                            if (imagePaths.size == 0) {
                                itemList.add(
                                    NeighborhoodItemInfo(
                                        caption,
                                        user_name,
                                        thumbnailUri.toString(),
                                        timeString,
                                        itemImagePaths
                                    )
                                )

                                if (itemList.size == it.documents.size) {
                                    result(Result.Success(NeighborhoodForm(itemList)))
                                }
                            } else {
                                val iter = imagePaths.iterator()
                                while (iter.hasNext()) {
                                    FirebaseStorage.getInstance()
                                        .getReferenceFromUrl(iter.next())
                                        .downloadUrl
                                        .addOnSuccessListener { imageUri ->
                                            itemImagePaths.add(imageUri.toString())
                                            if (itemImagePaths.size == imagePaths.size) {
                                                itemList.add(
                                                    NeighborhoodItemInfo(
                                                        caption,
                                                        user_name,
                                                        thumbnailUri.toString(),
                                                        timeString,
                                                        itemImagePaths
                                                    )
                                                )

                                                if (itemList.size == it.documents.size) {
                                                    result(Result.Success(NeighborhoodForm(itemList)))
                                                }
                                            }
                                        }
                                        .addOnFailureListener {
                                            result(Result.Error(it))
                                        }
                                }
                            }
                        }
                        .addOnFailureListener {
                            result(Result.Error(it))
                        }
                }
            }
            .addOnFailureListener {
                result(Result.Error(it))
            }
    }

}