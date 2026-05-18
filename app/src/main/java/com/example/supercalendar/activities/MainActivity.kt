package com.example.supercalendar.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.supercalendar.ui.theme.SuperCalendarTheme
import java.time.Month
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


enum class LookType {DailyView, MonthlyView, YearView}

val currentLook = LookType.DailyView


@Composable
fun appNavigation(): NavHostController {
    // 1. Создаем контроллер навигации
    val navController = rememberNavController()

    // 2. Создаем контейнер и задаем стартовый экран (startDestination)
    NavHost(navController = navController, startDestination = "calendar") {

        // Главный экран календаря
        composable("calendar") {
            // Передаем navController внутрь, чтобы экран мог вызывать другие экраны
            CalendarWrap(navController)
        }

        // Глубокий экран конкретного события
        composable("event_details") {
            DayDetailsScreen(navController)
        }
    }

    return navController
}

@Composable
fun DayDetailsScreen(x0: NavHostController) {
    TODO("Not yet implemented")
}


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperCalendarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    // Передали paddingValues из Scaffold, чтобы контент не залезал под системные бары
                    Box(modifier = Modifier.padding(paddingValues)) {
                        val navController = appNavigation()
                        CalendarWrap(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(onLookChanged: (LookType) -> Unit){
  Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {},
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            // Иконка бургера
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(18.dp) // Аккуратный размер для иконки в кнопке
            )

            // Небольшой отступ между иконкой и текстом
            Spacer(modifier = Modifier.width(8.dp))
        }
        Button(
            onClick = {onLookChanged(LookType.DailyView)},
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Daily", maxLines = 1)
        }
        Button(
            onClick = {onLookChanged(LookType.MonthlyView)},
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Monthly", maxLines = 1)
        }
        Button(
            onClick = {onLookChanged(LookType.YearView)},
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Year", maxLines = 1)
        }
    }
}


@Composable
fun CalendarElement(day: Number, month: String, navController: NavController){
    Row(
        modifier = Modifier
            .clickable {
                navController.navigate("");
            }
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp) // Внешний отступ между карточками
            .clip(RoundedCornerShape(12.dp))             // Срезаем углы, чтобы эффект клика не вылезал наружу
            .background(MaterialTheme.colorScheme.surfaceVariant) // Задаем фон карточки
            .clickable(onClick = { })                     // Делаем кликабельным (эффект волны будет внутри)
            .padding(16.dp),                              // Внутренний отступ для текста
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "День: $day",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = month,
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}



@Composable
fun CalendarWrap(navController: NavHostController) {
    var currentLook by remember { mutableStateOf(LookType.DailyView) }
    Column(modifier = Modifier.fillMaxSize()) {
        CalendarHeader(onLookChanged = {newLook -> currentLook = newLook})
        when(currentLook){
            LookType.DailyView -> {}
            LookType.MonthlyView -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Month.entries.forEach { month ->
                        items(1) { index ->
                            CalendarElement(
                                day = index + 1,
                                month = month.toString(),
                                navController = appNavigation()
                            )
                        }
                    }
                }
            }
            LookType.YearView -> {}
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    SuperCalendarTheme {
        CalendarWrap(navController = appNavigation())
    }
}
