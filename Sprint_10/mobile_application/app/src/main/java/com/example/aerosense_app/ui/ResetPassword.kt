package com.example.aerosense_app.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aerosense_app.R
import com.example.aerosense_app.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPassword(navController: NavHostController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    Image(painterResource(id = R.drawable.leftarrow),
        contentDescription = "leftArrow",
        modifier = Modifier
            .padding(start = 16.dp)
            .offset(x = -150.dp, y=-310.dp)
            .requiredWidth(50.dp)
            .requiredHeight(50.dp)
            .clickable {
                navController.navigate(Screen.Login.name)
            })


    Box() {
        // Login-related elements
        Text(
            text = "Reset Password",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(start = 16.dp, top = 200.dp)
        )

        // "Please sign in to continue" text
        Text(
            text = "Enter your email here",
            style = TextStyle(
                color = Color.Gray,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(start = 16.dp, top = 250.dp)
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 30.dp, end = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.email),
            contentDescription = "Email Icon",
            modifier = Modifier.size(35.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            value = email,
            onValueChange = {
                email = it
                // Perform email validation
                if(it.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches())
                {
                    error = null // No error
                    isError = false
                } else {
                    isError = true
                }
            },
            modifier = Modifier
                .weight(1f), // Take remaining space in the Row
            label = { Text("Email") }
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
            .padding(start = 16.dp, top = 415.dp)
    )


    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.offset(y = 445.dp)
    ) {

        Button(
            onClick = {

                val enteredEmail = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

                if(isError) {
                    error = "Invalid login details"
                }

                if (error == null && enteredEmail) {
                    Toast.makeText(context, "Email Sent", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.Login.name)
                }
                else{
                    error = "Invalid email format"
                }

            },
            modifier = Modifier
                .height(53.dp)
                .padding(start = 190.dp),
            shape = MaterialTheme.shapes.large // Apply rounded corners
        ) {
            Text(text = "Submit")
        }
    }
}



