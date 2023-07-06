package com.example.spravcavozidiel.Databaza


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

/**
 * Rozhranie `AutoDao` slúži ako Data Access Object pre triedu `Car`.
 * Definuje metódy pre operácie CRUD - vytovr, čítaj, prepíš a vymaž.
 */
@Dao
interface AutoDao {

    @Query("SELECT * FROM cars")
    fun getAllCars(): List<Auto>

    @Query("SELECT * FROM cars WHERE id = :carId")
    fun getCarById(carId: Int): Auto

    @Insert
    fun addCar(auto: Auto): Long

    @Update
    fun updateCar(auto: Auto): Int

    @Delete
    fun deleteCar(auto: Auto): Int

    /**
     * Vyhľadá konkrétne auto pomocou jeho ID.
     *
     * @param carId ID auta.
     * @return Auto s daným ID alebo `null`, ak sa nenájde.
     */
    @Query("DELETE FROM cars WHERE id = :carId")
    fun deleteCarById(carId: Int): Int
}