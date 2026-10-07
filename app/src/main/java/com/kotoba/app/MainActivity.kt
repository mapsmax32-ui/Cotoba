package com.kotoba.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
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

data class Lesson(val title:String,val jp:String,val reading:String,val meaning:String,val subtitle:String,val xp:Int,val question:String,val choices:List<String>,val answer:Int)
private val lessons = listOf(
Lesson("Приветствия","こんにちは","konnichiwa","здравствуйте / добрый день","Базовые приветствия",25,"Что значит こんにちは?",listOf("Спасибо","Здравствуйте","До свидания","Извините"),1),
Lesson("До встречи","さようなら","sayounara","до свидания","Прощаемся вежливо",25,"Что значит さようなら?",listOf("Доброе утро","До свидания","Пожалуйста","Добрый вечер"),1),
Lesson("Доброе утро","おはようございます","ohayou gozaimasu","доброе утро","Утреннее приветствие",25,"Выбери «доброе утро».",listOf("こんばんは","おはようございます","ありがとう","すみません"),1),
Lesson("Добрый вечер","こんばんは","konbanwa","добрый вечер","Приветствие вечером",25,"Как сказать «добрый вечер»?",listOf("こんばんは","こんにちは","おやすみなさい","はじめまして"),0),
Lesson("Спасибо","ありがとう","arigatou","спасибо","Благодарность",30,"Что значит ありがとう?",listOf("Спасибо","Привет","Пока","Извините"),0),
Lesson("Очень вежливо","ありがとうございます","arigatou gozaimasu","большое спасибо","Вежливая благодарность",30,"Как сказать «большое спасибо» вежливо?",listOf("すみません","ありがとう","ありがとうございます","どうぞ"),2),
Lesson("Извинение","すみません","sumimasen","извините / простите","Извинение и внимание",30,"Что значит すみません?",listOf("Извините","Спасибо","Вкусно","Друг"),0),
Lesson("Знакомство","はじめまして","hajimemashite","приятно познакомиться","Первое знакомство",35,"Что говорят при первом знакомстве?",listOf("はじめまして","いただきます","おやすみ","おいしい"),0),
Lesson("Имя","わたしは 〜です","watashi wa ... desu","я — ...","Представляемся",35,"Что означает わたしは アンナです?",listOf("Я Анна","Это Анна","Анна — учитель","Где Анна?"),0),
Lesson("Человек","ひと","hito","человек","Базовое существительное",25,"Как по-японски «человек»?",listOf("ひと","ねこ","みず","ほん"),0),
Lesson("Числа 1–3","いち・に・さん","ichi · ni · san","один · два · три","Считаем",30,"Как читается いち?",listOf("ni","ichi","san","yon"),1),
Lesson("Числа 4–10","よん・ご・ろく・なな","yon · go · roku · nana","четыре · пять · шесть · семь","Продолжаем считать",30,"Как сказать «пять»?",listOf("ご","ろく","なな","はち"),0),
Lesson("Дни недели","げつようび","getsuyoubi","понедельник","Календарь N5",35,"Какой день げつようび?",listOf("Понедельник","Среда","Пятница","Воскресенье"),0),
Lesson("Сегодня","きょう","kyou","сегодня","Календарь",30,"Что значит きょう?",listOf("Вчера","Сегодня","Завтра","Сейчас"),1),
Lesson("Время","いま なんじ？","ima nanji?","который сейчас час?","Спрашиваем время",40,"Что значит いま なんじ？",listOf("Где вокзал?","Который сейчас час?","Кто это?","Сколько стоит?"),1),
Lesson("Часы","いちじ・さんじ","ichiji · sanji","час · три часа","Говорим время",35,"Как сказать «три часа»?",listOf("さんじ","さんぷん","さんにち","さんさい"),0),
Lesson("Еда","ごはん","gohan","еда / рис","Базовая лексика еды",30,"Что значит ごはん?",listOf("Вода","Еда / рис","Чай","Магазин"),1),
Lesson("Вкусно","おいしい","oishii","вкусно","Описываем еду",30,"Как сказать «вкусно»?",listOf("おいしい","たかい","おおきい","さむい"),0),
Lesson("Кафе","みずを ください","mizu o kudasai","воды, пожалуйста","Просим в кафе",40,"Что вы просите этой фразой?",listOf("Кофе","Воду","Счёт","Меню"),1),
Lesson("Просьба","おねがいします","onegaishimasu","пожалуйста / прошу","Вежливая просьба",35,"Какой смысл у おねがいします?",listOf("Пожалуйста / прошу","До завтра","Очень вкусно","Японский язык"),0),
Lesson("Город","えき","eki","станция","Ориентация в городе",30,"Что значит えき?",listOf("Станция","Улица","Отель","Ресторан"),0),
Lesson("Где?","どこですか","doko desu ka","где?","Местоположение",40,"Что значит どこですか?",listOf("Что это?","Кто это?","Где это?","Почему?"),2),
Lesson("Семья","かぞく","kazoku","семья","Говорим о семье",30,"Как по-японски «семья»?",listOf("ともだち","かぞく","せんせい","がくせい"),1),
Lesson("Друг","ともだち","tomodachi","друг","Люди вокруг нас",30,"Что значит ともだち?",listOf("Друг","Семья","Учитель","Студент"),0),
Lesson("Студент","がくせい","gakusei","студент","Кто ты?",30,"Кто такой がくせい?",listOf("Учитель","Врач","Студент","Друг"),2),
Lesson("Учитель","せんせい","sensei","учитель","Люди и профессии",30,"Что значит せんせい?",listOf("Учитель","Студент","Врач","Сотрудник"),0),
Lesson("Нравится","すきです","suki desu","нравится","Предпочтения",45,"Что значит すきです?",listOf("Не знаю","Нравится","Не нравится","Понимаю"),1),
Lesson("Частица は","わたしは がくせいです","watashi wa gakusei desu","я студент","Базовая грамматика",45,"Что делает は?",listOf("Обозначает тему","Обозначает время","Означает «нет»","Это глагол"),0),
Lesson("です・ます","です・ます","desu · masu","вежливые формы","Вежливая речь N5",50,"Какая форма вежливая?",listOf("だ・る","です・ます","だった・た","ない・ん"),1),
Lesson("Мини-диалог","すみません。えきは どこですか。","sumimasen. eki wa doko desu ka","Извините. Где станция?","Собираем всё вместе",55,"Как спросить, где станция?",listOf("えきは どこですか","えきは なんですか","みずを ください","おいしいです"),0),
Lesson("N5 Boss","わたしは にほんごを べんきょうします","watashi wa nihongo o benkyou shimasu","я учу японский","Финальная проверка N5",100,"Что значит にほんごを べんきょうします?",listOf("Я люблю Японию","Я учу японский","Я говорю по-английски","Я иду на станцию"),1)
).distinctBy { it.jp }

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
        var xp by remember { mutableIntStateOf(126) }
        var done by remember { mutableIntStateOf(2) }
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
        item { Text("Твоя миссия",fontSize=20.sp,fontWeight=FontWeight.Bold) }
        item { Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
            Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).background(Color(0xFFFFE5DF),CircleShape),contentAlignment=Alignment.Center){Text("日",color=Red,fontSize=24.sp)}
                    Spacer(Modifier.width(12.dp)); Column { Text("Следующая миссия",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Bold); Text(nextLesson.title,fontSize=20.sp,fontWeight=FontWeight.Bold) }
                }
                Text("4 этапа · японский + ромадзи + мини-квиз",color=Indigo)
                Button(onClick=onStart,Modifier.fillMaxWidth(),shape=RoundedCornerShape(15.dp)){Icon(Icons.Rounded.PlayArrow,null);Spacer(Modifier.width(6.dp));Text("Начать миссию")}
            }
        }}
        item { Text("Режимы обучения",fontSize=20.sp,fontWeight=FontWeight.Bold) }
        item { Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) { ModeCard("あ","Хирагана",Modifier.weight(1f)); ModeCard("文","Грамматика",Modifier.weight(1f)); ModeCard("会","Диалог",Modifier.weight(1f)) } }
        item { OutlinedButton(onClick=onPath,Modifier.fillMaxWidth(),shape=RoundedCornerShape(15.dp)){Icon(Icons.Rounded.Map,null);Spacer(Modifier.width(6.dp));Text("Открыть карту Японии")} }
        item { Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){Stat("🔥 7","дней",Modifier.weight(1f));Stat(done.toString(),"уроков",Modifier.weight(1f));Stat("12","слов",Modifier.weight(1f))} }
    }
}
@Composable
fun MentorCard(level:Int,done:Int,xp:Int) {
    val bob by rememberInfiniteTransition(label="mentor").animateFloat(.96f,1.04f,infiniteRepeatable(tween(1400),RepeatMode.Reverse),label="bob")
    val stage=when(level){1->"Гусёнок";2,3->"Юный Котоба";4,5->"Котоба-ниндзя";else->"Мастер Котоба"}
    Card(Modifier.fillMaxWidth(),RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
        Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Box(Modifier.size(142.dp).scale(bob).background(Brush.radialGradient(listOf(Color(0xFFFFF2D6),Color(0xFFE8E1D3))),CircleShape),contentAlignment=Alignment.Center) { KotobaMascot(level,Modifier.size(132.dp)) }
                Spacer(Modifier.width(15.dp))
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    Text("МР. КОТОБА",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Black)
                    Text(stage,fontSize=21.sp,fontWeight=FontWeight.ExtraBold)
                    Text("「一緒に行こう！」",fontSize=16.sp,fontWeight=FontWeight.Bold)
                    Text("Мистер Котоба растёт вместе с тобой.",color=Indigo,fontSize=12.sp)
                    Text("Уровень "+level+" · "+done+" миссий",color=Sage,fontSize=12.sp)
                }
            }
            if(level<6) {
                LinearProgressIndicator(progress={((xp%200)/200f)},Modifier.fillMaxWidth().height(7.dp),color=Gold,trackColor=Mist)
                Text("До следующей формы: "+((level+1)*100-xp)+" XP",color=Indigo,fontSize=11.sp)
            } else Text("Форма мастера открыта · дальше будут новые аксессуары.",color=Sage,fontSize=11.sp)
        }
    }
}

@Composable
fun KotobaMascot(level:Int,modifier:Modifier=Modifier) {
    Canvas(modifier) {
        val c=center
        val body=when { level>=6->Color(0xFF8E6CCB); level>=4->Color(0xFF5E8F72); level>=2->Color(0xFF79A96B); else->Color(0xFF9BCB72) }
        val face=if(level>=6) Color(0xFFFFD76B) else Color(0xFFFFD5C8)
        val segments=when { level>=6->5; level>=4->4; level>=2->3; else->2 }
        for(i in 0 until segments) drawCircle(body,34f-i*1.5f,c.copy(x=c.x-32f+i*18f,y=c.y+25f))
        drawCircle(face,38f,c.copy(x=c.x+28f,y=c.y-5f))
        drawCircle(Color.White,8f,c.copy(x=c.x+15f,y=c.y-10f)); drawCircle(Color.White,8f,c.copy(x=c.x+42f,y=c.y-10f))
        drawCircle(Ink,3f,c.copy(x=c.x+17f,y=c.y-10f)); drawCircle(Ink,3f,c.copy(x=c.x+40f,y=c.y-10f))
        drawArc(Red,0f,180f,false,c.copy(x=c.x+14f,y=c.y+5f),style=Stroke(width=4f))
        drawLine(Sage,c.copy(x=c.x+9f,y=c.y-34f),c.copy(x=c.x,y=c.y-49f),strokeWidth=4f)
        drawLine(Sage,c.copy(x=c.x+45f,y=c.y-34f),c.copy(x=c.x+54f,y=c.y-49f),strokeWidth=4f)
        drawCircle(Gold,4f,c.copy(x=c.x,y=c.y-49f)); drawCircle(Red,4f,c.copy(x=c.x+54f,y=c.y-49f))
        if(level>=4) {
            drawCircle(Color.Transparent,42f,c.copy(x=c.x+28f,y=c.y-5f),style=Stroke(width=3f))
            drawLine(Gold,c.copy(x=c.x-8f,y=c.y+18f),c.copy(x=c.x-40f,y=c.y+4f),strokeWidth=8f)
            drawLine(Gold,c.copy(x=c.x-40f,y=c.y+4f),c.copy(x=c.x-22f,y=c.y-10f),strokeWidth=8f)
        }
        if(level>=6) { drawLine(Red,c.copy(x=c.x+14f,y=c.y+14f),c.copy(x=c.x+42f,y=c.y+14f),strokeWidth=4f) }
    }
}
@Composable
fun ModeCard(icon:String,title:String,m:Modifier) {
    Card(modifier=m,shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
        Column(Modifier.fillMaxWidth().padding(13.dp),horizontalAlignment=Alignment.CenterHorizontally) { Text(icon,fontSize=27.sp,color=Red,fontWeight=FontWeight.Bold); Text(title,fontSize=11.sp,fontWeight=FontWeight.Bold) }
    }
}

@Composable
fun Stat(a:String,b:String,m:Modifier){Card(modifier=m,shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.fillMaxWidth().padding(14.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(a,fontSize=22.sp,fontWeight=FontWeight.Bold);Text(b,fontSize=11.sp,color=Indigo)}}}

@Composable
fun PathScreen(modifier:Modifier,done:Int,onLesson:(Int)->Unit){
    LazyColumn(modifier.fillMaxSize(),contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        item{
            Text("Путь N5",fontSize=30.sp,fontWeight=FontWeight.Black)
            Text("日本の旅 · путешествие по Японии",color=Indigo)
            Spacer(Modifier.height(8.dp))
            Card(shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFFEDE7D8))){
                Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
                    Text("🗾  ТВОЯ КАРТА",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Black)
                    Text("Токио → Киото → Осака → Хоккайдо",fontSize=17.sp,fontWeight=FontWeight.Bold)
                    Text("Открывай города по мере прохождения глав.",color=Indigo,fontSize=12.sp)
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){MapDot("東京",done>=5);MapDot("京都",done>=12);MapDot("大阪",done>=20);MapDot("北海道",done>=29)}
                }
            }
        }
        itemsIndexed(lessons){i,l->
            val unlocked=i<=done; val finished=i<done
            Card(onClick={if(unlocked)onLesson(i)},enabled=unlocked,shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=if(finished)Color.White else if(unlocked)Color(0xFFFFF7F1) else Mist)){
                Row(Modifier.fillMaxWidth().padding(15.dp),verticalAlignment=Alignment.CenterVertically){
                    Box(Modifier.size(50.dp).background(if(finished)Sage else if(unlocked)Red else Color.Gray,CircleShape),contentAlignment=Alignment.Center){
                        if(finished)Icon(Icons.Rounded.CheckCircle,null,tint=Color.White) else if(!unlocked)Icon(Icons.Rounded.Lock,null,tint=Color.White) else Text((i+1).toString(),color=Color.White,fontWeight=FontWeight.Bold)
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)){Text(l.title,fontWeight=FontWeight.Bold,fontSize=16.sp);Text(l.jp,color=Indigo);Text(l.xp.toString()+" XP · "+l.subtitle,color=Color.Gray,fontSize=11.sp)}
                    if(i==29)Text("👑",fontSize=22.sp)
                }
            }
        }
    }
}
@Composable
fun MapDot(name:String,open:Boolean){Column(horizontalAlignment=Alignment.CenterHorizontally){Box(Modifier.size(34.dp).background(if(open)Red else Color.Gray,CircleShape),contentAlignment=Alignment.Center){Text("•",color=Color.White,fontSize=24.sp)};Text(name,fontSize=10.sp,fontWeight=FontWeight.Bold)}}

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
                    Column { Text(if(katakanaMode)"Катакана":"Хирагана",color=Gold,fontSize=13.sp,fontWeight=FontWeight.Bold); Text("Освоено "+learned+" / "+kana.size,color=Color.White,fontSize=19.sp,fontWeight=FontWeight.Bold); Text("Нажимай на символ, чтобы отметить его.",color=Color.White.copy(alpha=.72f),fontSize=11.sp) }
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
        Card(shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.fillMaxWidth().padding(22.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("称号",color=Gold,fontSize=12.sp,fontWeight=FontWeight.Bold);Text("Начинающий самурай",fontSize=24.sp,fontWeight=FontWeight.Bold);Text("Уровень "+(xp/100+1)+" · "+xp+" XP · "+done+" уроков",color=Indigo)}}
        Text("Достижения",fontSize=20.sp,fontWeight=FontWeight.Bold);Achievement("Первый шаг","Заверши первый урок",done>=1);Achievement("Серия 7","Занимайся семь дней",true);Achievement("Котоба","Собери 100 слов",false)
    }
}

@Composable
fun Achievement(t:String,s:String,on:Boolean){Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=if(on)Color.White else Mist)){Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(if(on)Icons.Rounded.EmojiEvents else Icons.Rounded.Lock,null,tint=if(on)Gold else Color.Gray,modifier=Modifier.size(30.dp));Spacer(Modifier.width(14.dp));Column{Text(t,fontWeight=FontWeight.Bold);Text(s,color=Indigo,fontSize=12.sp)}}}}

@Composable
fun LessonScreen(modifier:Modifier,lesson:Lesson,step:Int,onNext:()->Unit,onBack:()->Unit) {
    var selected by remember(lesson,step){mutableIntStateOf(-1)}
    val answered=selected>=0
    val correct=selected==lesson.answer
    LaunchedEffect(selected, step) {
        if (selected >= 0 && correct) {
            delay(700)
            onNext()
        }
    }
    val pulse by rememberInfiniteTransition(label="pulse").animateFloat(0.97f,1.03f,infiniteRepeatable(tween(1200),RepeatMode.Reverse),label="pulse")
    Column(modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            TextButton(onClick=onBack){Text("← Назад")}
            Spacer(Modifier.weight(1f))
            Text(lesson.xp.toString()+" XP",color=Gold,fontWeight=FontWeight.Bold)
        }
        Text(lesson.title,fontSize=30.sp,fontWeight=FontWeight.Bold)
        Text(when(step){0->"Запомни";1->"Пойми смысл";2->"Выбери ответ";else->"Финальная проверка"},color=Indigo)
        LinearProgressIndicator(progress={(step+1)/4f},Modifier.fillMaxWidth().height(8.dp),color=Red,trackColor=Mist)
        Spacer(Modifier.height(4.dp))
        AnimatedContent(targetState=step,transitionSpec={ (fadeIn(tween(260))+slideInHorizontally{it/4}) togetherWith fadeOut(tween(160)) },label="lessonStep") { current ->
            when(current) {
                0 -> {
                    Card(Modifier.fillMaxWidth().scale(pulse),shape=RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
                        Column(Modifier.padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(15.dp)) {
                            Box(Modifier.size(86.dp).background(Red.copy(alpha=.1f),CircleShape),contentAlignment=Alignment.Center){Text("日",fontSize=36.sp,color=Red,fontWeight=FontWeight.Bold)}
                            Text(lesson.jp,fontSize=34.sp,fontWeight=FontWeight.Black)
                            Text(lesson.reading,color=Gold,fontSize=17.sp,fontWeight=FontWeight.Bold)
                            Text(lesson.meaning,fontSize=19.sp,color=Indigo)
                            Text(lesson.subtitle,color=Color.Gray)
                        }
                    }
                    Button(onClick=onNext,Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(16.dp)){Text("Запомнил →",fontSize=16.sp)}
                }
                1 -> {
                    Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
                        Column(Modifier.padding(26.dp),verticalArrangement=Arrangement.spacedBy(15.dp)) {
                            Text("Мини-подсказка",color=Gold,fontWeight=FontWeight.Bold)
                            Text(lesson.jp,fontSize=30.sp,fontWeight=FontWeight.Bold)
                            Text("Читается: "+lesson.reading)
                            Text("Перевод: "+lesson.meaning,color=Indigo)
                            Text("Прочитай вслух 2 раза. Затем проверь себя.",color=Sage,fontWeight=FontWeight.SemiBold)
                        }
                    }
                    Button(onClick=onNext,Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(16.dp)){Text("Понял →",fontSize=16.sp)}
                }
                else -> {
                    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                        Text(lesson.question,fontSize=19.sp,fontWeight=FontWeight.Bold)
                        lesson.choices.forEachIndexed { index,choice ->
                            val bg=when { !answered -> Color.White; index==lesson.answer -> Color(0xFFE3EEDC); index==selected -> Color(0xFFF5D9D5); else -> Color.White }
                            Card(modifier=Modifier.fillMaxWidth().clickable(enabled=!answered){selected=index},shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=bg)) {
                                Row(Modifier.fillMaxWidth().padding(17.dp),verticalAlignment=Alignment.CenterVertically) {
                                    Box(Modifier.size(36.dp).background(Mist,CircleShape),contentAlignment=Alignment.Center){Text(('A'.code+index).toChar().toString(),fontWeight=FontWeight.Bold)}
                                    Spacer(Modifier.width(12.dp)); Text(choice,fontSize=16.sp); Spacer(Modifier.weight(1f))
                                    if(answered && index==lesson.answer) Icon(Icons.Rounded.CheckCircle,null,tint=Sage)
                                    if(answered && index==selected && !correct) Icon(Icons.Rounded.Close,null,tint=Red)
                                }
                            }
                        }
                        AnimatedVisibility(visible=answered,enter=fadeIn()+expandVertically()) { Text(if(correct) if(step==3) "Миссия завершена! 正解 🎉" else "Отлично! 正解 🎉" else "Почти. Правильный ответ подсвечен.",color=if(correct)Sage else Red,fontWeight=FontWeight.Bold) }
                        Button(onClick={if(answered) onNext() else selected=lesson.answer},Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(16.dp)) { Text(if(answered)if(step==3)"Завершить урок · +"+lesson.xp+" XP ✨" else "Продолжить →" else "Проверить") }
                    }
                }
            }
        }
    }
}
