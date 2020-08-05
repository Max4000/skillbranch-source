package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_root.*
import ru.skillbranch.skillarticles.R

class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        //setupToolBar()
    }
//    private fun setupToolBar(){
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val logo : ImageView? = if(toolbar.childCount > 2) toolbar.getChildAt(2) as ImageView else null
//        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
//        val lp : Toolbar.LayoutParams = logo?.layoutParams as Toolbar.LayoutParams
//
//    }
}