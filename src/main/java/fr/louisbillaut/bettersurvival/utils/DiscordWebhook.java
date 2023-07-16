package fr.louisbillaut.bettersurvival.utils;

import java.io.IOException;
import java.util.List;

import fr.louisbillaut.bettersurvival.game.Shop;
import fr.louisbillaut.bettersurvival.game.Trade;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class DiscordWebhook {
    private final String url;

    public DiscordWebhook(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String formatItemStackList(List<ItemStack> itemStackList) {
        StringBuilder sb = new StringBuilder();

        int size = itemStackList.size();
        for (int i = 0; i < size; i++) {
            ItemStack itemStack = itemStackList.get(i);
            String itemName = itemStack.getType().name();
            int quantity = itemStack.getAmount();

            sb.append(itemName).append(" x ").append(quantity);

            if (i < size - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    public void send(String playerName, Shop shop, Trade trade) throws IOException {
        if(url == null || url.equals(" ") || url.equals("")) return;
        if(shop.getLocation() == null) return;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String jsonContent = "{\n" +
                "  \"content\": \"🚨 Nouveau trade créé par: **" + playerName + "**\",\n" +
                "  \"embeds\": [\n" +
                "    {\n" +
                "      \"title\": \"" + formatItemStackList(trade.getItemsToExchange()) + " ➡ " + trade.getItemsToBuy().getType().name() + " x " + trade.getItemsToBuy().getAmount() + " (max: " + trade.getMaxTrade() +")\",\n" +
                "      \"description\": \"🛍️ disponible au shop: **" + shop.getName() + "**\\n\\n 📍coordonnées: **x: " + shop.getLocation().getX() + ", y: " + shop.getLocation().getY() + ", z: " + shop.getLocation().getZ() + "**\",\n" +
                "      \"color\": 5814783\n" +
                "    }\n" +
                "  ],\n" +
                "  \"attachments\": []\n" +
                "}";

        RequestBody requestBody = RequestBody.create(jsonContent, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Bukkit.getLogger().info("http error - Unexpected response code: " + response);
            }

        } finally {
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
        }
    }
}
