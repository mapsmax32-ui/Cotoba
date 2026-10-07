package com.kotoba.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Washi = Color(0xFFF5F1E8)
private val Ink = Color(0xFF202020)
private val Red = Color(0xFFB13A32)
private val Indigo = Color(0xFF27364A)
private val Gold = Color(0xFFB5965A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KotobaApp() }
    }
}

@Composable
fun KotobaApp() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Washi,
            surface = Washi,
            primary = Red,
            secondary = Indigo,
            onBackground = Ink,
            onSurface = Ink
        )
    ) {
        var selected by remember { mutableIntStateOf(0) }
        var activeLesson by remember { mutableStateOf(false) }
        var lessonStep by remember { mutableIntStateOf(0) }

        Scaffold(
            containerColor = Washi,
            bottomBar = {
                if (!activeLesson) {
                    NavigationBar(containerColor = Washi) {
                        listOf("Главная", "Обучение", "Слова", "Повторение", "Прогресс").forEachIndexed { i, title ->
                            NavigationBarItem(
                                selected = selected == i,
                                onClick = {
                                    selected = i
                                    activeLesson = false
                                },
                                icon = { Text(listOf("家", "学", "言", "復", "進")[i], fontSize = 18.sp) },
                                label = { Text(title, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            if (activeLesson) {
                LessonScreen(
                    modifier = Modifier.padding(padding),
                    step = lessonStep,
                    onNext = {
                        if (lessonStep < 2) lessonStep++ else {
                            activeLesson = false
                            selected = 4
                        }
                    },
                    onBack = {
                        activeLesson = false
                        lessonStep = 0
                    }
                )
            } else {
                when (selected) {
                    0 -> Home(
                        Modifier.padding(padding),
                        onContinue = {
                            selected = 1
                            activeLesson = true
                            lessonStep = 0
                        }
                    )
                    1 -> Learn(
                        Modifier.padding(padding),
                        onStart = {
                            activeLesson = true
                            lessonStep = 0
                        }
                    )
                    2 -> Words(
                        Modifier.padding(padding),
                        onLearn = { activeLesson = true; lessonStep = 0 }
                    )
                    3 -> Review(
                        Modifier.padding(padding),
                        onStart = { activeLesson = true; lessonStep = 0 }
                    )
                    else -> Progress(
                        Modifier.padding(padding),
                        onContinue = { selected = 1; activeLesson = true; lessonStep = 0 }
                    )
                }
            }
        }
    }
}

@Composable
fun Home(modifier: Modifier = Modifier, onContinue: () -> Unit) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("おかえりなさい", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text("Добро пожаловать обратно", color = Indigo)
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Ваш путь", fontSize = 14.sp, color = Indigo)
                Text("JLPT N5", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(progress = { 0.18f }, modifier = Modifier.fillMaxWidth(), color = Red)
                Text("18% завершено")
            }
        }
        Text("Сегодня", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("12", "слов", Modifier.weight(1f))
            StatCard("8", "минут", Modifier.weight(1f))
            StatCard("7", "дней", Modifier.weight(1f))
        }
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Text("Продолжить урок")
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Indigo)
        }
    }
}

@Composable
fun Learn(modifier: Modifier, onStart: () -> Unit) =
    SimplePage(modifier, "Обучение", "Урок 1 · Приветствия", "こんにちは", "Начать урок", onStart)

@Composable
fun Words(modifier: Modifier, onLearn: () -> Unit) =
    SimplePage(modifier, "Слова", "Сегодняшняя подборка", "こんにちは · ありがとう · すみません", "Учить слова", onLearn)

@Composable
fun Review(modifier: Modifier, onStart: () -> Unit) =
    SimplePage(modifier, "Повторение", "SRS", "Пора повторить 14 карточек", "Начать повторение", onStart)

@Composable
fun Progress(modifier: Modifier, onContinue: () -> Unit) =
    SimplePage(modifier, "Прогресс", "Ваш путь по N5", "18% · 126 XP · серия 7 дней", "Продолжить", onContinue)

@Composable
fun SimplePage(
    modifier: Modifier,
    title: String,
    subtitle: String,
    main: String,
    action: String,
    onAction: () -> Unit
) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Indigo)
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(main, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Button(onClick = onAction, shape = RoundedCornerShape(14.dp)) { Text(action) }
            }
        }
    }
}

@Composable
fun LessonScreen(
    modifier: Modifier,
    step: Int,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val titles = listOf("Приветствие", "Новая фраза", "Проверка")
    val main = listOf("こんにちは", "おはようございます", "Как сказать «спасибо»?")
    val hints = listOf(
        "konnichiwa · здравствуйте / добрый день",
        "ohayō gozaimasu · доброе утро",
        "Выбери правильный ответ"
    )

    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        TextButton(onClick = onBack) { Text("← Назад") }
        Text("Урок 1 · ${titles[step]}", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        LinearProgressIndicator(progress = { (step + 1) / 3f }, modifier = Modifier.fillMaxWidth(), color = Red)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(main[step], fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Text(hints[step], color = Indigo)
                if (step == 2) {
                    Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("ありがとう") }
                    OutlinedButton(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("さようなら") }
                } else {
                    Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                        Text(if (step == 0) "Дальше" else "Проверить")
                    }
                }
            }
        }
    }
}
