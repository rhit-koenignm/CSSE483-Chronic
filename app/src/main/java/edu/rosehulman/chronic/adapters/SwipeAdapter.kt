package edu.rosehulman.chronic.adapters

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeAdapter(adapter: PainDataAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    private lateinit var myadapter: PainDataAdapter

    init {
        myadapter = adapter
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        myadapter.removeAtPosition(position)
    }
}