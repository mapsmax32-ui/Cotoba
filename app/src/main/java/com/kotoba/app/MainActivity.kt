package com.kotoba.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Washi = Color(0xFFF5F1E8)
private val Ink = Color(0xFF202020)
private val Red = Color(0xFFB13A32)
private val Indigo = Color(0xFF27364A)
private val Gold = Color(0xFFB5965A)
private val Sage = Color(0xFF75846B)
private val Mist = Color(0xFFE8E1D3)

data class Lesson(val title: String, val jp: String, val subtitle: String, val xp: Int)
private val lessons = listOf(
    Lesson("Приветствия","こんにちは","Поздороваться и представиться",25),
    Lesson("Спасибо","ありがとう","Благодарность и вежливость",30),
    Lesson("Знакомство","はじめまして","Имя и первый разговор",35),
    Lesson("Числа","いち・に・さん","Считаем от 1 до 10",30),
    Lesson("Время","いま なんじ？","Часы и повседневный ритм",35),
    Lesson("Еда","おいしい！","Кафе, меню и заказ",40),
    Lesson("Город","ここは どこ？","Ориентируемся в Японии",40),
    Lesson("Семья","かぞく","Рассказываем о семье",35),
    Lesson("Хобби","すきです","Что тебе нравится",45),
    Lesson("Грамматика","です・ます","База N5",50),
    Lesson("Диалог","きっさてん","Кафе в Киото",55),
    Lesson("N5 Boss","JLPT N5","Финальная проверка",100)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KotobaApp() }
    }
}

@Composable
fun KotobaApp() {
    MaterialTheme(colorScheme = lightColorScheme(background=Washi,surface=Washi,primary=Red,secondary=Indigo,onBackground=Ink,onSurface=Ink)) {
        var tab by remember { mutableIntStateOf(0) }
        var lesson by remember { mutableIntStateOf(-1) }
        var step by remember { mutableIntStateOf(0) }
        var xp by remember { mutableIntStateOf(126) }
        var done by remember { mutableIntStateOf(2) }

        Scaffold(
            containerColor=Washi,
            bottomBar={
                if (lesson < 0) NavigationBar(containerColor=Color.White) {
                    val names=listOf("Главная","Путь","Слова","Повтор","Профиль")
                    val icons=listOf(Icons.Rounded.Home,Icons.Rounded.Map,Icons.Rounded.MenuBook,Icons.Rounded.LocalFireDepartment,Icons.Rounded.EmojiEvents)
                    names.forEachIndexed { i,n ->
                        NavigationBarItem(selected=tab==i,onClick={tab=i},icon={Icon(icons[i],null)},label={Text(n,fontSize=11.sp)})
                    }
                }
            }
        ) { pad ->
            if (lesson >= 0) {
                LessonScreen(Modifier.padding(pad),lessons[lesson],step,
                    onNext={
                        if(step<3) step++ else {
                            xp += lessons[lesson].xp
                            done=maxOf(done,lesson+1)
                            lesson=-1
                            tab=0
                            step=0
                        }
                    },
                    onBack={lesson=-1;step=0}
                )
            } else when(tab) {
                0 -> Home(Modifier.padding(pad),xp,done,{lesson=done.coerceAtMost(lessons.lastIndex);step=0},{tab=1})
                1 -> PathScreen(Modifier.padding(pad),done,{i->lesson=i;step=0})
                2 -> WordsScreen(Modifier.padding(pad),{lesson=0;step=0})
                3 -> ReviewScreen(Modifier.padding(pad),{lesson=1;step=0})
                else -> ProfileScreen(Modifier.padding(pad),xp,done)
            }
        }
    }
}

@Composable
fun Home(modifier:Modifier,xp:Int,done:Int,onStart:()->Unit,onPath:()->Unit) {
    val level=xp/100+1
    val progress=(xp%100)/100f
    val anim by animateFloatAsState(progress,tween(900),label="xp")
    LazyColumn(modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        item {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                Column { Text("おかえりなさい",fontSize=28.sp,fontWeight=FontWeight.Bold); Text("Продолжим твой путь?",color=Indigo) }
                Box(Modifier.size(52.dp).background(Red,CircleShape),contentAlignment=Alignment.Center){Text("N5",color=Color.White,fontWeight=FontWeight.Bold)}
            }
        }
        item {
            Card(Modifier.fillMaxWidth(),RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Indigo)) {
                Column(Modifier.padding(22.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                        Column { Text("УРОВЕНЬ "+level,color=Gold,fontSize=12.sp,fontWeight=FontWeight.Bold); Text("Самурай слов",color=Color.White,fontSize=25.sp,fontWeight=FontWeight.Bold) }
                        Text(xp.toString()+" XP",color=Color.White,fontWeight=FontWeight.Bold)
                    }
                    LinearProgressIndicator(progress={anim},Modifier.fillMaxWidth().height(8.dp),color=Gold,trackColor=Color.White.copy(alpha=.18f))
                    Text((100-(xp%100)).toString()+" XP до следующего уровня",color=Color.White.copy(alpha=.75f),fontSize=12.sp)
                }
            }
        }
        item {
            Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
                Row(Modifier.fillMaxWidth().padding(18.dp),verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(52.dp).background(Color(0xFFFFE6D6),CircleShape),contentAlignment=Alignment.Center){Icon(Icons.Rounded.LocalFireDepartment,null,tint=Red)}
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)){Text("Серия 7 дней",fontWeight=FontWeight.Bold,fontSize=17.sp);Text("Ещё один урок — и серия продолжится",color=Indigo,fontSize=12.sp)}
                    Text("🔥",fontSize=22.sp)
                }
            }
        }
        item{Text("Твоя миссия",fontSize=20.sp,fontWeight=FontWeight.Bold)}
        item{
            Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){
                Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
                    Text("Пройти урок «Приветствия»",fontWeight=FontWeight.Bold,fontSize=18.sp)
                    Text("4 мини-задания · 25 XP · ~5 минут",color=Indigo)
                    Button(onClick=onStart,Modifier.fillMaxWidth(),shape=RoundedCornerShape(15.dp)){Icon(Icons.Rounded.PlayArrow,null);Spacer(Modifier.width(6.dp));Text("Начать миссию")}
                }
            }
        }
        item{OutlinedButton(onClick=onPath,Modifier.fillMaxWidth(),shape=RoundedCornerShape(15.dp)){Text("Открыть карту обучения")}}
        item{Text("Сегодня",fontSize=20.sp,fontWeight=FontWeight.Bold)}
        item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){Stat("12","слов",Modifier.weight(1f));Stat("8","мин",Modifier.weight(1f));Stat(done.toString(),"уроков",Modifier.weight(1f))}}
    }
}

@Composable
fun Stat(a:String,b:String,m:Modifier){Card(modifier=m,shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.fillMaxWidth().padding(14.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(a,fontSize=22.sp,fontWeight=FontWeight.Bold);Text(b,fontSize=11.sp,color=Indigo)}}}

@Composable
fun PathScreen(modifier:Modifier,done:Int,onLesson:(Int)->Unit){
    LazyColumn(modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        item{Text("Путь N5",fontSize=30.sp,fontWeight=FontWeight.Bold);Text("Каждый урок — новый ранг.",color=Indigo);Spacer(Modifier.height(8.dp))}
        itemsIndexed(lessons){i,l->
            val unlocked=i<=done
            val finished=i<done
            Card(onClick={if(unlocked)onLesson(i)},enabled=unlocked,shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=if(finished)Color.White else if(unlocked)Color(0xFFFFF7F1) else Mist)){
                Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically){
                    Box(Modifier.size(52.dp).background(if(finished) Sage else if(unlocked) Red else Color.Gray, CircleShape),contentAlignment=Alignment.Center){
                        if(finished)Icon(Icons.Rounded.CheckCircle,null,tint=Color.White) else if(!unlocked)Icon(Icons.Rounded.Lock,null,tint=Color.White) else Text((i+1).toString(),color=Color.White,fontWeight=FontWeight.Bold)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)){Text(l.title,fontWeight=FontWeight.Bold,fontSize=17.sp);Text(l.jp,color=Indigo);Text(l.xp.toString()+" XP · "+l.subtitle,color=Color.Gray,fontSize=11.sp)}
                    if(finished)Text("✓",color=Sage,fontSize=24.sp,fontWeight=FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WordsScreen(modifier:Modifier,onStart:()->Unit){
    val words=listOf("こんにちは" to "здравствуйте","ありがとう" to "спасибо","すみません" to "извините","おいしい" to "вкусно","ともだち" to "друг","せんせい" to "учитель")
    LazyColumn(modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
        item{Text("Слова",fontSize=30.sp,fontWeight=FontWeight.Bold);Text("Коллекция, которую ты собираешь",color=Indigo);Spacer(Modifier.height(8.dp))}
        itemsIndexed(words){_,w->Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Row(Modifier.fillMaxWidth().padding(18.dp),horizontalArrangement=Arrangement.SpaceBetween){Column{Text(w.first,fontSize=22.sp,fontWeight=FontWeight.Bold);Text(w.second,color=Indigo)};Text("＋XP",color=Gold,fontWeight=FontWeight.Bold)}}}
        item{Button(onClick=onStart,Modifier.fillMaxWidth(),shape=RoundedCornerShape(15.dp)){Text("Тренировать слова")}}
    }
}

@Composable
fun ReviewScreen(modifier:Modifier,onStart:()->Unit){
    Column(modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
        Text("Повторение",fontSize=30.sp,fontWeight=FontWeight.Bold);Text("SRS · умное возвращение к словам",color=Indigo)
        Card(shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Indigo)){Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("14",fontSize=48.sp,color=Color.White,fontWeight=FontWeight.Bold);Text("карточек ждут тебя",color=Color.White,fontSize=18.sp);Button(onClick=onStart,colors=ButtonDefaults.buttonColors(containerColor=Gold),modifier=Modifier.fillMaxWidth()){Text("Начать повторение",color=Ink)}}}
        Text("Ритм",fontSize=20.sp,fontWeight=FontWeight.Bold);Text("🔥 7 дней подряд",fontSize=18.sp);Text("Лучший результат: 12 дней",color=Indigo)
    }
}

@Composable
fun ProfileScreen(modifier:Modifier,xp:Int,done:Int){
    Column(modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
        Text("Твой профиль",fontSize=30.sp,fontWeight=FontWeight.Bold)
        Card(shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.fillMaxWidth().padding(22.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("称号",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Bold);Text("Начинающий самурай",fontSize=24.sp,fontWeight=FontWeight.Bold);Text("Уровень "+(xp/100+1)+" · "+xp+" XP · "+done+" уроков",color=Indigo)}}
        Text("Достижения",fontSize=20.sp,fontWeight=FontWeight.Bold);Achievement("Первый шаг","Заверши первый урок",done>=1);Achievement("Серия 7","Занимайся семь дней",true);Achievement("Котоба","Собери 100 слов",false)
    }
}

@Composable
fun Achievement(t:String,s:String,on:Boolean){Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=if(on)Color.White else Mist)){Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(if(on)Icons.Rounded.EmojiEvents else Icons.Rounded.Lock,null,tint=if(on)Gold else Color.Gray,modifier=Modifier.size(30.dp));Spacer(Modifier.width(14.dp));Column{Text(t,fontWeight=FontWeight.Bold);Text(s,color=Indigo,fontSize=12.sp)}}}}

@Composable
fun LessonScreen(modifier:Modifier,lesson:Lesson,step:Int,onNext:()->Unit,onBack:()->Unit){
    val prompts=listOf("Послушай и запомни","Выбери правильное значение","Собери фразу","Финальная проверка")
    val scale by animateFloatAsState(if(step==3)1.04f else 1f,tween(500),label="card")
    Column(modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){TextButton(onClick=onBack){Text("← Назад")};Spacer(Modifier.weight(1f));Text(lesson.xp.toString()+" XP",color=Gold,fontWeight=FontWeight.Bold)}
        Text(lesson.title,fontSize=30.sp,fontWeight=FontWeight.Bold);Text(prompts[step],color=Indigo)
        LinearProgressIndicator(progress={(step+1)/4f},Modifier.fillMaxWidth().height(8.dp),color=Red,trackColor=Mist)
        Spacer(Modifier.height(10.dp))
        Card(Modifier.fillMaxWidth().scale(scale),RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){
            Column(Modifier.padding(26.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(18.dp)){
                Box(Modifier.size(84.dp).background(Red.copy(alpha=.1f),CircleShape),contentAlignment=Alignment.Center){Text(if(step==3)"★" else lesson.jp,fontSize=if(step==3)42.sp else 25.sp,fontWeight=FontWeight.Bold,color=Red)}
                Text(lesson.jp,fontSize=34.sp,fontWeight=FontWeight.Bold);Text(lesson.subtitle,color=Indigo)
                AnimatedVisibility(visible=step>=1,enter=fadeIn()){Text(when(step){1->"Правильный смысл связан с контекстом.";2->"こんにちは → здравствуйте";else->"Отлично. Ты готов двигаться дальше."},color=Sage,fontWeight=FontWeight.SemiBold)}
                Button(onClick=onNext,Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(16.dp)){Text(if(step==3)"Забрать XP" else "Продолжить",fontSize=16.sp)}
            }
        }
        if(step==1||step==2)Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){OutlinedButton(onClick=onNext,Modifier.weight(1f)){Text("Не уверен")};OutlinedButton(onClick=onNext,Modifier.weight(1f)){Text("Знаю")}}
    }
}
