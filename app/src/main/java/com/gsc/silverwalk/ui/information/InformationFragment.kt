package com.gsc.silverwalk.ui.information

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.gsc.silverwalk.R
import com.gsc.silverwalk.userinfo.UserInfo
import kotlinx.android.synthetic.main.fragment_information.*

class InformationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_information, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        FirebaseStorage.getInstance()
            .getReferenceFromUrl(UserInfo.getInstance().userThumbnailPath)
            .downloadUrl
            .addOnSuccessListener {
                if(isVisible) {
                    Glide.with(this).load(it).into(information_user_image)
                }
            }
            .addOnFailureListener {

            }

        information_user_text.setText(UserInfo.getInstance().userName)
    }
}