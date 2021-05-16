package com.kalus.mytablayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val list = arrayListOf(
            "Apple",
            "Banana",
            "PipeApple",
            "Dog",
            "Cat",
            "Monkey",
            "KiwiFruits",
            "Peach",
            "Cherry",
            "WaterMelon",
            "pear",
            "Orange",
            "Tangerine",
            "Pig",
            "Fox",
            "Kangroo",
            "DigDigDig"
        )

        val list1 = arrayListOf(
            "Apple",
           /* "Banana",*/
            "PipeApple")


        list.forEach {
            tab1.addTab(tab1.newTab().setText(it))
//            tab2.addTab(tab2.newTab().setText(it))
        }
        list1.forEach {
            tab2.addTab(tab2.newTab().setText(it))
        }



    }
}