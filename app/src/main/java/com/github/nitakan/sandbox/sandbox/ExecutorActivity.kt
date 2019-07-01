package com.github.nitakan.sandbox.sandbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.github.nitakan.sandbox.R
import com.github.nitakan.sandbox.databinding.ActivityExecutorBinding

class ExecutorActivity : AppCompatActivity() {

    lateinit var binding: ActivityExecutorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_executor)
        binding.sealedError.setOnClickListener {

        }
    }
}
