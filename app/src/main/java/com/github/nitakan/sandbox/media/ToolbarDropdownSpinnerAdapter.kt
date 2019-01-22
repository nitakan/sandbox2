package com.github.nitakan.sandbox.media

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.nitakan.sandbox.R

class ToolbarDropdownSpinnerAdapter(private val activity: AppCompatActivity, list: LiveData<List<ContentDirectory>>): BaseAdapter() {

    init {
        list.observe(activity, Observer {
            it?.also {list ->
                items.clear()
                items.addAll(list)
                this.notifyDataSetChanged()
            }
        })
    }

    val items = mutableListOf<ContentDirectory>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return if (convertView == null || convertView.tag.toString() == "NON_DROPDOWN") {
            activity.layoutInflater.inflate(R.layout.toolbar_spinner_item, parent, false).also {
                it.tag = "NON_DROPDOWN"
            }
        } else {
            convertView
        }.also {
            it.findViewById<TextView>(R.id.spinner_item_text).text = getName(position)
        }
    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = items.size

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {

        return if (convertView == null || convertView.tag.toString() == "DROPDOWN") {
            activity.layoutInflater.inflate(R.layout.toolbar_spinner_item_dropdown, parent, false).apply {
                tag = "DROPDOWN"
            }
        } else { convertView }.also {
            it.findViewById<TextView>(R.id.spinner_item_text).text = getName(position)
        }
    }

    private fun getName(position: Int) = getItem(position).let { dir ->
        when (dir) {
            is ContentDirectory.All -> "すべて"
            is ContentDirectory.Pictures -> "写真"
            is ContentDirectory.Videos -> "動画"
            is ContentDirectory.ContentDirectoryImpl -> {
                dir.getDirectoryName()
            }
        }
    }

    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun getViewTypeCount() = 1

    override fun getItemViewType(position: Int) = 0
}