package com.example.aerosense_app.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.aerosense_app.FirebaseViewModel
import com.example.aerosense_app.R
import com.example.aerosense_app.RegisterRequest
import com.example.aerosense_app.Screen
import com.example.aerosense_app.api.Repository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(navController: NavHostController, repository: Repository, firebaseModel: FirebaseViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var modelNum by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(start = 16.dp) // Add padding to the outer column
        ) {

            Spacer(modifier = Modifier.height(25.dp))

            // Login-related elements
            Text(
                text = "Create Account",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Name row with user image and text field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val imagePainter = rememberAsyncImagePainter(
                    model = R.drawable.user,
                    placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                    error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                )

                Image(
                    painter = imagePainter,
                    contentDescription = "User Icon",
                    modifier = Modifier.size(35.dp)
                )
//                Image(
//                    painter = painterResource(id = R.drawable.user),
//                    contentDescription = "User Icon",
//                    modifier = Modifier.size(35.dp)
//                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = name,
                    onValueChange = {
                        name = it

                        if(it.isNotEmpty()) {
                            error = null // No error
                            isError = false
                        } else {
                            error = "Full Name is required"
                            isError = true
                        }
                                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space in the Row
                    label = { Text("Full Name") }
                )
            }

            // Email Row with image and TextField
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val imagePainter = rememberAsyncImagePainter(
                    model = R.drawable.email,
                    placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                    error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                )

                Image(
                    painter = imagePainter,
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(35.dp)
                )
//                Image(
//                    painter = painterResource(id = R.drawable.email),
//                    contentDescription = "Email Icon",
//                    modifier = Modifier.size(35.dp)
//                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        //Found information on patterns from: https://developer.android.com/reference/android/util/Patterns#EMAIL_ADDRESS
                        if(it.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches())
                        {
                            error = null // No error
                            isError = false
                        } else {
                            error = "Email format is incorrect"
                            isError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space in the Row
                    label = { Text("Email") }
                )
            }

            // Password Row with image and TextField
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val imagePainter = rememberAsyncImagePainter(
                    model = R.drawable.password,
                    placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                    error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                )

                Image(
                    painter = imagePainter,
                    contentDescription = "Password Icon",
                    modifier = Modifier.size(35.dp)
                )
//                Image(
//                    painter = painterResource(id = R.drawable.password),
//                    contentDescription = "Password Icon",
//                    modifier = Modifier.size(35.dp)
//                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                        // Regex to check if password is 6 characters long, has a capital letter, a number and a special character
                        if (it.matches(Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\\$%^&*()]).{6,}\$"))) {
                            error = null // No error
                            isError = false
                        } else {
                            error = "Password must be 6 characters long, have a capital letter, a number & a special character"
                            isError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space in the Row
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )
            }

            //Confirm password row with image and text field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val imagePainter = rememberAsyncImagePainter(
                    model = R.drawable.password,
                    placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                    error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                )

                Image(
                    painter = imagePainter,
                    contentDescription = "Password Icon",
                    modifier = Modifier.size(35.dp)
                )
//                Image(
//                    painter = painterResource(id = R.drawable.password),
//                    contentDescription = "Password Icon",
//                    modifier = Modifier.size(35.dp)
//                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = confirm,
                    onValueChange = {
                        confirm = it

                        if(it == password) {
                            error = null // No error
                            isError = false
                        } else {
                            error = "Passwords do not match"
                            isError = true
                        }

                                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space in the Row
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val imagePainter = rememberAsyncImagePainter(
                    model = R.drawable.modelnum,
                    placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                    error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                )

                Image(
                    painter = imagePainter,
                    contentDescription = "Model Number",
                    modifier = Modifier.size(35.dp)
                )
//                Image(
//                    painter = painterResource(id = R.drawable.modelnum),
//                    contentDescription = "Model Number",
//                    modifier = Modifier.size(35.dp)
//                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = modelNum,
                    onValueChange = {
                        modelNum = it

                        if(it.isNotEmpty()) {
                            error = null // No error
                            isError = false
                        } else {
                            error = "Hub Model Number is required"
                            isError = true
                        }

                                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space in the Row
                    label = { Text("Hub Model Number") },
                    visualTransformation = PasswordVisualTransformation(),
                )
            }

            // Error text
            Text(
                text = error ?: "",
                style = TextStyle(
                    color = Color.Red,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(start = 230.dp)
            ) {
                Button(
                    onClick = {

                        val enteredName = name.isNotEmpty()
                        val enteredEmail = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        val enteredPassword = password.isNotEmpty() && password.matches(Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\\$%^&*()]).{6,}\$"))
                        val enteredConfirm = confirm.isNotEmpty() && confirm == password
                        val enteredModelNum = modelNum.isNotEmpty()

                        if(isError) {
                            error = "Invalid register details"
                        }

//                        if (error == null && enteredName && enteredEmail && enteredPassword && enteredConfirm && enteredModelNum) {
//                            repository.registerData(RegisterRequest(name, email, password, confirm, modelNum))
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe({ response ->
//                                    // Handle the successful response here
//                                    Log.d("Register", "Response: $response")
//                                }, { error ->
//                                    // Handle the error here
//                                    Log.d("RegisterFail", "Error: $error")
//                                })
//
//                            navController.navigate(Screen.DataScreen.name)
//
//                        }
//                        else {
//                            error = "Invalid register details"
//                        }

                        Log.d("Register", "Name: $enteredName")
                        Log.d("Register", "Email: $enteredEmail")
                        Log.d("Register", "Password: $enteredPassword")
                        Log.d("Register", "Confirm: $enteredConfirm")
                        Log.d("Register", "Model Number: $enteredModelNum")

                        if (enteredName && enteredEmail && enteredPassword && enteredConfirm && enteredModelNum) {
                            val auth = FirebaseAuth.getInstance()

                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // User successfully registered and logged in
                                        val user = auth.currentUser
                                        user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                                            if (tokenTask.isSuccessful) {
                                                val idToken = tokenTask.result?.token

                                                Log.d("RegisterToken", "ID Token: $idToken")

                                                firebaseModel.firebaseToken = idToken

                                                FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                                    OnCompleteListener { task ->
                                                    if (!task.isSuccessful) {
                                                        Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                                                        return@OnCompleteListener
                                                    }

                                                    // Get new FCM registration token
                                                    val fcmToken = task.result

                                                        Log.d("FCM", "Token: $fcmToken")

                                                    // Log and toast
//                                                    val msg = getString(R.string.msg_token_fmt, token)
//                                                    Log.d(TAG, msg)
                                                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                                                    sendTokenToBackend(idToken, modelNum, fcmToken, navController, repository)
                                                })

                                            } else {
                                                // Handle error in getting ID token
                                                error = "Error in getting Firebase ID token"
                                            }
                                        }
                                    } else {
                                        // Handle registration error
                                        error = task.exception?.message ?: "Registration failed"
                                    }
                                }
                        } else {
                            error = "Invalid register details"
                        }

                    },
                    modifier = Modifier
                        .height(53.dp),
                    shape = MaterialTheme.shapes.large // Apply rounded corners
                ) {
                    Text(text = "Register")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            //Make text for not having account and click to register
            Text(
                text = "Already have an account?",
                style = TextStyle(
                    color = Color.Blue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start)
                    .clickable {
                        navController.navigate(Screen.Login.name)
                    }
            )

        }
    }


}

private fun sendTokenToBackend(token: String?, modelNum: String, fcmToken: String, navController: NavHostController, repository: Repository) {
    if (token != null) {
        // Assuming `repository.registerData` is your method to send data to the backend
        repository.registerData(RegisterRequest(token, modelNum, fcmToken))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                // Handle successful response
                navController.navigate(Screen.DataScreen.name)
            }, { error ->
                // Handle error
                Log.d("BackendRegisterFail", "Error: $error")
            })
    }
}