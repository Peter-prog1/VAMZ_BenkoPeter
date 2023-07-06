package com.example.spravcavozidiel


import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spravcavozidiel.Databaza.Auto
import com.example.spravcavozidiel.Databaza.AutoDatabaza
import com.example.spravcavozidiel.databinding.ItemCarStatusBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

/**
 * [CarStatusAdapter] je adaptér RecyclerView na zobrazenie stavu áut.
 *
 * @property autos Zoznam áut, ktoré sa majú zobraziť.
 * @property carDatabase Databáza na získavanie záznamov o údržbe.
 */
class CarStatusAdapter(
    private val autos: List<Auto>,
    private val carDatabase: AutoDatabaza
) : RecyclerView.Adapter<CarStatusAdapter.CarStatusViewHolder>() {

    /**
     * [CarStatusViewHolder] obsahuje zobrazenia pre každú položku stavu auta v RecyclerView.
     *
     * @property binding Objekt dátovej väzby pre rozloženie položky stavu auta.
     * @property carDatabase Databáza na získavanie záznamov o údržbe.
     */
    class CarStatusViewHolder(
        private val binding: ItemCarStatusBinding,
        private val carDatabase: AutoDatabaza
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        /**
         * Spája údaje o aute s pohľadmi v [CarStatusViewHolder].
         *
         * @param auto Objekt auta obsahujúci údaje, ktoré sa majú zobraziť.
         */

        @SuppressLint("SetTextI18n")
        fun bind(auto: Auto) {
            with(binding) {
                tvBrandAndType.text = "${auto.brand} ${auto.type}"
                tvFuelConsumption.text = auto.consumption?.let {
                    val roundedConsumption = String.format("%.3f", it)
                    "Spotreba: $roundedConsumption L/100km"
                } ?: "Spotreba: Bez dát"
                tvOdometerReading.text = "Kilometre: ${auto.currentKilometers} km"
                val oilChangeDateLocal = LocalDate.parse(auto.lastOilChangeDate, dateFormatter)
                val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formattedDate = oilChangeDateLocal.format(format)
                tvOilChangeDate.text = "Dátum poslednej výmeny oleja: $formattedDate"


                CoroutineScope(Dispatchers.IO).launch {
                    val maintenanceRecords = carDatabase.udrzbaZaznamDao().getMaintenanceRecordsForCar(auto.id)
                    val totalMaintenanceCost = maintenanceRecords.sumOf { it.cost }

                    withContext(Dispatchers.Main) {
                        tvTotalMaintenanceCost.text = "Cena za údržbu vozidla: $totalMaintenanceCost €"

                        if (LocalDate.now().minusYears(1).isAfter(oilChangeDateLocal)) {
                            tvOilChangeDate.setTextColor(Color.RED)
                        }
                        if (auto.currentKilometers - auto.lastOilChangeKilometers >= 15000) {
                            tvOdometerReading.setTextColor(Color.RED)
                        }
                    }
                }
            }
        }


        companion object {
            /**
             * Vytvorí [CarStatusViewHolder] z danej [parent] ViewGroup.
             *
             * @param parent Skupina ViewGroup, do ktorej bude pridané nové zobrazenie.
             * @param carDatabase Databáza na získanie záznamov o údržbe.
             * @return Nová inštancia [CarStatusViewHolder].
             */
            fun from(parent: ViewGroup, carDatabase: AutoDatabaza): CarStatusViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCarStatusBinding.inflate(layoutInflater, parent, false)
                return CarStatusViewHolder(binding, carDatabase)
            }
        }
    }

    /**
     * Vytvorí nový [CarStatusViewHolder] nafúknutím rozloženia pre položku stavu auta.
     *
     * @param parent Skupina ViewGroup, do ktorej bude pridané nové zobrazenie.
     * @param viewType Typ nového zobrazenia.
     * @return Nová inštancia [CarStatusViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarStatusViewHolder {
        return CarStatusViewHolder.from(parent, carDatabase)
    }

    /**
     * Spája údaje s zobrazeniami v [CarStatusViewHolder].
     *
     * @param holder [CarStatusViewHolder], na ktorý sa majú naviazať údaje.
     * @param position Pozícia položky v množine údajov.
     */
    override fun onBindViewHolder(holder: CarStatusViewHolder, position: Int) {
        val car = autos[position]
        holder.bind(car)
    }

    /**
     * Vráti celkový počet položiek v množine údajov uchovávanej adaptérom.
     *
     * @return Celkový počet položiek v množine údajov.
     */
    override fun getItemCount(): Int {
        return autos.size
    }
}
