package com.example.aerosense_app.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.aerosense_app.FirebaseViewModel
import com.example.aerosense_app.ProfileRequest
import com.example.aerosense_app.R
import com.example.aerosense_app.api.Repository
import com.example.aerosense_app.ui.components.NavBar

@Composable
fun AsthmaProfile(navController: NavHostController, firebaseModel: FirebaseViewModel, repository: Repository){
    val selectionTrigger = arrayOf("Fumes", "Smoke", "Disinfectants", "Humidity", "Pollen", "Dust")
    val selectionSeverity = arrayOf("Mild intermittent asthma", "Mild Persistent Asthma", "Moderate persistent asthma", "Severe persistent asthma")
    var mainTrigger by remember { mutableStateOf("") }
    var asthmaSeverity by remember { mutableStateOf("") }

   NavBar(navController)

    // Obtain the token from the ViewModel
    val token = firebaseModel.firebaseToken
    Log.d("Token", "Token: $token")

// Check if the token is not null
    if (!token.isNullOrBlank()) {
        // Use the token to make the API call
        repository.fetchAsthmaProfile(token,
            onSuccess = { profileRequest ->
                if (profileRequest != null) {
                    mainTrigger = profileRequest.personalTrigger
                }
                if (profileRequest != null) {
                    asthmaSeverity = profileRequest.asthmaCondition
                }

                Log.d("Check asthma profile data", "Settings data: $profileRequest")
            },
            onError = { errorMessage ->
                Log.d("Check asthma profile Data", "Error: $errorMessage")
            }
        )
    } else {
        Log.d("asthma profile Error", "Error: Firebase token is null or blank")
    }

    Text(
        text = "Asthma Profile",
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        color = Color(0xff1e1e1e),
        style = TextStyle(
            fontSize = 35.sp,
            fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(top = 80.dp)
    )

    val imagePainter = rememberAsyncImagePainter(
        model = R.drawable.userprofile,
        placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
        error = painterResource(R.drawable.ic_broken_image) // Image shown on error
    )

    Image(
        painter = imagePainter,
        contentDescription = "Profile Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .offset(y = -110.dp)
            .requiredSize(size = 200.dp)
    )

//    Image(
//        painter = painterResource(id = R.drawable.userprofile),
//        contentDescription = "image 1",
//        contentScale = ContentScale.Crop,
//        modifier = Modifier
//            .offset(y = -110.dp)
//            .requiredSize(size = 200.dp))

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
        Log.d("Check main trigger", "Main trigger: $mainTrigger")
        if(mainTrigger != ""){
            profileSelectionDropDown(selection = selectionTrigger, mainTrigger, asthmaSeverity, true, repository = repository, token = token, onItemSelected = { mainTrigger = it })
        }
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
        Log.d("Check asthma severity", "Asthma severity: $asthmaSeverity")
        if(asthmaSeverity != "") {
            profileSelectionDropDown(selection = selectionSeverity, mainTrigger, asthmaSeverity, false, repository = repository, token = token, onItemSelected = { asthmaSeverity = it })
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileSelectionDropDown(selection: Array<String>, trigger: String, severity: String, isTrigger: Boolean, repository: Repository, token: String?, onItemSelected: (String) -> Unit) {
    //Code for this taken from: https://alexzh.com/jetpack-compose-dropdownmenu/
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    var refresh by remember { mutableStateOf(1) }
    if(isTrigger){
        selectedText = trigger
    }
    else{
        selectedText = severity
    }

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

                            val requestBody = ProfileRequest("","")
                            if(isTrigger){
                                requestBody.personalTrigger = item
                                requestBody.asthmaCondition = severity
                            }
                            else{
                                requestBody.asthmaCondition = item
                                requestBody.personalTrigger = trigger
                            }

                            if (token != null) {
                                repository.updateAsthmaProfile(token, requestBody,
                                    onSuccess = { settingsResponse ->
                                        Log.d("Settings", "Success: $settingsResponse")
                                    },
                                    onError = { errorMessage ->
                                        Log.d("Settings", "Error: $errorMessage")
                                    }
                                )
                            }

                            onItemSelected(item)

                            selectedText = item
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}