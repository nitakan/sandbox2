package jp.cookbiz.mkato.sandbox

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import jp.cookbiz.mkato.sandbox.choosemedia.ChooseMediaActivity
import jp.cookbiz.mkato.sandbox.databinding.ActivityMainBinding
import jp.cookbiz.mkato.sandbox.directories.MediaDirectoriesActivity
import jp.cookbiz.mkato.sandbox.media.MediaListActivity

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
