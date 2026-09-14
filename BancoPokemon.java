import java.util.ArrayList;
import java.util.List;

public class BancoPokemon {
    public static List<Pokemon> criarBanco(){
        List<Pokemon> PossivelPokemon = new ArrayList<>();

        PossivelPokemon.add(new PokemonEvolutivo("Bulbasaur", 1, "Overgrow", TipoPokemon.GRAMA, 1, ItemEvolutivo.DOCE, 1));
        PossivelPokemon.add(new PokemonEvolutivo("Ivysaur", 2, "Chlorophyll", TipoPokemon.GRAMA, 2, ItemEvolutivo.PEDRA, 1));
        PossivelPokemon.add(new PokemonEvolutivo("Venusaur", 3, "Frenzy Plant", TipoPokemon.GRAMA, 3, null, 1));
        
        PossivelPokemon.add(new PokemonEvolutivo("Charmander", 1, "Blaze", TipoPokemon.FOGO, 4, ItemEvolutivo.DOCE, 2));
        PossivelPokemon.add(new PokemonEvolutivo("Charmeleon", 2, "Fire Blast", TipoPokemon.FOGO, 5, ItemEvolutivo.PEDRA, 2));
        PossivelPokemon.add(new PokemonEvolutivo("Charizard", 3, "Flame Burst", TipoPokemon.FOGO, 6, null, 2));

        PossivelPokemon.add(new PokemonEvolutivo("Squirtle", 1, "Bubble", TipoPokemon.AGUA, 7, ItemEvolutivo.DOCE, 3));
        PossivelPokemon.add(new PokemonEvolutivo("Wartortle", 2, "Water Gun", TipoPokemon.AGUA, 8, ItemEvolutivo.PEDRA, 3));
        PossivelPokemon.add(new PokemonEvolutivo("Blastoise", 3, "Surf", TipoPokemon.AGUA, 9, null, 3));

        PossivelPokemon.add(new PokemonEvolutivo("Caterpie", 1, "Tackle", TipoPokemon.INSETO, 10, ItemEvolutivo.DOCE, 4));
        PossivelPokemon.add(new PokemonEvolutivo("Metapod", 2, "Harden", TipoPokemon.INSETO, 11, ItemEvolutivo.PEDRA, 4));
        PossivelPokemon.add(new PokemonEvolutivo("Butterfree", 3, "Silver Wind", TipoPokemon.INSETO, 12, null, 4));

        PossivelPokemon.add(new PokemonEvolutivo("Weedle", 1, "Poison Sting", TipoPokemon.INSETO, 13, ItemEvolutivo.DOCE, 5));
        PossivelPokemon.add(new PokemonEvolutivo("Kakuna", 2, "Harden", TipoPokemon.INSETO, 14, ItemEvolutivo.PEDRA, 5));
        PossivelPokemon.add(new PokemonEvolutivo("Beedrill", 3, "Twineedle", TipoPokemon.INSETO, 15, null, 5));

        PossivelPokemon.add(new PokemonEvolutivo("Pidgey", 1, "Wing Attack", TipoPokemon.VOADOR, 16, ItemEvolutivo.DOCE, 6 ));
        PossivelPokemon.add(new PokemonEvolutivo("Pidgeotto", 2, "Whirlwind", TipoPokemon.VOADOR, 17, ItemEvolutivo.PEDRA, 6));
        PossivelPokemon.add(new PokemonEvolutivo("Pidgeot", 3, "Fly", TipoPokemon.VOADOR, 18, null, 6));

        PossivelPokemon.add(new PokemonEvolutivo("Rattata", 1, "Hyper Fang", TipoPokemon.NORMAL, 19, ItemEvolutivo.DOCE, 7));
        PossivelPokemon.add(new PokemonEvolutivo("Raticate", 2, "Crunch", TipoPokemon.NORMAL, 20, null, 7));

        PossivelPokemon.add(new PokemonEvolutivo("Spearow", 1, "Drill Peck", TipoPokemon.VOADOR, 21, ItemEvolutivo.DOCE, 8));
        PossivelPokemon.add(new PokemonEvolutivo("Fearow", 2, "Aerial Ace", TipoPokemon.VOADOR, 22, null, 8));

        PossivelPokemon.add(new PokemonEvolutivo("Ekans", 1, "Sludge Bomb", TipoPokemon.VENENOSO, 23, ItemEvolutivo.DOCE, 9));
        PossivelPokemon.add(new PokemonEvolutivo("Arbok", 2, "Poison Fang", TipoPokemon.VENENOSO, 24, null, 9));

        PossivelPokemon.add(new PokemonEvolutivo("Pikachu", 1, "Thunder", TipoPokemon.ELETRICO, 25, ItemEvolutivo.DOCE, 10));
        PossivelPokemon.add(new PokemonEvolutivo("Raichu", 2, "Thunderbolt", TipoPokemon.ELETRICO, 26, null, 10));

        PossivelPokemon.add(new PokemonEvolutivo("Sandshrew", 1, "Earthquake", TipoPokemon.TERRESTRE, 27, ItemEvolutivo.DOCE, 11));
        PossivelPokemon.add(new PokemonEvolutivo("Sandslash", 2, "Sand Tomb", TipoPokemon.TERRESTRE, 28, null, 11));

        PossivelPokemon.add(new PokemonEvolutivo("Nidoran(fem)", 1, "Sludge Bomb", TipoPokemon.VENENOSO, 29, ItemEvolutivo.DOCE, 12));
        PossivelPokemon.add(new PokemonEvolutivo("Nidorina", 2, "Bite", TipoPokemon.VENENOSO, 30, ItemEvolutivo.PEDRA, 12));
        PossivelPokemon.add(new PokemonEvolutivo("Nidoqueen", 3, "Earthquake", TipoPokemon.TERRESTRE, 31, null, 12));

        PossivelPokemon.add(new PokemonEvolutivo("Nidoran(masc)", 1, "Sludge Bomb", TipoPokemon.VENENOSO, 32, ItemEvolutivo.DOCE, 13));
        PossivelPokemon.add(new PokemonEvolutivo("Nidorino", 2, "Bite", TipoPokemon.VENENOSO, 33, ItemEvolutivo.PEDRA, 13));
        PossivelPokemon.add(new PokemonEvolutivo("Nidoking", 3, "Earthquake", TipoPokemon.TERRESTRE, 34, null, 13));

        PossivelPokemon.add(new PokemonEvolutivo("Oddish", 1, "Giga Drain", TipoPokemon.GRAMA, 43, ItemEvolutivo.DOCE, 14));
        PossivelPokemon.add(new PokemonEvolutivo("Gloom", 2, "Sludge Bomb", TipoPokemon.VENENOSO, 44, ItemEvolutivo.PEDRA, 14));
        PossivelPokemon.add(new PokemonEvolutivo("Vileplume", 3, "Sunny Day", TipoPokemon.VENENOSO, 45, null, 14));

        PossivelPokemon.add(new PokemonEvolutivo("Poliwag", 1, "Hypnosis", TipoPokemon.AGUA, 60, ItemEvolutivo.DOCE, 15));
        PossivelPokemon.add(new PokemonEvolutivo("Poliwhirl", 2, "Water Pulse", TipoPokemon.AGUA, 61, ItemEvolutivo.PEDRA, 15));
        PossivelPokemon.add(new PokemonEvolutivo("Poliwrath", 3, "Brick Break", TipoPokemon.LUTADOR, 62, null, 15));

        PossivelPokemon.add(new PokemonEvolutivo("Abra", 1, "Confusion", TipoPokemon.PSIQUICO, 63, ItemEvolutivo.DOCE, 16));
        PossivelPokemon.add(new PokemonEvolutivo("Kadabra", 2, "Psychic", TipoPokemon.PSIQUICO, 64, ItemEvolutivo.PEDRA, 16));
        PossivelPokemon.add(new PokemonEvolutivo("Alakazam", 3, "Future Sight", TipoPokemon.PSIQUICO, 65, null, 16));

        PossivelPokemon.add(new PokemonEvolutivo("Machop", 1, "Low Kick", TipoPokemon.LUTADOR, 66, ItemEvolutivo.DOCE, 17));
        PossivelPokemon.add(new PokemonEvolutivo("Machoke", 2, "Karate Chop", TipoPokemon.LUTADOR, 67, ItemEvolutivo.PEDRA, 17));
        PossivelPokemon.add(new PokemonEvolutivo("Machamp", 3, "Cross Chop", TipoPokemon.LUTADOR, 68, null, 17));

        PossivelPokemon.add(new PokemonEvolutivo("Slowpoke", 1, "Confusion   ", TipoPokemon.PSIQUICO, 79, ItemEvolutivo.DOCE, 18));
        PossivelPokemon.add(new PokemonEvolutivo("Slowbro", 2, "Amnesia", TipoPokemon.PSIQUICO, 80, null, 18));

        PossivelPokemon.add(new PokemonEvolutivo("Gastly", 1, "Night Shade", TipoPokemon.FANTASMA, 92, ItemEvolutivo.DOCE, 19));
        PossivelPokemon.add(new PokemonEvolutivo("Haunter", 2, "Curse", TipoPokemon.FANTASMA, 93, ItemEvolutivo.PEDRA, 19));
        PossivelPokemon.add(new PokemonEvolutivo("Gengar", 3, "Shadow Punch", TipoPokemon.FANTASMA, 94, null, 19));

        PossivelPokemon.add(new PokemonEvolutivo("Exeggcute", 1, "Confusion", TipoPokemon.GRAMA, 102, ItemEvolutivo.DOCE, 20));
        PossivelPokemon.add(new PokemonEvolutivo("Exeggutor", 2, "Solar Beam", TipoPokemon.PSIQUICO, 103, null, 20));

        PossivelPokemon.add(new PokemonEvolutivo("Magikarp", 1, "Splash", TipoPokemon.AGUA, 129, ItemEvolutivo.DOCE, 22));
        PossivelPokemon.add(new PokemonEvolutivo("Gyarados", 2, "Surf", TipoPokemon.AGUA, 130, null, 22));

        PossivelPokemon.add(new Pokemon("Lapras", 1, "Ice Beam", TipoPokemon.AGUA, 131, null));

        PossivelPokemon.add(new Pokemon("Porygon", 1, "Tri Attack", TipoPokemon.NORMAL, 137, null));

        PossivelPokemon.add(new Pokemon("Snorlax", 1, "Body Slam", TipoPokemon.NORMAL, 143, null));

        PossivelPokemon.add(new PokemonEvolutivo("Dratini", 1, "Dragon Rage", TipoPokemon.DRAGAO, 147, ItemEvolutivo.DOCE, 21));
        PossivelPokemon.add(new PokemonEvolutivo("Dragonair", 2, "Dragon Claw", TipoPokemon.DRAGAO, 148, ItemEvolutivo.PEDRA, 21));
        PossivelPokemon.add(new PokemonEvolutivo("Dragonite", 3, "Outrage", TipoPokemon.DRAGAO, 149, null, 21));

        PossivelPokemon.add(new Pokemon("Mewtwo", 1, "Psychic", TipoPokemon.PSIQUICO, 150, null));

        PossivelPokemon.add(new Pokemon("Mew", 1, "Psychic", TipoPokemon.PSIQUICO, 151, null));


        return PossivelPokemon;

    }    
}
