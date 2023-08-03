package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.pets.Pet;

import java.util.ArrayList;
import java.util.List;

public class Cosmetics {
    List<Pet> pets = new ArrayList<>();

    public Cosmetics() {}

    public boolean addPet(Pet pet) {
        return pets.add(pet);
    }

    public List<Pet> getPets() {
        return pets;
    }
}
