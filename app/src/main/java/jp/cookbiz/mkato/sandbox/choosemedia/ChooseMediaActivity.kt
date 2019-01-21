package jp.cookbiz.mkato.sandbox.choosemedia

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import jp.cookbiz.mkato.sandbox.R
import jp.cookbiz.mkato.sandbox.databinding.ActivityChooseMediaBinding

class ChooseMediaActivity : AppCompatActivity() {

    companion object {
        fun startActivity(activity: Activity) {
            activity.startActivity(Intent(activity, ChooseMediaActivity::class.java))
        }
    }

    lateinit var binding: ActivityChooseMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_media)
        binding.button.setOnClickListener {
            val intent = Intent().also {intent ->
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(Intent.createChooser(intent, "Choose images"), 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> {
                if (resultCode != Activity.RESULT_OK) {
                    return
                }

                if (data?.clipData == null) {

                    val uri = data?.data?: return
                    val cursor = contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
                    val path = cursor?.let {
                        it.moveToFirst()
                        val path = it.getString(0)
                        it.close()
                        return@let path
                    }?: return

                    binding.text.text = path

                } else {
                    val clipData = data.clipData?: return
                    val itemCount = clipData.itemCount
                    val uriList = arrayListOf<Uri>()
                    for (i in 0 until itemCount) {
                        val uri = clipData.getItemAt(i)?.uri
                        uri?.let {
                            uriList.add(it)
                        }
                    }

                    val str = uriList.joinToString(separator = "\n") { it.path ?: "" }
                    binding.text.text = str
                }
            }
        }
    }
}
