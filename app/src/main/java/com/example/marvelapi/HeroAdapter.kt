package com.example.marvelapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class HeroAdapter(private val heroes: List<Hero>) :
    RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {

    class HeroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val heroImage: ImageView = view.findViewById(R.id.heroImage)
        val heroName: TextView = view.findViewById(R.id.heroName)
        val heroDescription: TextView = view.findViewById(R.id.heroDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hero, parent, false)
        return HeroViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        val hero = heroes[position]
        holder.heroName.text = hero.name
        holder.heroDescription.text =
            if (hero.description.isNotEmpty()) hero.description else "No description available"

        Picasso.get().load(hero.imageUrl).into(holder.heroImage)
    }

    override fun getItemCount(): Int = heroes.size
}
