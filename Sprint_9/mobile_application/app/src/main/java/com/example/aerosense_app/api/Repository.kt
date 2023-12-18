package com.example.aerosense_app.api

import android.util.Log
import com.example.aerosense_app.HomeData
import com.example.aerosense_app.LoginRequest
import com.example.aerosense_app.LoginResponse
import com.example.aerosense_app.RegisterRequest
import com.example.aerosense_app.RegisterResponse
import com.example.aerosense_app.SettingsRequest
import com.example.aerosense_app.SettingsResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Github copilot used while writing this code


class Repository(private val apiService: ApiService) {

    fun getAirQualityData(token: String): Call<HomeData> {
        return apiService.getAirQualityData(token)
    }

    fun getUserSettings(): Call<SettingsRequest> {
        return apiService.getUserSettings()
    }

    fun updateUserSettings(requestBody: SettingsRequest): Call<SettingsResponse> {
        return apiService.updateUserSettings(requestBody)
    }

    fun fetchAirQualityData(token: String, onSuccess: (HomeData?) -> Unit, onError: (String) -> Unit) {
        val call = getAirQualityData(token)
        call.enqueue(object : Callback<HomeData> {
            override fun onResponse(call: Call<HomeData>, response: Response<HomeData>) {
                Log.d("RepositoryResponse", response.toString())
                if (response.isSuccessful) {
                    onSuccess(response.body())
                    Log.d("Repository", "success: ${response.body()}")
                } else {
                    Log.d("Repository", "onResponse: ${response.code()}")
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HomeData>, t: Throwable) {
                Log.d("Repository", "onResponse: ${t.message}")
                onError("Failure: ${t.message}")
            }
        })
    }

    fun registerData(data: RegisterRequest): Single<RegisterResponse> {
        val request = RegisterRequest(firebaseToken = data.firebaseToken, modelNumber = data.modelNumber)
        return apiService.registerUser(request)
    }

    fun loginUser(email: String, password: String): Call<LoginResponse> {
        val loginRequest = LoginRequest(email, password)
        return apiService.login(loginRequest)
    }

}
