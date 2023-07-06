package com.example.spravcavozidiel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spravcavozidiel.Databaza.Auto
import com.example.spravcavozidiel.Databaza.AutoDatabaza
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * [PridanieVozidlaFragment] je Fragment na pridanie nového auta do databázy áut.
 *
 * Spravuje formulár, do ktorého môže používateľ zadať značku, typ, aktuálne kilometre,
 * dátum poslednej výmeny oleja a počet kilometrov pri poslednej výmene oleja.
 * Keď používateľ vyplní formulár a klikne na tlačidlo uložiť,
 * nové auto sa pridá do databázy áut.
 */

class PridanieVozidlaFragment : Fragment() {

    private lateinit var carDatabase: AutoDatabaza
    private lateinit var brandEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var currentKilometersEditText: EditText
    private lateinit var lastOilChangeDateEditText: EditText
    private lateinit var lastOilChangeKilometersEditText: EditText


    /**
     * Táto funkcia inicializuje databázu áut, zobrazenie EditText pre detaily auta,
     * a nastaví onClickListener pre tlačidlo uloženia.
     * Po kliknutí na tlačidlo uložiť overí uživateľský vstup, vytvorí sa nový objekt auta,
     * a pridá ho do databázy áut.
     *
     * @param view
     * @param SaveInstanceState Ak nie je null, tento fragment sa znovu vytvára z predchádzajúceho uloženého stavu.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pridanie_vozidla, container, false)

        carDatabase = AutoDatabaza.getDatabase(requireContext())

        brandEditText = view.findViewById(R.id.brandEditText)
        typeEditText = view.findViewById(R.id.typeEditText)
        currentKilometersEditText = view.findViewById(R.id.currentKilometersEditText)
        lastOilChangeDateEditText = view.findViewById(R.id.lastOilChangeDateEditText)
        lastOilChangeKilometersEditText = view.findViewById(R.id.lastOilChangeKilometersEditText)

        savedInstanceState?.let {
            brandEditText.setText(it.getString("brand"))
            typeEditText.setText(it.getString("type"))
            currentKilometersEditText.setText(it.getString("currentKilometers"))
            lastOilChangeDateEditText.setText(it.getString("lastOilChangeDate"))
            lastOilChangeKilometersEditText.setText(it.getString("lastOilChangeKilometers"))
        }

        val saveButton: Button = view.findViewById(R.id.saveButton)
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
                    Toast.makeText(
                        requireContext(),
                        "Vložte dátum v správnom formáte deň/mesiac/rok.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                currentKilometers < lastOilChangeKilometers -> {
                    Toast.makeText(
                        requireContext(),
                        "Aktuálne kilometre nemôžu byť menšie ako pri výmene oleja",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                else -> {
                    val newAuto = Auto(
                        0,
                        brand,
                        type,
                        currentKilometers,
                        lastOilChangeDate,
                        lastOilChangeKilometers,
                        0.0
                    )
                    GlobalScope.launch(Dispatchers.IO) {
                        carDatabase.autoDao().addCar(newAuto)
                        launch(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Auto pridané úspešne",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        return view
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString("brand", brandEditText.text.toString())
            putString("type", typeEditText.text.toString())
            putString("currentKilometers", currentKilometersEditText.text.toString())
            putString("lastOilChangeDate", lastOilChangeDateEditText.text.toString())
            putString("lastOilChangeKilometers", lastOilChangeKilometersEditText.text.toString())
        }
        super.onSaveInstanceState(outState)
    }
}
