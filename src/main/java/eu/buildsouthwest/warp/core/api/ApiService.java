package eu.buildsouthwest.warp.core.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ApiService {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final String baseUrl;
    private final String apiKey;

    public ApiService(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public CompletableFuture<Optional<JsonObject>> fetchCoords(double x, double z) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/projection/toGeo?mcpos:" + URLEncoder.encode(String.valueOf(x), StandardCharsets.UTF_8) + "," + URLEncoder.encode(String.valueOf(z), StandardCharsets.UTF_8)))
                .timeout(Duration.ofSeconds(8))
                .GET().build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if(response.statusCode() == 404) return Optional.<JsonObject>empty();
                    if (response.statusCode() != 200) {
                        throw new CompletionException(
                                new IOException("BTE API returned status code " + response.statusCode())
                        );
                    }

                    return Optional.of(JsonParser.parseString(response.body()).getAsJsonObject());
                });
    }
}
