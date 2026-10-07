package com.kotoba.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
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

enum class LessonKind { CHOICE, TAP, ORDER, DIALOGUE, TRANSLATE, FILL }

data class Lesson(
    val title:String,val jp:String,val reading:String,val meaning:String,val subtitle:String,
    val xp:Int,val question:String,val choices:List<String>,val answer:Int,val kind:LessonKind
)

private val lessons = listOf(
    Lesson("Токио 01 — Первое приветствие","こんにちは","konnichiwa","здравствуйте / добрый день","Начало пути · приветствие",35,"Найди «здравствуйте»",listOf("こんにちは","ありがとう","さようなら","おやすみ"),0,LessonKind.TAP),
    Lesson("Токио 02 — Доброе утро","おはようございます","ohayou gozaimasu","доброе утро","Утро в Токио",30,"Что скажешь утром?",listOf("こんばんは","おはようございます","こんにちは","またね"),1,LessonKind.CHOICE),
    Lesson("Токио 03 — Добрый вечер","こんばんは","konbanwa","добрый вечер","Вечерняя прогулка",30,"Выбери вечернее приветствие",listOf("こんばんは","おはようございます","ありがとう","すみません"),0,LessonKind.TAP),
    Lesson("Токио 04 — Спасибо","ありがとう","arigatou","спасибо","Первый вежливый обмен",35,"Собери фразу из слов",listOf("とう","あり","が","う"),0,LessonKind.ORDER),
    Lesson("Токио 05 — До свидания","さようなら","sayounara","до свидания","Прощаемся красиво",30,"Что подходит для прощания?",listOf("さようなら","ありがとう","はじめまして","どうぞ"),0,LessonKind.CHOICE),
    Lesson("Токио 06 — Извините","すみません","sumimasen","извините / простите","Останавливаем прохожего",35,"Ты хочешь привлечь внимание. Что сказать?",listOf("すみません","おいしい","ともだち","いち"),0,LessonKind.DIALOGUE),
    Lesson("Токио 07 — Знакомство","はじめまして","hajimemashite","приятно познакомиться","Первый разговор",40,"Тебя только что представили. Твой ответ?",listOf("はじめまして","おやすみ","いただきます","いってきます"),0,LessonKind.DIALOGUE),
    Lesson("Токио 08 — Моё имя","わたしは ことばです","watashi wa Kotoba desu","я — Котоба","Представляемся",40,"Заполни пропуск: わたしは ___ です",listOf("ことば","みず","えき","にほん"),0,LessonKind.FILL),
    Lesson("Токио 09 — Кто это?","これは だれですか","kore wa dare desu ka","кто это?","Знакомимся с людьми",40,"Выбери перевод",listOf("Кто это?","Где это?","Что это?","Сколько стоит?"),0,LessonKind.TRANSLATE),
    Lesson("Токио 10 — Человек","ひと","hito","человек","Базовая лексика",25,"Нажми на слово «человек»",listOf("ひと","ねこ","ほん","みず"),0,LessonKind.TAP),
    Lesson("Токио 11 — Один, два, три","いち・に・さん","ichi · ni · san","один · два · три","Считаем в сэнсодзи",30,"Как читается いち?",listOf("ichi","ni","san","yon"),0,LessonKind.CHOICE),
    Lesson("Токио 12 — Четыре и пять","よん・ご","yon · go","четыре · пять","Продолжаем счёт",30,"Как сказать «пять»?",listOf("ご","よん","ろく","なな"),0,LessonKind.TAP),
    Lesson("Токио 13 — Сегодня","きょう","kyou","сегодня","Планируем день",35,"Выбери значение きょう",listOf("вчера","сегодня","завтра","утром"),1,LessonKind.TRANSLATE),
    Lesson("Токио 14 — Который час?","いま なんじ？","ima nanji?","который сейчас час?","Метро не ждёт",40,"Как спросить время?",listOf("いま なんじ？","どこですか","だれですか","なんですか"),0,LessonKind.DIALOGUE),
    Lesson("Токио 15 — Три часа","さんじ","sanji","три часа","Учим часы",35,"Выбери «три часа»",listOf("さんじ","さんぷん","さんにち","さんさい"),0,LessonKind.TAP),
    Lesson("Токио 16 — Еда","ごはん","gohan","еда / рис","Обед в Токио",30,"Что значит ごはん?",listOf("еда / рис","вода","чай","магазин"),0,LessonKind.TRANSLATE),
    Lesson("Токио 17 — Вкусно!","おいしい","oishii","вкусно","Первый укус рамена",35,"Ты попробовал блюдо. Что скажешь?",listOf("おいしい","たかい","さむい","おそい"),0,LessonKind.DIALOGUE),
    Lesson("Токио 18 — Воду, пожалуйста","みずを ください","mizu o kudasai","воды, пожалуйста","Заказ в кафе",45,"Что просит Котоба?",listOf("кофе","воду","счёт","меню"),1,LessonKind.CHOICE),
    Lesson("Токио 19 — Где станция?","えきは どこですか","eki wa doko desu ka","где станция?","Ориентация в городе",45,"Собери вопрос",listOf("えきは","ですか","どこ"),0,LessonKind.ORDER),
    Lesson("Токио 20 — Вокзал","えき","eki","станция / вокзал","Первый маршрут",30,"Найди слово «станция»",listOf("えき","みせ","いえ","まち"),0,LessonKind.TAP),
    Lesson("Токио 21 — Друг","ともだち","tomodachi","друг","Люди вокруг нас",30,"Кто такой ともだち?",listOf("друг","семья","учитель","студент"),0,LessonKind.TRANSLATE),
    Lesson("Токио 22 — Мне нравится","すきです","suki desu","нравится","Говорим о вкусах",40,"Что значит すきです?",listOf("нравится","не знаю","извините","до свидания"),0,LessonKind.CHOICE),
    Lesson("Токио 23 — Частица は","わたしは がくせいです","watashi wa gakusei desu","я студент","Собираем грамматику N5",50,"Что делает は?",listOf("обозначает тему","обозначает время","означает «нет»","это глагол"),0,LessonKind.FILL),
    Lesson("Токио 24 — Большая проверка","すみません。えきは どこですか。","sumimasen. eki wa doko desu ka","Извините. Где станция?","Финал миссии Токио",100,"Как спросить, где станция?",listOf("えきは どこですか","みずを ください","おいしいです","ありがとう"),0,LessonKind.DIALOGUE)
)

data class Kana(val symbol:String,val romaji:String)
private val hiragana=listOf(Kana("あ","a"),Kana("い","i"),Kana("う","u"),Kana("え","e"),Kana("お","o"),Kana("か","ka"),Kana("き","ki"),Kana("く","ku"),Kana("け","ke"),Kana("こ","ko"),Kana("さ","sa"),Kana("し","shi"),Kana("す","su"),Kana("せ","se"),Kana("そ","so"),Kana("た","ta"),Kana("ち","chi"),Kana("つ","tsu"),Kana("て","te"),Kana("と","to"),Kana("な","na"),Kana("に","ni"),Kana("ぬ","nu"),Kana("ね","ne"),Kana("の","no"),Kana("は","ha"),Kana("ひ","hi"),Kana("ふ","fu"),Kana("へ","he"),Kana("ほ","ho"),Kana("ま","ma"),Kana("み","mi"),Kana("む","mu"),Kana("め","me"),Kana("も","mo"),Kana("や","ya"),Kana("ゆ","yu"),Kana("よ","yo"),Kana("ら","ra"),Kana("り","ri"),Kana("る","ru"),Kana("れ","re"),Kana("ろ","ro"),Kana("わ","wa"),Kana("を","wo"),Kana("ん","n"))
private val katakana=listOf(Kana("ア","a"),Kana("イ","i"),Kana("ウ","u"),Kana("エ","e"),Kana("オ","o"),Kana("カ","ka"),Kana("キ","ki"),Kana("ク","ku"),Kana("ケ","ke"),Kana("コ","ko"),Kana("サ","sa"),Kana("シ","shi"),Kana("ス","su"),Kana("セ","se"),Kana("ソ","so"),Kana("タ","ta"),Kana("チ","chi"),Kana("ツ","tsu"),Kana("テ","te"),Kana("ト","to"),Kana("ナ","na"),Kana("ニ","ni"),Kana("ヌ","nu"),Kana("ネ","ne"),Kana("ノ","no"),Kana("ハ","ha"),Kana("ヒ","hi"),Kana("フ","fu"),Kana("ヘ","he"),Kana("ホ","ho"),Kana("マ","ma"),Kana("ミ","mi"),Kana("ム","mu"),Kana("メ","me"),Kana("モ","mo"),Kana("ヤ","ya"),Kana("ユ","yu"),Kana("ヨ","yo"),Kana("ラ","ra"),Kana("リ","ri"),Kana("ル","ru"),Kana("レ","re"),Kana("ロ","ro"),Kana("ワ","wa"),Kana("ヲ","wo"),Kana("ン","n"))

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
        var xp by remember { mutableIntStateOf(0) }
        var done by remember { mutableIntStateOf(0) }
        var xpPop by remember { mutableStateOf(false) }
        LaunchedEffect(xpPop) { if (xpPop) { delay(1000); xpPop = false } }

        Scaffold(
            containerColor=Washi,
            bottomBar={
                if (lesson < 0) NavigationBar(containerColor=Color.White) {
                    val names=listOf("Главная","Путь","Алфавит","Слова","Повтор","Профиль")
                    val icons=listOf(Icons.Rounded.Home,Icons.Rounded.Map,Icons.Rounded.TextFields,Icons.Rounded.MenuBook,Icons.Rounded.LocalFireDepartment,Icons.Rounded.EmojiEvents)
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
                            xpPop=true
                        }
                    },
                    onBack={lesson=-1;step=0}
                )
            } else when(tab) {
                0 -> Home(Modifier.padding(pad),xp,done,{lesson=done.coerceAtMost(lessons.lastIndex);step=0},{tab=1})
                1 -> PathScreen(Modifier.padding(pad),done,{i->lesson=i;step=0})
                2 -> AlphabetScreen(Modifier.padding(pad))
                3 -> WordsScreen(Modifier.padding(pad),{lesson=done.coerceAtMost(lessons.lastIndex);step=0})
                4 -> ReviewScreen(Modifier.padding(pad),{lesson=done.coerceAtMost(lessons.lastIndex);step=0})
                else -> ProfileScreen(Modifier.padding(pad),xp,done)
            }
        }
    }
}

@Composable
fun Home(modifier:Modifier,xp:Int,done:Int,onStart:()->Unit,onPath:()->Unit) {
    val level=xp/100+1
    val nextIndex=done.coerceAtMost(lessons.lastIndex)
    val nextLesson=lessons[nextIndex]
    val progress=(xp%100)/100f
    val anim by animateFloatAsState(progress,tween(900),label="xp")
    LazyColumn(modifier.fillMaxSize(),contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(15.dp)) {
        item { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
            Column { Text("おかえりなさい",fontSize=28.sp,fontWeight=FontWeight.Bold); Text("Твой путь начинается здесь",color=Indigo) }
            Box(Modifier.size(54.dp).background(Red,CircleShape),contentAlignment=Alignment.Center){Text("N5",color=Color.White,fontWeight=FontWeight.Black)}
        }}
        item { MentorCard(level,done,xp) }
        item { Card(Modifier.fillMaxWidth(),RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Indigo)) {
            Column(Modifier.padding(22.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                    Column { Text("УРОВЕНЬ "+level,color=Gold,fontSize=12.sp,fontWeight=FontWeight.Bold); Text("Самурай слов",color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Bold) }
                    Text(xp.toString()+" XP",color=Color.White,fontWeight=FontWeight.Bold)
                }
                LinearProgressIndicator(progress={anim},Modifier.fillMaxWidth().height(8.dp),color=Gold,trackColor=Color.White.copy(alpha=.18f))
                Text((100-(xp%100)).toString()+" XP до следующего уровня",color=Color.White.copy(alpha=.75f),fontSize=12.sp)
            }
        }}
        item { Text("Твоя первая миссия",fontSize=20.sp,fontWeight=FontWeight.Bold) }
        item { Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
            Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).background(Color(0xFFFFE5DF),CircleShape),contentAlignment=Alignment.Center){Text("東京",color=Red,fontSize=13.sp,fontWeight=FontWeight.Bold)}
                    Spacer(Modifier.width(12.dp)); Column { Text("東京 · Миссия 1 · 24 урока",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Bold); Text(nextLesson.title,fontSize=20.sp,fontWeight=FontWeight.Bold) }
                }
                Text("24 интерактивных урока · диалоги · сборка фраз · выборы · мини-игры",color=Indigo)
                Button(onClick=onStart,Modifier.fillMaxWidth(),shape=RoundedCornerShape(15.dp)){Icon(Icons.Rounded.PlayArrow,null);Spacer(Modifier.width(6.dp));Text("Начать миссию")}
            }
        }}
        item { Text("Режимы обучения",fontSize=20.sp,fontWeight=FontWeight.Bold) }
        item { Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) { ModeCard("あ","Хирагана",Modifier.weight(1f)); ModeCard("文","Грамматика",Modifier.weight(1f)); ModeCard("会","Диалог",Modifier.weight(1f)) } }
        item { OutlinedButton(onClick=onPath,Modifier.fillMaxWidth(),shape=RoundedCornerShape(15.dp)){Icon(Icons.Rounded.Map,null);Spacer(Modifier.width(6.dp));Text("Открыть карту Японии")} }
        item { Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){Stat("🔥 7","дней",Modifier.weight(1f));Stat(done.toString(),"миссий",Modifier.weight(1f));Stat("12","слов",Modifier.weight(1f))} }
    }
}
@Composable
fun MentorCard(level:Int,done:Int,xp:Int) {
    val bob by rememberInfiniteTransition(label="mentor").animateFloat(.98f,1.03f,infiniteRepeatable(tween(1300),RepeatMode.Reverse),label="bob")
    val stage=when(level){1->"Младенец Котоба";2,3->"Юный Котоба";4,5->"Котоба-ниндзя";else->"Мастер Котоба"}
    Card(Modifier.fillMaxWidth(),RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
        Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Box(Modifier.size(154.dp).scale(bob).background(Brush.radialGradient(listOf(Color(0xFFFFF5DC),Color(0xFFE8E1D3))),CircleShape),contentAlignment=Alignment.Center) {
                    KotobaMascot(level,Modifier.size(146.dp))
                }
                Spacer(Modifier.width(15.dp))
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    Text("МР. КОТОБА",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Black)
                    Text(stage,fontSize=21.sp,fontWeight=FontWeight.ExtraBold)
                    Text(if(level==1)"「おぎゃー！」" else "「一緒に行こう！」",fontSize=16.sp,fontWeight=FontWeight.Bold)
                    Text(if(level==1)"Он только появился. Первый урок — его первый шаг." else "Маскот растёт вместе с твоим прогрессом.",color=Indigo,fontSize=12.sp)
                    Text("Уровень "+level+" · "+done+" уроков",color=Sage,fontSize=12.sp)
                }
            }
            LinearProgressIndicator(progress={((xp%100)/100f)},Modifier.fillMaxWidth().height(7.dp),color=Gold,trackColor=Mist)
            Text(if(level==1)"Первый уровень · Младенец Котоба · 0–99 XP" else "До следующего облика: "+(100-(xp%100))+" XP",color=Indigo,fontSize=11.sp)
        }
    }
}
@Composable
fun KotobaMascot(level:Int,modifier:Modifier=Modifier) {
    Canvas(modifier) {
        val c=center
        val green=Color(0xFF9BCB72)
        val green2=Color(0xFF78A95F)
        val skin=Color(0xFFFFD7C9)
        val diaper=Color(0xFFF7F0DF)
        if(level==1){
            drawCircle(green,39f,c.copy(x=c.x-12f,y=c.y+28f))
            drawCircle(green,47f,c.copy(x=c.x+24f,y=c.y-6f))
            drawCircle(green2,20f,c.copy(x=c.x-37f,y=c.y+41f))
            drawCircle(green2,18f,c.copy(x=c.x+55f,y=c.y+43f))
            drawOval(diaper,topLeft=c.copy(x=c.x-8f,y=c.y+27f),size=androidx.compose.ui.geometry.Size(66f,38f))
            drawLine(Gold,c.copy(x=c.x-8f,y=c.y+42f),c.copy(x=c.x+58f,y=c.y+42f),strokeWidth=3f)
            drawCircle(skin,4f,c.copy(x=c.x+2f,y=c.y+42f))
            drawCircle(skin,4f,c.copy(x=c.x+45f,y=c.y+42f))
            drawCircle(Color.White,9f,c.copy(x=c.x+9f,y=c.y-14f))
            drawCircle(Color.White,9f,c.copy(x=c.x+39f,y=c.y-14f))
            drawCircle(Ink,4f,c.copy(x=c.x+11f,y=c.y-13f))
            drawCircle(Ink,4f,c.copy(x=c.x+38f,y=c.y-13f))
            drawArc(Red,10f,160f,false,c.copy(x=c.x+8f,y=c.y+1f),style=Stroke(width=4f))
            drawLine(green2,c.copy(x=c.x+1f,y=c.y-48f),c.copy(x=c.x-13f,y=c.y-67f),strokeWidth=5f)
            drawLine(green2,c.copy(x=c.x+48f,y=c.y-45f),c.copy(x=c.x+62f,y=c.y-64f),strokeWidth=5f)
            drawOval(green2,topLeft=c.copy(x=c.x-22f,y=c.y-78f),size=androidx.compose.ui.geometry.Size(23f,11f))
            drawOval(green2,topLeft=c.copy(x=c.x+55f,y=c.y-74f),size=androidx.compose.ui.geometry.Size(23f,11f))
            drawCircle(Red,5f,c.copy(x=c.x+24f,y=c.y+27f))
        } else {
            val body=when { level>=6->Color(0xFF8E6CCB); level>=4->Color(0xFF5E8F72); else->green }
            val segments=when { level>=6->5; level>=4->4; else->3 }
            for(i in 0 until segments) drawCircle(body,31f-i*1.2f,c.copy(x=c.x-38f+i*18f,y=c.y+25f))
            drawCircle(green,42f,c.copy(x=c.x+24f,y=c.y-6f))
            drawCircle(Color.White,9f,c.copy(x=c.x+11f,y=c.y-13f)); drawCircle(Color.White,9f,c.copy(x=c.x+40f,y=c.y-13f))
            drawCircle(Ink,4f,c.copy(x=c.x+13f,y=c.y-12f)); drawCircle(Ink,4f,c.copy(x=c.x+38f,y=c.y-12f))
            drawArc(Red,0f,180f,false,c.copy(x=c.x+10f,y=c.y+3f),style=Stroke(width=4f))
            drawLine(green2,c.copy(x=c.x+5f,y=c.y-42f),c.copy(x=c.x-7f,y=c.y-60f),strokeWidth=5f)
            drawLine(green2,c.copy(x=c.x+43f,y=c.y-42f),c.copy(x=c.x+56f,y=c.y-60f),strokeWidth=5f)
            drawOval(green2,topLeft=c.copy(x=c.x-17f,y=c.y-72f),size=androidx.compose.ui.geometry.Size(25f,12f))
            drawOval(green2,topLeft=c.copy(x=c.x+51f,y=c.y-72f),size=androidx.compose.ui.geometry.Size(25f,12f))
            if(level>=4) drawLine(Gold,c.copy(x=c.x-15f,y=c.y+18f),c.copy(x=c.x-46f,y=c.y+3f),strokeWidth=8f)
        }
    }
}
@Composable
fun ModeCard(icon:String,title:String,m:Modifier) {
    Card(modifier=m,shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) { Column(Modifier.fillMaxWidth().padding(13.dp),horizontalAlignment=Alignment.CenterHorizontally) { Text(icon,fontSize=27.sp,color=Red,fontWeight=FontWeight.Bold); Text(title,fontSize=11.sp,fontWeight=FontWeight.Bold) } }
}
@Composable
fun Stat(a:String,b:String,m:Modifier){Card(modifier=m,shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.fillMaxWidth().padding(14.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(a,fontSize=22.sp,fontWeight=FontWeight.Bold);Text(b,fontSize=11.sp,color=Indigo)}}}

@Composable
fun PathScreen(modifier:Modifier,done:Int,onLesson:(Int)->Unit){
    LazyColumn(modifier.fillMaxSize(),contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
        item{
            Text("Путь N5",fontSize=30.sp,fontWeight=FontWeight.Black)
            Text("日本の旅 · четыре региона · один путь",color=Indigo)
        }
        item{
            Card(shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){
                Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
                    Text("🗾  КАРТА ЯПОНИИ",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Black)
                    Text("Только четыре города",fontSize=21.sp,fontWeight=FontWeight.Bold)
                    Text("Сейчас открыт только Токио. Остальные регионы появятся после прохождения пути.",color=Indigo,fontSize=12.sp)
                    JapanMap(done)
                }
            }
        }
        item{
            Text("東京 · Миссия 1",fontSize=22.sp,fontWeight=FontWeight.Black)
            Text(done.toString()+" / "+lessons.size+" уроков пройдено",color=Indigo)
        }
        itemsIndexed(lessons){i,l->
            val unlocked=i<=done
            val finished=i<done
            Card(onClick={if(unlocked)onLesson(i)},enabled=unlocked,shape=RoundedCornerShape(22.dp),
                colors=CardDefaults.cardColors(containerColor=if(finished)Color.White else if(unlocked)Color(0xFFFFF7F1) else Mist)){
                Row(Modifier.fillMaxWidth().padding(15.dp),verticalAlignment=Alignment.CenterVertically){
                    Box(Modifier.size(50.dp).background(if(finished)Sage else if(unlocked)Red else Color.Gray,CircleShape),contentAlignment=Alignment.Center){
                        if(finished) Icon(Icons.Rounded.CheckCircle,null,tint=Color.White)
                        else if(!unlocked) Icon(Icons.Rounded.Lock,null,tint=Color.White)
                        else Text((i+1).toString(),color=Color.White,fontWeight=FontWeight.Bold)
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)){
                        Text(l.title,fontWeight=FontWeight.Bold,fontSize=16.sp)
                        Text(l.jp,color=Indigo)
                        Text(l.xp.toString()+" XP · "+l.subtitle,color=Color.Gray,fontSize=11.sp)
                    }
                }
            }
        }
    }
}
@Composable
fun JapanMap(done:Int){
    Canvas(Modifier.fillMaxWidth().height(310.dp).background(Color(0xFFF0ECE1),RoundedCornerShape(24.dp))){
        val w=size.width; val h=size.height
        val sea=Color(0xFFDCE8E8)
        drawRect(sea)
        val land=Color(0xFFE9E2CF)
        val stroke=Color(0xFFB7AD98)
        val p=Path().apply{
            moveTo(w*.22f,h*.18f); lineTo(w*.30f,h*.11f); lineTo(w*.38f,h*.15f); lineTo(w*.47f,h*.24f)
            lineTo(w*.55f,h*.32f); lineTo(w*.61f,h*.43f); lineTo(w*.66f,h*.57f); lineTo(w*.60f,h*.72f)
            lineTo(w*.51f,h*.84f); lineTo(w*.40f,h*.77f); lineTo(w*.31f,h*.66f); lineTo(w*.25f,h*.51f)
            lineTo(w*.18f,h*.36f); close()
        }
        drawPath(p,land,style=Fill)
        drawPath(p,stroke,style=Stroke(width=3f))
        val tokyo=Offset(w*.57f,h*.49f)
        val kyoto=Offset(w*.48f,h*.57f)
        val osaka=Offset(w*.43f,h*.62f)
        val hokkaido=Offset(w*.43f,h*.15f)
        drawLine(stroke,tokyo,kyoto,strokeWidth=4f)
        drawLine(stroke,kyoto,osaka,strokeWidth=4f)
        drawLine(stroke,kyoto,hokkaido,strokeWidth=4f)
        cityPin(tokyo,Red,done>=1,"東京")
        cityPin(kyoto,Color(0xFF6C7A8E),done>=8,"京都")
        cityPin(osaka,Color(0xFF7D8E62),done>=16,"大阪")
        cityPin(hokkaido,Color(0xFFB5965A),done>=24,"北海道")
    }
}
fun androidx.compose.ui.graphics.drawscope.DrawScope.cityPin(p:Offset,color:Color,open:Boolean,label:String){
    drawCircle(if(open)color else Color(0xFF8E8E8E),14f,p)
    drawCircle(Color.White,5f,p)
    drawContext.canvas.nativeCanvas.drawText(label,p.x-18f,p.y+31f,android.graphics.Paint().apply{this.color=Ink.toArgb();textSize=26f;isFakeBoldText=true})
}
@Composable
fun AlphabetScreen(modifier:Modifier) {
    var katakanaMode by rememberSaveable { mutableStateOf(true) }
    var learned by rememberSaveable { mutableIntStateOf(0) }
    val kana=if(katakanaMode) katakana else hiragana
    LazyColumn(modifier.fillMaxSize(),contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item {
            Text("Алфавит",fontSize=30.sp,fontWeight=FontWeight.Black)
            Text("かな · отдельный тренажёр Хираганы и Катаканы",color=Indigo)
            Row(Modifier.fillMaxWidth().background(Color.White,RoundedCornerShape(16.dp)).padding(4.dp)) {
                listOf(false to "あ Хирагана",true to "カ Катакана").forEach { (isKat,label) ->
                    Button(onClick={katakanaMode=isKat},modifier=Modifier.weight(1f),colors=ButtonDefaults.buttonColors(containerColor=if(katakanaMode==isKat) Red else Color.Transparent,contentColor=if(katakanaMode==isKat) Color.White else Ink),shape=RoundedCornerShape(12.dp)){Text(label,fontSize=12.sp)}
                }
            }
        }
        item {
            Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=Indigo)) {
                Row(Modifier.fillMaxWidth().padding(18.dp),verticalAlignment=Alignment.CenterVertically) {
                    Text(if(katakanaMode)"カ" else "あ",fontSize=52.sp,color=Color.White,fontWeight=FontWeight.Black)
                    Spacer(Modifier.width(14.dp))
                    Column { Text(if(katakanaMode)"Катакана" else "Хирагана",color=Gold,fontSize=13.sp,fontWeight=FontWeight.Bold); Text("Освоено "+learned+" / "+kana.size,color=Color.White,fontSize=19.sp,fontWeight=FontWeight.Bold); Text("Нажимай на символ, чтобы отметить его.",color=Color.White.copy(alpha=.72f),fontSize=11.sp) }
                }
            }
        }
        itemsIndexed(kana.chunked(5)) { _,row ->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                row.forEach { k ->
                    var isLearned by rememberSaveable(k.symbol,katakanaMode) { mutableStateOf(false) }
                    Card(onClick={if(!isLearned){isLearned=true;learned++}},modifier=Modifier.weight(1f),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=if(isLearned)Color(0xFFE3EEDC) else Color.White)) {
                        Column(Modifier.fillMaxWidth().padding(vertical=13.dp),horizontalAlignment=Alignment.CenterHorizontally) { Text(k.symbol,fontSize=28.sp,fontWeight=FontWeight.Black,color=Red); Text(k.romaji,fontSize=11.sp,color=Indigo); if(isLearned) Icon(Icons.Rounded.CheckCircle,null,tint=Sage,modifier=Modifier.size(16.dp)) }
                    }
                }
                repeat(5-row.size){Spacer(Modifier.weight(1f))}
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
        Card(shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.fillMaxWidth().padding(22.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("称号",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Bold);Text("Начинающий самурай",fontSize=24.sp,fontWeight=FontWeight.Bold);Text("Уровень "+(xp/100+1)+" · "+xp+" XP · "+done+" миссий",color=Indigo)}}
        Text("Достижения",fontSize=20.sp,fontWeight=FontWeight.Bold);Achievement("Первый шаг","Заверши первую миссию в Токио",done>=1);Achievement("Серия 7","Занимайся семь дней",true);Achievement("Котоба","Собери 100 слов",false)
    }
}
@Composable
fun Achievement(t:String,s:String,on:Boolean){Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=if(on)Color.White else Mist)){Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(if(on)Icons.Rounded.EmojiEvents else Icons.Rounded.Lock,null,tint=if(on)Gold else Color.Gray,modifier=Modifier.size(30.dp));Spacer(Modifier.width(14.dp));Column{Text(t,fontWeight=FontWeight.Bold);Text(s,color=Indigo,fontSize=12.sp)}}}}

@Composable
fun LessonScreen(modifier:Modifier,lesson:Lesson,step:Int,onNext:()->Unit,onBack:()->Unit) {
    var selected by remember(lesson,step){mutableIntStateOf(-1)}
    var order by remember(lesson,step){mutableStateOf(listOf<String>())}
    var checked by remember(lesson,step){mutableStateOf(false)}
    val correct = selected==lesson.answer
    val orderTarget = when(lesson.title){
        "Токио 04 — Спасибо" -> listOf("あり","が","とう")
        "Токио 19 — Где станция?" -> listOf("えきは","どこ","ですか")
        else -> lesson.choices
    }
    val orderCorrect = order == orderTarget
    val interactiveDone = when(lesson.kind){
        LessonKind.ORDER -> orderCorrect
        else -> selected==lesson.answer
    }
    Column(modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            TextButton(onClick=onBack){Text("← Назад")}
            Spacer(Modifier.weight(1f))
            Text(lesson.xp.toString()+" XP",color=Gold,fontWeight=FontWeight.Bold)
        }
        Text(lesson.title,fontSize=28.sp,fontWeight=FontWeight.Black)
        Text(when(step){0->"Шаг 1 · Знакомство";1->"Шаг 2 · Пойми";2->"Шаг 3 · Играй";else->"Шаг 4 · Босс"},color=Indigo)
        LinearProgressIndicator(progress={(step+1)/4f},Modifier.fillMaxWidth().height(8.dp),color=Red,trackColor=Mist)
        AnimatedContent(targetState=step,transitionSpec={fadeIn(tween(220)) togetherWith fadeOut(tween(140))},label="lesson") { current ->
            when(current){
                0 -> Column(verticalArrangement=Arrangement.spacedBy(14.dp)){
                    Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){
                        Column(Modifier.padding(26.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(13.dp)){
                            Box(Modifier.size(90.dp).background(Red.copy(alpha=.1f),CircleShape),contentAlignment=Alignment.Center){Text("東京",fontSize=23.sp,color=Red,fontWeight=FontWeight.Black)}
                            Text(lesson.jp,fontSize=27.sp,fontWeight=FontWeight.Black)
                            Text(lesson.reading,color=Gold,fontWeight=FontWeight.Bold)
                            Text(lesson.meaning,fontSize=18.sp,color=Indigo)
                            Text(lesson.subtitle,color=Color.Gray)
                        }
                    }
                    Button(onClick=onNext,Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(16.dp)){Text("Дальше →")}
                }
                1 -> Column(verticalArrangement=Arrangement.spacedBy(14.dp)){
                    Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){
                        Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(13.dp)){
                            Text("Мини-тренировка",color=Gold,fontWeight=FontWeight.Bold)
                            Text(lesson.question,fontSize=20.sp,fontWeight=FontWeight.Bold)
                            Text("Прочитай японскую фразу вслух и представь ситуацию в Токио.",color=Sage)
                            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                                AssistChip(onClick={},label={Text("🔊 Слушаю")})
                                AssistChip(onClick={},label={Text("👄 Повторяю")})
                            }
                        }
                    }
                    Button(onClick=onNext,Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(16.dp)){Text("Я готов →")}
                }
                2 -> InteractiveTask(lesson,selected,order,checked,{selected=it},{order=order+it},{checked=true},{order=order.dropLast(1)})
                else -> Column(verticalArrangement=Arrangement.spacedBy(12.dp)){
                    Text("Финальный бросок",fontSize=21.sp,fontWeight=FontWeight.Bold)
                    Text(lesson.question,color=Indigo)
                    lesson.choices.forEachIndexed { i,ch ->
                        val bg=when{selected<0->Color.White;i==lesson.answer->Color(0xFFE3EEDC);i==selected->Color(0xFFF5D9D5);else->Color.White}
                        Card(onClick={if(selected<0)selected=i},modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=bg)){
                            Row(Modifier.padding(16.dp).fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                                Text(ch,fontSize=17.sp,fontWeight=if(i==lesson.answer)FontWeight.Bold else FontWeight.Normal)
                                Spacer(Modifier.weight(1f))
                                if(selected>=0 && i==lesson.answer)Icon(Icons.Rounded.CheckCircle,null,tint=Sage)
                            }
                        }
                    }
                    if(selected>=0) Text(if(correct)"正解 · отлично! Миссия продвигается." else "Почти! Посмотри на подсказку и попробуй ещё.",color=if(correct)Sage else Red,fontWeight=FontWeight.Bold)
                    Button(onClick={if(selected==lesson.answer){onNext()}else{selected=-1}},Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(16.dp)){Text(if(selected==lesson.answer)"Завершить урок · +"+lesson.xp+" XP ✨" else "Переиграть")}
                }
            }
        }
    }
}

@Composable
fun InteractiveTask(
    lesson:Lesson,selected:Int,order:List<String>,checked:Boolean,
    onSelect:(Int)->Unit,onAdd:(String)->Unit,onCheck:()->Unit,onUndo:()->Unit
){
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)){
        when(lesson.kind){
            LessonKind.ORDER -> {
                Text("Собери фразу в правильном порядке",fontSize=20.sp,fontWeight=FontWeight.Bold)
                Text(if(order.isEmpty())"Нажимай на части фразы снизу." else order.joinToString(" · "),color=Indigo,fontSize=18.sp)
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()){
                    lesson.choices.forEach { token ->
                        val used=order.contains(token)
                        Button(onClick={if(!used)onAdd(token)},enabled=!used,shape=RoundedCornerShape(14.dp)){Text(token)}
                    }
                }
                Button(onClick={if(order.isNotEmpty())onUndo()},Modifier.fillMaxWidth(),enabled=order.isNotEmpty()){Text("↩ Убрать последнее")}
                Button(onClick=onCheck,Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(16.dp),enabled=order.size==3){Text(if(checked)if(order==when(lesson.title){"Токио 04 — Спасибо"->listOf("あり","が","とう");else->listOf("えきは","どこ","ですか")})"Правильно! 🎉" else "Порядок неверный" else "Проверить порядок")}
                if(checked) Text(if(order==when(lesson.title){"Токио 04 — Спасибо"->listOf("あり","が","とう");else->listOf("えきは","どこ","ですか")})"Отличная сборка!" else "Попробуй снова.",color=if(order==when(lesson.title){"Токио 04 — Спасибо"->listOf("あり","が","とう");else->listOf("えきは","どこ","ですか")})Sage else Red,fontWeight=FontWeight.Bold)
            }
            else -> {
                Text(lesson.question,fontSize=20.sp,fontWeight=FontWeight.Bold)
                lesson.choices.forEachIndexed { i,ch ->
                    val isSel=i==selected
                    val bg=if(isSel && selected>=0)if(i==lesson.answer)Color(0xFFE3EEDC) else Color(0xFFF5D9D5) else Color.White
                    Card(onClick={if(selected<0)onSelect(i)},modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=bg)){
                        Row(Modifier.padding(17.dp).fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                            Box(Modifier.size(38.dp).background(Mist,CircleShape),contentAlignment=Alignment.Center){Text(('A'.code+i).toChar().toString(),fontWeight=FontWeight.Bold)}
                            Spacer(Modifier.width(12.dp)); Text(ch,fontSize=17.sp); Spacer(Modifier.weight(1f))
                            if(isSel && i==lesson.answer)Icon(Icons.Rounded.CheckCircle,null,tint=Sage)
                            if(isSel && i!=lesson.answer)Icon(Icons.Rounded.Close,null,tint=Red)
                        }
                    }
                }
                if(selected>=0) Text(if(selected==lesson.answer)"Правильно! Теперь финальный шаг." else "Не совсем. Выбери другой вариант.",color=if(selected==lesson.answer)Sage else Red,fontWeight=FontWeight.Bold)
                Button(onClick={if(selected==lesson.answer)onCheck()else onSelect(-1)},Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(16.dp)){Text(if(selected==lesson.answer)"Продолжить →" else "Попробовать ещё")}
            }
        }
    }
}