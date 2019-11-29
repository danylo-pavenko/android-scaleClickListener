package com.dansdev.scaleclick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.dansdev.scaleclicklistener.OnScaleClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnClick.setOnTouchListener(object: OnScaleClickListener() {
            override fun onClick(view: View?) {
                Toast.makeText(this@MainActivity, "Are you clicked Button", Toast.LENGTH_SHORT).show()
            }
        })

        ivClick.setOnTouchListener(object: OnScaleClickListener() {
            override fun onClick(view: View?) {
                Toast.makeText(this@MainActivity, "Are you clicked on ImageView", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
