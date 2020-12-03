package pl.pokelist.mypokemonlist

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_leaderboards.*
import kotlinx.android.synthetic.main.user_in_leaderboard.view.*


class LeaderBoardsActivity : AppCompatActivity() {
    public val adapter = GroupAdapter<GroupieViewHolder>()
    var active = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboards)

        val appSettingsPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingsPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        leaderboards_recyclerView_leaderboards.adapter = adapter

        verifyUserIsLoggedIn()
        fetchUsers()
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

    private fun fetchUsers(){
        val usersRef = FirebaseDatabase.getInstance().getReference("/users")
        //=====START OF USER REFERENCE=====

        usersRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    adapter.add(UserItem(user.userProfileImageUrl, user.userNickname, user.pokemonCount, user.luckyCount, user.shinyCount, user.totalCount))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if(active){
                    finish()
                    startActivity(getIntent())
                    finish()
                    overridePendingTransition(0, 0);
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        //=====END OF USER REFERENCE=====
    }
    class UserItem(private val imageUrl: String, private val nickname: String, val pokemonCount: Int, val luckyCount: Int, val shinyCount: Int, val totalCount: Int): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val pozycja = position + 1
            viewHolder.itemView.tag = pozycja
            if(pozycja == 1){
                viewHolder.itemView.prize_imageView_leaderboard.isVisible = true
                viewHolder.itemView.prize_imageView_leaderboard.setBackgroundResource(R.color.leaderboardFirst)
            }else if(pozycja == 2){
                viewHolder.itemView.prize_imageView_leaderboard.isVisible = true
                viewHolder.itemView.prize_imageView_leaderboard.setBackgroundResource(R.color.leaderboardSecond)
            }else if(pozycja == 3){
                viewHolder.itemView.prize_imageView_leaderboard.isVisible = true
                viewHolder.itemView.prize_imageView_leaderboard.setBackgroundResource(R.color.leaderboardThird)
            }
            viewHolder.itemView.id_leaderboard.text = "#$pozycja"
            Log.d("TEST", "$imageUrl")
            if(imageUrl != "NoProfileImage"){
                val transformation: Transformation = RoundedTransformationBuilder()
                        .borderColor(R.color.altText)
                        .borderWidthDp(0f)
                        .cornerRadiusDp(45f)
                        .oval(false)
                        .build()
                Picasso.get().load("$imageUrl").fit().transform(transformation).into(viewHolder.itemView.avatar_leaderboard)
            }else{
                viewHolder.itemView.avatar_leaderboard.setBackgroundResource(R.drawable.user_icon)
            }
            viewHolder.itemView.nickname_textView_leaderboard.text = nickname
            viewHolder.itemView.pokedexCount_textView_leaderboard.text = pokemonCount.toString()
            viewHolder.itemView.luckyCount_textView_leaderboard.text = luckyCount.toString()
            viewHolder.itemView.shinyCount_textView_leaderboard.text = shinyCount.toString()
            viewHolder.itemView.totalCount_textView_leaderboard.text = totalCount.toString()
        }

        override fun getLayout(): Int {
            return R.layout.user_in_leaderboard
        }
    }
    override fun onStart() {
        super.onStart()
        active = true
    }

    override fun onStop() {
        super.onStop()
        active = false
    }
    override fun onBackPressed(){
        super.onBackPressed()
        val returnIntent = Intent(this, PokedexActivity::class.java)
        startActivity(returnIntent)
        overridePendingTransition(0, 0)
    }
}