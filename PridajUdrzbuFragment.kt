package com.example.spravcavozidiel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spravcavozidiel.Databaza.AutoDatabaza
import com.example.spravcavozidiel.Databaza.UdrzbaZaznam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


/**
 * [PridajUdrzbuFragment] je fragment na pridanie záznamu údržby.
 *
 * Tento fragment zobrazí formulár na zadanie podrobností záznamu o údržbe, overenie vstupu a uloženie záznamu do databázy.
 * Zahŕňa tiež spracovanie chýb, ktoré upozorní používateľa na neplatný vstup.
 */
class PridajUdrzbuFragment : Fragment() {

    private lateinit var carDatabase: AutoDatabaza
    private lateinit var kilometersEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var costEditText: EditText

    private var carId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pridaj_udrzbu, container, false)
    }

    /**
     * Táto funkcia nastavuje zobrazenie  fragmentu.
     *
     * Najprv inicializuje databázu áut a rôzne vstupné polia, potom nastaví onClickListener pre tlačidlo uloženia.
     *
     * @param zobrazí zobrazenie vrátené onCreateView.
     * @param SaveInstanceState, ak nie je null, tento fragment sa znovu vytvára z predchádzajúceho uloženého stavu.
     *
     * Funkcia vykonáva nasledujúce úlohy:
     *
     * Načítava databázu áut a ID auta z odovzdaných argumentov - arguments.
     * Načítava podrobnosti o aute z databázy na základe ID vozidla.
     * Overuje používateľské vstupy:
     * Ak akékoľvek overenie zlyhá, zobrazí sa príslušné chybové hlásenie.
     * Ak prebehnú všetky overenia, uloží sa nový záznam o údržbe do databázy vozidla s hodnotami zadanými používateľom.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carDatabase = AutoDatabaza.getDatabase(requireContext())
        carId = arguments?.getInt("carId", -1) ?: -1

        kilometersEditText = view.findViewById(R.id.kilometersEditText)
        dateEditText = view.findViewById(R.id.dateEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        costEditText = view.findViewById(R.id.costEditText)

        savedInstanceState?.let {
            kilometersEditText.setText(it.getString("kilometers"))
            dateEditText.setText(it.getString("date"))
            descriptionEditText.setText(it.getString("description"))
            costEditText.setText(it.getString("cost"))
        }

        val saveMaintenanceButton: Button = view.findViewById(R.id.saveMaintenanceButton)
        saveMaintenanceButton.setOnClickListener {

            val kilometers: Int? = kilometersEditText.text.toString().toIntOrNull()

            val date = dateEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val cost = costEditText.text.toString().toDoubleOrNull()

            GlobalScope.launch(Dispatchers.IO) {
                val car = carDatabase.autoDao().getCarById(carId)

                if (car == null) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Nenašlo sa auto s ID",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                format.isLenient = false
                val parsedDate = try {
                    format.parse(date)
                } catch (e: Exception) {
                    null
                }

                if (parsedDate == null) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Vložte správny dátum dd/MM/yyyy.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                if (kilometers == null) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Zadajte správne kilometre",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                if (kilometers > car.currentKilometers) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Zadajte správne kilometre",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                if (cost == null) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Zadajte správne sumu.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                carDatabase.udrzbaZaznamDao().addMaintenanceRecord(
                    UdrzbaZaznam(
                        id = 0, car_id = carId, date = date, kilometers = kilometers,
                        description = description, cost = cost
                    )
                )

                launch(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Záznam o údržbe pridaný",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    /**
     * Po uložení stavu uloží aktuálny vstup.
     */
        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putString("kilometers", kilometersEditText.text.toString())
            outState.putString("date", dateEditText.text.toString())
            outState.putString("description", descriptionEditText.text.toString())
            outState.putString("cost", costEditText.text.toString())
        }
    }
