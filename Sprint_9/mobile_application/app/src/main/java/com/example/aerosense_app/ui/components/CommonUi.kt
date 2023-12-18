package com.example.aerosense_app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aerosense_app.R
import com.example.aerosense_app.Screen

@Composable
fun DropDown(navController: NavHostController) {
    //Parts of the following code adapted from: https://alexzh.com/jetpack-compose-dropdownmenu/
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
            .offset(y = 13.dp, x = -30.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Image(
                painter = painterResource(id = R.drawable.hamburger),
                contentDescription = "hamburger",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .requiredWidth(width = 50.dp)
                    .requiredHeight(height = 50.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Home") },
                onClick = { navController.navigate(Screen.DataScreen.name)
                }
            )
            DropdownMenuItem(
                text = { Text("Location") },
                onClick = { navController.navigate(Screen.Location.name) }
            )
//            DropdownMenuItem(
//                text = { Text("History") },
//                onClick = {  }
//            )
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = { navController.navigate(Screen.Settings.name) }
            )
//            DropdownMenuItem(
//                text = { Text("Sign Out") },
//                onClick = { navController.navigate(Screen.Login.name) }
//            )
        }
    }
}

@Composable
fun NavBar(navController: NavHostController){
    NavigationBar() {
        Box(
            modifier = Modifier
                .requiredWidth(width = 100.dp)
                .requiredHeight(height = 93.dp)
        ) {
            //Blue background
            Box(
                modifier = Modifier
                    .requiredWidth(width = 718.dp)
                    .requiredHeight(height = 65.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color(0xff0d99ff))
            )
            Image(
                painter = painterResource(id = R.drawable.logo_no_background),
                contentDescription = "logo-no-background",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = -10.dp, x = 140.dp)
                    .requiredWidth(width = 200.dp)
                    .requiredHeight(height = 50.dp)
            )
            DropDown(navController);

            IconButton(onClick = { navController.navigate(Screen.AsthmaProfile.name)},
                modifier = Modifier
                    .requiredWidth(50.dp)
                    .requiredHeight(50.dp)
                    .offset(x = 295.dp, y = 10.dp)) {

                Image(
                    painter = painterResource(id = R.drawable.userprofile),
                    contentDescription = "userProfile",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

        }
    }
}

//Have to opt in for Experimental Material 3 API in order to use TextField
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionDropDown(selection: Array<String>) {
    //Code for this taken from: https://alexzh.com/jetpack-compose-dropdownmenu/
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selection[0]) }

    Box(
        modifier = Modifier
            .padding(10.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                selection.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}