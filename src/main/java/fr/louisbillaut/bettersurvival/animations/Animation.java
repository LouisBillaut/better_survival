package fr.louisbillaut.bettersurvival.animations;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public abstract class Animation {
    protected Main instance;
    protected BukkitTask animation;

    public Animation(){}
    public Animation(Main instance) {
        this.instance = instance;
    }

    public abstract void startAnimation(Player player);

    public void stopAnimation() {
        animation.cancel();
    }
}
