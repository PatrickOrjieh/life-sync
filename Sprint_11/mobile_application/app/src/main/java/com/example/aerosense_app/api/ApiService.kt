package com.example.aerosense_app.api

import com.example.aerosense_app.HistoryData
import com.example.aerosense_app.HomeData
import com.example.aerosense_app.LocationData
import com.example.aerosense_app.LoginRequest
import com.example.aerosense_app.LoginResponse
import com.example.aerosense_app.Notification
import com.example.aerosense_app.ProfileRequest
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

    @GET("/api/locations")
    fun getLocationData(@Header("X-Access-Token") token: String): Call<List<LocationData>>

    @POST("/api/register")
    fun registerUser(@Body request: RegisterRequest): Single<RegisterResponse>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("/api/settings")
    fun getUserSettings(@Header("X-Access-Token") token: String): Call<SettingsRequest>

    @POST("/api/settings")
    fun updateUserSettings(@Header("X-Access-Token") token: String, @Body settings: SettingsRequest): Call<SettingsResponse>

    @GET("/api/asthma-profile")
    fun getAsthmaProfile(@Header("X-Access-Token") token: String): Call<ProfileRequest>

    @POST("/api/asthma-profile")
    fun updateAsthmaProfile(@Header("X-Access-Token") token: String, @Body settings: ProfileRequest): Call<SettingsResponse>

    @GET("/api/get_notifications")
    fun getUserNotifications(@Header("X-Access-Token") token: String): Call<List<Notification>>

    @GET("/api/history")
    fun getHistory(@Header("X-Access-Token") token: String): Call<HistoryData>

    @GET("/api/history?week=last")
    fun getLastWeekHistory(@Header("X-Access-Token") token: String): Call<HistoryData>

//    @GET("/api/location")
//    fun getLocation(@Header("X-Access-Token") token: String): Call<LocationData>

}
