package pl.pokelist.mypokemonlist


import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val appSettingsPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingsPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        upload_photo_button_register.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_textView_register.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }

    var uri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null){
            uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            upload_photo_button_register.setText("Photo uploaded")
            upload_photo_button_register.visibility = View.INVISIBLE
            upload_photo_CircleImageView_register.setImageBitmap(bitmap)

        }
    }

    private fun performRegister(){
        val nick = nick_editText_register.text.toString()
        val email = email_editText_register.text.toString()
        val password1 = password_editText_register.text.toString()
        val password2 = password2_editText_register.text.toString()
        var druzyna = ""
        if(instinct_radioButton_register.isChecked){
            druzyna = "Instinct"
        }else if(mystic_radioButton_register.isChecked){
            druzyna = "Mystic"
        }else if(valor_radioButton_register.isChecked){
            druzyna = "Valor"
        }else{
            Toast.makeText(this, "Please check your team!", Toast.LENGTH_SHORT).show()
            return
        }

        if(nick.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and try again!", Toast.LENGTH_SHORT).show()
            return
        }
        if(nick.length > 15){
            Toast.makeText(this, "Your nickname is too long (${nick.length} characters) when max length is 15 characters", Toast.LENGTH_SHORT).show()
            return
        }
        if(password1 == password2){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password1)
                .addOnCompleteListener{
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this, "Account successfully created", Toast.LENGTH_SHORT).show()


                    uploadImageToFirebaseStorage(druzyna)
                    saveUserWithoutImageToFirebaseDatabase(druzyna)

                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to create account: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show()
            return
        }
    }
    private fun uploadImageToFirebaseStorage(druzyna: String){
        if(uri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(uri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToFirebaseDatabase(it.toString(),druzyna)
                    }
                }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String, druzyna: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, nick_editText_register.text.toString(), email_editText_register.text.toString(), profileImageUrl, druzyna, 0, 0, 0, 0)
        createPokemonDummyData(uid)
        ref.setValue(user)
                .addOnSuccessListener {
                    val intent = Intent(this, PokedexActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
    }
    private fun saveUserWithoutImageToFirebaseDatabase(druzyna: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, nick_editText_register.text.toString(), email_editText_register.text.toString(), "NoProfileImage", druzyna, 0, 0, 0, 0)
        createPokemonDummyData(uid)
        ref.setValue(user)
            .addOnSuccessListener {
                val intent = Intent(this, PokedexActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
    }
    private fun createPokemonDummyData(uid: String){
        val refPokemonInfo = FirebaseDatabase.getInstance().getReference("/pokemon_user_info")
        for(i in 1..809){
            val dummyDataPokemonUserInfo = PokemonUserInfo(0,0,0,i)

            refPokemonInfo.child("$uid").child("$i").setValue(dummyDataPokemonUserInfo)
        }
    }
}