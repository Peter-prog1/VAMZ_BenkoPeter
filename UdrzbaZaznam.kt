package com.example.spravcavozidiel.Databaza

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index


/**
 * Dátová trieda `UdrzbaZaznam` predstavuje záznam o údržbe auta v aplikácii.
 */
@Entity(
    tableName = "maintenance_records",
    foreignKeys = [
        ForeignKey(
            entity = Auto::class,
            parentColumns = ["id"],
            childColumns = ["car_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("car_id")]
)
data class UdrzbaZaznam(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val car_id: Int,
    val date: String,
    val kilometers: Int,
    val description: String,
    val cost: Double
)


