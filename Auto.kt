package com.example.spravcavozidiel.Databaza

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Dátová trieda `Car` reprezentuje objekt auta v aplikácii.
 * Zahŕňa potrebné vlastnosti automobilu pre prácu aplikácie.
 */

@Entity(tableName = "cars")
data class Auto(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var brand: String,
    var type: String,
    var currentKilometers: Int,
    var lastOilChangeDate: String,
    var lastOilChangeKilometers: Int,
    var consumption: Double? = null
){

    override fun toString(): String {
        return "$brand $type"
    }
}