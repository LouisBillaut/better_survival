package fr.louisbillaut.bettersurvival.utils;

import java.io.IOException;

import fr.louisbillaut.bettersurvival.game.Shop;
import fr.louisbillaut.bettersurvival.game.Trade;
import okhttp3.*;
import org.bukkit.Bukkit;

public class DiscordWebhook {
    private final String url;
    private final OkHttpClient httpClient;

    public DiscordWebhook(String url) {
        this.url = url;
        this.httpClient = new OkHttpClient();
    }

    public String getUrl() {
        return url;
    }

    public void send(String playerName, Shop shop, Trade trade) throws IOException {
        if(url == null || url.equals(" ") || url.equals("")) return;
        if(shop.getLocation() == null) return;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String jsonContent = "{\n" +
                "  \"content\": \"Nouveau trade créé par: " + playerName + "\",\n" +
                "  \"embeds\": [\n" +
                "    {\n" +
                "      \"title\": \"" + trade.getItemsToExchange().toString() + " ➡\uFE0F " + trade.getItemsToBuy().toString() + " (max: " + trade.getMaxTrade() +")\",\n" +
                "      \"description\": \"disponible au shop: " + shop.getName() + "\\n\\ncoordonnées: x: " + shop.getLocation().getX() + ", y: " + shop.getLocation().getY() + ", z: " + shop.getLocation().getZ() + "\",\n" +
                "      \"color\": 5814783\n" +
                "    }\n" +
                "  ],\n" +
                "  \"attachments\": []\n" +
                "}";

        Bukkit.getLogger().info(jsonContent);
        RequestBody requestBody = RequestBody.create(jsonContent, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bukkit.getLogger().info("error http: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                response.close();
            }
        });
    }
}
