package com.example.oluwafemioyebayoinfo6214project2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerViewAdapter(val listofPlaces: List<Places>): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.list_item,parent,false)

        return MyViewHolder(listItem)
    }

    override fun getItemCount(): Int {
        return  listofPlaces.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val place = listofPlaces[position]
        holder.bid(place)
    }

}
class MyViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun bid(place: Places){
        val textView = view.findViewById<TextView>(R.id.tvName)

        textView.text = place.name

    }
}