package com.example.supercalendar.activities

import EventEntity
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
enum class LookType { WeeklyView, MonthlyView, YearView }

val allMonths: List<Month> = Month.entries.toList()
val currentMonth = LocalDate.now().month.toString()

val currentLook = LookType.WeeklyView

fun CheckToday(day : LocalDateTime, currentDay : Int): Boolean {

    if(day.dayOfMonth == currentDay){
        return true
    }

    return false
}

@Composable
fun appNavigation(): NavHostController {
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
fun DayDetailsScreen(
    date: String,
    navController: NavHostController,
    viewModel: DayDetailsViewModel = viewModel() // Automates lifecycle management
) {
    val context = LocalContext.current
    var eventInput by remember { mutableStateOf("") }
    val currentEvents by viewModel.savedEvents.collectAsState()

    // Trigger loading once when screen opens or date string shifts
    LaunchedEffect(date) {
        viewModel.loadEvents(context, date)
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Детали дня",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Выбранная дата: $date",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text box input where the user writes down their schedule items
            OutlinedTextField(
                value = eventInput,
                onValueChange = { eventInput = it },
                label = { Text("Введите событие") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // HERE is your completed request button
            Button(
                onClick = {
                    viewModel.addEvent(context, date, eventInput)
                    eventInput = "" // Wipes input field on database submission
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить событие")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "События на этот день:", fontWeight = FontWeight.Bold)

            // Dynamic scroll container reading updates out of the DB layer
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(currentEvents) { event ->
                    Text(
                        text = "• ${event.description}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Button(
                        onClick = (event.deleteEvent())
                    ) {
                        Text("delete")
                    }
                }
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад в календарь")
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем Room
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

class DayDetailsViewModel : ViewModel() {

    private val _savedEvents = MutableStateFlow<List<EventEntity>>(emptyList())
    val savedEvents: StateFlow<List<EventEntity>> = _savedEvents

    // Automatically load existing events when opening the details screen
    fun loadEvents(context: android.content.Context, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            _savedEvents.value = db.eventDao.getEventsForDate(date)
        }
    }

    // Insert an event and refresh the displayed list
    fun addEvent(context: android.content.Context, date: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            db.eventDao.insertEvent(EventEntity(date = date, description = text))
            // Reload updated data
            loadEvents(context, date)
        }
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
                        text = timeInstance.month.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

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
