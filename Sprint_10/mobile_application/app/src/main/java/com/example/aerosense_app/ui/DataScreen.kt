package com.example.aerosense_app.ui

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.aerosense_app.FirebaseViewModel
import com.example.aerosense_app.R
import com.example.aerosense_app.Screen
import com.example.aerosense_app.api.Repository
import com.example.aerosense_app.ui.components.NavBar
import kotlin.math.sin

//Github copilot used when writing some of this code

@Composable
fun dataScreen(navController: NavHostController, repository: Repository, firebaseModel: FirebaseViewModel) {
    var percent by remember { mutableIntStateOf(54) }
    var percentageColor by remember { mutableStateOf(Color(0xff1e1e1e)) }
    var time by remember { mutableStateOf("") }
    var pmTwo by remember { mutableFloatStateOf(0.0F) }
    var pmTwoColor by remember { mutableStateOf(Color(0xff1e1e1e)) }
    var pmTen by remember { mutableFloatStateOf(0.0F) }
    var pmTenColor by remember { mutableStateOf(Color(0xff1e1e1e)) }
    var voc by remember { mutableFloatStateOf(0.0F) }
    var vocColor by remember { mutableStateOf(Color(0xff1e1e1e)) }
    var expanded by remember { mutableStateOf(false) }
    var pmOne by remember { mutableFloatStateOf(0.0F) }
    var temperature by remember { mutableFloatStateOf(0.0F) }
    var humidity by remember { mutableFloatStateOf(0.0F) }
    var gas_resistance by remember { mutableFloatStateOf(0.0F) }
    var pollenCount by remember { mutableIntStateOf(0) }



//    percent = 58
//    time = "12:56"
//    pmTwo = 18.0F
//    pmTen = 42.0F
//    voc = 505.0F

// Obtain the token from the ViewModel
    val token = firebaseModel.firebaseToken
    Log.d("Token", "Token: $token")

// Check if the token is not null
    if (!token.isNullOrBlank()) {
        // Use the token to make the API call
        repository.fetchAirQualityData(token,
            onSuccess = { homeData ->
                if (homeData != null) {
                    time = homeData.last_updated
                }
                if (homeData != null) {
                    pmTwo = homeData.pm25
                }
                if (homeData != null) {
                    pmTen = homeData.pm10
                }
                if (homeData != null) {
                    voc = homeData.voc_level
                }
                if (homeData != null) {
                    pmOne = homeData.pm1
                }
                if (homeData != null) {
                    temperature = homeData.temperature
                }
                if (homeData != null) {
                    humidity = homeData.humidity
                }
                if (homeData != null) {
                    gas_resistance = homeData.gas_resistance
                }
                if (homeData != null) {
                    pollenCount = homeData.pollenCount
                }

                percent = calculateAirQualityPercentage(voc.toDouble()).toInt()

                Log.d("DataScreen", "Air quality data: $homeData")
            },
            onError = { errorMessage ->
                Log.d("DataScreen", "Error: $errorMessage")
            }
        )
    } else {
        Log.d("DataScreen", "Error: Firebase token is null or blank")
    }

    //NavBar(navController)

    NavBar(navController)

    Box {
        LazyColumn(
            modifier = Modifier
                .padding(top = 65.dp)
        ) {
            item {
                //Box for the circle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(height = 320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Animated wavy circle based on air quality percentage
                    val circleSize = 220.dp
                    val airQualityPercentage =
                        calculateAirQualityPercentage(voc.toDouble()).toFloat() / 100f

                    WavyCircleProgress(
                        percentage = airQualityPercentage,
                        size = circleSize, // Use the reduced size here
                        color = getAirQualityColor(airQualityPercentage)
                    )

                    // Text displaying the percentage of clean air
                    Text(
                        text = "${(airQualityPercentage * 100).toInt()}% Clean",
                        color = percentageColor,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium
                    )

                    val imagePainter1 = rememberAsyncImagePainter(
                        model = R.drawable.infobutton,
                        placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                        error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                    )

                    IconButton(
                        onClick = { navController.navigate(Screen.EducationPage.name) },
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .offset(x = 300.dp, y = 40.dp)
                    ) {
                        Image(
                            painter = imagePainter1,
                            contentDescription = "download 2",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.requiredWidth(width = 40.dp)
                        )
                    }

                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                )
                {
                    //Centered text underneath the circle box
                    Text(
                        text = "Updated: $time",
                        color = Color(0xff1e1e1e),
                        lineHeight = 3.75.em,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .align(alignment = Alignment.Center)
                            .offset(y = -20.dp)
                    )
                }

                //Box for the particle stats
                Box(
                    modifier = Modifier
                        .requiredWidth(width = 534.dp)
                        .requiredHeight(height = 123.dp)
                ) {
                    val imagePainter = rememberAsyncImagePainter(
                        model = R.drawable.particles,
                        placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                        error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                    )

                    Image(
                        painter = imagePainter,
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
//        Image(
//            painter = painterResource(id = R.drawable.particles),
//            contentDescription = "download 1",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .align(alignment = Alignment.TopStart)
//                .offset(
//                    x = 100.dp,
//                    y = 4.5.dp
//                )
//                .requiredWidth(width = 100.dp)
//                .requiredHeight(height = 100.dp)
//        )


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

                    //If statements to change the color of the air quality measures
                    if (pmTwo >= 0 && pmTwo <= 10) {
                        //Make the color green
                        pmTwoColor = Color(0xffff50f413)
                    } else if (pmTwo > 10 && pmTwo <= 20) {
                        pmTwoColor = Color(0xffffa629)
                    } else if (pmTwo > 20 && pmTwo <= 25) {
                        pmTwoColor = Color(0xfff24822)
                    } else {
                        pmTwoColor = Color(0xffaf21d2)
                    }

                    Text(
                        text = pmTwo.toString() + "µg/m^3",
                        color = pmTwoColor,
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

                    //If statements to change the color of the air quality measures
                    if (pmTen >= 0 && pmTen <= 20) {
                        //Make the color green
                        pmTenColor = Color(0xffff50f413)
                    } else if (pmTen > 20 && pmTen < 40) {
                        pmTenColor = Color(0xffffa629)
                    } else if (pmTen > 40 && pmTen < 50) {
                        pmTenColor = Color(0xfff24822)
                    } else {
                        pmTenColor = Color(0xffaf21d2)
                    }

                    Text(
                        text = pmTen.toString() + "µg/m^3",
                        color = pmTenColor,
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
                    modifier = Modifier
                        .requiredWidth(width = 541.dp)
                        .requiredHeight(height = 137.dp)
                        .padding(top=10.dp)
                ) {
                    val imagePainter = rememberAsyncImagePainter(
                        model = R.drawable.gascloud,
                        placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                        error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                    )

                    Image(
                        painter = imagePainter,
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
//        Image(
//            painter = painterResource(id = R.drawable.gascloud),
//            contentDescription = "download 2",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .requiredWidth(width = 100.dp)
//                .requiredHeight(height = 100.dp)
//                .align(alignment = Alignment.TopStart)
//                .offset(
//                    x = 105.dp,
//                    y = 4.5.dp
//                )
//        )

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

                    //If statements to change the color of the air quality measures
                    if (voc > 0.0 && voc <= 0.5) {
                        //Make the color green
                        vocColor = Color(0xffff50f413)
                    } else if (voc > 0.5 && voc <= 1.0) {
                        vocColor = Color(0xffffa629)
                    } else if (voc > 1.0 && voc <= 2.0) {
                        vocColor = Color(0xfff24822)
                    } else {
                        vocColor = Color(0xffaf21d2)
                    }

                    Text(
                        text = "$voc ppb",
                        color = vocColor,
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
                    modifier = Modifier
                        .requiredWidth(width = 437.dp)
                        .requiredHeight(height = 129.dp)
                ) {
                    val imagePainter = rememberAsyncImagePainter(
                        model = R.drawable.location,
                        placeholder = painterResource(R.drawable.loading_image), // Image shown while loading
                        error = painterResource(R.drawable.ic_broken_image) // Image shown on error
                    )

                    Image(
                        painter = imagePainter,
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
//        Image(
//            painter = painterResource(id = R.drawable.location),
//            contentDescription = "download 3",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .requiredWidth(width = 100.dp)
//                .requiredHeight(height = 100.dp)
//                .align(alignment = Alignment.TopStart)
//                .offset(
//                    x = 50.dp,
//                    y = 4.5.dp
//                )
//        )

                    Box(modifier = Modifier
                        .offset(
                            x = 169.dp,
                            y = 33.5.dp
                        )
                        .clickable {
                            navController.navigate(Screen.Location.name)
                        }) {

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

                        )
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                    .requiredWidth(width = 130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = if (expanded) "View Less" else "View More")
                    }
                }
            }
            if (expanded) {
                item {
                    MeasurementCard(
                        measurementName = "PM1",
                        measurementValue = pmOne,
                        color = Color(0xff1e1e1e),
                        imageRes = R.drawable.particles
                    )
                }
                item {
                    MeasurementCard(
                        measurementName = "Temperature",
                        measurementValue = temperature,
                        color = Color(0xff1e1e1e),
                        imageRes = R.drawable.temperature
                    )
                }
                item {
                    MeasurementCard(
                        measurementName = "Humidity",
                        measurementValue = humidity,
                        color = Color(0xff1e1e1e),
                        imageRes = R.drawable.humidity
                    )
                }
                item {
                    MeasurementCard(
                        measurementName = "Gas Resistance",
                        measurementValue = gas_resistance,
                        color = Color(0xff1e1e1e),
                        imageRes = R.drawable.gascloud
                    )
                }
                item {
                    MeasurementCard(
                        measurementName = "Pollen Count",
                        measurementValue = pollenCount.toFloat(),
                        color = Color(0xff1e1e1e),
                        imageRes = R.drawable.pollen
                    )
                }
            }
        }

    }

}

fun calculateAirQualityPercentage(voc: Double): Double {
    return when {
        voc < 0.5 -> 100.0 - (voc / 0.5 * 20) // 80-100%
        voc in 0.5..1.0 -> 60.0 - ((voc - 0.5) / 0.5 * 20) // 40-60%
        voc in 1.0..2.0 -> 40.0 - ((voc - 1.0) / 1.0 * 40) // 0-40%
        else -> 0.0 // 0%
    }
}

@Composable
fun WavyCircleProgress(percentage: Float, size: Dp, color: Color) {
    val infiniteTransition = rememberInfiniteTransition()
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(), // A full sine wave cycle
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.size(size)) {
        val circlePath = Path().apply {
            addOval(oval = Rect(Offset.Zero, size = Size(size.toPx(), size.toPx())))
        }
        clipPath(circlePath) {
            drawCircle(
                color = Color.Transparent,
                radius = size.toPx() / 2
            )

            val waveHeight = 20f
            val waveTop = size.toPx() * (1 - percentage) + waveHeight

            // Draw wavy fill
            val path = Path().apply {
                moveTo(0f, waveTop)
                for (x in 0 until size.toPx().toInt()) {
                    val angle = waveOffset + (x * 2 * Math.PI / size.toPx())
                    val y = waveTop - waveHeight + sin(angle).toFloat() * waveHeight // Adjust wave height here
                    lineTo(x.toFloat(), y)
                }
                lineTo(size.toPx(), size.toPx())
                lineTo(0f, size.toPx())
                close()
            }

            drawPath(path = path, color = color)
        }

        drawCircle(
            color = color,
            radius = size.toPx() / 2 - 1.dp.toPx(),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

fun getAirQualityColor(percentage: Float): Color {
    return when {
        percentage >= 0.8 -> Color(0xffff15e002) // Very good - Green with transparency
        percentage >= 0.6 -> Color(0x66FFFF00) // Moderate - Yellow with transparency
        percentage >= 0.4 -> Color(0x66FFA500) // Poor - Orange with transparency
        else -> Color(0x66FF0000)              // Very Poor - Red with transparency
    }
}

@Composable
fun MeasurementCard(
    measurementName: String,
    measurementValue: Float,
    color: Color,
    imageRes: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "$measurementName icon",
                modifier = Modifier.size(40.dp) // Adjust the size as needed
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("$measurementName -", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Text(
                    "$measurementValue",
                    color = color,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}



