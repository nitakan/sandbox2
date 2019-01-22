package com.github.nitakan.sandbox

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.nitakan.sandbox.choosemedia.ChooseMediaActivity
import com.github.nitakan.sandbox.databinding.ActivityMainBinding
import com.github.nitakan.sandbox.directories.MediaDirectoriesActivity
import com.github.nitakan.sandbox.media.MediaListActivity

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.listenerToMediaList = View.OnClickListener {
            MediaListActivity.startActivity(this)
        }

        binding.listenerToDirectoriesList = View.OnClickListener {
            MediaDirectoriesActivity.startActivity(this)
        }

        binding.listenerToChooseMedia = View.OnClickListener {
            ChooseMediaActivity.startActivity(this)
        }


    }
}
