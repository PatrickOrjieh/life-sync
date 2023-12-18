package com.example.aerosense_app.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aerosense_app.FirebaseViewModel
import com.example.aerosense_app.R
import com.example.aerosense_app.api.Repository
import com.example.aerosense_app.ui.components.NavBar


data class Notifications(
    val time: String,
    val header: String,
    val message: String
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Notifications(navController: NavHostController, repository: Repository, firebaseModel: FirebaseViewModel){

    NavBar(navController)

    // Sample notification data
    var notifications = remember { mutableStateListOf(Notifications("time","header","message")) }

    val token = firebaseModel.firebaseToken
    Log.d("Token", "Token: $token")

// Check if the token is not null
    if (!token.isNullOrBlank()) {
        notifications.clear()
        // Use the token to make the API call
        repository.fetchUserNotifications(token,
            onSuccess = { notification ->

                Log.d("Notification Screen", "Success: $notification")

                if (notification != null) {

                    for (i in 0 until notification.size) {
                        val time = notification[i].time
                        val header = notification[i].header
                        val message = notification[i].message

                        notifications.add(Notifications(time, header, message))
                    }
                }
            },
            onError = { errorMessage ->
                Log.d("Notification Screen", "Error: $errorMessage")
            }
        )
    } else {
        Log.d("Notification Screen", "Error")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 70.dp),
    ) {

        item {
            Text(
                text = "Notifications",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .padding(top=10.dp, start=55.dp)
            )
        }

        items(notifications) { notification ->
            NotificationCard(notification = notification)
        }
    }
}

@Composable
fun NotificationCard(notification: Notifications) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable{ expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.alert),
                contentDescription = "alert bell",
                modifier = Modifier
                    .requiredSize(50.dp)
                    .padding(start=15.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = notification.time,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )

                Text(
                    text = notification.header,
                    fontSize = 14.sp,
                )

                if (expanded) {
                    Text(
                        text = notification.message,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top=15.dp)
                    )
                }
            }
        }
    }
}