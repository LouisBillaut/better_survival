package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.animations.Animation;
import fr.louisbillaut.bettersurvival.pets.Pet;
import org.bukkit.configuration.ConfigurationSection;

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

    public List<Animation> getAnimations() {
        return animations;
    }

    public void saveToConfig(ConfigurationSection config) {
        List<String> configPets = new ArrayList<>();
        for(Pet p: pets) {
            configPets.add(p.getName());
        }
        config.set("pets", configPets);

        List<String> configAnimations = new ArrayList<>();
        for(Animation p: animations) {
            configAnimations.add(p.getName());
        }
        config.set("animations", configAnimations);
    }

    public void loadFromConfig(Main instance, ConfigurationSection config) {
        if (config.contains("pets")) {
            List<String> configPets = config.getStringList("pets");
            for (String name: configPets) {
                Pet p = Pet.getPetFromOriginalName(name);
                if (p == null) continue;
                pets.add(p);
            }
        }
        if (config.contains("animations")) {
            List<String> configAnimations = config.getStringList("animations");
            for (String name: configAnimations) {
                Animation p = Animation.getAnimationFromOriginalName(name);
                if (p == null) continue;
                animations.add(p);
            }
        }
    }
}
