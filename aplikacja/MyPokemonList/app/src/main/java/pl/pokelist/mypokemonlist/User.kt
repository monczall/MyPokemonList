package pl.pokelist.mypokemonlist

class User(val uid: String, val userNickname: String, val userEmail: String, val userProfileImageUrl: String, val team: String, val pokemonCount: Int, val luckyCount: Int, val shinyCount: Int, val totalCount: Int) {
    constructor() : this("","", "", "", "", -1, -1, -1, -1)
}