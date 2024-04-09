package com.example.xxmaterialdesign.nestedscroll.nestedscrolling

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParent
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.forEachIndexed

/**
 *  author : baize
 *  date : 2024/4/9 15:24
 *  description : 自定义滚动的NestedScrollParent
 */
class MyNestedScrollParentView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent {
  companion object{
    const val TAG = "baize_nested_parent"
  }

  private var realHeight: Int = 0
  private var mParentHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)

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

  //=========================NestedScrolling实现方法=========================
  override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
    Log.i(TAG, "1.onStartNestedScroll child:$child target:${target} nestedScrollAxes:$nestedScrollAxes")
    return true
  }

  /**
   * parentHelper中会把axes轴记录下来，用于后续逻辑 TODO：什么逻辑
   */
  override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
    Log.i(TAG, "2.onNestedScrollAccepted child:$child target:${target} axes:$axes")
    mParentHelper.onNestedScrollAccepted(child, target, axes)
  }

  override fun onStopNestedScroll(child: View) {
    super.onStopNestedScroll(child)
  }

  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int
  ) {
    super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
  }

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
    super.onNestedPreScroll(target, dx, dy, consumed)
  }

  override fun onNestedFling(
    target: View,
    velocityX: Float,
    velocityY: Float,
    consumed: Boolean
  ): Boolean {
    return super.onNestedFling(target, velocityX, velocityY, consumed)
  }

  override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
    return super.onNestedPreFling(target, velocityX, velocityY)
  }

  override fun getNestedScrollAxes(): Int {
    return super.getNestedScrollAxes()
  }
}


