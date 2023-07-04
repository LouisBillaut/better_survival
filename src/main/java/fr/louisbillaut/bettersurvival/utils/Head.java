package fr.louisbillaut.bettersurvival.utils;

import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import org.bukkit.Material;

public class Head {
    public static ItemStack getCustomHead(String texture) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (texture != null && !texture.isEmpty()) {
            try {
                Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
                Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
                Object gameProfile = gameProfileClass.getConstructor(UUID.class, String.class).newInstance(UUID.randomUUID(), null);
                Field propertiesField = gameProfileClass.getDeclaredField("properties");
                propertiesField.setAccessible(true);
                Object properties = propertiesField.get(gameProfile);
                Method putMethod = properties.getClass().getMethod("put", Object.class, Object.class);
                Object textureProperty = propertyClass.getConstructor(String.class, String.class).newInstance("textures", texture);
                putMethod.invoke(properties, "textures", textureProperty);
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, gameProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        head.setItemMeta(meta);
        return head;
    }
}
