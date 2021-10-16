package com.example.stefatools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class ListaAdapter(
    private val listaStref: ArrayList<Strefa>,
    private val listener: OnItemClickListener
                   ) :
    RecyclerView.Adapter<ListaAdapter.ListaViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.strefawiersz,
        parent, false)
        return ListaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        val currentStrefa = listaStref[position]
        holder.numerView.text = currentStrefa.numer.toString()
        holder.nazwaView.text = currentStrefa.nazwa
        holder.dlugoscView.text = currentStrefa.dlugosc
        holder.opisView.text = currentStrefa.opis
    }

    inner class ListaViewHolder(itemView: View) : ViewHolder(itemView),
        View.OnClickListener {
        val numerView: TextView = itemView.findViewById(R.id.numerView)
        val nazwaView: TextView = itemView.findViewById(R.id.nazwaView)
        val dlugoscView: TextView = itemView.findViewById(R.id.dlugoscView)
        val opisView: TextView = itemView.findViewById(R.id.opisView)

        init{
            itemView.setOnClickListener(this)
        }

         override fun onClick(v: View?){
            val position:Int = absoluteAdapterPosition
             if (position != RecyclerView.NO_POSITION)
             listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return listaStref.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }




}

