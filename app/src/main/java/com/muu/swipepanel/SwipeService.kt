package com.muu.swipepanel

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Color
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.content.Context
import android.view.*
import android.widget.*
import kotlin.math.min
import kotlin.math.roundToInt

class SwipeService : AccessibilityService() {
 companion object { var instance: SwipeService? = null; private set }
 private var panel: LinearLayout? = null
 private var wm: WindowManager? = null
 private var params: WindowManager.LayoutParams? = null
 override fun onServiceConnected() { super.onServiceConnected(); instance=this }
 override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {}
 override fun onInterrupt() {}
 override fun onDestroy() { hidePanel(); instance=null; super.onDestroy() }
 private fun dp(v:Int):Int=(v*resources.displayMetrics.density).roundToInt()
 private fun bg(color:Int, radius:Float=12f)=GradientDrawable().apply { setColor(color); cornerRadius=dp(radius.toInt()).toFloat() }
 fun showPanel() {
  if(panel!=null) return
  wm=getSystemService(Context.WINDOW_SERVICE) as WindowManager
  val box=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(7),dp(7),dp(7),dp(7)); background=bg(Color.argb(185,9,21,42)) }
  val header=TextView(this).apply { text="⠿  SWIPE  ·  移動"; textSize=12f; setTextColor(Color.WHITE); gravity=Gravity.CENTER; setPadding(4,dp(6),4,dp(8)) }
  box.addView(header,LinearLayout.LayoutParams(-1,dp(34)))
  val symbols=arrayOf(arrayOf("↖","↑","↗"),arrayOf("←","×","→"),arrayOf("↙","↓","↘"))
  val dirs=arrayOf(arrayOf(-1 to -1,0 to -1,1 to -1),arrayOf(-1 to 0,0 to 0,1 to 0),arrayOf(-1 to 1,0 to 1,1 to 1))
  for(y in 0..2) {
   val row=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
   for(x in 0..2) {
    val b=TextView(this).apply { text=symbols[y][x]; textSize=24f; setTextColor(Color.WHITE); gravity=Gravity.CENTER; background=bg(if(x==1&&y==1) Color.rgb(40,65,90) else Color.argb(225,18,83,151)); setOnClickListener { if(x==1&&y==1) hidePanel() else swipe(dirs[y][x].first,dirs[y][x].second) } }
    row.addView(b,LinearLayout.LayoutParams(dp(49),dp(49)).apply { setMargins(dp(2),dp(2),dp(2),dp(2)) })
   }
   box.addView(row)
  }
  val p=WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,PixelFormat.TRANSLUCENT).apply { gravity=Gravity.TOP or Gravity.START; x=dp(12); y=dp(180) }
  var startX=0; var startY=0; var touchX=0f; var touchY=0f
  header.setOnTouchListener { _,event -> when(event.actionMasked) {
   MotionEvent.ACTION_DOWN -> { startX=p.x; startY=p.y; touchX=event.rawX; touchY=event.rawY; true }
   MotionEvent.ACTION_MOVE -> { p.x=startX+(event.rawX-touchX).roundToInt(); p.y=startY+(event.rawY-touchY).roundToInt(); wm?.updateViewLayout(box,p); true }
   else -> true
  } }
  try { wm?.addView(box,p); panel=box; params=p } catch(e:Exception) { Toast.makeText(this,"パネル表示に失敗: ${e.message}",Toast.LENGTH_LONG).show() }
 }
 fun hidePanel() { panel?.let { try { wm?.removeView(it) } catch(_:Exception) {} }; panel=null; params=null }
 private fun swipe(dx:Int,dy:Int) {
  val screen=Point(); @Suppress("DEPRECATION") (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealSize(screen)
  val cx=screen.x/2f; val cy=screen.y/2f
  val prefs=getSharedPreferences("settings",Context.MODE_PRIVATE)
  val distance=min(screen.x,screen.y)*prefs.getInt("distance",30)/100f
  val endX=(cx+dx*distance).coerceIn(1f,screen.x-1f)
  val endY=(cy+dy*distance).coerceIn(1f,screen.y-1f)
  val path=Path().apply { moveTo(cx,cy); lineTo(endX,endY) }
  val stroke=GestureDescription.StrokeDescription(path,0,prefs.getInt("duration",250).toLong())
  val gesture=GestureDescription.Builder().addStroke(stroke).build()
  if(!dispatchGesture(gesture,null,null)) Toast.makeText(this,"スワイプを実行できませんでした",Toast.LENGTH_SHORT).show()
 }
}
