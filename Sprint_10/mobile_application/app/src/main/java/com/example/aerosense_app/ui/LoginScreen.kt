
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import com.example.aerosense_app.Screen
import com.example.aerosense_app.api.Repository
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController, repository: Repository, firebaseModel: FirebaseViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var validEmail by remember { mutableStateOf(false) }
    var validPassword by remember { mutableStateOf(false) }

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
            val imagePainter = rememberAsyncImagePainter(
                model = R.drawable.logo_no_background,
                placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                error = painterResource(R.drawable.ic_broken_image) // Image shown on error
            )

            Image(
                painter = imagePainter,
                contentDescription = "AeroSense Logo",
                modifier = Modifier
                    .size(300.dp)
            )
//            Image(
//                painter = painterResource(id = R.drawable.logo_no_background),
//                contentDescription = "AeroSense Logo",
//                modifier = Modifier
//                    .size(300.dp)
//            )

            // Login-related elements
            Text(
                text = "Log In",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start)
                    .offset(y = -50.dp)
            )

            // "Please sign in to continue" text
            Text(
                text = "Please sign in to continue",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start)
                    .offset(y = -50.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Row with image and TextField
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset(y = -50.dp)
            ) {
                val imagePainter2 = rememberAsyncImagePainter(
                    model = R.drawable.email,
                    placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                    error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                )

                Image (
                    painter = imagePainter2,
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
                    value = email.trim(),

                    onValueChange = {
                        email = it
                        // Perform email validation
                        if(android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches())
                        {
                            loginError = null // No error
                            validEmail = true
                        } else {
                            loginError = "Invalid login details"
                            validEmail = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Take remaining space in the Row
                        .background(Color.White),
                    label = { Text("Email") }
                )
            }

            // Password Row with image and TextField
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset(y = -50.dp)
            ) {
                val imagePainter3 = rememberAsyncImagePainter(
                    model = R.drawable.password,
                    placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                    error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                )

                Image(
                    painter = imagePainter3,
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
                            loginError = null // No error
                            validPassword = true
                        } else {
                            loginError = "Invalid login details"
                            validPassword = false
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

            // Error text
            Text(
                text = loginError ?: "",
                style = TextStyle(
                    color = Color.Red,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start)
                    .offset(y = -50.dp)
            )

            Spacer(modifier = Modifier.height(30.dp)  )

            // Login Button wrapped in a Row
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .offset(y = -50.dp)
            ) {

                Text(
                    text = "Forgot Password?",
                    style = TextStyle(
                        color = Color.Blue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 10.dp)
                        .clickable {
                            navController.navigate(Screen.ResetPassword.name)
                        }
                )

                Button(
                    onClick = {

                        email = email.trim()
                        Log.d("Login", "Email: $email")

                        FirebaseAuth
                            .getInstance()
                            .signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { signInTask ->
                                if (signInTask.isSuccessful) {
                                    // Login successful, get the user and ID token
                                    val user = FirebaseAuth.getInstance().currentUser
                                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                                        if (tokenTask.isSuccessful) {
                                            val idToken = tokenTask.result?.token

                                            Log.d("LoginToken", "idToken: $idToken")

                                            // Set the token in the ViewModel
                                            firebaseModel.firebaseToken = idToken

                                            // Navigate to the data screen
                                            navController.navigate(Screen.DataScreen.name)
                                        } else {
                                            // Handle error in getting ID token
                                            loginError = "Error in getting Firebase ID token"
                                        }
                                    }
                                } else {
                                    // Login failed
                                    Log.d("Login", "Login failed")
                                    loginError = "Invalid login details"
                                }
                            }
                            .addOnFailureListener {
                                Log.d("Login", "Login failed: ${it.localizedMessage}")
                                loginError = "Invalid login details"
                            }



//                        if (validEmail && validPassword) {
//
//                            navController.navigate(Screen.DataScreen.name)
//                        } else {
//                            loginError = "Invalid login details"
//                        }
                    },
                    modifier = Modifier
                        .height(53.dp)
                        .padding(start = 60.dp),
                    shape = MaterialTheme.shapes.large // Apply rounded corners
                ) {
                    Text(text = "Log In")
                }
            }

            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 10.dp)
            ) {

                //Make text for not having account and click to register
                Text(
                    text = "Don't have an account?",
                    style = TextStyle(
                        color = Color.Blue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable {
                            navController.navigate(Screen.Register.name)
                        }
                )
            }
        }
    }
}
