package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.animations.Animation;
import fr.louisbillaut.bettersurvival.pets.Pet;

import java.util.ArrayList;
import java.util.List;

public class Cosmetics {
    List<Pet> pets = new ArrayList<>();
    List<Animation> animations = new ArrayList<>();

    public Cosmetics() {}

    public boolean addPet(Pet pet) {
        return pets.add(pet);
    }
    public boolean addAnimation(Animation animation) {
        return animations.add(animation);
    }

    public List<Pet> getPets() {
        return pets;
    }
}
