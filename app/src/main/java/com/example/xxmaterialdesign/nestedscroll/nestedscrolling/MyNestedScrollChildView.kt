package com.example.xxmaterialdesign.nestedscroll.nestedscrolling

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import androidx.core.view.forEachIndexed
import kotlin.math.roundToInt

/**
 *  author : baize
 *  date : 2024/4/9 15:24
 *  description : 自定义滚动的NestedScrollChild
 */
class MyNestedScrollChildView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingChild {
  companion object{
    const val TAG = "baize_nested_child"
  }

  private var mLastMotionY = 0
  private var realHeight = 0

  private var mChildHelper: NestedScrollingChildHelper = NestedScrollingChildHelper(this)

  init {
    //u1.启用嵌套滑动
    setNestedScrollingEnabled(true)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    realHeight = 0
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val unSpecifiedSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED)
    forEachIndexed { index, view ->
      measureChild(view, widthMeasureSpec, unSpecifiedSpec)
      realHeight += view.measuredHeight
    }
    Log.d(TAG, "onMeasure realHeight:$realHeight")
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
  }

  /**
   * 需要处理的是滑动距离，所以getY()和getRawY()计算出来的值没有区别。
   */
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val consumed = IntArray(2) //[0]:x,[1]:y 记录消费距离
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        mLastMotionY = event.getY().toInt()
        Log.d(TAG, "down y:${event.getY().roundToInt()}")
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
      }
      MotionEvent.ACTION_MOVE -> {
        val y: Int = event.getY().toInt()
        val deltaY: Int = mLastMotionY - y
        Log.d(TAG, "move deltaY:$deltaY")
        scrollBy(0, deltaY)
        mLastMotionY = y
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        Log.d(TAG, "up or cancel")
        stopNestedScroll()
      }
    }
    return true
  }

  override fun scrollTo(x: Int, y: Int) {
    val sY = y.coerceAtLeast(0).coerceAtMost(realHeight - height)
    Log.d(TAG, "scrollTo y:$y scrollY:$scrollY realHeight：$realHeight 修正后sY:$sY")
    if (sY != scrollY) {
      super.scrollTo(x, sY)
    }
  }


  //=========================NestedScrolling实现方法=========================
  /**
   * 启用或禁用嵌套滚动的方法，设置为true，并且当前界面的View的层次结构是支持嵌套滚动的（也就是需要NestedScrollingParent嵌套NestedScrollingChild）才会触发嵌套滚动。
   * 一般直接代理给NestedScrollingChildHelper的同名方法。
   */
  override fun setNestedScrollingEnabled(enabled: Boolean) {
    mChildHelper.setNestedScrollingEnabled(enabled)
    Log.i(TAG, "1. setNestedScrollingEnabled:$enabled")
  }

  /**
   * 判断当前View是否支持嵌套滑动。
   * 一般直接代理给NestedScrollingChildHelper的同名方法。
   */
  override fun isNestedScrollingEnabled(): Boolean {
    Log.i(TAG, "1. isNestedScrollingEnabled:${mChildHelper.isNestedScrollingEnabled()}")
    return mChildHelper.isNestedScrollingEnabled()
  }

  /**
   * 表示View开始滚动了，一般在ACTION_DOWN中主动调用，如果返回true则表示有父布局支持嵌套滑动。
   * 一般直接代理给NestedScrollingChildHelper的同名方法。
   * 此时如果有Parent，则会触发Parent的onStartNestedScroll()方法和onNestedScrollAccepted()方法。
   * childHelper内部会循环遍历父布局，如果找到NestedScrollingParent，并且onStartNestedScroll()返回true，则将此Parent记录下来，并触发Parent的onNestedScrollAccepted方法。
   * @param axes 表示滑动轴（方向），有三个值可选：SCROLL_AXIS_NONE：初始状态, SCROLL_AXIS_HORIZONTAL：横向, SCROLL_AXIS_VERTICAL：纵向
   */
  override fun startNestedScroll(axes: Int): Boolean {
    Log.i(TAG, "2. startNestedScroll axes:$axes")
    return mChildHelper.startNestedScroll(axes)
  }

  /**
   * 一般是在事件结束比如ACTION_UP和ACTION_CANCEL中调用，告诉父布局滚动结束。
   * 一般直接代理给NestedScrollingChildHelper的同名方法。
   * childHelper中会将记录的Parent清空，并调用Parent的onStopNestedScroll()告知停止滚动，做一些释放操作。
   */
  override fun stopNestedScroll() {
    Log.i(TAG, "5. stopNestedScroll")
    mChildHelper.stopNestedScroll()
  }

  /**
   * 判断当前View是否有嵌套滑动的Parent。
   * 一般直接代理给NestedScrollingChildHelper的同名方法。
   * 返回是否记录了配合滑动的Parent。
   */
  override fun hasNestedScrollingParent(): Boolean {
    Log.i(TAG, "1. hasNestedScrollingParent ${mChildHelper.hasNestedScrollingParent()}")
    return mChildHelper.hasNestedScrollingParent()
  }


  /**
   * 在当前View消费滚动距离之前把滑动距离传给父布局，相当于把优先处理权交给Parent
   * 一般直接代理给NestedScrollingChildHelper的同名方法。
   * @param dx: 当前水平方向滑动距离
   * @param dy: 当前垂直方向滑动距离
   * @param consumed 输出参数，会将Parent消费掉的距离封装进该参数 consumed[0]表示水平方向的消费，consumed[1]表示垂直方向的消费
   * @return true:代表Parent是否消费了滚动距离
   * childHelper内部会调用Parent的onNestedPreScroll，此时Parent决定是否消费滑动距离，并将消费的距离赋值给consumed数组
   */
  override fun dispatchNestedPreScroll(
    dx: Int,
    dy: Int,
    consumed: IntArray?,
    offsetInWindow: IntArray?
  ): Boolean {
    Log.i(TAG, "3.dispatchNestedPreScroll: dx$dx,dy:$dy,consumed:$consumed,offsetInWindow:$offsetInWindow")
    return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
  }

  /**
   * 在当前View消费滚动距离之后，通过调用该方法，把剩下的滚动距离传给父布局，如果没有嵌套滑动的Parent或不支持嵌套滚动，调用该方法也没啥用。
   * 一般直接代理给NestedScrollingChildHelper的同名方法。
   * @param dxConsumed 被当前View消费了的水平方向滑动距离
   * @param dyConsumed 被当前View消费了的垂直方向滑动距离
   * @param dxUnconsumed 未被消费的水平滑动距离
   * @param dyUnconsumed 未被消费的垂直滑动距离
   * @param offsetInWindow 输出可选参数。如果不是null，该方法完成返回时，会将视图从该操作完成之后的本地视图坐标中的偏移量封装进该参数中，offsetInWindow[0]水平方向，offsetInWindow[1]垂直方向
   * @return true:表示滑动事件分发成功 false:分发失败
   */
  override fun dispatchNestedScroll(
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    offsetInWindow: IntArray?
  ): Boolean {
    Log.i(TAG, "4.dispatchNestedScroll: dxConsumed:$dxConsumed,dyConsumed:$dyConsumed,dxUnconsumed:$dxUnconsumed,dyUnconsumed:$dyUnconsumed,offsetInWindow:$offsetInWindow")
    return mChildHelper.dispatchNestedScroll(
      dxConsumed,
      dyConsumed,
      dxUnconsumed,
      dyUnconsumed,
      offsetInWindow
    )
  }


  /**
   * 再当前View处理惯性滑动前，先将滑动事件分发给Parent。
   * 一般直接代理给NestedScrollingChildHelper的同名方法。
   * 如果想自己处理惯性滑动事件，就不应该调用该方法给Parent处理；如果给Parent并且返回了true，表示Parent已经处理了，自己就不应该再做处理。
   * childHelper内部会调用Parent的onNestedPreFling()方法，处理惯性滑动
   *  @param velocityX: 水平滑动速度
   *  @param velocityY: 垂直滑动速度
   *  @return true:Parent处理了滑动事件 false:Parent未处理，但不代表Parent后面就不处理了
   */
  override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
    Log.i(TAG, "6. dispatchNestedPreFling velocityX:$velocityX velocityY:$velocityY")
    return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
  }

  /**
   * 将惯性滑动的速度分发给Parent。
   * 一般直接代理给NestedScrollingChildHelper的同名方法。
   * childHelper内部会调到Parent的onNestedFling()方法，处理惯性滑动
   * @param velocityX: 水平滑动速度
   * @param velocityY: 垂直滑动速度
   * @param consumed 表示当前View消费了滑动事件，否则传入false
   * @return true:Parent处理了滑动事件 false:Parent未处理
   */
  override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
    Log.i(TAG, "7. dispatchNestedFling velocityX:$velocityX velocityY:$velocityY consumed:$consumed")
    return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
  }

}


