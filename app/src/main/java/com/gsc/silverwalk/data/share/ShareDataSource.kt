package com.gsc.silverwalk.data.share

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileInputStream
import java.util.*

class ShareDataSource {

    fun postShareData(caption: String, location: String, photoPathList: List<String>, done: () -> Unit){
        // storage bitmap update
        val documentId = "Park Misook" + Date().toString()
        val imagePathList : MutableList<String> = mutableListOf()

        val iter = photoPathList.iterator()
        while(iter.hasNext()){
            val item = iter.next()
            val pathString =
                "neighborhood/" + documentId + "/" + item.replace("/","")

            val storeRef = FirebaseStorage.getInstance()
                .reference
                .child(pathString)

            val stream = FileInputStream(File(item))
            val uploadTask = storeRef.putStream(stream)

            imagePathList.add("gs://sliverwork.appspot.com/" + pathString)
        }

        val data = hashMapOf(
            "caption" to caption,
            "location" to location,
            "image_paths" to imagePathList,
            "user_name" to "Park Misook",
            "user_thumbnail_path" to "gs://sliverwork.appspot.com/users/X5NztOqVUgGPT84WmSiK/thumbnail.PNG",
            "time" to Timestamp(Date())
        )

        Firebase.firestore
            .collection("share")
            .document(documentId)
            .set(data)
            .addOnSuccessListener {
                done()
            }
            .addOnFailureListener {
                done()
            }
    }
}