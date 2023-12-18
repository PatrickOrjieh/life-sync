package com.example.aerosense_app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aerosense_app.ui.components.NavBar

//Github Copilot used while writing this code

@Composable
fun History(navController: NavHostController){
    var airPercent by remember { mutableFloatStateOf(0f) }
    var pmTwo by remember { mutableIntStateOf(0) }
    var pmTwoColor by remember { mutableStateOf(Color(0xff1e1e1e)) }
    var highVOC by remember { mutableIntStateOf(0) }
    var percentageColor by remember { mutableStateOf(Color(0xff1e1e1e)) }
    var voc by remember { mutableFloatStateOf(0.0F) }
    var vocColor by remember { mutableStateOf(Color(0xff1e1e1e)) }

    //hardcoded values for now
    airPercent = 80f
    pmTwo = 100
    highVOC = 50

    NavBar(navController)

    Text(
        text = "History",
        textAlign = TextAlign.Center,
        color = Color(0xff1e1e1e),
        style = TextStyle(
            fontSize = 35.sp,
            fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(top = 90.dp)
    )

    Text(
        text = "Average Weekly Air Quality",
        textAlign = TextAlign.Center,
        color = Color(0xff1e1e1e),
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(top = 170.dp)
    )

    DrawGraph()

    if(pmTwo >=0 && pmTwo <= 10){
        //Make the color green
        pmTwoColor = Color(0xffff50f413 )
    }
    else if(pmTwo > 10 && pmTwo <= 20) {
        pmTwoColor = Color(0xffffa629)
    }
    else if(pmTwo > 20 && pmTwo <= 25){
        pmTwoColor = Color(0xfff24822)
    }
    else{
        pmTwoColor = Color(0xffaf21d2)
    }

    if(voc > 0.0 && voc <= 0.5){
        //Make the color green
        vocColor = Color(0xffff50f413 )
    }
    else if(voc > 0.5 && voc <= 1.0) {
        vocColor = Color(0xffffa629)
    }
    else if(voc > 1.0 && voc <= 2.0){
        vocColor = Color(0xfff24822)
    }
    else{
        vocColor = Color(0xffaf21d2)
    }

    Text(
        text = "Weekly Averages:",
        textAlign = TextAlign.Center,
        color = Color(0xff1e1e1e),
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(top = 520.dp)
    )

    Box(
        modifier = Modifier.padding(top = 20.dp)
    ){

    Box(
        modifier = Modifier.padding(top = 560.dp)
            .padding(start = 20.dp)
    ) {
        Text(
            text = "Cleanliness",
            color = getAirQualityColor(airPercent),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
        )

        Text(
            text = "$airPercent%",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
            ),
            modifier = Modifier.padding(top = 30.dp, start= 30.dp)
        )
    }

    Box(
        modifier = Modifier.padding(top = 560.dp)
            .padding(start = 175.dp)
    ) {
        Text(
            text = "PM",
            color = pmTwoColor,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
        )

        Text(
            text = "$pmTwo µg/m^3",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
            ),
            modifier = Modifier.padding(top = 30.dp)
                .offset(x = -35.dp)
        )

//        Text(
//            text = "-> Date: 11/12/23",
//            color = Color(0xff1e1e1e),
//            style = TextStyle(
//                fontSize = 20.sp,
//            ),
//            modifier = Modifier.padding(top = 60.dp)
//        )
    }

    Box(
        modifier = Modifier.padding(top = 560.dp)
            .padding(start = 280.dp)
    ) {
        Text(
            text = "VOC",
            color = vocColor,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
        )

        Text(
            text = "$highVOC ppb",
            color = Color(0xff1e1e1e),
            style = TextStyle(
                fontSize = 20.sp,
            ),
            modifier = Modifier.padding(top = 30.dp, start = 10.dp)
                .offset(x = -20.dp)
        )
    }

//        Text(
//            text = "-> Date: 10/12/23",
//            color = Color(0xff1e1e1e),
//            style = TextStyle(
//                fontSize = 20.sp,
//            ),
//            modifier = Modifier.padding(top = 60.dp)
//        )
    }

}

//Parts of graph code adapted from: https://stackoverflow.com/questions/58589507/draw-simple-xy-graph-plot-with-kotlin-without-3rd-party-library
@Composable
fun DrawGraph() {
    var yaxisDifference by remember { mutableIntStateOf(48) }

    // Sample data for Monday to Sunday
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Sample data for percentages
    val percentages = listOf(50f, 80f, 30f, 60f, 90f, 20f, 70f)

    // Scaling factor for a larger graph
    val scaleFactor = 2.5f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(start= 5.dp)
            .requiredHeight(230.dp)
            .requiredWidth(280.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val maxPercentage = 100f
        val maxDayIndex = daysOfWeek.size - 1

        val scaleX = canvasWidth / (maxDayIndex * scaleFactor)
        val scaleY = canvasHeight / maxPercentage

        // Create Paint object
        val paint = Paint().asFrameworkPaint().apply {
            color = Color.Black.toArgb()
            textSize = 12.sp.toPx()
        }

        // Draw X-axis
        drawLine(
            color = Color.Black,
            start = Offset(0f, canvasHeight),
            end = Offset(canvasWidth, canvasHeight),
            strokeWidth = 2f
        )

        // Draw Y-axis
        drawLine(
            color = Color.Black,
            start = Offset(0f, 0f),
            end = Offset(0f, canvasHeight),
            strokeWidth = 2f
        )

        for (i in 0..5) {
            // Calculate y-axis position based on index
            val yCoord = canvasHeight - i * 20f * scaleY

            // Draw axis line
            drawLine(
                color = Color.Gray,
                start = Offset(0f, yCoord),
                end = Offset(canvasWidth, yCoord),
                strokeWidth = 1f
            )
        }


        // Draw axis labels and align with data points
        daysOfWeek.forEachIndexed { index, day ->
            val xCoord = index * scaleX * scaleFactor
            val yCoord = canvasHeight - percentages[index] * scaleY

            // Draw X-axis label
            drawContext.canvas.nativeCanvas.drawText(
                day,
                xCoord,
                canvasHeight + 16.dp.toPx(),
                paint
            )
        }

        val percentageLabels = listOf("0%", "20%", "40%", "60%", "80%", "100%")
        percentageLabels.forEachIndexed { index, label ->
            val xCoord = -35.dp.toPx()
            val yCoord = canvasHeight - index * 20f * scaleY

            // Draw Y-axis label
            drawContext.canvas.nativeCanvas.drawText(
                label,
                xCoord,
                yCoord,
                paint
            )
        }

        // Draw the graph line
        val path = Path()
        daysOfWeek.forEachIndexed { index, _ ->
            val xCoord = index * scaleX * scaleFactor
            val yCoord = canvasHeight - percentages[index] * scaleY
            val point = Offset(xCoord, yCoord)

            if (index == 0) {
                path.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
            }
        }

        drawPath(path, color = Color.Blue, style = Stroke(4f))
    }
}

