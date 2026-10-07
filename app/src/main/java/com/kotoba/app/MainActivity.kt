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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.toArgb
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
    val bob by rememberInfiniteTransition(label="mentor").animateFloat(
        .985f,1.015f,
        infiniteRepeatable(tween(1500),RepeatMode.Reverse),
        label="bob"
    )
    val stage = when {
        xp >= 5000 -> "Легенда"
        xp >= 2000 -> "Мастер"
        xp >= 1000 -> "Знаток"
        xp >= 500 -> "Ученик"
        xp >= 100 -> "Новичок"
        else -> "Младенец"
    }
    val nextXp = when {
        xp < 100 -> 100
        xp < 500 -> 500
        xp < 1000 -> 1000
        xp < 2000 -> 2000
        xp < 5000 -> 5000
        else -> 5000
    }
    val progress = if (xp >= 5000) 1f else {
        val prev = when {
            xp < 100 -> 0
            xp < 500 -> 100
            xp < 1000 -> 500
            xp < 2000 -> 1000
            else -> 2000
        }
        ((xp-prev).toFloat()/(nextXp-prev).coerceAtLeast(1)).coerceIn(0f,1f)
    }
    Card(Modifier.fillMaxWidth(),RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
        Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Box(
                    Modifier.size(166.dp).scale(bob)
                        .background(Brush.radialGradient(listOf(Color(0xFFFFF8E8),Color(0xFFE8E1D3))),CircleShape),
                    contentAlignment=Alignment.Center
                ) {
                    KotobaMascot(xp,Modifier.size(158.dp))
                }
                Spacer(Modifier.width(15.dp))
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    Text("KOTOBA",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Black)
                    Text(stage,fontSize=23.sp,fontWeight=FontWeight.ExtraBold)
                    Text(
                        when {
                            xp < 100 -> "「おぎゃー！」"
                            xp < 500 -> "「いっしょに！」"
                            xp < 1000 -> "「べんきょうしよう！」"
                            xp < 2000 -> "「わかった！」"
                            xp < 5000 -> "「まかせて！」"
                            else -> "「いこう！」"
                        },
                        fontSize=15.sp,fontWeight=FontWeight.Bold
                    )
                    Text(
                        if(xp < 100) "Первый уровень. Он только появился в Токио."
                        else "Маскот эволюционирует вместе с твоим XP.",
                        color=Indigo,fontSize=12.sp
                    )
                    Text("Уровень $level · $done уроков · $xp XP",color=Sage,fontSize=12.sp)
                }
            }
            LinearProgressIndicator(
                progress={progress},
                Modifier.fillMaxWidth().height(7.dp),
                color=Gold,trackColor=Mist
            )
            Text(
                if(xp >= 5000) "Форма Легенды достигнута · 5 000 XP"
                else "Следующая форма: $nextXp XP",
                color=Indigo,fontSize=11.sp
            )
        }
    }
}

@Composable
fun KotobaMascot(xp:Int,modifier:Modifier=Modifier) {
    Canvas(modifier) {
        val sx = size.width / 160f
        val sy = size.height / 160f
        scale(sx, sy) {
            val cx = 80f
            val green = Color(0xFFA9D957)
            val lightGreen = Color(0xFFC7EA72)
            val darkGreen = Color(0xFF5F9E3E)
            val leaf = Color(0xFF4F9E2C)
            val leafLight = Color(0xFF83C83D)
            val cream = Color(0xFFF4E8C9)
            val scarf = Color(0xFFD9362F)
            val scarfDark = Color(0xFFA92225)
            val brown = Color(0xFF6A4930)
            val skin = Color(0xFFFFC7B7)

            val stage = when {
                xp >= 5000 -> 5
                xp >= 2000 -> 4
                xp >= 1000 -> 3
                xp >= 500 -> 2
                xp >= 100 -> 1
                else -> 0
            }

            // Tail/body: soft segmented caterpillar silhouette like the reference.
            val segments = when(stage) {
                0 -> 3
                1 -> 4
                2 -> 5
                3 -> 5
                4,5 -> 6
                else -> 3
            }
            for (i in 0 until segments) {
                val x = 28f + i*18f
                val y = 112f + if(i%2==0) 4f else 0f
                drawCircle(
                    if(i==segments-1) green else lightGreen,
                    23f + (i.coerceAtMost(3))*1.2f,
                    Offset(x,y)
                )
                drawCircle(darkGreen.copy(alpha=.30f),5f,Offset(x-10f,y+7f))
            }

            // Cream belly.
            drawOval(
                cream,
                topLeft=Offset(52f,91f),
                size=androidx.compose.ui.geometry.Size(70f,48f)
            )

            // Head.
            drawCircle(green,42f,Offset(102f,61f))
            drawCircle(lightGreen.copy(alpha=.65f),34f,Offset(94f,52f))

            // Leaf antennae.
            fun antenna(x:Float,lean:Float) {
                drawLine(darkGreen,Offset(x,28f),Offset(x+lean,9f),strokeWidth=4f)
                drawOval(
                    leaf,
                    topLeft=Offset(x+lean-9f,0f),
                    size=androidx.compose.ui.geometry.Size(23f,12f)
                )
                drawLine(leafLight,Offset(x+lean-3f,3f),Offset(x+lean+7f,7f),strokeWidth=1.5f)
            }
            antenna(91f,-8f)
            antenna(119f,10f)

            // Huge glossy anime eyes.
            fun eye(x:Float) {
                drawCircle(Color.White,11f,Offset(x,55f))
                drawCircle(Color(0xFF5B3B24),7f,Offset(x,57f))
                drawCircle(Color.Black,4.6f,Offset(x,58f))
                drawCircle(Color.White,2.3f,Offset(x-2f,54f))
            }
            eye(91f); eye(116f)

            // Blush.
            drawCircle(Color(0xFFFF8F91).copy(alpha=.42f),5f,Offset(80f,70f))
            drawCircle(Color(0xFFFF8F91).copy(alpha=.42f),5f,Offset(128f,70f))

            // Happy mouth.
            drawArc(scarfDark,15f,150f,false,Offset(96f,65f),style=Stroke(width=3f))

            // Stage 0 = baby wrapped in a diaper, exactly the starting fantasy.
            if(stage == 0) {
                drawOval(Color.White.copy(alpha=.96f),Offset(59f,87f),androidx.compose.ui.geometry.Size(66f,30f))
                drawArc(Red,0f,180f,false,Offset(61f,91f),style=Stroke(width=2f))
                drawCircle(skin,4f,Offset(65f,109f))
                drawCircle(skin,4f,Offset(118f,108f))
            } else {
                // Red scarf appears from Novice onward.
                drawOval(scarf,Offset(58f,82f),androidx.compose.ui.geometry.Size(72f,20f))
                drawPath(
                    Path().apply {
                        moveTo(112f,92f); lineTo(145f,105f); lineTo(126f,111f); close()
                    },
                    scarf
                )
                drawLine(scarfDark,Offset(61f,91f),Offset(130f,91f),strokeWidth=3f)

                // Medal from Student onward.
                if(stage >= 2) {
                    drawCircle(scarfDark,9f,Offset(105f,105f))
                    drawCircle(Gold,7f,Offset(105f,105f))
                    drawLine(Ink,Offset(102f,101f),Offset(102f,109f),strokeWidth=1.6f)
                    drawLine(Ink,Offset(99f,105f),Offset(108f,105f),strokeWidth=1.6f)
                }

                // Backpack / scroll.
                if(stage >= 2) {
                    drawRoundRect(
                        brown,Offset(34f,88f),
                        androidx.compose.ui.geometry.Size(25f,35f),
                        cornerRadius=6f
                    )
                    drawLine(Gold,Offset(38f,94f),Offset(54f,94f),strokeWidth=2f)
                    drawLine(brown,Offset(43f,86f),Offset(39f,78f),strokeWidth=3f)
                    drawLine(brown,Offset(51f,86f),Offset(55f,78f),strokeWidth=3f)
                }

                // Bigger accessories and leaf cape at Master/Legend.
                if(stage >= 4) {
                    drawOval(leaf,Offset(22f,60f),androidx.compose.ui.geometry.Size(34f,50f))
                    drawLine(darkGreen,Offset(38f,67f),Offset(38f,99f),strokeWidth=2f)
                    drawPath(
                        Path().apply {
                            moveTo(42f,82f); lineTo(25f,108f); lineTo(45f,101f); close()
                        },
                        leafLight
                    )
                }

                // Legend crown-like leaf crest.
                if(stage >= 5) {
                    drawOval(Gold,Offset(118f,24f),androidx.compose.ui.geometry.Size(24f,8f))
                    drawOval(Gold,Offset(128f,28f),androidx.compose.ui.geometry.Size(20f,7f))
                }
            }

            // Small feet/paws.
            if(stage >= 1) {
                drawOval(brown,Offset(72f,128f),androidx.compose.ui.geometry.Size(14f,10f))
                drawOval(brown,Offset(101f,130f),androidx.compose.ui.geometry.Size(14f,10f))
            }

            // Character highlight and leaf spots.
            drawCircle(Color.White.copy(alpha=.18f),7f,Offset(78f,43f))
            drawCircle(darkGreen.copy(alpha=.32f),4f,Offset(74f,59f))
            drawCircle(darkGreen.copy(alpha=.25f),3f,Offset(124f,84f))
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
    Canvas(
        Modifier.fillMaxWidth()
            .height(390.dp)
            .background(Color(0xFF111923),RoundedCornerShape(28.dp))
    ){
        val w=size.width; val h=size.height
        // Four separate megacity-islands. Tokyo is deliberately dominant and glowing.
        metroIsland(Offset(w*.55f,h*.53f),w*.33f,h*.29f,done>=1,Red,"東京","TOKYO",true)
        metroIsland(Offset(w*.25f,h*.34f),w*.20f,h*.20f,done>=8,Color(0xFF6C7180),"京都","KYOTO",false)
        metroIsland(Offset(w*.25f,h*.73f),w*.19f,h*.18f,done>=16,Color(0xFF667267),"大阪","OSAKA",false)
        metroIsland(Offset(w*.72f,h*.18f),w*.22f,h*.18f,done>=24,Color(0xFF667786),"北海道","HOKKAIDO",false)
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.metroIsland(
    center:Offset, width:Float, height:Float, open:Boolean, accent:Color,
    jp:String, en:String, hero:Boolean
){
    val base=if(open) accent.copy(alpha=.92f) else Color(0xFF2B323A)
    val edge=if(open) Color.White.copy(alpha=.22f) else Color(0xFF4A525B)
    if(open && hero){
        drawCircle(accent.copy(alpha=.10f),maxOf(width,height)*.72f,center)
        drawCircle(accent.copy(alpha=.12f),maxOf(width,height)*.53f,center)
        drawCircle(accent.copy(alpha=.16f),maxOf(width,height)*.37f,center)
    }
    val p=Path().apply{
        moveTo(center.x-width*.48f,center.y-height*.05f)
        cubicTo(center.x-width*.42f,center.y-height*.48f,center.x-width*.05f,center.y-height*.55f,center.x+width*.28f,center.y-height*.40f)
        cubicTo(center.x+width*.54f,center.y-height*.22f,center.x+width*.49f,center.y+height*.22f,center.x+width*.24f,center.y+height*.43f)
        cubicTo(center.x-width*.05f,center.y+height*.55f,center.x-width*.40f,center.y+height*.42f,center.x-width*.48f,center.y+height*.05f)
        close()
    }
    drawPath(p,base)
    drawPath(p,edge,style=Stroke(width=3f))
    // dense lights make each island read as a living metropolis
    val lights=if(hero) 26 else 10
    for(i in 0 until lights){
        val angle=i*2.399f
        val rx=width*.32f*(.45f+(i%4)*.15f)
        val ry=height*.28f*(.45f+(i%3)*.16f)
        val px=center.x+kotlin.math.cos(angle)*rx
        val py=center.y+kotlin.math.sin(angle)*ry
        drawCircle(if(open) Gold.copy(alpha=.82f) else Color(0xFF555D66).copy(alpha=.45f),if(hero)3.2f else 2.4f,Offset(px,py))
    }
    val paint=android.graphics.Paint().apply{
        color=Color.White.toArgb(); textAlign=android.graphics.Paint.Align.CENTER; isFakeBoldText=true
    }
    paint.textSize=if(hero)38f else 27f
    drawContext.canvas.nativeCanvas.drawText(jp,center.x,center.y+5f,paint)
    paint.textSize=if(hero)15f else 11f
    paint.color=if(open)Gold.toArgb() else Color(0xFF858B92).toArgb()
    drawContext.canvas.nativeCanvas.drawText(if(open)en else "LOCKED",center.x,center.y+28f,paint)
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
                2 -> InteractiveTask(lesson,selected,order,checked,{selected=it},{order=order+it},{checked=true},{order=order.dropLast(1)},onNext)
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
    onSelect:(Int)->Unit,onAdd:(String)->Unit,onCheck:()->Unit,onUndo:()->Unit,onSuccess:()->Unit
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
                Button(onClick={
                    if(orderCorrect) onSuccess() else onCheck()
                },Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(16.dp),enabled=order.size==3){
                    Text(if(checked && orderCorrect)"Продолжить →" else if(checked)"Проверить ещё раз" else "Проверить порядок")
                }
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
                Button(onClick={if(selected==lesson.answer)onSuccess()else onSelect(-1)},Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(16.dp)){Text(if(selected==lesson.answer)"Продолжить →" else "Попробовать ещё")}
            }
        }
    }
}