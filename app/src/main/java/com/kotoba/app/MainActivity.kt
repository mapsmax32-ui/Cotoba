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

        Scaffold(
            containerColor = Washi,
            bottomBar = {
                NavigationBar(containerColor = Washi) {
                    listOf("Главная", "Обучение", "Слова", "Повторение", "Прогресс").forEachIndexed { i, title ->
                        NavigationBarItem(
                            selected = selected == i,
                            onClick = { selected = i },
                            icon = { Text(listOf("家","学","言","復","進")[i], fontSize = 18.sp) },
                            label = { Text(title, fontSize = 11.sp) }
                        )
                    }
                }
            }
        ) { padding ->
            when (selected) {
                0 -> Home(Modifier.padding(padding))
                1 -> Learn(Modifier.padding(padding))
                2 -> Words(Modifier.padding(padding))
                3 -> Review(Modifier.padding(padding))
                else -> Progress(Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun Home(modifier: Modifier = Modifier) {
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
        Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
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
fun Learn(modifier: Modifier) = SimplePage(modifier, "Обучение", "Урок 1 · Приветствия", "こんにちは", "Начать урок")
@Composable
fun Words(modifier: Modifier) = SimplePage(modifier, "Слова", "Сегодняшняя подборка", "こんにちは · ありがとう · すみません", "Учить слова")
@Composable
fun Review(modifier: Modifier) = SimplePage(modifier, "Повторение", "SRS", "Пора повторить 14 карточек", "Начать повторение")
@Composable
fun Progress(modifier: Modifier) = SimplePage(modifier, "Прогресс", "Ваш путь по N5", "18% · 126 XP · серия 7 дней", "Продолжить")

@Composable
fun SimplePage(modifier: Modifier, title: String, subtitle: String, main: String, action: String) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Indigo)
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(main, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Button(onClick = {}, shape = RoundedCornerShape(14.dp)) { Text(action) }
            }
        }
    }
}
