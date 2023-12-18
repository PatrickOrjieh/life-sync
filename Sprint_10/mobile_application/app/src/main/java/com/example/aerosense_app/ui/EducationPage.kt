package com.example.aerosense_app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.aerosense_app.R
import com.example.aerosense_app.ui.components.NavBar


@Composable
fun EducationPage(navController: NavHostController) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        NavBar(navController)
        Content()
    }
}
@Composable
fun Content() {
    Column(modifier = Modifier.padding(16.dp)
    ) {
        Text(
            "Understanding Air Quality Measurements",
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Section(
            title = "PM2.5 and PM10 - Particulate Matter",
            imageRes = R.drawable.pm2_5_pm10,
            description = "Particulate matters (PM2.5 and PM10) are microscopic particles suspended in the air, often a byproduct of combustion processes and industrial activities. PM2.5 particles are incredibly small, measuring 2.5 micrometers or less, and can penetrate deep into the lungs and even enter the bloodstream. PM10 particles, while larger, still pose significant health risks, particularly to respiratory and cardiovascular health. High levels of these particles can aggravate asthma, reduce lung function, and increase the risk of heart attacks and strokes. Understanding the concentration of PM2.5 and PM10 in the air is crucial for assessing the risk to public health, especially in urban areas with high traffic or industrial activity."
        )

        Section(
            title = "VOCs - Volatile Organic Compounds",
            imageRes = R.drawable.voc,
            description = "Volatile Organic Compounds (VOCs) encompass a wide range of chemicals that vaporize at room temperature. Sources include everything from paint and cleaning supplies to vehicle emissions and industrial processes. Some VOCs are harmless, while others, like benzene and formaldehyde, can be toxic even at low levels. Long-term exposure to harmful VOCs can lead to serious health issues, including liver and kidney damage, nervous system impairment, and increased cancer risk. Monitoring VOC levels, particularly indoors where they can accumulate, is essential for maintaining a healthy living environment."
        )

        Section(
            title = "Temperature and Humidity",
            imageRes = R.drawable.temp_humd,
            description = "Temperature and humidity greatly influence air quality and comfort. Higher temperatures can accelerate the release of pollutants and the formation of ground-level ozone, a key component of smog. Humidity affects the concentration of pollutants like PM2.5; high humidity levels can lead to the formation of secondary particulate matter, exacerbating air quality issues. Both high and low humidity levels can affect respiratory health and comfort. Maintaining an optimal indoor climate not only ensures comfort but also plays a role in sustaining good air quality."
        )

        Section(
            title = "Air Quality Categories",
            imageRes = R.drawable.air_cat,
            description = """
            Air quality is categorized into four main groups to simplify the understanding of its impact on health:
            
            • Very Good: Indicates low levels of pollutants. Air quality is considered satisfactory, and air pollution poses little or no risk.
            
            • Moderate: Air quality is acceptable; however, there might be a moderate health concern for a very small number of individuals who are unusually sensitive to air pollution.
            
            • Poor: Members of sensitive groups may experience health effects, but the general public is unlikely to be affected. Children, the elderly, and individuals with respiratory or heart conditions should reduce prolonged or heavy exertion.
            
            • Very Poor: Health warnings of emergency conditions. The entire population is likely to be affected, and it is advised to limit outdoor activities, keep windows closed, and use air purifiers indoors.
        """.trimIndent()
        )
    }
}

@Composable
fun Section(title: String, imageRes: Int, description: String) {
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = title,
        fontSize = 20.sp,
        color = Color.DarkGray,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    Image(
        painter = rememberAsyncImagePainter(model = imageRes),
        contentDescription = title,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    )
    Text(
        text = if (expanded) description else "${description.take(100)}...",
        fontSize = 16.sp,
        color = Color.Black,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    TextButton(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(text = if (expanded) "Read less" else "Read more")
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Preview
@Composable
fun EducationPagePreview() {
    EducationPage(navController = NavHostController(LocalContext.current))
}
