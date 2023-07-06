package com.example.spravcavozidiel


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spravcavozidiel.Databaza.Auto


/**
 * Toto rozhranie poskytuje metódy callback na spracovanie udalostí kliknutia na tlačidlo v `CarAdapter` na spracovanie
 * v aplikácii.
 */
interface OnCarClickListener {
    /**
     * Volá sa po kliknutí na tlačidlo „Pridať údržbu“.
     *
     * @param carId ID vozidla, pre ktoré sa má pridať údržba.
     */
    fun onAddMaintenanceClicked(carId: Int)

    /**
     * Volá sa po kliknutí na tlačidlo „Vymazať auto“.
     *
     * @param carId ID vozidla, ktoré sa má vymazať.
     */
    fun onDeleteCarClicked(carId: Int)

    /**
     * Volá sa po kliknutí na tlačidlo „Upraviť auto“.
     *
     * @param carId ID auta, ktoré sa má upraviť.
     */
    fun onEditCarClicked(carId: Int)
}
/**
 * Táto trieda je Adapter pre RecyclerView v `CarListFragment`.
 * Obsahuje zoznam „Áut“ a odosiela akcie používateľského rozhrania do fragmentu prostredníctvom nástroja
 * „OnCarClickListener“.
 */
class CarAdapter(
    private var autos: List<Auto>,
    private val onAddMaintenanceClickListener: OnCarClickListener
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {


    inner class CarViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val brandTextView: TextView = view.findViewById(R.id.brandTextView)
        val typeTextView: TextView = view.findViewById(R.id.typeTextView)
        val currentKilometersTextView: TextView = view.findViewById(R.id.currentKilometersTextView)
        val addMaintenanceButton: Button = view.findViewById(R.id.addMaintenanceButton)
        var carId: Int = 0
        val editCarButton: Button = view.findViewById(R.id.editCarButton)
        val deleteCarButton: Button = view.findViewById(R.id.deleteCarButton)

        init {
            addMaintenanceButton.setOnClickListener {
                onAddMaintenanceClickListener.onAddMaintenanceClicked(carId)
            }

            editCarButton.setOnClickListener {
                onAddMaintenanceClickListener.onEditCarClicked(carId)
            }
            deleteCarButton.setOnClickListener {
                onAddMaintenanceClickListener.onDeleteCarClicked(carId)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.car_item, parent, false)
        return CarViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = autos[position]
        holder.brandTextView.text = car.brand
        holder.typeTextView.text = car.type
        holder.currentKilometersTextView.text = "${car.currentKilometers} km"
        holder.carId = car.id
    }


    override fun getItemCount(): Int = autos.size

    /**
     * Aktualizuje zobrazený zoznam áut v RecyclerView.
     *
     * @param newAutos Nový zoznam áut na zobrazenie.
     */
    fun updateCars(newAutos: List<Auto>) {
        autos = newAutos
        notifyDataSetChanged()
    }
}

