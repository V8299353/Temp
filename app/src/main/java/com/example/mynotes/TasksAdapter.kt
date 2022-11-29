package com.example.mynotes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.core.motion.utils.Utils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mynotes.databinding.LayoutSingleTaskBinding
import com.example.mynotes.helper.Helper

class TasksAdapter(val context: Context,var onNoteClick: OnNoteClick) : ListAdapter<TaskModel, TasksAdapter.VH>(TaskDiff()) {

    class TaskDiff : DiffUtil.ItemCallback<TaskModel>() {
        override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem.uuid == newItem.uuid
        }
        override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.description ==  newItem.description &&
                    oldItem.mapUrl == newItem.mapUrl &&
                    oldItem.imageUrl ==  newItem.imageUrl
        }
    }

    inner class VH(val binding: LayoutSingleTaskBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val holder= LayoutSingleTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VH(holder)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        with(holder.binding) {
            this.title.text = getItem(position).title
            this.desc.text = getItem(position).description
            getItem(position).mapUrl?.let{
                this.mapLink.text = it
            } ?: run {
                this.mapLL.visibility = View.GONE
            }
            getItem(position).imageUrl?.let {
                Glide.with(context)
                    .load(it)
                    .apply(RequestOptions().fitCenter())
                    .error(R.drawable.ic_baseline_account_circle_24)
                    .into(this.image)
            } ?: run {
                this.image.visibility = View.GONE
            }
            this.dateCreated.text = "Date Created : ${Helper.getDateString(getItem(position).createdDate!!)}"
            getItem(position).updatedDate?.let {
                this.dateModified.text = "Date Modified : ${Helper.getDateString(it)}"
            } ?: run {
                this.dateModified.visibility = View.GONE
            }

            this.root.setOnClickListener {
                onNoteClick.onNoteClicked(getItem(position))
            }
        }
    }
}