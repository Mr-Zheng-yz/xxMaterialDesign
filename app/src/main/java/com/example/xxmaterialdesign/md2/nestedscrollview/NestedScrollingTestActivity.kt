package com.example.xxmaterialdesign.md2.nestedscrollview

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.xxmaterialdesign.databinding.ActivityNestedScrollingTestBinding

/**
 *  author : baize
 *  date : 2024/4/9 15:20
 *  description :
 */
class NestedScrollingTestActivity: AppCompatActivity() {

  private lateinit var vb: ActivityNestedScrollingTestBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    vb = ActivityNestedScrollingTestBinding.inflate(LayoutInflater.from(this))
    setContentView(vb.root)
  }

}