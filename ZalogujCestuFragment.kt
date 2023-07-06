package com.example.spravcavozidiel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

class ZalogujCestuFragment : Fragment() {

    private val viewModel: ZalogujCestuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log_journey, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val carSpinner = view.findViewById<Spinner>(R.id.spinner_select_car)
        val kilometersEditText = view.findViewById<EditText>(R.id.edit_text_kilometers)
        val fuelEditText = view.findViewById<EditText>(R.id.edit_text_fuel)
        val logButton = view.findViewById<Button>(R.id.button_log_data)

        savedInstanceState?.let { savedState ->
            kilometersEditText.setText(savedState.getString("kilometers"))
            fuelEditText.setText(savedState.getString("fuel"))
            carSpinner.setSelection(savedState.getInt("spinnerPosition"))
        }

        viewModel.cars.observe(viewLifecycleOwner, Observer { cars ->
            val carNames = cars.map { it.toString() }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, carNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            carSpinner.adapter = adapter

            carSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedCar = cars[position]
                    viewModel.selectCar(selectedCar)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        })


        logButton.setOnClickListener {
            val kilometersStr = kilometersEditText.text.toString()
            val fuelStr = fuelEditText.text.toString()

            if (kilometersStr.isEmpty() || fuelStr.isEmpty()) {
                Toast.makeText(requireContext(), "Prosím naplnte všetky polia", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val kilometers = kilometersStr.toInt()
            val fuel = fuelStr.toDouble()

            viewModel.logJourney(kilometers, fuel)
            Toast.makeText(requireContext(), "Dáta úspešne uložené", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val carSpinner = view?.findViewById<Spinner>(R.id.spinner_select_car)
        val kilometersEditText = view?.findViewById<EditText>(R.id.edit_text_kilometers)
        val fuelEditText = view?.findViewById<EditText>(R.id.edit_text_fuel)

        outState.putString("kilometers", kilometersEditText?.text.toString())
        outState.putString("fuel", fuelEditText?.text.toString())
        outState.putInt("spinnerPosition", carSpinner?.selectedItemPosition ?: 0)
    }
}
