package com.gsc.silverwalk.retrofit

import android.content.ContentValues.TAG
import android.util.Log
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        private val retrofitClient: RetrofitClient = RetrofitClient()

        fun getInstance(): RetrofitClient {
            return retrofitClient
        }
    }

    fun buildRetrofit(): RetrofitService {
        val retrofit: Retrofit? = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service: RetrofitService = retrofit!!.create(RetrofitService :: class.java)
        return service
    }

    fun getCurrentWeather(lat : String, lon : String,
                          success: (JSONObject) -> Unit,
                          error: (Call<JsonObject>,Throwable) -> Unit) {

        var res: Call<JsonObject> = RetrofitClient
                .getInstance()
                .buildRetrofit()
                .getCurrentWeather(lat, lon, "98f72a185ab70464886924c037e07ed0")

        val result = res.enqueue(object: Callback<JsonObject> {

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                error(call,t)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                success(JSONObject(response.body().toString()))
            }
        })

        return result
    }
}