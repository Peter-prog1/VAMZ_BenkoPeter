
package com.example.spravcavozidiel




import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected


/**
 * [PrehladVozidielMain] slúži ako počiatočný bod pre aplikáciu, ktorý riadi navigáciu medzi rôznymi časťami aplikácie - fragmentami.
 *
 * Táto aktivita zahŕňa [NavHostFragment], ktorý umožňuje prepínanie medzi fragmentami. Okrem toho spravuje
 * ponuku možností a spracuje výber položiek pomocou rozhrania navigácie.
 */
class PrehladVozidielMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.hlavne_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}

