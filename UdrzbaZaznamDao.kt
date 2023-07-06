package com.example.spravcavozidiel.Databaza


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Rozhranie `UdrzbaZaznamDao` slúži ako objekt prístupu k údajom pre triedu `UdrzbaZaznam`.
 * Definuje metódy pre operácie CRUD.
 */
@Dao
interface UdrzbaZaznamDao {

    @Query("SELECT * FROM maintenance_records WHERE car_id = :carId")
    fun getMaintenanceRecordsForCar(carId: Int): List<UdrzbaZaznam>

    @Insert
    fun addMaintenanceRecord(maintenanceRecord: UdrzbaZaznam): Long
}