package com.chrismott.anxious

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chrismott.anxious.ui.theme.AnxiousTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin

private const val lightYellow = 0xFFF9DBBD
private const val lightPink = 0xFFFFA5AB
private const val pink = 0xFFDA627D
private const val darkPink = 0xFFA53860
private const val wine = 0xFF450920

@Composable
fun HistoryIcon() {
    Icon(
        painter = painterResource(R.drawable.baseline_history_24),
        contentDescription = "history"
    )
}


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnxiousTheme {
                val navController = rememberNavController()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(lightPink),
                ) {
                    Column(Modifier.padding(top = 80.dp, start = 20.dp, end = 20.dp)) {

                        Greeting("chris")
                        NavHost(navController = navController, startDestination = "home") {
                            composable("home") {
                                Question("how’s anxiety?", Modifier.padding(top = 20.dp))
                                RadialSlider(Modifier.padding(top = 200.dp)) {
                                    navController.navigate(
                                        "when"
                                    )
                                }
                            }
                            composable("when") {
                                Question("when was it?", Modifier.padding(top = 20.dp))
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Row() {
        Text(
            text = "hey, ",
            modifier = modifier,
            fontSize = 40.sp,
            lineHeight = 40.sp,
            color = Color(pink),
            fontWeight = FontWeight.Light
        )
        Text(
            text = "$name!",
            modifier = modifier,
            fontSize = 40.sp,
            lineHeight = 40.sp,
            color = Color(pink),
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun Question(prompt: String, modifier: Modifier) {
    Text(
        text = prompt,
        modifier = modifier,
        fontSize = 50.sp,
        lineHeight = 50.sp,
        color = Color(darkPink),
        fontWeight = FontWeight.Medium
    )
}

//@Preview(showBackground=true, backgroundColor = lightPink)
@Composable
fun RadialSlider(modifier: Modifier = Modifier, letGo: () -> Unit = {}) {
    var radius by remember {
        mutableStateOf(0f)
    }

    var shapeCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var handleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var angle by remember {
        mutableStateOf(180.0)
    }

    val gap = 80f
    val start = 90f + (gap / 2)
    val handleEnd = 90f - (gap / 2)
    val end = 360 - gap

    var score by remember {
        mutableStateOf(0)
    }

    Box(modifier = modifier) {

        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(onDragEnd = {
                            Log.d("complete", score.toString())
                            letGo()
                        }) { change, dragAmount ->
                            handleCenter += dragAmount

                            angle = getRotationAngle(handleCenter, shapeCenter, start, handleEnd)
                            change.consume()
                        }
                    }
                    .padding(30.dp)
            ) {
                shapeCenter = center
                radius = size.minDimension / 2

                val x = (shapeCenter.x + cos(Math.toRadians(angle)) * radius).toFloat()
                val y = (shapeCenter.y + sin(Math.toRadians(angle)) * radius).toFloat()

                handleCenter = Offset(x, y)

                // outline
                drawArc(
                    color = Color(pink),
                    style = Stroke(30f),
                    startAngle = start,
                    sweepAngle = end,
                    useCenter = false,
                )

                val newAngle = getSweepAngle(angle, gap)
                score = round((newAngle / 280) * 10).toInt()
                // selected
                drawArc(
                    color = Color(darkPink),
                    startAngle = 90f + (gap / 2),
                    sweepAngle = (newAngle).toFloat(),
                    useCenter = false,
                    style = Stroke(30f),
                )
                // handle
                drawCircle(color = Color(lightPink), center = handleCenter, radius = 60f)
                drawCircle(color = Color(darkPink), center = handleCenter, radius = 50f)
            }

            Text(
                score.toString(),
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 80.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                color = Color(darkPink),
                fontSize = 100.sp
            )
            val notes = arrayOf(
                "totally fine",
                "bit iffy",
                "slightly worried",
                "stressed",
                "worried",
                "can't focus",
                "struggling",
                "struggling a lot",
                "pretty dysfunctional",
                "can't function much",
                "can't function"
            )
            Text(
                notes[score],
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 200.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                color = Color(darkPink),
                fontSize = 20.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = lightPink)
@Composable
fun CornerSlider(modifier: Modifier = Modifier, letGo: () -> Unit = {}) {
    var radius by remember {
        mutableStateOf(0f)
    }

    var shapeCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var handleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var angle by remember {
        mutableStateOf(225.0)
    }

    var score by remember {
        mutableStateOf(0)
    }
    Box(
        modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(onDragEnd = {
                        letGo()
                    }) { change, dragAmount ->
                        handleCenter += dragAmount

                        angle = getRotationAngle(handleCenter, shapeCenter, -180f, -90f)
                        change.consume()
                    }
                }
                .padding(0.dp)
        ) {
            shapeCenter = center + Offset(x = 530.0f, y = 530.0f)
            radius = 740f

            val x = (shapeCenter.x + cos(Math.toRadians(angle)) * radius).toFloat()
            val y = (shapeCenter.y + sin(Math.toRadians(angle)) * radius).toFloat()

            handleCenter = Offset(x, y)

            // outline amount
            drawArc(
                color = Color(pink),
                style = Stroke(50f),
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = false,
                size = Size(1500f, 1500f),
                topLeft = Offset(x = 330f, y = 330f)
            )

            val newAngle = getSweepAngle(angle, 180f)
            score = round((newAngle / 90) * 10).toInt()

            // filled amount
            drawArc(
                color = Color(pink),
                startAngle = 180f,
                sweepAngle = (newAngle).toFloat(),
                useCenter = true,
                size = Size(1500f, 1500f),
                topLeft = Offset(x = 330f, y = 330f)
            )

            // filled corner
            drawArc(
                color = Color(darkPink),
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = true,
                size = Size(750f, 750f),
                topLeft = Offset(x = 710f, y = 710f)
            )
            // selected outline
            drawArc(
                color = Color(darkPink),
                startAngle = 180f,
                sweepAngle = (newAngle).toFloat(),
                useCenter = false,
                style = Stroke(50f),
                size = Size(1500f, 1500f),
                topLeft = Offset(x = 330f, y = 330f)
            )
            // handle
            drawCircle(color = Color(lightPink), center = handleCenter, radius = 80f)
            drawCircle(color = Color(darkPink), center = handleCenter, radius = 65f)
            drawCircle(color = Color(lightPink), center = handleCenter, radius = 45f)

            // time arc
            drawArc(
                color = Color(lightYellow),
                startAngle = -90f,
                sweepAngle = -5f,
                useCenter = false,
                style = Stroke(100f, cap = StrokeCap.Round),
                size = Size(1860f, 1860f),
                topLeft = Offset(x = 150f, y = 150f)
            )

        }
        HistoryIcon()
        Text(
            score.toString(),
            fontSize = 50.sp,
            textAlign = TextAlign.Right,
            color = Color(lightYellow),
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 20.dp, top = 310.dp)
        )
    }

}

private fun getSweepAngle(angle: Double, gap: Float): Double {
    var newAngle = angle
    if (angle < 90) {
        newAngle = angle + 360 - 90f - (gap / 2)
    } else {
        newAngle = angle - 90f - (gap / 2)
    }
    return newAngle
}

private fun getRotationAngle(
    currentPosition: Offset,
    center: Offset,
    lowerLimit: Float,
    upperLimit: Float
): Double {
    val (dx, dy) = currentPosition - center
    val theta = atan2(dy, dx).toDouble()

    var angle = Math.toDegrees(theta)
    Log.d("degrees", angle.toString())
    if (angle < lowerLimit || angle > 0) {
        angle = lowerLimit.toDouble()
    }
    if (angle > upperLimit) {
        angle = upperLimit.toDouble()
    }
    return angle
}
