package com.example.aerosense_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Surface
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aerosense_app.ui.theme.AeroSense_AppTheme

enum class Screen {
    Register,
    Login,
    DataScreen,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AeroSense_AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // A surface container using the 'background' color from the theme
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screen.Login.name) {
                        composable("Login") { Login(navController) }
                        composable("Register") { Register(navController) }
                        composable("dataScreen") { dataScreen() }
                    }
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun App() {
//    dataScreen()
//}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Login(navController: NavHostController) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

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
                Image(
                    painter = painterResource(id = R.drawable.logo_no_background),
                    contentDescription = "AeroSense Logo",
                    modifier = Modifier
                        .size(300.dp)
                )

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
                    Image(
                        painter = painterResource(id = R.drawable.email),
                        contentDescription = "Email Icon",
                        modifier = Modifier.size(35.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
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
                        .offset(y = -50.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = "Password Icon",
                        modifier = Modifier.size(35.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), // Take remaining space in the Row
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                    )
                }

                // Login Button wrapped in a Row for right alignment
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(start = 240.dp)
                        .offset(y = -50.dp)
                ) {
                    Button(
                        onClick = {
                            // Handle login button click
                            navController.navigate(Screen.DataScreen.name)
                        },
                        modifier = Modifier
                            .height(53.dp),
                        shape = MaterialTheme.shapes.large // Apply rounded corners
                    ) {
                        Text(text = "Log In")
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 20.dp)
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Register(navController: NavHostController) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirm by remember { mutableStateOf("") }

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
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "User Icon",
                        modifier = Modifier.size(35.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = name,
                        onValueChange = { name = it },
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
                    Image(
                        painter = painterResource(id = R.drawable.email),
                        contentDescription = "Email Icon",
                        modifier = Modifier.size(35.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
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
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = "Password Icon",
                        modifier = Modifier.size(35.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
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
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = "Password Icon",
                        modifier = Modifier.size(35.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), // Take remaining space in the Row
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(start = 230.dp)
                ) {
                    Button(
                        onClick = {
                            // Handle login button click
                        },
                        modifier = Modifier
                            .height(53.dp),
                        shape = MaterialTheme.shapes.large // Apply rounded corners
                    ) {
                        Text(text = "Register")
                    }
                }

                Spacer(modifier = Modifier.height(165.dp))

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

    @Composable
    fun dataScreen(modifier: Modifier = Modifier) {
        var percent by remember { mutableIntStateOf(0) }
        var time by remember { mutableStateOf("") }
        var pmTwo by remember { mutableIntStateOf(0) }
        var pmTen by remember { mutableIntStateOf(0) }
        var isMenuExpanded by remember { mutableStateOf(false) }

        percent = 58
        time = "12:56"
        pmTwo = 18
        pmTen = 42


        NavBar(modifier)

            //Box for the circle
            Box(
                modifier = modifier
                    .requiredWidth(width = 320.dp)
                    .requiredHeight(height = 291.dp)
                    .offset(y = -180.dp)

            ) {
                // Use drawBehind to draw a hollow black circle with a border
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 15.dp, y = 19.dp)
                        .requiredWidth(width = 287.dp)
                        .requiredHeight(height = 272.dp)
                        .drawBehind {
                            drawCircle(
                                color = Color.Black,
                                center = center,
                                radius = size.minDimension / 3,
                                style = Stroke(4.dp.toPx())
                            )
                        }
                )
                Text(
                    text = "57% Clean",
                    color = Color(0xfff24822),
                    lineHeight = 3.75.em,
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                )
            }

            Box() {
                //Centered text underneath the circle box
                Text(
                    text = "Updated: " + time,
                    color = Color(0xff1e1e1e),
                    lineHeight = 3.75.em,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                        .offset(y = -50.dp)
                )
            }

            //Box for the particle stats
            Box(
                modifier = modifier
                    .requiredWidth(width = 534.dp)
                    .requiredHeight(height = 123.dp)
                    .offset(y = 40.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.particles),
                    contentDescription = "download 1",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(
                            x = 100.dp,
                            y = 4.5.dp
                        )
                        .requiredWidth(width = 100.dp)
                        .requiredHeight(height = 100.dp)
                )
                Text(
                    text = "PM2.5 -",
                    color = Color(0xff1e1e1e),
                    lineHeight = 3.75.em,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(
                            x = 227.dp,
                            y = 15.dp
                        )
                )
                Text(
                    text = pmTwo.toString() + "µg/m^3",
                    color = Color(0xffffa629),
                    lineHeight = 3.75.em,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(
                            x = 320.dp,
                            y = 15.dp
                        )
                )
                Text(
                    text = "PM10 -",
                    color = Color(0xff1e1e1e),
                    lineHeight = 3.75.em,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(
                            x = 227.dp,
                            y = 63.dp
                        )
                )
                Text(
                    text = pmTen.toString() + "µg/m^3",
                    color = Color(0xffffa629),
                    lineHeight = 3.75.em,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(
                            x = 320.dp,
                            y = 63.dp
                        )
                )
            }

            //Box for the gas stats
            Box(
                modifier = modifier
                    .requiredWidth(width = 541.dp)
                    .requiredHeight(height = 137.dp)
                    .offset(y = 180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gascloud),
                    contentDescription = "download 2",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .requiredWidth(width = 100.dp)
                        .requiredHeight(height = 100.dp)
                        .align(alignment = Alignment.TopStart)
                        .offset(
                            x = 105.dp,
                            y = 4.5.dp
                        )
                )
                Text(
                    text = "VOC Level -",
                    color = Color(0xff1e1e1e),
                    lineHeight = 3.75.em,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(
                            x = 227.dp,
                            y = 43.dp
                        )
                )
                Text(
                    text = "505 ppb",
                    color = Color(0xfff24822),
                    lineHeight = 3.75.em,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(
                            x = 345.dp,
                            y = 43.dp
                        )
                )
            }

            //Box for location
            Box(
                modifier = modifier
                    .requiredWidth(width = 437.dp)
                    .requiredHeight(height = 129.dp)
                    .offset(y = 300.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = "download 3",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .requiredWidth(width = 100.dp)
                        .requiredHeight(height = 100.dp)
                        .align(alignment = Alignment.TopStart)
                        .offset(
                            x = 50.dp,
                            y = 4.5.dp
                        )
                )
                Text(
                    text = "View Location",
                    color = Color(0xff237ec1),
                    lineHeight = 3.75.em,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(
                            x = 169.dp,
                            y = 33.5.dp
                        )
                )
            }

        }


        @Composable
        fun DropDown() {
            val context = LocalContext.current
            var expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopEnd)
                    .offset(y = 13.dp, x=-30.dp)
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    Image(
                        painter = painterResource(id = R.drawable.hamburger),
                        contentDescription = "download 3",
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
                        onClick = { //Navigation
                     }
                    )
                    DropdownMenuItem(
                        text = { Text("Location") },
                        onClick = { }
                    )
                    DropdownMenuItem(
                        text = { Text("History") },
                        onClick = {  }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {  }
                    )
                }
            }
        }

@Composable
fun NavBar(modifier: Modifier = Modifier){
    NavigationBar(modifier) {
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
                    .offset(y = -10.dp, x = 150.dp)
                    .requiredWidth(width = 200.dp)
                    .requiredHeight(height = 50.dp)
            )
            DropDown();

        }
    }
}