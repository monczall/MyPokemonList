package pl.pokelist.mypokemonlist

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_pokedex.*
import kotlinx.android.synthetic.main.pokemon_pokedex.view.*


class PokedexActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokedex)

        pokedex_recyclerView_pokedex.adapter = adapter

        val appSettingsPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingsPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        verifyUserIsLoggedIn()

        fetchPokemons()

    }

    companion object{
        val POKEMON_KEY = "POKEMON_KEY"
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
    private fun fetchPokemons() {
        val uid = FirebaseAuth.getInstance().uid
        val pokemonRef = FirebaseDatabase.getInstance().getReference("/pokemons")
        var pokemonImageUrl = "000.png"
        pokemonRef.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val pokemon = snapshot.getValue(Pokemon::class.java)
                if(pokemon != null){
                    val pokemonUserInfoRef = FirebaseDatabase.getInstance().getReference("/pokemon_user_info/$uid/${pokemon.pokemon_id}")
                    pokemonUserInfoRef.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val pokemonUserInfo = snapshot.getValue(PokemonUserInfo::class.java)
                            if(pokemonUserInfo != null){
                                    when(pokemon.pokemon_id){
                                        in 1..9 -> {
                                            pokemonImageUrl = "00${pokemon.pokemon_id}-${pokemon.pokemon_name.toLowerCase()}.png"
                                            adapter.add(PokemonItem(pokemon, pokemonImageUrl, pokemonUserInfo.inPokedex, pokemonUserInfo.inLucky, pokemonUserInfo.inShiny))
                                        }
                                        in 10..99 -> {
                                            pokemonImageUrl = "0${pokemon.pokemon_id}-${pokemon.pokemon_name.toLowerCase()}.png"
                                            adapter.add(PokemonItem(pokemon, pokemonImageUrl, pokemonUserInfo.inPokedex, pokemonUserInfo.inLucky, pokemonUserInfo.inShiny))
                                        }
                                        in 100..820 -> {
                                            pokemonImageUrl = "${pokemon.pokemon_id}-${pokemon.pokemon_name.toLowerCase()}.png"
                                            adapter.add(PokemonItem(pokemon, pokemonImageUrl, pokemonUserInfo.inPokedex, pokemonUserInfo.inLucky, pokemonUserInfo.inShiny))
                                        }
                                    }
                            }
                            adapter.setOnItemClickListener { item, view ->
                                val intent = Intent(view.context, PokemonActivity::class.java)
                                intent.putExtra(POKEMON_KEY, view.tag.toString())
                                startActivity(intent)
                                overridePendingTransition(0, 0);
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    class PokemonItem(private val pokemon: Pokemon, private val imageurl: String, private val inPokedex: Int, private val inLucky: Int, private val inShiny: Int): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            if(inPokedex == 1 && inLucky == 1 && inShiny == 1){
                // POKEDEX . LUCKY . SHINY
                viewHolder.itemView.setBackgroundResource(R.color.pokemonLucky)
            }else if(inPokedex == 1 && inLucky == 1 && inShiny == 0){
                // POKEDEX . LUCKY . NOT SHINY
                viewHolder.itemView.setBackgroundResource(R.color.pokemonLucky)
            }else if(inPokedex == 1 && inLucky == 0 && inShiny == 1){
                // POKEDEX . NOT LUCKY . SHINY
                viewHolder.itemView.setBackgroundResource(R.color.pokemonRegistered)
            }else if(inPokedex == 1 && inLucky == 0 && inShiny == 0){
                // POKEDEX . NOT LUCKY . NOT SHINY
                viewHolder.itemView.setBackgroundResource(R.color.pokemonRegistered)
            }else{
                // NOT POKEDEX . NOT LUCKY . NOT SHINY
                viewHolder.itemView.setBackgroundResource(R.color.pokemonNotRegistered)
                //viewHolder.itemView.textView
            }
            viewHolder.itemView.tag = position
            viewHolder.itemView.pokemonId_textView_pokedex.text = pokemon.pokemon_id.toString()
            viewHolder.itemView.pokemonName_textView_pokedex.text = pokemon.pokemon_name
            Picasso.get().load("https://raw.githubusercontent.com/TheArtificial/pokemon-icons/master/_icons/PNG/1x/$imageurl").into(viewHolder.itemView.pokemonImage_imageView_pokedex)
        }
        override fun getLayout(): Int {
            return R.layout.pokemon_pokedex
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_leaderboards -> {
                val intent = Intent (this, LeaderBoardsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            R.id.menu_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            R.id.menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            R.id.menu_sign_out -> {
                Firebase.auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}