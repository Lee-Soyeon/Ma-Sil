package com.gsc.silverwalk.data.mission

import android.location.Location
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.JsonObject
import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.location.LocationClient
import com.gsc.silverwalk.retrofit.RetrofitClient
import com.gsc.silverwalk.ui.fragment.mission.MissionImage
import com.gsc.silverwalk.ui.fragment.mission.MissionWeather
import com.gsc.silverwalk.userinfo.UserInfo
import org.json.JSONObject
import retrofit2.Call
import java.lang.Exception

class MissionDataSource {

    fun initMissionDataList(result: (Result<QuerySnapshot>) -> Unit) {
        val userTodayMissionDocs = Firebase.firestore
            .collection("users").document(UserInfo.getInstance().uid)
            .collection("today_mission").get()

        userTodayMissionDocs
            .addOnSuccessListener {
                result(Result.Success(it))
            }
            .addOnFailureListener {
                result(Result.Error(it))
            }
    }

    fun getMissionLocationInfo(location: String, result: (Result<MissionImage>) -> Unit) {

        var locationImageUrl: String? = null
        var locationThumbnailUri: String? = null
        var locationUserName: String? = null

        // Set BackGround Image
        Firebase.firestore.collection("location")
            .whereEqualTo(
                "name",
                location
            )
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    querySnapshot.documents[0].reference.collection("images").get()
                        .addOnSuccessListener { imagesSnapshot ->
                            if (!imagesSnapshot.isEmpty) {
                                val imageData =
                                    imagesSnapshot.documents.get(0).data

                                FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(
                                        imageData?.get("image_path").toString()
                                    )
                                    .downloadUrl.addOnSuccessListener { uri ->
                                        locationImageUrl = uri.toString()
                                        if (locationThumbnailUri != null) {
                                            result(
                                                Result.Success(
                                                    MissionImage(
                                                        locationImageUrl,
                                                        locationThumbnailUri,
                                                        locationUserName
                                                    )
                                                )
                                            )
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        result(Result.Error(exception))
                                    }

                                FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(
                                        imageData?.get("user_thumbnail_path").toString()
                                    )
                                    .downloadUrl.addOnSuccessListener { uri ->
                                        locationThumbnailUri = uri.toString()
                                        if (locationImageUrl != null) {
                                            result(
                                                Result.Success(
                                                    MissionImage(
                                                        locationImageUrl,
                                                        locationThumbnailUri,
                                                        locationUserName
                                                    )
                                                )
                                            )
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        result(Result.Error(exception))
                                    }

                                locationUserName = imageData?.get("user_name").toString()
                            }
                        }
                        .addOnFailureListener { exception ->
                            result(Result.Error(exception))
                        }
                }
            }
            .addOnFailureListener { exception ->
                result(Result.Error(exception))
            }
    }

    fun getWeatherInfo(result: (Result<MissionWeather>) -> Unit) {
        // Location Data
        LocationClient.getInstance().getLastLocation { location: Location ->
            RetrofitClient.getInstance().getCurrentWeather(
                location.latitude.toString(),
                location.longitude.toString(),
                { weatherJSON: JSONObject ->
                    val mainJsonArray = weatherJSON.getJSONObject("main")
                    val weatherJsonArray = JSONObject(
                        weatherJSON.getJSONArray("weather")[0].toString()
                    )

                    result(
                        Result.Success(
                            MissionWeather(
                                ((mainJsonArray.getString("temp")
                                    .toFloat() / 10.0f).toInt().toString() + "ºF"),
                                weatherJsonArray.getString("main")
                            )
                        )
                    )
                },
                { call: Call<JsonObject>, t: Throwable ->
                    result(Result.Error(Exception()))
                })
        }
    }
}