package pl.pokelist.mypokemonlist

import android.R.attr.password
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_settings.*


class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val appSettingsPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingsPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        dont_have_account_textView_login.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        login_button_login.setOnClickListener {
            doLogin()
        }
    }
    private fun doLogin(){
        var userEmail = email_editText_login.text.toString()
        var userPassword = password_editText_login.text.toString()

        if(userEmail.isEmpty() || userPassword.isEmpty()){
            Toast.makeText(this, "Please fill all fields and try again!", Toast.LENGTH_SHORT).show()
            return
        }

        Firebase.auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = Firebase.auth.currentUser
                        Toast.makeText(baseContext, "Successful login", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, PokedexActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    } else {
                        Toast.makeText(baseContext, "Email and password don't match", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}