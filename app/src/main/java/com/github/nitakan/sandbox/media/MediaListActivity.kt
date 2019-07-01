package com.github.nitakan.sandbox.media

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.nitakan.sandbox.R
import com.github.nitakan.sandbox.databinding.ActivityMediaListBinding

class MediaListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaListBinding

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MediaListViewModel::class.java)
    }

    companion object {
        fun startActivity(activity: Activity) {
            activity.startActivity(Intent(activity, MediaListActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_list)
        setSupportActionBar(binding.toolBar.toolBar)


        //binding.toolBar.toolbarTitle.text = "投稿"
        binding.setLifecycleOwner(this)

        ToolbarDropdownSpinnerAdapter(this, viewModel.requestDir2(this)).also {adapter ->
            binding.toolBar.toolbarSpinner.adapter = adapter
            binding.toolBar.toolbarSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {  }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val item = adapter.getItem(position)
                    viewModel.requestFileInDir2(this@MediaListActivity, item)
                }
            }
        }

        binding.mediaList.also {view ->
            view.adapter = MediaListAdapter(this, viewModel, viewModel.getFileList())
            view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }
}
