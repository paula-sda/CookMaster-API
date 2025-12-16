package com.cookmaster.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CookMasterSDK {
    
    private final String apiKey;
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public static class CookMasterException extends RuntimeException {
        private final int statusCode;
        public CookMasterException(String message, int statusCode) { super(message); this.statusCode = statusCode; }
        public int getStatusCode() { return statusCode; }
    }

    public CookMasterSDK(String apiKey) { this(apiKey, "https://api.cookmaster.com/v1"); }

    public CookMasterSDK(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private <T> T executeRequest(HttpRequest request, TypeReference<T> type) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                String msg = "Error " + response.statusCode();
                try {
                    Map<String,Object> error = objectMapper.readValue(response.body(), new TypeReference<Map<String,Object>>(){});
                    msg = (String) error.getOrDefault("message", msg);
                } catch (Exception ignored){}
                throw new CookMasterException(msg, response.statusCode());
            }
            if (response.body()==null || response.body().isEmpty()) return null;
            return objectMapper.readValue(response.body(), type);
        } catch (IOException | InterruptedException e) {
            throw new CookMasterException("Error de conexión: "+e.getMessage(), 0);
        }
    }

    private HttpRequest.Builder createRequestBuilder(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + endpoint))
                .header("X-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .header("User-Agent", "CookMaster-Java-SDK/1.0");
    }

    // ===================== Endpoints =====================
    public List<Map<String,Object>> getRecetasTodas(int page, int limit){
        if(page<1) page=1; if(limit<1||limit>50) limit=50;
        String endpoint = String.format("recetas/todas?page=%d&limit=%d", page, limit);
        HttpRequest request = createRequestBuilder(endpoint).GET().build();
        return executeRequest(request, new TypeReference<List<Map<String,Object>>>(){});
    }

    public Map<String,Object> getReceta(int id){
        if(id<=0) throw new IllegalArgumentException("ID inválido");
        HttpRequest request = createRequestBuilder("recetas/"+id).GET().build();
        return executeRequest(request, new TypeReference<Map<String,Object>>(){});
    }

    public Map<String,String> eliminarReceta(int id){
        if(id<=0) throw new IllegalArgumentException("ID inválido");
        HttpRequest request = createRequestBuilder("recetas/"+id).DELETE().build();
        return executeRequest(request, new TypeReference<Map<String,String>>(){});
    }

    public Map<String,Object> crearReceta(Map<String,Object> receta){
        List<String> required = Arrays.asList("nombre","categoria","ingredientes","pasos");
        List<String> missing = new ArrayList<>();
        for(String r : required) if(!receta.containsKey(r)) missing.add(r);
        if(!missing.isEmpty()) throw new IllegalArgumentException("Faltan campos: "+String.join(",",missing));
        if(!(receta.get("ingredientes") instanceof List)) throw new IllegalArgumentException("'ingredientes' debe ser lista");
        if(!(receta.get("pasos") instanceof List)) throw new IllegalArgumentException("'pasos' debe ser lista");
        if(((List<?>)receta.get("ingredientes")).size()>20) throw new IllegalArgumentException("Máx 20 ingredientes");
        try{
            String body = objectMapper.writeValueAsString(receta);
            HttpRequest request = createRequestBuilder("recetas").POST(HttpRequest.BodyPublishers.ofString(body)).build();
            return executeRequest(request, new TypeReference<Map<String,Object>>(){});
        }catch(IOException e){ throw new CookMasterException("Error al serializar receta: "+e.getMessage(),0);}
    }

    // ===================== Ejemplo uso =====================
    public static void main(String[] args){
        CookMasterSDK api = new CookMasterSDK("TU_API_KEY");
        try{
            System.out.println(api.getRecetasTodas(1,10));
        }catch(CookMasterException e){
            System.err.println("Error API: "+e.getMessage()+" (Código "+e.getStatusCode()+")");
        }
    }
}
