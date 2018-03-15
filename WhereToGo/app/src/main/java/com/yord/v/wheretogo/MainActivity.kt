package com.yord.v.wheretogo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val foodList = arrayListOf("Glory", "Happy", "Mr.Pizza", "Chinese", "Witch")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        where_btn.setOnClickListener {
            val random = Random()
            val randomPlace = random.nextInt(foodList.count())
            place_txt.text = foodList[randomPlace]
        }

        add_place_btn.setOnClickListener {
            val newPlace = add_place_txt.text.toString()
            if (!newPlace.isEmpty()){
                if (!foodList.contains(newPlace)) {
                    foodList.add(add_place_txt.text.toString())
                    Toast.makeText(this, newPlace + ", added!", LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, newPlace + ", is already in the list. Add new one!", LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Insert a new Place, please!", LENGTH_SHORT).show()
            }
            add_place_txt.text.clear()
        }
    }
}
