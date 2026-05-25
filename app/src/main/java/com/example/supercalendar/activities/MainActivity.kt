package com.example.supercalendar.activities

import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.supercalendar.ui.theme.SuperCalendarTheme
import java.time.Month
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.supercalendar.data.AppDatabase
import java.time.LocalDate
import java.time.LocalDateTime

enum class LookType { WeeklyView, MonthlyView, YearView }

val allMonths: List<Month> = Month.entries.toList()
val currentMonth: Int = LocalDate.now().month.value

val initMonth = allMonths[currentMonth]
var monthSwitch: Int = initMonth.value;
var currentPage = monthSwitch;
fun switchMonth(page: Int) : Month {
    currentPage += page;
    if (currentPage <= 0){
        currentPage = 11
    } else if(currentPage >= 11 ) {
        currentPage = 0
    }

    Log.d("page", page.toString())
    Log.d("monthSwitch", monthSwitch.toString())
    Log.d("currentPage", currentPage.toString())
    Log.d("allMonths", allMonths[currentPage].toString())

    return allMonths[currentPage]
}

fun CheckToday(day : LocalDateTime, currentDay : Int): Boolean {

    if(day.dayOfMonth == currentDay){
        return true
    }

    return false
}
@Composable
fun appNavigation(): NavHostController {
    switchMonth(currentMonth)
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "calendar") {
        composable("calendar") {
            CalendarWrap(navController)
        }
        composable(
            route = "event_details/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getString("date") ?: "unknown date"
            DayDetailsScreen(date = selectedDate, navController = navController)
        }
    }
    return navController
}

@Composable
fun DayDetailsScreen(date: String, navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Детали дня",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Выбранная дата: $date",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Назад в календарь")
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDatabase.getDatabase(applicationContext)

        enableEdgeToEdge()
        setContent {
            SuperCalendarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        appNavigation()
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(onLookChanged: (LookType) -> Unit) {
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
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Button(
            onClick = { onLookChanged(LookType.WeeklyView) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Daily", maxLines = 1)
        }
        Button(
            onClick = { onLookChanged(LookType.MonthlyView) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Monthly", maxLines = 1)
        }
        Button(
            onClick = { onLookChanged(LookType.YearView) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Year", maxLines = 1)
        }
    }
}

@Composable
fun CalendarElement(day: Int, monthName: String, year: Int, navController: NavController) {
    val formattedDate = "$year-$monthName-$day"

    // Аналог тернарного оператора: если это сегодня, подсвечиваем контейнер цветом PrimaryContainer
    val cellBackground = if (CheckToday(day = LocalDateTime.now(), currentDay = day )) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(cellBackground) // Применили цвет
            .clickable {
                navController.navigate("event_details/$formattedDate")
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$day",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (CheckToday(day = LocalDateTime.now(), currentDay = day )) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (CheckToday(day = LocalDateTime.now(), currentDay = day )) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
fun CalendarWrap(navController: NavHostController) {
    var currentLook by remember { mutableStateOf(LookType.WeeklyView) }
    // Получаем текущее время локально внутри Composable-контекста
    val timeInstance = remember { LocalDateTime.now() }

    Column(modifier = Modifier.fillMaxSize()) {
        CalendarHeader(onLookChanged = { newLook -> currentLook = newLook })
        when (currentLook) {
            LookType.WeeklyView -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Дневной/Недельный вид")
                }
            }

            LookType.MonthlyView -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = timeInstance.year.toString() + " / " + timeInstance.month.toString() + " " + "(${currentMonth})" + " / " + timeInstance.dayOfMonth ,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                        Button(
                            onClick = { switchMonth(1) },
                            //modifier = Modifier.fillMaxSize(),
                        ) { Text("<") }
                        Button(
                        onClick = { switchMonth(-1) },
                        //modifier = Modifier.fillMaxSize(),
                    )  { Text(">") }



                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(timeInstance.month.length(timeInstance.toLocalDate().isLeapYear)) { index ->
                            CalendarElement(
                                day = index + 1,
                                monthName = timeInstance.month.toString(),
                                year = timeInstance.year,
                                navController = navController
                            )
                        }
                    }
                }
            }

            LookType.YearView -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Month.entries.size) { index ->
                        val currentMonthObj = Month.entries[index]
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .clickable { currentLook = LookType.MonthlyView },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentMonthObj.toString().take(3),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    SuperCalendarTheme {
        CalendarWrap(navController = rememberNavController())
    }
}
