package com.example.spravcavozidiel

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
/**
 * Aplikačná trieda, ktorá inicializuje knižnicu AndroidThreeTen pri spustení aplikácie.
 * AndroidThreeTen je knižnica, ktorá poskytuje API pre dátum a čas zavedené v Java 8 pre Android.
 * Používa sa na správu funkcií s dátumom a časom.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}