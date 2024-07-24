package com.example.kotlintodopractice.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlintodopractice.R
import com.example.kotlintodopractice.databinding.EachTodoItemBinding
import com.example.kotlintodopractice.utils.model.ToDoData


class TaskAdapter(private var list: MutableList<ToDoData>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private  val TAG = "TaskAdapter"
    private var listener:TaskAdapterInterface? = null


    fun setListener(listener:TaskAdapterInterface){
        this.listener = listener
    }
    class TaskViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this.task

                // Set the correct image for the task menu
                binding.taskMenu.setImageResource(this.imageId)

                Log.d(TAG, "onBindViewHolder: " + this)

                binding.editTask.setOnClickListener {
                    listener?.let { it.onEditItemClicked(this, position) }
                }

                binding.deleteTask.setOnClickListener {
                    listener?.let { it.onDeleteItemClicked(this, position) }
                }

                binding.taskMenu.setOnClickListener {
                    listener?.let { it.onMenuItemClicked(this, position, binding.taskMenu) }
                }

                binding.doneTask.setOnClickListener {
                    listener?.let { it.onDoneItemClicked(this, position) }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getViewHolder(position: Int): ToDoData? {
        return if (position < list.size) list[position] else null
    }

    fun updateList(newList: MutableList<ToDoData>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    interface TaskAdapterInterface{
        fun onDeleteItemClicked(toDoData: ToDoData , position : Int)
        fun onEditItemClicked(toDoData: ToDoData , position: Int)
        fun onMenuItemClicked(toDoData: ToDoData, position: Int, taskMenu: ImageView)
        fun onDoneItemClicked(toDoData: ToDoData, position: Int)
    }
}