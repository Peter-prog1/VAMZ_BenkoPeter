package com.example.spravcavozidiel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.spravcavozidiel.Databaza.Auto
import com.example.spravcavozidiel.Databaza.AutoDatabaza
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * [ZalogujCestuViewModel] je ViewModel trieda, ktorá je zodpovedná za prípravu a správu údajov pre fragmenty.
 *
 * @property application Inštancia triedy aplikácie vašej aplikácie.
 */
class ZalogujCestuViewModel(application: Application) : AndroidViewModel(application) {
    private val _selectedCar = MutableLiveData<Auto>()
    private val carDatabase = AutoDatabaza.getDatabase(application)
    private var _cars: List<Auto> = listOf()
    val cars: LiveData<List<Auto>> = carDatabase.autoDao().getAllCarsLive()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            _cars = carDatabase.autoDao().getAllCars()
        }
    }


    /**
     * Nastaví atribút _selectedCar na vybrané auto.
     *
     * @param auto objekt auta, ktorý sa nastaví ako vybraný.
     */
    fun selectCar(auto: Auto) {
        _selectedCar.value = auto
    }

    /**
     * Zaznamená prejdenú cestu auta do databázy.
     *
     * @param kilometers Kilometre prejdené autom.
     * @param fuel Množstvo paliva spotrebovaného autom.
     */
    fun logJourney(kilometers: Int, fuel: Double) {
        val car = _selectedCar.value
        if (car != null) {
            car.currentKilometers += kilometers
            val newConsumption = (fuel / kilometers) * 100
            car.consumption = calculateAverage(car.consumption, newConsumption)
            viewModelScope.launch(Dispatchers.IO) {
                carDatabase.autoDao().updateCar(car)
            }
        }
    }
   }
/**
 * Vypočíta priemernú spotrebu auta.
 *
 * @param oldConsumption Stará spotreba auta.
 * @param newConsumption Nová spotreba auta.
 * @return Priemerná spotreba auta.
 */
    private fun calculateAverage(oldConsumption: Double?, newConsumption: Double): Double {
        if (oldConsumption == null || oldConsumption == 0.0) return newConsumption
        return (oldConsumption + newConsumption) / 2
    }

