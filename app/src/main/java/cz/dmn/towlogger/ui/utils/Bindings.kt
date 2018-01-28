package cz.dmn.towlogger.ui.utils

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView

@BindingAdapter("adapter")
fun bindAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    recyclerView.adapter = adapter
}