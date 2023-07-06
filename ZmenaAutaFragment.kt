package com.example.spravcavozidiel


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spravcavozidiel.Databaza.Auto
import com.example.spravcavozidiel.Databaza.AutoDatabaza
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * [ZmenaAutaFragment] zodpovedná za úpravu a aktualizáciu záznamov o zvolenom automobile v databáze.
 *
 * Trieda je súčasťou komponentu Android Navigation, ktorý nám umožňuje rozdeliť aplikáciu na
 * modulárne komponenty. Vzhľad tohto fragmentu je definované v súbore `fragment_zmena_vozidla.xml`.
 *
 * @property carDatabase inštancia databázy áut používaná na pracovanie s databázov.
 * @property brandEditText Pole EditText pre zadanie zmeneného značky auta.
 * @property typeEditText Pole EditText pre zadanie zmeneného typu auta.
 * @property currentKilometersEditText EditText pole pre zadanie zmenených kilometrov auta.
 * @property lastOilChangeDateEditText EditText pole pre autokm pri poslednej výmene oleja.
 * @property lastOilChangeKilometersEditText EditText field for the car kilometers during the last oil change.
 * @property uiScope Scope Coroutine pre spustenie úloh v hlavnom vlákne alebo vlákne používateľského rozhrania.
 * @property ioScope Scope Coroutine pre spustenie úloh vo vlákne input/output alebo na pozadí.
 */

class ZmenaAutaFragment : Fragment() {

    private lateinit var carDatabase: AutoDatabaza
    private lateinit var brandEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var currentKilometersEditText: EditText
    private lateinit var lastOilChangeDateEditText: EditText
    private lateinit var lastOilChangeKilometersEditText: EditText
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)


    /**
     * Volá sa, aby fragment vytvoril inštanciu svojho layout uživateľského rozhrania.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return  Vráti zobrazenie používateľského rozhrania fragmentu alebo hodnotu null.
     */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_zmena_vozidla, container, false)
    }

    /**
     * onViewCreated sa volá po onCreateView.
     * Inicializuje prvky používateľského rozhrania fragmentu
     * a nastaví potrebné click listenery a coroutine scopes na prácu s databázou.
     *
     * Získa carId zvolené používatelom a zavolá getCarById nad databázou na získanie údajov o aute z databázy.
     * Podrobnosti o zvolenom aute sa zobrazia v používateľskom rozhraní.
     * Po kliknutí tlačidla uložiť overí zadané údaje a aktualizuje údaje auta v databáze.
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val carId = arguments?.getInt("carId", -1) ?: -1

        if (carId == -1) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            Handler(Looper.getMainLooper()).post {
                findNavController().popBackStack()
            }
            return
        }


        carDatabase = AutoDatabaza.getDatabase(requireContext())

        ioScope.launch {
            val car = carDatabase.autoDao().getCarById(carId)

            uiScope.launch {
                if (car == null) {
                    Toast.makeText(
                        requireContext(),
                        "Auto s ID sa nenašlo",
                        Toast.LENGTH_LONG
                    ).show()
                    Handler(Looper.getMainLooper()).post {
                        findNavController().popBackStack()
                    }
                    return@launch
                }

                brandEditText = view.findViewById(R.id.brandEditText)
                typeEditText = view.findViewById(R.id.typeEditText)
                currentKilometersEditText = view.findViewById(R.id.currentKilometersEditText)
                lastOilChangeDateEditText = view.findViewById(R.id.lastOilChangeDateEditText)
                lastOilChangeKilometersEditText = view.findViewById(R.id.lastOilChangeKilometersEditText)


                savedInstanceState?.let {
                    brandEditText.setText(it.getString("brand") ?: car.brand)
                    typeEditText.setText(it.getString("type") ?: car.type)
                    currentKilometersEditText.setText(
                        it.getString("currentKilometers") ?: car.currentKilometers.toString()
                    )
                    lastOilChangeDateEditText.setText(
                        it.getString("lastOilChangeDate") ?: car.lastOilChangeDate
                    )
                    lastOilChangeKilometersEditText.setText(
                        it.getString("lastOilChangeKilometers")
                            ?: car.lastOilChangeKilometers.toString()
                    )
                } ?: run {
                    brandEditText.setText(car.brand)
                    typeEditText.setText(car.type)
                    currentKilometersEditText.setText(car.currentKilometers.toString())
                    lastOilChangeDateEditText.setText(car.lastOilChangeDate)
                    lastOilChangeKilometersEditText.setText(car.lastOilChangeKilometers.toString())
                }


                val saveButton = view.findViewById<Button>(R.id.saveButton)
                saveButton.setOnClickListener {
                    val brand = brandEditText.text.toString()
                    val type = typeEditText.text.toString()
                    val currentKilometers = currentKilometersEditText.text.toString().toInt()
                    val lastOilChangeDate = lastOilChangeDateEditText.text.toString()
                    val lastOilChangeKilometers = lastOilChangeKilometersEditText.text.toString().toInt()

                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    format.isLenient = false
                    val date = try {
                        format.parse(lastOilChangeDate)
                    } catch (e: Exception) {
                        null
                    }
                    when {
                        date == null -> {

                            return@setOnClickListener
                        }
                        currentKilometers < lastOilChangeKilometers -> {

                            return@setOnClickListener
                        }
                        else -> {
                            ioScope.launch {
                                val updatedAuto = Auto(
                                    id = carId,
                                    brand = brand,
                                    type = type,
                                    currentKilometers = currentKilometers,
                                    lastOilChangeDate = lastOilChangeDate,
                                    lastOilChangeKilometers = lastOilChangeKilometers
                                )

                                try {
                                    carDatabase.autoDao().updateCar(updatedAuto)
                                    uiScope.launch {
                                        Toast.makeText(
                                            requireContext(),
                                            "Auto upravené",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        findNavController().popBackStack()
                                    }
                                } catch (e: Exception) {
                                    uiScope.launch {
                                        Toast.makeText(
                                            requireContext(),
                                            "Nepodarilo sa upraviť auto. Skúste to znova.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     * Uloženie stavu fragmentu
     *
     * @param outState
     */

override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString("brand", brandEditText.text.toString())
    outState.putString("type", typeEditText.text.toString())
    outState.putString("currentKilometers", currentKilometersEditText.text.toString())
    outState.putString("lastOilChangeDate", lastOilChangeDateEditText.text.toString())
    outState.putString("lastOilChangeKilometers", lastOilChangeKilometersEditText.text.toString())
    }
}

