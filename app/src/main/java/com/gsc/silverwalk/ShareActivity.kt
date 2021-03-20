package com.gsc.silverwalk

import android.app.ActionBar
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.view.iterator
import androidx.fragment.app.FragmentManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gsc.silverwalk.ui.mission.MissionStartDialog
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.framelayout_history_camera_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.zip.Inflater
import kotlin.collections.ArrayList

class ShareActivity : AppCompatActivity() {

    var location = ""
    lateinit var imagePath : ArrayList<String>

    private val dialogObject : WaitDialog = WaitDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        supportActionBar?.hide()

        // cancel button
        share_activity_cancel_button.setOnClickListener(View.OnClickListener {
            finish()
        })

        // done button
        share_activity_done_button.setOnClickListener(View.OnClickListener {
            val selectList: MutableList<String> = mutableListOf()
            val iterator = share_activity_photos_gridlayout.iterator()
            var index = 0
            while(iterator.hasNext()){
                val item = iterator.next()
                if(item.history_camera_check_image.visibility == View.VISIBLE){
                    selectList.add(imagePath[index])
                }
                ++index
            }

            postShareData(share_activity_edittext.text.toString(), selectList)
        })

        // intent
        location =
                if(intent.hasExtra("location"))
                    (intent.getStringExtra("location")!!) else location

        // set ui data
        setRouteImage()
        addMyPhotos()
    }

    fun setRouteImage() {
        // Something To Do
    }

    fun addMyPhotos(){
        imagePath =
                if(intent.hasExtra("imagePath"))
                    (intent.getSerializableExtra("imagePath") as ArrayList<String>)
                else arrayListOf<String>()

        val it = imagePath.iterator()
        while(it.hasNext()){
            val newImage = layoutInflater.inflate(
                    R.layout.framelayout_history_camera_view,
                    share_activity_photos_gridlayout, false)

            share_activity_photos_gridlayout.addView(newImage)

            newImage.setOnClickListener(View.OnClickListener {
                if(it.history_camera_check_image.visibility == View.INVISIBLE){
                    it.history_camera_blur_image.visibility = View.VISIBLE
                    it.history_camera_check_image.visibility = View.VISIBLE
                }else{
                    it.history_camera_blur_image.visibility = View.INVISIBLE
                    it.history_camera_check_image.visibility = View.INVISIBLE
                }
            })

            val layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED), GridLayout.spec(GridLayout.UNDEFINED))
            layoutParams.width = convertDpToPixel(120.0f).toInt()
            layoutParams.height = convertDpToPixel(120.0f).toInt()

            newImage.layoutParams = layoutParams

            val path = it.next()
            val file = File(path)
            if(file.exists()){
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                newImage.history_camera_main_image.setImageBitmap(bitmap)
            }
        }
    }

    fun postShareData(caption: String, imageList : List<String>){

        dialogObject.show(supportFragmentManager, "wait dialog")

        // storage bitmap update
        val documentId = "Park Misook" + Date().toString()
        val imagePathList : MutableList<String> = mutableListOf()

        for(i in 0..imageList.size - 1){
            val pathString =
                "neighborhood/" + documentId + "/" + imageList[i].replace("/","")

            val storeRef = FirebaseStorage.getInstance()
                    .reference
                    .child(pathString)

            val stream = FileInputStream(File(imageList[i]))
            val uploadTask = storeRef.putStream(stream)
            uploadTask
                    .addOnSuccessListener {
                        Log.d("TAG","success FireStorage upload")
                        dialogObject.dismiss()
                        finish()
                    }.addOnFailureListener{
                        dialogObject.dismiss()
                        Log.d("TAG","fail FireStorage upload")
                    }

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
                    if(imagePathList.isEmpty()){
                        dialogObject.dismiss()
                        finish()
                    }
                    Log.d("TAG","success FireStore upload")
                }
                .addOnFailureListener {
                    if(imagePathList.isEmpty()){
                        dialogObject.dismiss()
                    }
                    Log.d("TAG","fail FireStore upload")
                }
    }

    fun convertDpToPixel(dp : Float) : Float {
        return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertPixelsToDp(px : Float) : Float {
        return px / resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
    }
}