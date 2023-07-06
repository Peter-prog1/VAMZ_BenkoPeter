package com.example.spravcavozidiel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spravcavozidiel.Databaza.Auto
import com.example.spravcavozidiel.Databaza.AutoDatabaza
import kotlinx.coroutines.*

/**
 * [ServisneUkonyFragment] fragment na zobrazenie záznamov o údržbe konkrétneho auta.
 * Používateľ si môže vybrať auto zo Spinneru a príslušné záznamy o údržbe sa zobrazia pod autom v RecyclerView.
 *
 * Databáza @property carDatabase na zobrazenie automobilov v spinnery.
 * @property maintenanceRecordsRecyclerView RecyclerView na zobrazenie záznamov o údržbe.
 * @property carSpinner Spinner na výber áut.
 * @property uiScope CoroutineScope na spustenie úloh používateľského rozhrania.
 * @property ioScope CoroutineScope na spustenie úloh input output.
 */
class ServisneUkonyFragment : Fragment() {

    private lateinit var carDatabase: AutoDatabaza
    private lateinit var maintenanceRecordsRecyclerView: RecyclerView
    private lateinit var carSpinner: Spinner
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_servisne_zaznamy, container, false)

        carDatabase = AutoDatabaza.getDatabase(requireContext())

        carSpinner = view.findViewById(R.id.car_spinner)
        maintenanceRecordsRecyclerView = view.findViewById(R.id.maintenance_records_recycler_view)
        maintenanceRecordsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        ioScope.launch {
            val cars = carDatabase.autoDao().getAllCars()
            uiScope.launch {
                val carAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cars)
                carAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                carSpinner.adapter = carAdapter
                carSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        val auto = parent.getItemAtPosition(position) as Auto
                        updateMaintenanceRecords(auto.id)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            }
        }

        return view
    }

    /**
     * Získajte záznamy o údržbe z databázy pre konkrétne auto, podľa ID a aktualizujte adaptér RecyclerView.
     *
     * @param carId Jedinečný identifikátor auta, pre ktoré sa majú získať záznamy o údržbe.
     */
    private fun updateMaintenanceRecords(carId: Int) {
        ioScope.launch {
            val maintenanceRecords = carDatabase.udrzbaZaznamDao().getMaintenanceRecordsForCar(carId)
            uiScope.launch {
                val maintenanceRecordAdapter = ServisneUkonyAdapter(maintenanceRecords)
                maintenanceRecordsRecyclerView.adapter = maintenanceRecordAdapter
            }
        }
    }
}
