package com.example.spravcavozidiel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spravcavozidiel.Databaza.UdrzbaZaznam

/**
 * [ServisneUkonyAdapter] je adaptér RecyclerView na zobrazovanie záznamov o údržbe.
 *
 * Každý záznam zobrazuje dátum údržby, kilometre auta pri údržbe údržby, popis údržby a cenu údržbu.
 *
 * @property maintenanceRecords Zoznam záznamov údržby, ktoré sa majú zobraziť.
 */

class ServisneUkonyAdapter (
    private val maintenanceRecords: List<UdrzbaZaznam>
) : RecyclerView.Adapter<ServisneUkonyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.date_text_view)
        val kilometersTextView: TextView = view.findViewById(R.id.kilometers_text_view)
        val descriptionTextView: TextView = view.findViewById(R.id.description_text_view)
        val costTextView: TextView = view.findViewById(R.id.cost_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.servisny_ukon_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * onBindViewHolder
     *
     * Túto funkciu volá RecyclerView na zobrazenie údajov.
     * Aktualizuje obsah ViewHolder tak, aby reprezentoval položku servisného úkonu.
     *
     * @param holder  reprezentuje obsah položky.
     * @param position Pozícia položky v rámci adaptéra.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val maintenanceRecord = maintenanceRecords[position]
        holder.dateTextView.text = maintenanceRecord.date
        holder.kilometersTextView.text = "${maintenanceRecord.kilometers} km"
        holder.descriptionTextView.text = maintenanceRecord.description
        holder.costTextView.text = "${maintenanceRecord.cost} €"
    }


    override fun getItemCount() = maintenanceRecords.size
}