package no.freshify.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/publications")
public class PublicationController {

    @GetMapping("/dealerfront")
    public ResponseEntity<String> getDealerFront(
            @RequestParam("r_lat") Double rLat,
            @RequestParam("r_lng") Double rLng,
            @RequestParam("r_radius") Double rRadius,
            @RequestParam("limit") Integer limit,
            @RequestParam("order_by") String orderBy,
            @RequestParam("types") String types,
            @RequestParam("business_category_ids") String businessCategoryIds) {

        String url = "https://etilbudsavis.no/api/squid/v2/dealerfront?r_lat=" + rLat +
                "&r_lng=" + rLng +
                "&r_radius=" + rRadius +
                "&limit=" + limit +
                "&order_by=" + orderBy +
                "&types=" + types +
                "&business_category_ids=" + businessCategoryIds;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            ArrayNode catalogsNode = objectMapper.createArrayNode();

            for (JsonNode node : rootNode) {
                ArrayNode catalogs = (ArrayNode) node.get("catalogs");
                String brandName = node.get("dealer").get("name").asText();

                for (JsonNode catalog : catalogs) {
                    String label = catalog.get("label").asText();

                    if (!label.contains(brandName)) {
                        String newLabel = brandName + " " + label;
                        ((ObjectNode) catalog).put("label", newLabel);
                    }
                }

                catalogsNode = catalogsNode.addAll(catalogs);
            }

            return ResponseEntity.ok(objectMapper.writeValueAsString(catalogsNode));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the response");
        }
    }
}