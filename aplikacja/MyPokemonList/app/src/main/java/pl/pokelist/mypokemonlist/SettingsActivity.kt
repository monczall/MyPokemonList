package pl.pokelist.mypokemonlist

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class

SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        verifyUserIsLoggedIn()

        //START OF DARK MODE PART
        val appSettingsPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingsPrefs.edit()
        val isNightModeOn: Boolean = appSettingsPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            dark_mode_switch_Settings.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            dark_mode_switch_Settings.isChecked = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        dark_mode_switch_Settings.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                sharedPrefsEdit.apply()
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                sharedPrefsEdit.apply()
            }
        }
        //END OF DARK MODE PART

        //START OF DELETE ACCOUNT PART
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Account delete title")
        builder.setMessage("Account delete message")

        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            Toast.makeText(applicationContext, android.R.string.yes, Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(android.R.string.no) { _, _ ->
            Toast.makeText(applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT).show()
        }

        val alertDialog = builder.create()

        delete_account_button_Settings.setOnClickListener{
            alertDialog.show()
        }
        //END OF DELETE ACCOUNT PART
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}
