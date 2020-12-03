package pl.pokelist.mypokemonlist

class Pokemon(val pokemon_id: Int, val pokemon_name: String, val pokemon_generation: Int, val pokemon_Available: Int, val pokemon_Shiny: Int){
    constructor() : this(-1,"",-1,-1,-1)
}