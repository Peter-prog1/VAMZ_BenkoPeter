package com.example.spravcavozidiel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spravcavozidiel.Databaza.AutoDatabaza

import kotlinx.coroutines.*

/**
 * [StatusVozidielFragment] predstavuje Fragment zodpovedný za zobrazenie statusu všetkých áut uložených v databáze.
 * Používa RecyclerView na zobrazenie stavu auta.
 */
class StatusVozidielFragment : Fragment() {

    private lateinit var carDatabase: AutoDatabaza
    private lateinit var autoStatusAdapter: AutoStatusAdapter
    private lateinit var rvCarStatusList: RecyclerView
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_status_vozidiel, container, false)
        rvCarStatusList = view.findViewById(R.id.rv_car_status_list)
        rvCarStatusList.layoutManager = LinearLayoutManager(activity)
        loadCarData()
        return view
    }

    /**
     * Načíta zoznam áut z databázy a pripojí ho k RecyclerView.
     */
    private fun loadCarData() {
        carDatabase = AutoDatabaza.getDatabase(requireContext())

        ioScope.launch {
            val cars = carDatabase.autoDao().getAllCars()
            uiScope.launch {
                autoStatusAdapter = AutoStatusAdapter(cars, carDatabase)
                rvCarStatusList.adapter = autoStatusAdapter
            }
        }
    }
}
