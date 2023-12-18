package com.example.aerosense_app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aerosense_app.R
import com.example.aerosense_app.ui.components.NavBar
import com.example.aerosense_app.ui.components.SelectionDropDown

@Composable
fun AsthmaProfile(navController: NavHostController){
    val selectionTrigger = arrayOf("Fumes", "Smoke", "Disinfectants", "Humidity", "Pollen", "Dust")
    val selectionSeverity = arrayOf("Mild intermittent asthma", "Mild Persistent Asthma", "Moderate persistent asthma", "Severe persistent asthma")

   NavBar(navController)

    Text(
        text = "Asthma Profile",
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        color = Color(0xff1e1e1e),
        style = TextStyle(
            fontSize = 35.sp,
            fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(top = 80.dp)
    )

    Image(
        painter = painterResource(id = R.drawable.userprofile),
        contentDescription = "image 1",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .offset(y = -110.dp)
            .requiredSize(size = 200.dp))

    Text(
        text = "Main Trigger",
        color = Color(0xff1e1e1e),
        style = TextStyle(
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(top = 350.dp, start = 25.dp)
    )

    Box(
        modifier = Modifier
            .padding(top = 400.dp, start = 20.dp)
    )
    {
        SelectionDropDown(selection = selectionTrigger)
    }

    Text(
        text = "Asthma Severity",
        color = Color(0xff1e1e1e),
        style = TextStyle(
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(top = 500.dp, start = 25.dp)
    )

    Box(
        modifier = Modifier
            .padding(top = 550.dp, start = 20.dp)
    )
    {
        SelectionDropDown(selection = selectionSeverity)
    }

}

