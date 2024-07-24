package com.example.kotlintodopractice.utils.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.kotlintodopractice.R
import com.example.kotlintodopractice.utils.moddel.ToDoType

class TaskTypesAdapter(private val context: Context, private val imageItems: List<ToDoType>) : BaseAdapter() {

    override fun getCount(): Int {
        return imageItems.size
    }

    override fun getItem(position: Int): Any {
        return imageItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.task_type_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val toDoType = imageItems[position]
        viewHolder.imageView.setImageResource(toDoType.imageResId)

        return view
    }

    private class ViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
}