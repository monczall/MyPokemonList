package pl.pokelist.mypokemonlist

class PokemonUserInfo(var inShiny: Int, var inLucky: Int, var inPokedex: Int, var pokemon_id: Int){
    constructor() : this(-1,-1,-1,-1,)
}