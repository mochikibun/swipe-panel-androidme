package com.muu.swipepanel

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
 private val blue = Color.rgb(46, 147, 255)
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); draw() }
 override fun onResume() { super.onResume(); draw() }
 private fun draw() {
  val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32,48,32,24); setBackgroundColor(Color.rgb(11,18,31)) }
  fun title(s:String, size:Float) = TextView(this).apply { text=s; textSize=size; setTextColor(Color.WHITE); setPadding(0,8,0,16) }
  fun button(s:String, action:()->Unit) { root.addView(Button(this).apply { text=s; setTextColor(Color.WHITE); setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue)); setOnClickListener { action() } }, LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=12 }) }
  root.addView(title("SWIPE PANEL",26f)); root.addView(title("画面中央から8方向にスワイプします。\nボタンパネルはドラッグで移動できます。",15f))
  button("① 他のアプリの上に表示を許可") { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) }
  button("② ユーザー補助サービスを有効化") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
  val prefs=getSharedPreferences("settings", MODE_PRIVATE)
  root.addView(title("スワイプ距離（画面短辺に対する割合）",16f))
  val distance=SeekBar(this).apply { max=60; progress=prefs.getInt("distance",30)-10; setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s:SeekBar?,p:Int,fromUser:Boolean) { if(fromUser) prefs.edit().putInt("distance",p+10).apply() }; override fun onStartTrackingTouch(s:SeekBar?){}; override fun onStopTrackingTouch(s:SeekBar?){} }) }
  root.addView(distance)
  root.addView(title("スワイプ時間（ミリ秒）",16f))
  val duration=SeekBar(this).apply { max=950; progress=prefs.getInt("duration",250)-50; setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s:SeekBar?,p:Int,fromUser:Boolean) { if(fromUser) prefs.edit().putInt("duration",p+50).apply() }; override fun onStartTrackingTouch(s:SeekBar?){}; override fun onStopTrackingTouch(s:SeekBar?){} }) }
  root.addView(duration)
  button("③ パネルを表示") { if(!Settings.canDrawOverlays(this)) { Toast.makeText(this,"先に重ねて表示を許可してください",Toast.LENGTH_LONG).show() } else if(SwipeService.instance==null) { Toast.makeText(this,"ユーザー補助サービスを有効にしてください",Toast.LENGTH_LONG).show() } else SwipeService.instance?.showPanel() }
  button("パネルを非表示") { SwipeService.instance?.hidePanel() }
  root.addView(title("操作にはユーザー補助サービスを使用します。画面の内容は読み取らず、外部送信もしません。アプリによってはジェスチャーが動作しない場合があります。",13f))
  setContentView(ScrollView(this).apply { addView(root) })
 }
}
