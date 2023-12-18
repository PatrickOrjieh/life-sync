package com.example.aerosense_app.api

import android.util.Log
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
import com.example.aerosense_app.database.HomeDataDao
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Github copilot used while writing this code


class Repository(private val apiService: ApiService, private val homeDataDao: HomeDataDao) {

    fun getAirQualityData(token: String): Call<HomeData> {
        return apiService.getAirQualityData(token)
    }

    fun getUserSettings(token: String): Call<SettingsRequest> {
        return apiService.getUserSettings(token)
    }

    fun getLocations(token: String): Call<List<LocationData>> {
        return apiService.getLocationData(token)
    }

    fun getUserNotifications(token: String): Call<List<Notification>> {
        return apiService.getUserNotifications(token)
    }

    fun getAsthmaProfile(token: String): Call<ProfileRequest> {
        return apiService.getAsthmaProfile(token)
    }

    fun getHistory(token: String): Call<HistoryData> {
        return apiService.getHistory(token)
    }

    fun getLastWeekHistory(token: String): Call<HistoryData> {
        return apiService.getLastWeekHistory(token)
    }

//    fun updateUserSettings(requestBody: SettingsRequest): Call<SettingsResponse> {
//        return apiService.updateUserSettings(requestBody)
//    }

//    fun fetchAirQualityData(token: String, onSuccess: (HomeData?) -> Unit, onError: (String) -> Unit) {
//        val call = getAirQualityData(token)
//        call.enqueue(object : Callback<HomeData> {
//            override fun onResponse(call: Call<HomeData>, response: Response<HomeData>) {
//                Log.d("RepositoryResponse", response.toString())
//                if (response.isSuccessful) {
//                    onSuccess(response.body())
//                    Log.d("Repository", "success: ${response.body()}")
//                } else {
//                    Log.d("Repository", "onResponse: ${response.code()}")
//                    onError("Error: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<HomeData>, t: Throwable) {
//                Log.d("Repository", "onResponse: ${t.message}")
//                onError("Failure: ${t.message}")
//            }
//        })
//    }

    fun fetchAirQualityData(
        token: String,
        onSuccess: (HomeData?) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = getAirQualityData(token)
        call.enqueue(object : Callback<HomeData> {
            override fun onResponse(call: Call<HomeData>, response: Response<HomeData>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        saveAirQualityData(data)
                        onSuccess(data)
                    }
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HomeData>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }

    fun fetchSettings(
        token: String,
        onSuccess: (SettingsRequest?) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = getUserSettings(token)
        call.enqueue(object : Callback<SettingsRequest> {
            override fun onResponse(
                call: Call<SettingsRequest>,
                response: Response<SettingsRequest>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        onSuccess(data)
                    }
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SettingsRequest>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }

    fun fetchUserNotifications(
        token: String,
        onSuccess: (List<Notification>?) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = getUserNotifications(token)
        call.enqueue(object : Callback<List<Notification>> {
            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        onSuccess(data)
                    }
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }

    fun fetchLocationData(
        token: String,
        onSuccess: (List<LocationData>?) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = getLocations(token)
        call.enqueue(object : Callback<List<LocationData>> {
            override fun onResponse(call: Call<List<LocationData>>, response: Response<List<LocationData>>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        onSuccess(data)
                    }
                    Log.d("Repository", "Location success: ${response.body()}")
                } else {
                    Log.d("Repository", "onResponse: $response")
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<LocationData>>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }

    fun fetchUserHistory(
        token: String,
        onSuccess: (HistoryData?) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = getHistory(token)
        call.enqueue(object : Callback<HistoryData> {
            override fun onResponse(call: Call<HistoryData>, response: Response<HistoryData>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        onSuccess(data)
                    }
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HistoryData>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }

    fun fetchLastWeekHistory(
        token: String,
        onSuccess: (HistoryData?) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = getLastWeekHistory(token)
        call.enqueue(object : Callback<HistoryData> {
            override fun onResponse(call: Call<HistoryData>, response: Response<HistoryData>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        onSuccess(data)
                    }
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HistoryData>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }

    fun updateUserSettings(
        token: String,
        settings: SettingsRequest,
        onSuccess: (SettingsResponse?) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = apiService.updateUserSettings(token, settings)
        call.enqueue(object : Callback<SettingsResponse> {
            override fun onResponse(
                call: Call<SettingsResponse>,
                response: Response<SettingsResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }

    fun fetchAsthmaProfile(token: String, onSuccess: (ProfileRequest?) -> Unit, onError: (String) -> Unit)
    {
        val call = getAsthmaProfile(token)
        call.enqueue(object : Callback<ProfileRequest> {
            override fun onResponse(
                call: Call<ProfileRequest>,
                response: Response<ProfileRequest>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        onSuccess(data)
                    }
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProfileRequest>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }

    fun updateAsthmaProfile(
        token: String,
        settings: ProfileRequest,
        onSuccess: (SettingsResponse?) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = apiService.updateAsthmaProfile(token, settings)
        call.enqueue(object : Callback<SettingsResponse> {
            override fun onResponse(
                call: Call<SettingsResponse>,
                response: Response<SettingsResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }


    fun saveAirQualityData(homeData: HomeData) {
        Thread {
            homeDataDao.insertAll(homeData)
            Log.d("Repository", "Saved data to Room: $homeData")
        }.start()
    }

    fun getCachedAirQualityData(): List<HomeData> {
        val data = homeDataDao.getAll()
        Log.d("Repository", "Retrieved data from Room: $data")
        return data
    }


    fun registerData(data: RegisterRequest): Single<RegisterResponse> {
        val request = RegisterRequest(firebaseToken = data.firebaseToken, modelNumber = data.modelNumber, fcmToken = data.fcmToken)
        return apiService.registerUser(request)
    }

    fun loginUser(email: String, password: String): Call<LoginResponse> {
        val loginRequest = LoginRequest(email, password)
        return apiService.login(loginRequest)
    }

}
