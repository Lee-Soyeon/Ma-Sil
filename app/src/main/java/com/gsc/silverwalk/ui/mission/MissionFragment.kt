package com.gsc.silverwalk.ui.mission

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gsc.silverwalk.MissionActivity
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.fragment_mission.*

class MissionFragment : Fragment() {

    // Current Mission Parameter
    private var currentMissionIndex = 0
    private val MissionLevelStringArray : Array<Int> = arrayOf(
        R.string.Easy, R.string.Moderate, R.string.Hard
    )

    // Dialog Parameter
    private lateinit var dialogBuilder : AlertDialog.Builder
    private lateinit var dialogView : View
    private lateinit var dialogButton : Button
    private lateinit var dialogObject : AlertDialog

    // Today Mission Data
    private var missionDataList : MutableList<Map<String,Any>> = mutableListOf()
    private var LocationImageDataList : MutableList<Map<String,Any>> = mutableListOf()

    // UID
    private val UID = "X5NztOqVUgGPT84WmSiK"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_mission, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Init Dialog Configuration
        dialogBuilder = AlertDialog.Builder(context)
        dialogView = layoutInflater.inflate(R.layout.dialog_mission_start, null)
        dialogBuilder.setView(dialogView)
        dialogButton = dialogView.findViewById<Button>(R.id.mission_cancle_dialog_cnacle_button)
        dialogButton.setOnClickListener(View.OnClickListener {
            // Activity Show
            val nextIntent = Intent(context, MissionActivity::class.java)
            startActivity(nextIntent)
            dialogObject.dismiss()
        })
        dialogObject = dialogBuilder.create()
        dialogObject.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // start Mission Button
        mission_select_start_button.setOnClickListener(View.OnClickListener {
            dialogObject.show()
        })

        // prev Mission Button
        mission_select_left_button.setOnClickListener(View.OnClickListener {
            currentMissionIndex =
                if (currentMissionIndex == 0) (missionDataList.size - 1)
                else currentMissionIndex - 1

            setMissionParameterByCurrentMissionIndex()
        })

        // next Mission Button
        mission_select_right_button.setOnClickListener(View.OnClickListener {
            currentMissionIndex =
                if (currentMissionIndex == (missionDataList.size - 1)) 0
                else currentMissionIndex + 1

            setMissionParameterByCurrentMissionIndex()
        })

        findTodayMissionFromFireBase()
    }

    fun findTodayMissionFromFireBase()
    {
        // load Firebase Data
        val userTodayMissionDocs = Firebase.firestore
            .collection("users").document(UID)
            .collection("today_mission").get()

        userTodayMissionDocs
            .addOnSuccessListener { result ->
                for(document in result){
                    missionDataList.add(document.data)
                }

                // Set TodayMission
                setMissionParameterByCurrentMissionIndex()

                // Disable ProgressBar
                mission_progressBar.visibility = View.INVISIBLE
                mission_linearlayout.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.d("FireStore Error", "get failed with ", exception)
            }
    }

    fun setMissionParameterByCurrentMissionIndex(){

        missionImageProgressVisiblity(View.VISIBLE,View.INVISIBLE)

        // mission_index_text
        mission_info_index_text.setText((currentMissionIndex + 1).toString() + "/5")

        // Set mission string
        var mission_string =
            getString(R.string.mission_display_1) +
            getString(R.string.mission_display_2) +
            getString(R.string.mission_display_3)
        mission_string = mission_string.replace("(%location)",
            missionDataList[currentMissionIndex].get("location").toString())
        mission_string = mission_string.replace("(%time)",
            ((missionDataList[currentMissionIndex].get("time") as Long) / 60).toString())
        mission_string = mission_string.replace("(%type)",
            missionDataList[currentMissionIndex].get("type").toString())

        // Update Display Text
        val splitString = mission_string.split("(%tab)")
        mission_display_1_text.setText(splitString[0])
        mission_display_2_text.setText(splitString[1])
        mission_display_3_text.setText(splitString[2])

        mission_level_text.setText(MissionLevelStringArray[
                (missionDataList[currentMissionIndex].get("level") as Long).toInt()])

        // Set BackGround Image
        Firebase.firestore.collection("location")
            .whereEqualTo("name",
                missionDataList[currentMissionIndex].get("location").toString())
            .get()
            .addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty){
                    querySnapshot.documents[0].reference.collection("images").get()
                        .addOnSuccessListener { imagesSnapshot ->
                            LocationImageDataList.clear()
                            for(doc in imagesSnapshot){
                                LocationImageDataList.add(doc.data)
                            }

                            changeImage(0)
                        }
                        .addOnFailureListener { exception ->
                            Log.d("Fire Store Failed",
                                    "get failed with " , exception)
                        }
                }else{
                    // mission_background.setImageURI("DefaultImageURL")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("FireStore Failed", "get failed with ", exception)
            }
    }

    fun changeImage(index : Int) {
        FirebaseStorage.getInstance()
                .getReferenceFromUrl(
                        LocationImageDataList[index].get("image_path").toString())
                .downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).into(mission_background)
                }
                .addOnFailureListener { exception ->
                    Log.d("Fire Storage Failed","get failed with ", exception)
                }

        FirebaseStorage.getInstance()
                .getReferenceFromUrl(
                        LocationImageDataList[index].get("user_thumbnail_path").toString())
                .downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).into(mission_image_user_info_image)
                }
                .addOnFailureListener { exception ->
                    Log.d("Fire Storage Failed","get failed with ", exception)
                }

        mission_image_user_info_text.setText(
                LocationImageDataList[index].get("user_name").toString())

        Handler().postDelayed(
                {
                    missionImageProgressVisiblity(View.INVISIBLE,View.VISIBLE)
                }, 1000)
    }

    fun missionImageProgressVisiblity(progressVisiblity : Int, imageVisiblity : Int) {
        mission_image_progressBar.visibility = progressVisiblity
        mission_background.visibility = imageVisiblity
        mission_image_user_info_linearlayout.visibility = imageVisiblity
    }
}