package com.gsc.silverwalk.ui.mission

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
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
    private lateinit var missionLevelCardView : Array<CardView>

    // Dialog Parameter
    private lateinit var dialogBuilder : AlertDialog.Builder
    private lateinit var dialogView : View
    private lateinit var dialogButton : Button
    private lateinit var dialogObject : AlertDialog

    // Today Mission Data
    private var missionDataList : MutableList<Map<String,Any>> = mutableListOf()
    private var backGroundImageURL : List<String> = listOf()

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

        // MissionLevelCardView Init
        missionLevelCardView = arrayOf(
            mission_level1_cardview, mission_level2_cardview,
            mission_level3_cardview, mission_level4_cardview,
            mission_level5_cardview
        )

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

                setMissionParameterByCurrentMissionIndex()
            }
            .addOnFailureListener { exception ->
                Log.d("FireStore Error", "get failed with ", exception)
            }
    }

    fun setMissionParameterByCurrentMissionIndex(){

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
                    backGroundImageURL =
                        querySnapshot.documents.get(0).data?.get("image_paths") as List<String>

                    FirebaseStorage.getInstance().getReferenceFromUrl(backGroundImageURL[0])
                        .downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(this).load(uri).into(mission_background)
                        }
                        .addOnFailureListener { exception ->
                            Log.d("Fire Storage Failed","get failed with ", exception)
                        }
                }else{
                    // mission_background.setImageURI("DefaultImageURL")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("FireStore Failed", "get failed with ", exception)
            }
    }
}