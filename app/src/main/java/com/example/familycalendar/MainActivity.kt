package com.example.familycalendar

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.foundation.layout.statusBarsPadding

private object AuroraPalette {
    val DeepIndigo = Color(0xFF1D1A39)
    val RichPurple = Color(0xFF451952)
    val PlumBurgundy = Color(0xFF662549)
    val RoseBerry = Color(0xFFAE445A)
    val WarmAmber = Color(0xFFF39F5A)
    val SoftPowder = Color(0xFFE8BCB9)

    val GlassSurfaceStrong = Color(0xFF28204A).copy(alpha = 0.80f)
    val GlassSurfaceSoft = Color(0xFF231C42).copy(alpha = 0.60f)
    val GlassBorder = SoftPowder.copy(alpha = 0.22f)
    val GlassHighlight = Color.White.copy(alpha = 0.08f)

    val DadAccent = Color(0xFF7CC6FE)
    val MomAccent = Color(0xFFF472B6)
    val DaughterAccent = Color(0xFF34D399)
    val SharedAccent = WarmAmber
}

private val AuroraLiquidColorScheme = darkColorScheme(
    primary = AuroraPalette.WarmAmber,
    onPrimary = AuroraPalette.DeepIndigo,
    primaryContainer = AuroraPalette.PlumBurgundy,
    onPrimaryContainer = AuroraPalette.SoftPowder,
    secondary = AuroraPalette.RoseBerry,
    onSecondary = Color.White,
    tertiary = AuroraPalette.SoftPowder,
    onTertiary = AuroraPalette.DeepIndigo,
    background = AuroraPalette.DeepIndigo,
    onBackground = Color(0xFFF9F5F6),
    surface = AuroraPalette.GlassSurfaceStrong,
    onSurface = Color(0xFFF9F5F6),
    surfaceVariant = AuroraPalette.GlassSurfaceSoft,
    onSurfaceVariant = AuroraPalette.SoftPowder.copy(alpha = 0.82f),
    outline = AuroraPalette.GlassBorder
)

private enum class AppDestination(
    val label: String,
    val icon: ImageVector
) {
    HOME("Главная", Icons.Default.Home),
    CALENDAR("Календарь", Icons.Default.CalendarMonth),
    ADD("Добавить", Icons.Default.Add),
    FAMILY("Семья", Icons.Default.Group),
    SETTINGS("Настройки", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AuroraLiquidColorScheme) {
                AuroraBackdrop {
                    FamilyCalendarScreen()
                }
            }
        }
    }
}

data class FamilyEvent(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val author: String = "",
    val colorHex: Long = 0xFFF39F5A
)

data class WorkShift(
    val id: String = "",
    val person: String = "",
    val date: String = ""
)

internal data class MonthPage(
    val year: Int,
    val month: Int,
    val label: String,
    val cells: List<Date?>
)

class CalendarViewModel : ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .build()
            )
            .build()
    }
    private val eventsCollection = db.collection("events")
    private val shiftsCollection = db.collection("work_schedules")

    private val _events = MutableStateFlow<List<FamilyEvent>>(emptyList())
    val events = _events.asStateFlow()

    private val _shifts = MutableStateFlow<List<WorkShift>>(emptyList())
    val shifts = _shifts.asStateFlow()

    init {
        eventsCollection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val list = snapshot.documents.mapNotNull { doc ->
                doc.toObject<FamilyEvent>()?.copy(id = doc.id)
            }
            _events.value = list
        }

        shiftsCollection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val list = snapshot.documents.mapNotNull { doc ->
                doc.toObject<WorkShift>()?.copy(id = doc.id)
            }
            _shifts.value = list
        }
    }

    private fun colorForAuthor(author: String): Long = when (author) {
        "Папа" -> 0xFF7CC6FE
        "Мама" -> 0xFFF472B6
        "Дочка" -> 0xFF34D399
        else -> 0xFFF39F5A
    }

    fun addEvent(title: String, date: String, time: String, author: String) {
        val docRef = eventsCollection.document()
        val newEvent = FamilyEvent(
            id = docRef.id,
            title = title,
            date = date,
            time = time,
            author = author,
            colorHex = colorForAuthor(author)
        )
        docRef.set(newEvent).addOnFailureListener { e ->
            Log.e("FamilyCalendar", "Ошибка добавления события", e)
        }
    }

    fun updateEvent(id: String, title: String, date: String, time: String, author: String) {
        if (id.isBlank()) return
        val updatedFields = mapOf(
            "title" to title,
            "date" to date,
            "time" to time,
            "author" to author,
            "colorHex" to colorForAuthor(author)
        )
        eventsCollection.document(id)
            .update(updatedFields)
            .addOnFailureListener { e ->
                Log.e("FamilyCalendar", "Ошибка обновления события", e)
            }
    }

    fun deleteEvent(eventId: String) {
        if (eventId.isBlank()) return
        eventsCollection.document(eventId).delete()
    }

    fun replaceWorkShifts(person: String, newDates: Set<String>) {
        val batch = db.batch()
        val existingForPerson = _shifts.value.filter { it.person == person }
        existingForPerson.forEach { oldShift ->
            if (oldShift.date !in newDates && oldShift.id.isNotBlank()) {
                batch.delete(shiftsCollection.document(oldShift.id))
            }
        }
        newDates.forEach { dateStr ->
            val docId = "${person}_${dateStr.replace(".", "-")}"
            val docRef = shiftsCollection.document(docId)
            batch.set(docRef, WorkShift(id = docId, person = person, date = dateStr))
        }
        batch.commit().addOnFailureListener { e ->
            Log.e("FamilyCalendar", "Ошибка обновления графика работы", e)
        }
    }

    fun deleteWorkShift(shiftId: String) {
        if (shiftId.isBlank()) return
        shiftsCollection.document(shiftId).delete()
    }
}

@Composable
private fun AuroraBackdrop(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val animationsEnabled = remember(context) {
        val scale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        scale > 0f
    }

    val phasePrimary: Float
    val phaseSecondary: Float
    val phaseWarm: Float

    if (animationsEnabled) {
        val transition = rememberInfiniteTransition(label = "aurora_live_flow")

        val p1 by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 11000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "aurora_p1"
        )
        val p2 by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 15000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "aurora_p2"
        )
        val p3 by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 19000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "aurora_p3"
        )
        phasePrimary = p1
        phaseSecondary = p2
        phaseWarm = p3
    } else {
        phasePrimary = 0.35f
        phaseSecondary = 0.55f
        phaseWarm = 0.45f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuroraPalette.DeepIndigo)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Глубокая базовая подложка
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AuroraPalette.DeepIndigo,
                        AuroraPalette.RichPurple.copy(alpha = 0.65f),
                        AuroraPalette.DeepIndigo
                    )
                )
            )

            // 1. Крупное фиолетовое облако (#451952 -> #662549)
            val center1 = Offset(
                x = w * (0.12f + 0.58f * phasePrimary),
                y = h * (0.10f + 0.24f * (1f - phaseSecondary))
            )
            val radius1 = w * (0.78f + 0.16f * phaseSecondary)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuroraPalette.RichPurple.copy(alpha = 0.82f),
                        AuroraPalette.PlumBurgundy.copy(alpha = 0.34f),
                        Color.Transparent
                    ),
                    center = center1,
                    radius = radius1
                ),
                radius = radius1,
                center = center1
            )

            // 2. Розово-бордовое световое пятно (#AE445A)
            val center2 = Offset(
                x = w * (0.88f - 0.62f * phaseSecondary),
                y = h * (0.30f + 0.32f * phasePrimary)
            )
            val radius2 = w * (0.72f + 0.18f * phasePrimary)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuroraPalette.RoseBerry.copy(alpha = 0.48f),
                        AuroraPalette.PlumBurgundy.copy(alpha = 0.24f),
                        Color.Transparent
                    ),
                    center = center2,
                    radius = radius2
                ),
                radius = radius2,
                center = center2
            )

            // 3. Тёплый янтарный перелив (#F39F5A + #AE445A)
            val center3 = Offset(
                x = w * (0.20f + 0.60f * (1f - phaseWarm)),
                y = h * (0.72f - 0.28f * phaseSecondary)
            )
            val radius3 = w * (0.68f + 0.16f * phaseWarm)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuroraPalette.WarmAmber.copy(alpha = 0.26f),
                        AuroraPalette.RoseBerry.copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = center3,
                    radius = radius3
                ),
                radius = radius3,
                center = center3
            )

            // 4. Мягкий пудровый блик в верхней зоне (#E8BCB9)
            val center4 = Offset(
                x = w * (0.75f - 0.45f * phaseWarm),
                y = h * (0.14f + 0.18f * phasePrimary)
            )
            val radius4 = w * (0.55f + 0.12f * (1f - phasePrimary))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuroraPalette.SoftPowder.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center4,
                    radius = radius4
                ),
                radius = radius4,
                center = center4
            )
        }

        content()
    }
}

@Composable
private fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    containerColor: Color = AuroraPalette.DeepIndigo.copy(alpha = 0.24f),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val clickableMod = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AuroraPalette.RichPurple.copy(alpha = 0.30f),
                        containerColor,
                        AuroraPalette.DeepIndigo.copy(alpha = 0.34f)
                    )
                )
            )
            // Мягкий "линзовый" блик сверху-слева как в стекле Apple / Honor
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.16f),
                        AuroraPalette.SoftPowder.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(600f, 380f)
                )
            )
            // Направленный световой ободок по контуру стекла
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.42f),
                        AuroraPalette.SoftPowder.copy(alpha = 0.18f),
                        AuroraPalette.RoseBerry.copy(alpha = 0.14f),
                        Color.White.copy(alpha = 0.22f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(900f, 700f)
                ),
                shape = shape
            )
            .then(clickableMod)
    ) {
        // Тонкий верхний specular-блик по кромке
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.38f),
                            AuroraPalette.SoftPowder.copy(alpha = 0.16f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

private fun buildMonthPages(locale: Locale, monthsBack: Int = 3, monthsAhead: Int = 12): List<MonthPage> {
    val monthFormatter = SimpleDateFormat("LLLL yyyy", locale)
    val baseCal = Calendar.getInstance(locale).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.MONTH, -monthsBack)
    }

    val pages = mutableListOf<MonthPage>()
    val totalMonths = monthsBack + monthsAhead + 1

    repeat(totalMonths) {
        val year = baseCal.get(Calendar.YEAR)
        val month = baseCal.get(Calendar.MONTH)
        val rawTitle = monthFormatter.format(baseCal.time)
        val title = rawTitle.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

        val daysInMonth = baseCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = baseCal.get(Calendar.DAY_OF_WEEK)
        val leadingEmpty = (firstDayOfWeek - Calendar.MONDAY + 7) % 7

        val cells = mutableListOf<Date?>()
        repeat(leadingEmpty) { cells.add(null) }

        val dayCal = (baseCal.clone() as Calendar)
        for (day in 1..daysInMonth) {
            dayCal.set(Calendar.DAY_OF_MONTH, day)
            cells.add(dayCal.time)
        }

        pages.add(
            MonthPage(
                year = year,
                month = month,
                label = title,
                cells = cells
            )
        )

        baseCal.add(Calendar.MONTH, 1)
    }

    return pages
}

@Composable
private fun TodayHeroCard(
    todayDateStr: String,
    weekdayLabel: String,
    todayEventsCount: Int,
    nextEvent: FamilyEvent?,
    todayShifts: List<WorkShift>,
    onOpenToday: () -> Unit,
    onAddEvent: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        cornerRadius = 24.dp,
        containerColor = AuroraPalette.PlumBurgundy.copy(alpha = 0.42f),
        onClick = onOpenToday
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "СЕГОДНЯ • ${weekdayLabel.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = AuroraPalette.WarmAmber
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = todayDateStr,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AuroraPalette.WarmAmber.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, AuroraPalette.WarmAmber.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = "Событий: $todayEventsCount",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AuroraPalette.WarmAmber,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (nextEvent != null) {
                Text(
                    text = "Ближайшее: ${nextEvent.title} (${nextEvent.time})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AuroraPalette.SoftPowder,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "На сегодня свободных слотов больше нет или дел пока нет",
                        fontSize = 13.sp,
                        color = AuroraPalette.SoftPowder.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = onAddEvent,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text("+ План", color = AuroraPalette.WarmAmber, fontSize = 13.sp)
                    }
                }
            }

            if (todayShifts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    todayShifts.forEach { shift ->
                        val c = if (shift.person == "Папа") AuroraPalette.DadAccent else AuroraPalette.MomAccent
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = c.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "${shift.person} на смене",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = c,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedMonthCalendar(
    monthPages: List<MonthPage>,
    selectedDateStr: String,
    showAllEvents: Boolean,
    datesWithEvents: Set<String>,
    dadWorkDates: Set<String>,
    momWorkDates: Set<String>,
    dateFormatter: SimpleDateFormat,
    dayNumFormatter: SimpleDateFormat,
    locale: Locale,
    onSelectDate: (String) -> Unit
) {
    val initialPage = remember(monthPages) {
        val now = Calendar.getInstance(locale)
        val curYear = now.get(Calendar.YEAR)
        val curMonth = now.get(Calendar.MONTH)
        monthPages.indexOfFirst { it.year == curYear && it.month == curMonth }.coerceAtLeast(0)
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { monthPages.size }
    )
    val coroutineScope = rememberCoroutineScope()
    val weekDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    val todayStr = remember { dateFormatter.format(Calendar.getInstance(locale).time) }

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        cornerRadius = 22.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = CircleShape,
                    color = AuroraPalette.GlassSurfaceSoft,
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(
                        onClick = {
                            val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
                            coroutineScope.launch { pagerState.animateScrollToPage(prev) }
                        },
                        enabled = pagerState.currentPage > 0
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Предыдущий месяц")
                    }
                }

                Text(
                    text = monthPages.getOrNull(pagerState.currentPage)?.label.orEmpty(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = CircleShape,
                    color = AuroraPalette.GlassSurfaceSoft,
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(
                        onClick = {
                            val next = (pagerState.currentPage + 1).coerceAtMost(monthPages.lastIndex)
                            coroutineScope.launch { pagerState.animateScrollToPage(next) }
                        },
                        enabled = pagerState.currentPage < monthPages.lastIndex
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Следующий месяц")
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { wd ->
                    Text(
                        text = wd,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AuroraPalette.SoftPowder.copy(alpha = 0.78f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { pageIndex ->
                val page = monthPages[pageIndex]
                val rows = page.cells.chunked(7)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    rows.forEach { weekRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            for (col in 0 until 7) {
                                val cellDate = weekRow.getOrNull(col)
                                if (cellDate == null) {
                                    Box(modifier = Modifier.weight(1f).height(42.dp))
                                } else {
                                    val strDate = dateFormatter.format(cellDate)
                                    val isSelected = !showAllEvents && (strDate == selectedDateStr)
                                    val isToday = strDate == todayStr
                                    val hasEvents = strDate in datesWithEvents

                                    val cellBg by animateColorAsState(
                                        targetValue = when {
                                            isSelected -> AuroraPalette.WarmAmber
                                            isToday -> AuroraPalette.PlumBurgundy.copy(alpha = 0.55f)
                                            else -> Color.Transparent
                                        },
                                        label = "day_bg"
                                    )
                                    val cellTextColor = if (isSelected) {
                                        AuroraPalette.DeepIndigo
                                    } else {
                                        Color.White
                                    }
                                    val borderMod = if (isToday && !isSelected) {
                                        Modifier.border(
                                            1.dp,
                                            AuroraPalette.WarmAmber.copy(alpha = 0.75f),
                                            RoundedCornerShape(12.dp)
                                        )
                                    } else {
                                        Modifier
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(cellBg)
                                            .then(borderMod)
                                            .clickable { onSelectDate(strDate) },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = dayNumFormatter.format(cellDate),
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = cellTextColor
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                            if (strDate in dadWorkDates) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(AuroraPalette.DadAccent)
                                                )
                                            }
                                            if (strDate in momWorkDates) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(AuroraPalette.MomAccent)
                                                )
                                            }
                                            if (hasEvents) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            if (isSelected) cellTextColor else AuroraPalette.WarmAmber
                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = AuroraPalette.GlassBorder)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(AuroraPalette.DadAccent))
                    Text("Папа", fontSize = 11.sp, color = AuroraPalette.SoftPowder.copy(alpha = 0.85f))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(AuroraPalette.MomAccent))
                    Text("Мама", fontSize = 11.sp, color = AuroraPalette.SoftPowder.copy(alpha = 0.85f))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(AuroraPalette.WarmAmber))
                    Text("События", fontSize = 11.sp, color = AuroraPalette.SoftPowder.copy(alpha = 0.85f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyCalendarScreen(viewModel: CalendarViewModel = viewModel()) {
    val events by viewModel.events.collectAsState()
    val shifts by viewModel.shifts.collectAsState()
    val context = LocalContext.current

    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<FamilyEvent?>(null) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var notificationStatusHint by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationStatusHint = if (granted) {
            "Уведомления разрешены"
        } else {
            "Уведомления отключены в системе"
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(events) {
        events.forEach { event ->
            ReminderAlarmReceiver.scheduleReminder(
                context = context,
                title = event.title,
                dateStr = event.date,
                timeStr = event.time,
                author = event.author,
                minutesBefore = 60L
            )
        }
    }

    val locale = Locale("ru")
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", locale) }
    val dayNameFormatter = remember { SimpleDateFormat("EEE", locale) }
    val weekdayFullFormatter = remember { SimpleDateFormat("EEEE", locale) }
    val dayNumFormatter = remember { SimpleDateFormat("d", locale) }

    val todayStr = remember { dateFormatter.format(Date()) }
    val todayWeekday = remember {
        weekdayFullFormatter.format(Date()).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }

    val daysList = remember {
        val list = mutableListOf<Date>()
        val cal = Calendar.getInstance(locale).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        repeat(14) {
            list.add(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    var selectedDateStr by remember { mutableStateOf(todayStr) }
    var showAllEvents by remember { mutableStateOf(false) }
    var isCalendarExpanded by remember { mutableStateOf(false) }
    val monthPages = remember(locale) { buildMonthPages(locale, monthsBack = 3, monthsAhead = 12) }

    val authorsFilter = listOf("Все", "Папа", "Мама", "Дочка", "Общее")
    var selectedAuthor by remember { mutableStateOf("Все") }

    val datesWithEvents = remember(events) {
        events.map { it.date.trim() }.toSet()
    }

    val dadWorkDates = remember(shifts) {
        shifts.filter { it.person == "Папа" }.map { it.date.trim() }.toSet()
    }

    val momWorkDates = remember(shifts) {
        shifts.filter { it.person == "Мама" }.map { it.date.trim() }.toSet()
    }

    val todayEvents = remember(events, todayStr) {
        events.filter { it.date.trim() == todayStr }.sortedBy { it.time }
    }

    val todayShifts = remember(shifts, todayStr) {
        shifts.filter { it.date.trim() == todayStr }
    }

    val dayShifts = remember(shifts, selectedDateStr, showAllEvents) {
        if (showAllEvents) emptyList()
        else shifts.filter { it.date.trim() == selectedDateStr }
    }

    val filteredEvents = remember(events, selectedDateStr, showAllEvents, selectedAuthor, currentDestination) {
        events
            .filter { event ->
                val dateMatches = if (currentDestination == AppDestination.CALENDAR) {
                    event.date.trim() == selectedDateStr
                } else {
                    showAllEvents || event.date.trim() == selectedDateStr
                }
                val authorMatches = selectedAuthor == "Все" || event.author.trim() == selectedAuthor
                dateMatches && authorMatches
            }
            .sortedBy { it.time }
    }

    val hasNotificationPermission = remember(context, notificationStatusHint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    val canScheduleExactAlarms = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.canScheduleExactAlarms()
        } else {
            true
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Surface(
                color = AuroraPalette.GlassSurfaceSoft.copy(alpha = 0.78f),
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Наша семья",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "$todayWeekday, $todayStr",
                            fontSize = 12.sp,
                            color = AuroraPalette.SoftPowder
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    notificationStatusHint = if (canScheduleExactAlarms) {
                                        "Уведомления активны • Напоминание за 60 мин"
                                    } else {
                                        "Уведомления активны • Резервный режим будильника"
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Уведомления",
                                tint = if (hasNotificationPermission) AuroraPalette.WarmAmber else AuroraPalette.SoftPowder.copy(alpha = 0.6f)
                            )
                        }

                        TextButton(onClick = { showScheduleDialog = true }) {
                            Text(
                                text = "График",
                                color = AuroraPalette.WarmAmber,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = AuroraPalette.GlassSurfaceStrong.copy(alpha = 0.90f),
                tonalElevation = 0.dp
            ) {
                AppDestination.entries.forEach { dest ->
                    val isSelected = currentDestination == dest && dest != AppDestination.ADD
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (dest == AppDestination.ADD) {
                                showCreateDialog = true
                            } else {
                                currentDestination = dest
                                if (dest == AppDestination.CALENDAR) {
                                    isCalendarExpanded = true
                                    showAllEvents = false
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = dest.icon,
                                contentDescription = dest.label
                            )
                        },
                        label = {
                            Text(
                                text = dest.label,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AuroraPalette.WarmAmber,
                            selectedTextColor = AuroraPalette.WarmAmber,
                            indicatorColor = AuroraPalette.PlumBurgundy.copy(alpha = 0.65f),
                            unselectedIconColor = AuroraPalette.SoftPowder.copy(alpha = 0.72f),
                            unselectedTextColor = AuroraPalette.SoftPowder.copy(alpha = 0.72f)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentDestination == AppDestination.HOME || currentDestination == AppDestination.CALENDAR) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = AuroraPalette.WarmAmber,
                    contentColor = AuroraPalette.DeepIndigo
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить событие")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            notificationStatusHint?.let { hint ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = AuroraPalette.PlumBurgundy.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = hint,
                            fontSize = 12.sp,
                            color = AuroraPalette.SoftPowder,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = AuroraPalette.SoftPowder,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { notificationStatusHint = null }
                        )
                    }
                }
            }

            when (currentDestination) {
                AppDestination.HOME -> {
                    Spacer(modifier = Modifier.height(6.dp))
                    TodayHeroCard(
                        todayDateStr = todayStr,
                        weekdayLabel = todayWeekday,
                        todayEventsCount = todayEvents.size,
                        nextEvent = todayEvents.firstOrNull(),
                        todayShifts = todayShifts,
                        onOpenToday = {
                            showAllEvents = false
                            selectedDateStr = todayStr
                        },
                        onAddEvent = {
                            selectedDateStr = todayStr
                            showAllEvents = false
                            showCreateDialog = true
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (showAllEvents) "Предстоящие • Все дни" else "Дата: $selectedDateStr",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AuroraPalette.SoftPowder
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = showAllEvents,
                                onClick = { showAllEvents = !showAllEvents },
                                label = { Text(if (showAllEvents) "Все дни" else "По дню", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = AuroraPalette.GlassSurfaceSoft,
                                    labelColor = AuroraPalette.SoftPowder,
                                    selectedContainerColor = AuroraPalette.WarmAmber,
                                    selectedLabelColor = AuroraPalette.DeepIndigo
                                )
                            )

                            TextButton(
                                onClick = {
                                    showAllEvents = false
                                    selectedDateStr = todayStr
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Text("Сегодня", fontSize = 12.sp, color = AuroraPalette.WarmAmber)
                            }

                            FilterChip(
                                selected = isCalendarExpanded,
                                onClick = { isCalendarExpanded = !isCalendarExpanded },
                                label = {
                                    Text(if (isCalendarExpanded) "Свернуть" else "Месяц", fontSize = 12.sp)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isCalendarExpanded) Icons.Default.ExpandLess else Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = AuroraPalette.GlassSurfaceSoft,
                                    labelColor = AuroraPalette.SoftPowder,
                                    selectedContainerColor = AuroraPalette.PlumBurgundy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isCalendarExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        ExpandedMonthCalendar(
                            monthPages = monthPages,
                            selectedDateStr = selectedDateStr,
                            showAllEvents = showAllEvents,
                            datesWithEvents = datesWithEvents,
                            dadWorkDates = dadWorkDates,
                            momWorkDates = momWorkDates,
                            dateFormatter = dateFormatter,
                            dayNumFormatter = dayNumFormatter,
                            locale = locale,
                            onSelectDate = { pickedDate ->
                                showAllEvents = false
                                selectedDateStr = pickedDate
                                isCalendarExpanded = false
                            }
                        )
                    }

                    if (!isCalendarExpanded) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(daysList) { dateObj ->
                                val strDate = dateFormatter.format(dateObj)
                                val isSelected = !showAllEvents && (strDate == selectedDateStr)
                                val hasEvents = strDate in datesWithEvents

                                val bgColor by animateColorAsState(
                                    targetValue = if (isSelected) {
                                        AuroraPalette.WarmAmber
                                    } else {
                                        AuroraPalette.GlassSurfaceSoft
                                    },
                                    label = "week_pill_bg"
                                )
                                val contentColor = if (isSelected) {
                                    AuroraPalette.DeepIndigo
                                } else {
                                    Color.White
                                }

                                Column(
                                    modifier = Modifier
                                        .width(56.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(bgColor)
                                        .border(
                                            1.dp,
                                            if (isSelected) AuroraPalette.WarmAmber else AuroraPalette.GlassBorder,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            showAllEvents = false
                                            selectedDateStr = strDate
                                        }
                                        .padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = dayNameFormatter.format(dateObj).replace(".", ""),
                                        fontSize = 11.sp,
                                        color = contentColor.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = dayNumFormatter.format(dateObj),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = contentColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        if (strDate in dadWorkDates) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(AuroraPalette.DadAccent)
                                            )
                                        }
                                        if (strDate in momWorkDates) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(AuroraPalette.MomAccent)
                                            )
                                        }
                                        if (hasEvents) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSelected) contentColor else AuroraPalette.WarmAmber
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(authorsFilter) { author ->
                            FilterChip(
                                selected = selectedAuthor == author,
                                onClick = { selectedAuthor = author },
                                label = { Text(author, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = AuroraPalette.GlassSurfaceSoft,
                                    labelColor = AuroraPalette.SoftPowder,
                                    selectedContainerColor = AuroraPalette.WarmAmber,
                                    selectedLabelColor = AuroraPalette.DeepIndigo
                                )
                            )
                        }
                    }

                    if (dayShifts.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            dayShifts.forEach { shift ->
                                val color = if (shift.person == "Папа") AuroraPalette.DadAccent else AuroraPalette.MomAccent
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = color.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "${shift.person}: смена",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = color
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Убрать рабочий день",
                                            tint = color,
                                            modifier = Modifier
                                                .size(15.dp)
                                                .clickable { viewModel.deleteWorkShift(shift.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    EventsFeedSection(
                        filteredEvents = filteredEvents,
                        emptyMessage = if (showAllEvents) {
                            "Событий пока нет. Нажмите +, чтобы добавить первое."
                        } else {
                            "На $selectedDateStr событий пока нет."
                        },
                        onAddClick = { showCreateDialog = true },
                        onEditEvent = { editingEvent = it },
                        onDeleteEvent = { viewModel.deleteEvent(it.id) }
                    )
                }

                AppDestination.CALENDAR -> {
                    Spacer(modifier = Modifier.height(6.dp))
                    ExpandedMonthCalendar(
                        monthPages = monthPages,
                        selectedDateStr = selectedDateStr,
                        showAllEvents = false,
                        datesWithEvents = datesWithEvents,
                        dadWorkDates = dadWorkDates,
                        momWorkDates = momWorkDates,
                        dateFormatter = dateFormatter,
                        dayNumFormatter = dayNumFormatter,
                        locale = locale,
                        onSelectDate = { pickedDate ->
                            selectedDateStr = pickedDate
                            showAllEvents = false
                        }
                    )

                    if (dayShifts.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            dayShifts.forEach { shift ->
                                val color = if (shift.person == "Папа") AuroraPalette.DadAccent else AuroraPalette.MomAccent
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = color.copy(alpha = 0.16f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "${shift.person}: смена",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = color
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Убрать рабочий день",
                                            tint = color,
                                            modifier = Modifier
                                                .size(15.dp)
                                                .clickable { viewModel.deleteWorkShift(shift.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    EventsFeedSection(
                        filteredEvents = filteredEvents,
                        emptyMessage = "На $selectedDateStr событий нет.",
                        onAddClick = { showCreateDialog = true },
                        onEditEvent = { editingEvent = it },
                        onDeleteEvent = { viewModel.deleteEvent(it.id) }
                    )
                }

                AppDestination.FAMILY -> {
                    FamilyMembersSection(
                        events = events,
                        shifts = shifts,
                        onOpenScheduleDialog = { showScheduleDialog = true }
                    )
                }

                AppDestination.SETTINGS -> {
                    SettingsSection(
                        hasNotificationPermission = hasNotificationPermission,
                        canScheduleExactAlarms = canScheduleExactAlarms,
                        onRequestNotificationPermission = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    )
                }

                AppDestination.ADD -> Unit
            }
        }

        if (showCreateDialog) {
            EventEditorDialog(
                initialEvent = null,
                initialDate = if (showAllEvents) todayStr else selectedDateStr,
                onDismiss = { showCreateDialog = false },
                onSave = { title, date, time, author ->
                    viewModel.addEvent(title, date, time, author)
                    ReminderAlarmReceiver.scheduleReminder(
                        context = context,
                        title = title,
                        dateStr = date,
                        timeStr = time,
                        author = author,
                        minutesBefore = 60L
                    )
                    showCreateDialog = false
                }
            )
        }

        editingEvent?.let { current ->
            EventEditorDialog(
                initialEvent = current,
                onDismiss = { editingEvent = null },
                onSave = { title, date, time, author ->
                    viewModel.updateEvent(
                        id = current.id,
                        title = title,
                        date = date,
                        time = time,
                        author = author
                    )
                    ReminderAlarmReceiver.scheduleReminder(
                        context = context,
                        title = title,
                        dateStr = date,
                        timeStr = time,
                        author = author,
                        minutesBefore = 60L
                    )
                    editingEvent = null
                }
            )
        }

        if (showScheduleDialog) {
            WorkScheduleDialog(
                monthPages = monthPages,
                currentShifts = shifts,
                dateFormatter = dateFormatter,
                dayNumFormatter = dayNumFormatter,
                locale = locale,
                onDismiss = { showScheduleDialog = false },
                onSave = { person, dates ->
                    viewModel.replaceWorkShifts(person, dates)
                    showScheduleDialog = false
                }
            )
        }
    }
}

@Composable
private fun EventsFeedSection(
    filteredEvents: List<FamilyEvent>,
    emptyMessage: String,
    onAddClick: () -> Unit,
    onEditEvent: (FamilyEvent) -> Unit,
    onDeleteEvent: (FamilyEvent) -> Unit
) {
    if (filteredEvents.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = emptyMessage,
                        fontSize = 14.sp,
                        color = AuroraPalette.SoftPowder,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onAddClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuroraPalette.WarmAmber,
                            contentColor = AuroraPalette.DeepIndigo
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Добавить событие", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredEvents, key = { it.id }) { event ->
                Box(modifier = Modifier.animateItem()) {
                    EventCard(
                        event = event,
                        onEdit = { onEditEvent(event) },
                        onDelete = { onDeleteEvent(event) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FamilyMembersSection(
    events: List<FamilyEvent>,
    shifts: List<WorkShift>,
    onOpenScheduleDialog: () -> Unit
) {
    val members = listOf(
        Triple("Папа", AuroraPalette.DadAccent, true),
        Triple("Мама", AuroraPalette.MomAccent, true),
        Triple("Дочка", AuroraPalette.DaughterAccent, false),
        Triple("Общее", AuroraPalette.SharedAccent, false)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp,
                containerColor = AuroraPalette.PlumBurgundy.copy(alpha = 0.42f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Рабочий график семьи",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Отмечай и меняй рабочие дни целиком по месяцам",
                            fontSize = 12.sp,
                            color = AuroraPalette.SoftPowder
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = onOpenScheduleDialog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuroraPalette.WarmAmber,
                            contentColor = AuroraPalette.DeepIndigo
                        )
                    ) {
                        Text("График", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        items(members) { (name, accent, hasShifts) ->
            val memberEventsCount = events.count { it.author == name }
            val memberShiftsCount = shifts.count { it.person == name }

            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(accent)
                        )
                        Column {
                            Text(
                                text = name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = if (hasShifts) {
                                    "Событий: $memberEventsCount • Смен в графике: $memberShiftsCount"
                                } else {
                                    "Событий: $memberEventsCount"
                                },
                                fontSize = 12.sp,
                                color = AuroraPalette.SoftPowder.copy(alpha = 0.8f)
                            )
                        }
                    }

                    if (hasShifts) {
                        TextButton(onClick = onOpenScheduleDialog) {
                            Text("Править", color = accent, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    hasNotificationPermission: Boolean,
    canScheduleExactAlarms: Boolean,
    onRequestNotificationPermission: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Уведомления и будильники",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Разрешение уведомлений: ${if (hasNotificationPermission) "Включено" else "Требуется разрешение"}",
                        fontSize = 13.sp,
                        color = AuroraPalette.SoftPowder
                    )
                    Text(
                        text = "Точное расписание AlarmManager: ${if (canScheduleExactAlarms) "Активно" else "Резервный режим"}",
                        fontSize = 13.sp,
                        color = AuroraPalette.SoftPowder.copy(alpha = 0.85f)
                    )
                    if (!hasNotificationPermission) {
                        Button(
                            onClick = onRequestNotificationPermission,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuroraPalette.WarmAmber,
                                contentColor = AuroraPalette.DeepIndigo
                            )
                        ) {
                            Text("Разрешить уведомления")
                        }
                    }
                }
            }
        }

        item {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Синхронизация и оффлайн-кэш",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "События и рабочие смены синхронизируются через Firestore и хранятся в локальном кэше устройства при перезапуске или отсутствии сети.",
                        fontSize = 13.sp,
                        color = AuroraPalette.SoftPowder.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: FamilyEvent,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val accentColor = Color(event.colorHex)

    LiquidGlassCard(
        cornerRadius = 22.dp,
        containerColor = AuroraPalette.DeepIndigo.copy(alpha = 0.26f),
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Стеклянная цветная грань участника слева
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.95f),
                                accentColor.copy(alpha = 0.45f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp, end = 6.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AuroraPalette.DeepIndigo.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                        ) {
                            Text(
                                text = "${event.date} • ${event.time}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = AuroraPalette.SoftPowder,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (event.author.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = accentColor.copy(alpha = 0.20f),
                                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = event.author,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = accentColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = AuroraPalette.SoftPowder.copy(alpha = 0.80f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditorDialog(
    initialEvent: FamilyEvent? = null,
    initialDate: String? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, date: String, time: String, author: String) -> Unit
) {
    val locale = Locale("ru")
    val calendarNow = remember { Calendar.getInstance(locale) }
    val defaultDateStr = remember(locale) {
        SimpleDateFormat("dd.MM.yyyy", locale).format(calendarNow.time)
    }
    val defaultTimeStr = remember(locale) {
        SimpleDateFormat("HH:mm", locale).format(calendarNow.time)
    }

    var title by remember { mutableStateOf(initialEvent?.title ?: "") }
    var date by remember {
        mutableStateOf(initialEvent?.date ?: initialDate ?: defaultDateStr)
    }
    var time by remember {
        mutableStateOf(initialEvent?.time ?: defaultTimeStr)
    }

    val authors = listOf("Папа", "Мама", "Дочка", "Общее")
    var selectedAuthor by remember {
        mutableStateOf(initialEvent?.author?.takeIf { it in authors } ?: "Общее")
    }

    LaunchedEffect(initialEvent, initialDate) {
        val nowCal = Calendar.getInstance(locale)
        title = initialEvent?.title ?: ""
        date = initialEvent?.date
            ?: initialDate
                    ?: SimpleDateFormat("dd.MM.yyyy", locale).format(nowCal.time)
        time = initialEvent?.time
            ?: SimpleDateFormat("HH:mm", locale).format(nowCal.time)
        selectedAuthor = initialEvent?.author?.takeIf { it in authors } ?: "Общее"
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AuroraPalette.DeepIndigo.copy(alpha = 0.98f),
        contentColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (initialEvent == null) "Новое событие" else "Редактировать событие",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название события") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Дата") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату")
                        }
                    },
                    modifier = Modifier
                        .weight(1.15f)
                        .clickable { showDatePicker = true }
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Время") },
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.Schedule, contentDescription = "Выбрать время")
                        }
                    },
                    modifier = Modifier
                        .weight(0.85f)
                        .clickable { showTimePicker = true }
                )
            }

            // Быстрый выбор популярных слотов одной рукой
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val quickTimes = listOf("09:00", "12:00", "18:00", "20:00")
                items(quickTimes) { preset ->
                    FilterChip(
                        selected = time == preset,
                        onClick = { time = preset },
                        label = { Text(preset, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = AuroraPalette.GlassSurfaceSoft,
                            labelColor = AuroraPalette.SoftPowder,
                            selectedContainerColor = AuroraPalette.PlumBurgundy,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Text(
                text = "Участник:",
                fontSize = 13.sp,
                color = AuroraPalette.SoftPowder
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(authors) { person ->
                    val active = selectedAuthor == person
                    FilterChip(
                        selected = active,
                        onClick = { selectedAuthor = person },
                        label = { Text(person, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = AuroraPalette.GlassSurfaceSoft,
                            labelColor = AuroraPalette.SoftPowder,
                            selectedContainerColor = AuroraPalette.WarmAmber,
                            selectedLabelColor = AuroraPalette.DeepIndigo
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Отмена", color = AuroraPalette.SoftPowder)
                }
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSave(title.trim(), date.trim(), time.trim(), selectedAuthor)
                        }
                    },
                    modifier = Modifier.weight(1.4f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuroraPalette.WarmAmber,
                        contentColor = AuroraPalette.DeepIndigo
                    )
                ) {
                    Text("Сохранить", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = calendarNow.timeInMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val picked = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = millis
                            }
                            val localCal = Calendar.getInstance(locale).apply {
                                set(
                                    picked.get(Calendar.YEAR),
                                    picked.get(Calendar.MONTH),
                                    picked.get(Calendar.DAY_OF_MONTH)
                                )
                            }
                            date = SimpleDateFormat("dd.MM.yyyy", locale).format(localCal.time)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("ОК", color = AuroraPalette.WarmAmber)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена", color = AuroraPalette.SoftPowder)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val initialParts = time.split(":")
        val initHour = initialParts.getOrNull(0)?.toIntOrNull() ?: calendarNow.get(Calendar.HOUR_OF_DAY)
        val initMinute = initialParts.getOrNull(1)?.toIntOrNull() ?: calendarNow.get(Calendar.MINUTE)
        val timePickerState = rememberTimePickerState(
            initialHour = initHour,
            initialMinute = initMinute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Выберите время") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        time = String.format(locale, "%02d:%02d", timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("ОК", color = AuroraPalette.WarmAmber)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Отмена", color = AuroraPalette.SoftPowder)
                }
            }
        )
    }
}

@Composable
private fun WorkScheduleDialog(
    monthPages: List<MonthPage>,
    currentShifts: List<WorkShift>,
    dateFormatter: SimpleDateFormat,
    dayNumFormatter: SimpleDateFormat,
    locale: Locale,
    onDismiss: () -> Unit,
    onSave: (person: String, dates: Set<String>) -> Unit
) {
    val people = listOf("Папа", "Мама")
    var selectedPerson by remember { mutableStateOf("Папа") }

    var selectedDates by remember {
        mutableStateOf(
            currentShifts.filter { it.person == "Папа" }.map { it.date.trim() }.toSet()
        )
    }

    LaunchedEffect(selectedPerson, currentShifts) {
        selectedDates = currentShifts
            .filter { it.person == selectedPerson }
            .map { it.date.trim() }
            .toSet()
    }

    val initialPage = remember(monthPages) {
        val now = Calendar.getInstance(locale)
        val curYear = now.get(Calendar.YEAR)
        val curMonth = now.get(Calendar.MONTH)
        monthPages.indexOfFirst { it.year == curYear && it.month == curMonth }.coerceAtLeast(0)
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { monthPages.size }
    )
    val coroutineScope = rememberCoroutineScope()
    val weekDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    val personAccent = if (selectedPerson == "Папа") AuroraPalette.DadAccent else AuroraPalette.MomAccent

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AuroraPalette.DeepIndigo.copy(alpha = 0.96f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("График работы", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Text(
                    text = "Дней: ${selectedDates.size}",
                    fontSize = 13.sp,
                    color = personAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        people.forEach { person ->
                            FilterChip(
                                selected = selectedPerson == person,
                                onClick = { selectedPerson = person },
                                label = { Text(person) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = AuroraPalette.GlassSurfaceSoft,
                                    labelColor = AuroraPalette.SoftPowder,
                                    selectedContainerColor = AuroraPalette.WarmAmber,
                                    selectedLabelColor = AuroraPalette.DeepIndigo
                                )
                            )
                        }
                    }

                    if (selectedDates.isNotEmpty()) {
                        TextButton(
                            onClick = { selectedDates = emptySet() },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text("Очистить всё", fontSize = 12.sp, color = AuroraPalette.SoftPowder)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
                            coroutineScope.launch { pagerState.animateScrollToPage(prev) }
                        },
                        enabled = pagerState.currentPage > 0,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Предыдущий месяц")
                    }

                    Text(
                        text = monthPages.getOrNull(pagerState.currentPage)?.label.orEmpty(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = {
                            val next = (pagerState.currentPage + 1).coerceAtMost(monthPages.lastIndex)
                            coroutineScope.launch { pagerState.animateScrollToPage(next) }
                        },
                        enabled = pagerState.currentPage < monthPages.lastIndex,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Следующий месяц")
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    weekDays.forEach { wd ->
                        Text(
                            text = wd,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuroraPalette.SoftPowder.copy(alpha = 0.78f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { pageIndex ->
                    val page = monthPages[pageIndex]
                    val rows = page.cells.chunked(7)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        rows.forEach { weekRow ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                for (col in 0 until 7) {
                                    val cellDate = weekRow.getOrNull(col)
                                    if (cellDate == null) {
                                        Box(modifier = Modifier.weight(1f).height(36.dp))
                                    } else {
                                        val strDate = dateFormatter.format(cellDate)
                                        val isMarked = strDate in selectedDates

                                        val bg = if (isMarked) {
                                            personAccent.copy(alpha = 0.28f)
                                        } else {
                                            Color.Transparent
                                        }
                                        val borderMod = if (isMarked) {
                                            Modifier.border(1.5.dp, personAccent, RoundedCornerShape(8.dp))
                                        } else {
                                            Modifier
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(bg)
                                                .then(borderMod)
                                                .clickable {
                                                    selectedDates = if (isMarked) {
                                                        selectedDates - strDate
                                                    } else {
                                                        selectedDates + strDate
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = dayNumFormatter.format(cellDate),
                                                fontSize = 13.sp,
                                                fontWeight = if (isMarked) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isMarked) personAccent else Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedPerson, selectedDates) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuroraPalette.WarmAmber,
                    contentColor = AuroraPalette.DeepIndigo
                )
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = AuroraPalette.SoftPowder)
            }
        }
    )
}