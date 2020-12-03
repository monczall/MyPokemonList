package pl.pokelist.mypokemonlist

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pokemon.*


class PokemonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon)

        verifyUserIsLoggedIn()

        val appSettingsPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingsPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //POBIERANIE ID POKEMONA Z WIDOKU
        var id: Int
        val name = intent.getStringExtra(PokedexActivity.POKEMON_KEY)
        if (name != null) {
            id = name.toInt()
            id++
            supportActionBar?.title = id.toString()
        } else {
            id = -1
            supportActionBar?.title = "Error_fetching_id"
        }
        //USTAWIENIE REFERENCJI DO BAZY
        val uid = FirebaseAuth.getInstance().uid
        val pokemonInfoReference = FirebaseDatabase.getInstance().getReference("pokemon_user_info/$uid/$id")
        val leaderboardReference = FirebaseDatabase.getInstance().getReference("users/$uid")
        val pokemonReference = FirebaseDatabase.getInstance().getReference("pokemons/$id")
        //=====START OF LEADERBOARD REFERENCE=====
        leaderboardReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var pokemonCount = snapshot.child("pokemonCount").value.toString().toInt()
                var luckyCount = snapshot.child("luckyCount").value.toString().toInt()
                var shinyCount = snapshot.child("shinyCount").value.toString().toInt()
                var totalCount = snapshot.child("totalCount").value.toString().toInt()
                Log.d("TEST", "PokedexCount: $pokemonCount")
                Log.d("TEST", "LuckydexCount: $luckyCount")
                Log.d("TEST", "ShinyCount: $shinyCount")
                Log.d("TEST", "Points: $totalCount")

                //=====START OF POKEMON INFO REFERENCE=====
                pokemonInfoReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var pokemonId = snapshot.child("pokemon_id").value.toString().toInt()
                        var inPokedex = snapshot.child("inPokedex").value.toString().toInt()
                        var inLucky = snapshot.child("inLucky").value.toString().toInt()
                        var inShiny = snapshot.child("inShiny").value.toString().toInt()
                        Log.d("TEST", "PokemonUserInfoId: $pokemonId")
                        Log.d("TEST", "PokemonUserInfoInPokedex: $inPokedex")
                        Log.d("TEST", "PokemonUserInfoInLucky: $inLucky")
                        Log.d("TEST", "PokemonUserInfoInShiny: $inShiny")

                        //USTAWIENIE SWITCHY
                        if(inPokedex == 1 && inLucky == 1 && inShiny == 1){
                            pokedex_switch_pokemon.isChecked = true
                            lucky_switch_pokedex.isChecked = true
                            shiny_switch_pokemon.isChecked = true
                            pokedex_switch_pokemon.isEnabled = true
                            lucky_switch_pokedex.isEnabled = true
                            shiny_switch_pokemon.isEnabled = true
                        }else if(inPokedex == 1 && inLucky == 1 && inShiny == 0 && czyShinyValue_textView_pokemon.text == "Yes"){
                            pokedex_switch_pokemon.isChecked = true
                            lucky_switch_pokedex.isChecked = true
                            shiny_switch_pokemon.isChecked = false
                            pokedex_switch_pokemon.isEnabled = true
                            lucky_switch_pokedex.isEnabled = true
                            shiny_switch_pokemon.isEnabled = true
                        }else if(inPokedex == 1 && inLucky == 1 && inShiny == 0 && czyShinyValue_textView_pokemon.text == "No"){
                            pokedex_switch_pokemon.isChecked = true
                            lucky_switch_pokedex.isChecked = true
                            shiny_switch_pokemon.isChecked = false
                            pokedex_switch_pokemon.isEnabled = true
                            lucky_switch_pokedex.isEnabled = true
                            shiny_switch_pokemon.isEnabled = false
                            shiny_switch_pokemon.text = "Shiny (Not Available)"
                        }else if(inPokedex == 1 && inLucky == 0 && inShiny == 1){
                            pokedex_switch_pokemon.isChecked = true
                            lucky_switch_pokedex.isChecked = false
                            shiny_switch_pokemon.isChecked = true
                            pokedex_switch_pokemon.isEnabled = true
                            lucky_switch_pokedex.isEnabled = true
                            shiny_switch_pokemon.isEnabled = true
                        }else if(inPokedex == 1 && inLucky == 0 && inShiny == 0 && czyShinyValue_textView_pokemon.text == "Yes"){
                            pokedex_switch_pokemon.isChecked = true
                            lucky_switch_pokedex.isChecked = false
                            shiny_switch_pokemon.isChecked = false
                            pokedex_switch_pokemon.isEnabled = true
                            lucky_switch_pokedex.isEnabled = true
                            shiny_switch_pokemon.isEnabled = true
                        }else if(inPokedex == 1 && inLucky == 0 && inShiny == 0 && czyShinyValue_textView_pokemon.text == "No"){
                            pokedex_switch_pokemon.isChecked = true
                            lucky_switch_pokedex.isChecked = false
                            shiny_switch_pokemon.isChecked = false
                            pokedex_switch_pokemon.isEnabled = true
                            lucky_switch_pokedex.isEnabled = true
                            shiny_switch_pokemon.isEnabled = false
                            shiny_switch_pokemon.text = "Shiny (Not Available)"
                        }else if(inPokedex == 0 && inLucky == 0 && inShiny == 0 && czyDostepnyValue_textView_pokemon.text == "Yes" && czyShinyValue_textView_pokemon.text == "Yes"){
                            pokedex_switch_pokemon.isChecked = false
                            lucky_switch_pokedex.isChecked = false
                            shiny_switch_pokemon.isChecked = false
                            pokedex_switch_pokemon.isEnabled = true
                            lucky_switch_pokedex.isEnabled = false
                            shiny_switch_pokemon.isEnabled = false
                        }else if(inPokedex == 0 && inLucky == 0 && inShiny == 0 && czyDostepnyValue_textView_pokemon.text == "Yes" && czyShinyValue_textView_pokemon.text == "No"){
                            pokedex_switch_pokemon.isChecked = false
                            lucky_switch_pokedex.isChecked = false
                            shiny_switch_pokemon.isChecked = false
                            pokedex_switch_pokemon.isEnabled = true
                            lucky_switch_pokedex.isEnabled = false
                            shiny_switch_pokemon.isEnabled = false
                            shiny_switch_pokemon.text = "Shiny (Not Available)"
                        }else if(inPokedex == 0 && inLucky == 0 && inShiny == 0 && czyDostepnyValue_textView_pokemon.text == "No"){
                            pokedex_switch_pokemon.isChecked = false
                            lucky_switch_pokedex.isChecked = false
                            shiny_switch_pokemon.isChecked = false
                            pokedex_switch_pokemon.isEnabled = false
                            lucky_switch_pokedex.isEnabled = false
                            shiny_switch_pokemon.isEnabled = false
                            pokedex_switch_pokemon.text = "In Pokedex (Not Available)"
                            lucky_switch_pokedex.text = "Lucky (Not Available)"
                            shiny_switch_pokemon.text = "Shiny (Not Available)"
                        }
                        //ZMIANA W WYGLADZIE I BAZIE PRZEZ SWITCHE
                        pokedex_switch_pokemon.setOnCheckedChangeListener { _, isChecked ->
                            if(isChecked){
                                lucky_switch_pokedex.isEnabled = true
                                shiny_switch_pokemon.isEnabled = czyShinyValue_textView_pokemon.text == "Yes"
                                pokemonInfoReference.child("inPokedex").setValue(1)
                                leaderboardReference.child("pokemonCount").setValue(++pokemonCount)
                                leaderboardReference.child("totalCount").setValue(++totalCount)
                                Log.d("TEST", "Zaznaczono Pokedex")
                            }else{
                                if(lucky_switch_pokedex.isChecked) lucky_switch_pokedex.isChecked = false
                                if(shiny_switch_pokemon.isChecked) shiny_switch_pokemon.isChecked = false
                                lucky_switch_pokedex.isEnabled = false
                                shiny_switch_pokemon.isEnabled = false
                                pokemonInfoReference.child("inPokedex").setValue(0)
                                leaderboardReference.child("pokemonCount").setValue(--pokemonCount)
                                leaderboardReference.child("totalCount").setValue(--totalCount)
                                Log.d("TEST", "Odznaczono Pokedex")
                            }
                        }
                        lucky_switch_pokedex.setOnCheckedChangeListener { _, isChecked ->
                            if(isChecked){
                                Log.d("TEST", "Zaznaczono Lucky")
                                pokemonInfoReference.child("inLucky").setValue(1)
                                leaderboardReference.child("luckyCount").setValue(++luckyCount)
                                leaderboardReference.child("totalCount").setValue(++totalCount)
                            }else{
                                Log.d("TEST", "Odznaczono Lucky")
                                pokemonInfoReference.child("inLucky").setValue(0)
                                leaderboardReference.child("luckyCount").setValue(--luckyCount)
                                leaderboardReference.child("totalCount").setValue(--totalCount)
                            }
                        }
                        shiny_switch_pokemon.setOnCheckedChangeListener { _, isChecked ->
                            if(isChecked){
                                Log.d("TEST", "Zaznaczono Shiny")
                                pokemonInfoReference.child("inShiny").setValue(1)
                                leaderboardReference.child("shinyCount").setValue(++shinyCount)
                                leaderboardReference.child("totalCount").setValue(++totalCount)
                            }else{
                                Log.d("TEST", "Odznaczono Shiny")
                                pokemonInfoReference.child("inShiny").setValue(0)
                                leaderboardReference.child("shinyCount").setValue(--shinyCount)
                                leaderboardReference.child("totalCount").setValue(--totalCount)
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("TEST","ERROR POBIERANIA Z BAZY DLA USER INFO")
                    }

                })
                //=====END OF POKEMON INFO REFERENCE=====
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TEST","ERROR POBIERANIA Z BAZY DLA LEADERBOARDOW")
            }
        })
        //=====END OF LEADERBOARD REFERENCE=====

        //=====START OF POKEMON REFERENCE=====
        pokemonReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                //ZAPISANIE ZMIENNYCH Z BAZY
                val pokemonId = snapshot.child("pokemon_id").value.toString().toInt()
                val pokemonName = snapshot.child("pokemon_name").value.toString()
                val pokemonNameToLowercase = pokemonName.toLowerCase()
                val pokemonGeneration = snapshot.child("pokemon_generation").value.toString().toInt()
                val pokemonAvailable = snapshot.child("pokemon_Available").value.toString().toInt()
                val pokemonShiny = snapshot.child("pokemon_Shiny").value.toString().toInt()

                Log.d("TEST", "PokemonId: $pokemonId")
                Log.d("TEST", "PokemonName: $pokemonName")
                Log.d("TEST", "PokemonNameToLowercase: $pokemonNameToLowercase")
                Log.d("TEST", "PokemonGeneration: $pokemonGeneration")
                Log.d("TEST", "PokemonAvailable: $pokemonAvailable")
                Log.d("TEST", "PokemonShiny: $pokemonShiny")
                //ZMIANA ACTION BARA
                supportActionBar?.title = pokemonName

                //ZMIANA NAZWY POKEMONA
                nazwaPokemona_textView_pokemon.text = pokemonName

                //ZMIANA NAZWY GENERACJI
                when (pokemonGeneration) {
                    1 -> generacjaValue_textView_pokemon.text = "Kanto"
                    2 -> generacjaValue_textView_pokemon.text = "Johoto"
                    3 -> generacjaValue_textView_pokemon.text = "Hoenn"
                    4 -> generacjaValue_textView_pokemon.text = "Sinnoh"
                    5 -> generacjaValue_textView_pokemon.text = "Unova"
                    6 -> generacjaValue_textView_pokemon.text = "Kalos"
                    7 -> generacjaValue_textView_pokemon.text = "Alola"
                    8 -> generacjaValue_textView_pokemon.text = "Galar"
                    else -> generacjaValue_textView_pokemon.text = "Error_setting_generation_name"
                }

                //TWORZENIE LINKU DO OBRAZKA POKEMONA
                var imageurl = ""
                when {
                    id<9 -> imageurl = "00$id-$pokemonNameToLowercase.png"
                    id<100 -> imageurl = "0$id-$pokemonNameToLowercase.png"
                    id<1000 -> imageurl = "$id-$pokemonNameToLowercase.png"
                }
                Picasso.get().load("https://raw.githubusercontent.com/TheArtificial/pokemon-icons/master/_icons/PNG/1x/$imageurl").into(pokemon_imageView_pokemon)

                //NAZWA TYPU POKEMONA
                typValue_textView_pokemon.text = "NOT IMPLEMENTED"

                //CZY DOSTEPNY W POKEDEXIE
                when (pokemonAvailable){
                    0 -> czyDostepnyValue_textView_pokemon.text = "No"
                    1 -> czyDostepnyValue_textView_pokemon.text = "Yes"
                    else -> czyDostepnyValue_textView_pokemon.text = "Error_setting_pokemon_available"
                }
                //CZY DOSTEPNY W POKEDEXIE
                when (pokemonShiny){
                    0 -> czyShinyValue_textView_pokemon.text = "No"
                    1 -> czyShinyValue_textView_pokemon.text = "Yes"
                    else -> czyShinyValue_textView_pokemon.text = "Error_setting_pokemon_available"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TEST","ERROR POBIERANIA Z BAZY DLA POKEMONOW")
            }
        })
        //=====END OF POKEMON REFERENCE=====
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

    //POWRACANIE DO INTENTU PO NACISNIECIU KLAWISZA POWROTU
    override fun onBackPressed(){
        super.onBackPressed()
        val returnIntent = Intent(this, PokedexActivity::class.java)
        startActivity(returnIntent)
        overridePendingTransition(0, 0)
    }
}