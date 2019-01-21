package jp.cookbiz.mkato.sandbox.directories

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.cookbiz.mkato.sandbox.R
import jp.cookbiz.mkato.sandbox.databinding.ActivityMediaDirectoriesBinding
import jp.cookbiz.mkato.sandbox.databinding.DirectoryListItemBinding
import jp.cookbiz.mkato.sandbox.model.MediaStorageUseCase

class MediaDirectoriesActivity : AppCompatActivity() {

    companion object {
        fun startActivity(activity: Activity) {
            activity.startActivity(Intent(activity, MediaDirectoriesActivity::class.java))
        }
    }

    val viewModel: MediaDirectoriesViewModel by lazy {
        ViewModelProviders.of(this, MediaDirectoriesViewModel.MyViewModelProvider(this.application))
            .get(MediaDirectoriesViewModel::class.java)
    }

    lateinit var binding: ActivityMediaDirectoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_directories)
        binding.setLifecycleOwner(this)

        binding.listView.layoutManager = LinearLayoutManager(this)
        binding.listView.adapter = ListAdapter(this, viewModel.getDirectoryList())


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        } else {
            request()
        }
    }

    private fun request() {
        viewModel.getMediaStorageDirectoryUri(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty()) {
                    request()
                } else {
                    //
                }
            }
        }
    }

    class ListAdapter(val activity: AppCompatActivity, val data: LiveData<List<MediaStorageUseCase.MediaDirectory>>)
        :RecyclerView.Adapter<ListAdapter.ViewHolder>() {

        private val list = arrayListOf<MediaStorageUseCase.MediaDirectory>()

        init {
            data.observe(activity, Observer {
                list.clear()
                list.addAll(it)
                this.notifyDataSetChanged()
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = DirectoryListItemBinding.inflate(activity.layoutInflater)
            binding.setLifecycleOwner(activity)
            return ViewHolder(binding)
        }

        override fun getItemCount() = list.size


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.binding.item = item
        }

        class ViewHolder(val binding: DirectoryListItemBinding) : RecyclerView.ViewHolder(binding.root)
    }



}
