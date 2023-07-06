package com.example.spravcavozidiel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spravcavozidiel.Databaza.AutoDatabaza
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * [CarListFragment] je fragment, ktorý zobrazuje zoznam áut.
 *
 * Využíva [CarAdapter] pre RecyclerView a spracuváva akcie používateľského rozhrania z RecyclerView.
 *
 * @property carDatabase Databáza používaná na ukladanie a získavanie informácií o autách.
 * @property carAdapter Adaptér používaný na zobrazenie áut v RecyclerView.
 */
class CarListFragment : Fragment(), OnCarClickListener {

    private lateinit var carDatabase: AutoDatabaza
    private lateinit var carAdapter: CarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_car_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carDatabase = AutoDatabaza.getDatabase(requireContext())

        val carRecyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        carAdapter = CarAdapter(emptyList(), this)
        carRecyclerView.adapter = carAdapter
        carRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val addCarButton: Button = view.findViewById(R.id.fab_add_car)
        addCarButton.setOnClickListener {
            Log.d("CarListFragment", "Button clicked")
            findNavController().navigate(R.id.action_carList_to_pridanieVozidla)


        }

        loadCars()
    }

    private fun loadCars() {
        GlobalScope.launch(Dispatchers.IO) {
            val cars = carDatabase.autoDao().getAllCars()
            launch(Dispatchers.Main) {
                carAdapter.updateCars(cars)
            }
        }
    }
    /**
     * Keď uživateľ vyberie auto na pridanie záznamu o údržbe, spustí fragment na pridanie údržby s formulárom.
     *
     * @param carId ID vybraného auta.
     */
    override fun onAddMaintenanceClicked(carId: Int) {
        val action = R.id.action_carList_to_pridajUdrzbu
        val bundle = bundleOf("carId" to carId)
        findNavController().navigate(action, bundle)
    }

    /**
     * Keď uživateľ vyberie auto na vymazanie, dané auto s carId sa nájde v databáze a vymaže.
     * Potom sa znova zobrazí zoznam áut, už bez toho vymazaného.
     *
     * @param carId ID vybraného auta.
     */

    override fun onDeleteCarClicked(carId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            carDatabase.autoDao().deleteCarById(carId)

            launch(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Auto úspešne vymazané",
                    Toast.LENGTH_LONG
                ).show()

                loadCars()
            }
        }
    }



    /**
     * Keď uživateľ vyberie auto na vykonanie úpravy, spustí sa fragment na upravenie auta s daným carId.
     *
     * @param carId ID vybraného auta.
     */
    override fun onEditCarClicked(carId: Int) {
        val action = R.id.action_carList_to_zmenaAuta
        val bundle = bundleOf("carId" to carId)
        findNavController().navigate(action, bundle)
    }



}
