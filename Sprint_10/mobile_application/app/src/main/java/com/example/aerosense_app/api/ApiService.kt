package com.example.aerosense_app.api

import com.example.aerosense_app.HomeData
import com.example.aerosense_app.LoginRequest
import com.example.aerosense_app.LoginResponse
import com.example.aerosense_app.RegisterRequest
import com.example.aerosense_app.RegisterResponse
import com.example.aerosense_app.SettingsRequest
import com.example.aerosense_app.SettingsResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @GET("/api/home")
    fun getAirQualityData(@Header("X-Access-Token") token: String): Call<HomeData>

    @POST("/api/register")
    fun registerUser(@Body request: RegisterRequest): Single<RegisterResponse>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
    @GET("/api/settings")
    fun getUserSettings(): Call<SettingsRequest>

    @POST("updateUserSettings")
    fun updateUserSettings(@Body settings: SettingsRequest): Call<SettingsResponse>

}
