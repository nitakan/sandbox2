package jp.cookbiz.mkato.sandbox.media

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import jp.cookbiz.mkato.sandbox.R
import jp.cookbiz.mkato.sandbox.databinding.MediaListItemBinding

class MediaListAdapter(private val activity: AppCompatActivity, private val viewModel: MediaListViewModel, items: LiveData<List<MediaContent>>): RecyclerView.Adapter<MediaListAdapter.ViewHolder>() {

    init {
        items.observe( activity, Observer {
            it?.also {list ->
                itemChanged(list)
            }
        })
    }

    private val items = arrayListOf<MediaContent>()
    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<MediaListItemBinding>(activity.layoutInflater, R.layout.media_list_item, parent, false)
        binding.setLifecycleOwner(activity)
        return ViewHolder(binding)
    }

    fun findIndex(view: View) = recyclerView.getChildLayoutPosition(view)


    override fun getItemCount() = items.size

    private fun getItem(position: Int) = items[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = getItem(position)
    }

    private fun itemChanged(list: List<MediaContent>) {
        items.clear()
        items.addAll(list)
        this.notifyDataSetChanged()
    }

    class ViewHolder(val binding: MediaListItemBinding): RecyclerView.ViewHolder(binding.root)
}