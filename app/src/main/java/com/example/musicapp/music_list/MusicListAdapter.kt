package com.example.musicapp.music_list


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.databinding.MusicListItemBinding
import com.example.musicapp.model.Music


class MusicListAdapter(val clickListener: MusicListListener) : ListAdapter<Music, MusicListAdapter.MusicViewHolder>(DiffCallback){
    companion object DiffCallback: DiffUtil.ItemCallback <Music>(){
        override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.id == newItem.id
        }
    }
    class MusicViewHolder(private var binding : MusicListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: MusicListListener, music: Music){
            binding.music = music
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(MusicListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val image =  getItem(position)
        holder.bind(clickListener, image)
    }
}

class MusicListListener(val clickListener: (music: Music) -> Unit){
    fun onClick(music: Music) = clickListener(music)

}