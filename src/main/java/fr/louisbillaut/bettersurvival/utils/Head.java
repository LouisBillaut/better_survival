package fr.louisbillaut.bettersurvival.utils;

import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import org.bukkit.Material;

public class Head {
    public static String villagerHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI2ZWMxY2ExODViNDdhYWQzOWY5MzFkYjhiMGE4NTAwZGVkODZhMTI3YTIwNDg4NmVkNGIzNzgzYWQxNzc1YyJ9fX0=";
    public static String quartzArrowRight = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0=";
    public static String world = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM4Y2YzZjhlNTRhZmMzYjNmOTFkMjBhNDlmMzI0ZGNhMTQ4NjAwN2ZlNTQ1Mzk5MDU1NTI0YzE3OTQxZjRkYyJ9fX0=";
    public static String quartzPlus = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ==";
    public static String quartzMinus = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ2YjEyOTNkYjcyOWQwMTBmNTM0Y2UxMzYxYmJjNTVhZTVhOGM4ZjgzYTE5NDdhZmU3YTg2NzMyZWZjMiJ9fX0=";
    public static String quartzArrowLeft = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzNlOTE5MTlkYjBhY2VmZGMyNzJkNjdmZDg3YjRiZTg4ZGM0NGE5NTg5NTg4MjQ0NzRlMjFlMDZkNTNlNiJ9fX0=";
    public static String ghostLava = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQ0Zjc4YzE1YjM1ZDQ3OWRmYTE3NjRmZWJlMTgzMjdjZDk3NjJlNDc0ZDZhYWVmYmY5OWQ3MDNlZDg1Njg1MCJ9fX0=";
    public static String snifferEgg = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODJjNDAzM2EzMmEwMTM1ZTk3YzJiNWJjOWRiNGZkZWEzM2FkMDhlMGY4ZjNkMTY4YzcxNzkyZjBmOWZhYmFjIn19fQ==";
    public static String rainCloud = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY2YjEwYmY2ZWUyY2Q3ZTNhYzk2ZDk3NDllYTYxNmFhOWM3MzAzMGJkY2FlZmZhZWQyNDllNTVjODQ5OTRhYyJ9fX0=";
    public static String star = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDVmNWNhYjE5OTUwODcyNWVhMmJiZTNlZDJkODRiODkxYzVkOGI3ZWU3ZWFlZGM2OWU2NzE5NmRjNmZlZTNkOSJ9fX0=";
    public static String angry = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTU3ZWUxMDIxMzIzMmM0NmJhZTc5ZWM2ZTRhYTBlMDI2ODA0NmRiZDMwMDc2MTIyNzBmMGRiN2VhNWYxNjE5YSJ9fX0=";
    public static String angel = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQ4ZDE2YWFlYTkyMTQ2NDFjYzY5YTBkMmQyNjM2N2FjM2VmNzVhNDdkZWNmZGZmZjdjN2RiNTg2NGU2YzE0ZiJ9fX0=";
    public static String happy = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTUxYzhlMjNkZGY5MTZjZjE1ZTY5NjE1ZGVhMWRjZTIwZTY5N2I5YmJmOWYyODg0MGM0MmFiOTI5NzQ1MTI4ZSJ9fX0=";
    public static String Heart = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQwZjI5MjVkZjM3ZmJkNWY4MDgyOWNhZmIzZTllOWFjMDBmNzVhOTkxYjIxMzFjMjZlMGIyMTk4ZTMwMDAxOCJ9fX0=";
    public static String Music = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJhYTNiNDc4MGI0M2M2ZmRhYThjMDc0ZDI4NzE4N2I2ZjJlNDk3NjJhMDJhNjA3MTdmYTE5NmI4MThkNjYyYyJ9fX0=";
    public static String smoke = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFiZjA3MWRkNzI4NTRkZTIzNGI0NjNhNzEwMWZiMWQwYTJmODMyOWY4NjVhZTVmNTQzOWNhZjRlMGRhOTJiOCJ9fX0=";
    public static String spell = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTAzMjdmYjM0MzE5Zjg5YWM1YWI0OGI0ZDc5MjUxZjEzZjA2N2ViZWE3ZGE1Zjg4Yjc1ZjQ3OWE3Mzg5OTI0ZSJ9fX0=";
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
