package com.gsc.silverwalk.data.history

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gsc.silverwalk.ui.history.HistoryForm
import com.gsc.silverwalk.data.Result

class HistoryDataSource {

    fun getLocationStatistics(location: String, result: (Result<HistoryForm>) -> Unit) {

        Firebase.firestore
            .collection("location")
            .whereEqualTo("name", location)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    it.documents[0].reference
                        .collection("done_users")
                        .get()
                        .addOnSuccessListener {
                            var totalEasyCount = 0
                            var totalModerateCount = 0
                            var totalHardCount = 0
                            var totalMenCount = 0
                            var totalWomenCount = 0
                            var total50Count = 0
                            var total60Count = 0
                            var total70Count = 0
                            var count = 0

                            for (result in it) {
                                val age = result.get("age") as Long
                                val level = result.get("level") as Long
                                val gender = result.get("gender").toString()

                                if (age < 60L) {
                                    ++total50Count
                                } else if (age < 70L) {
                                    ++total60Count
                                } else {
                                    ++total70Count
                                }

                                if (level == 0L) {
                                    ++totalEasyCount
                                } else if (level == 1L) {
                                    ++totalModerateCount
                                } else {
                                    ++totalHardCount
                                }

                                if (gender == "men") {
                                    ++totalMenCount
                                } else {
                                    ++totalWomenCount
                                }

                                ++count
                            }

                            result(
                                Result.Success(
                                    HistoryForm(
                                        totalEasyCount,
                                        totalModerateCount,
                                        totalHardCount,
                                        totalMenCount,
                                        totalWomenCount,
                                        total50Count,
                                        total60Count,
                                        total70Count,
                                        count
                                    )
                                )
                            )
                        }
                        .addOnFailureListener {
                            result(Result.Error(it))
                        }
                }
            }
    }
}