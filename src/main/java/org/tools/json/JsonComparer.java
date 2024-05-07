package org.tools.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonComparer {
    private static final Logger LOGGER = Logger.getLogger(JsonComparer.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String compare(String json1, String json2) throws JsonProcessingException {

        JsonNode jsonNode1 = mapper.readTree(json1);
        JsonNode jsonNode2 = mapper.readTree(json2);

        Map<String, Object> result1 = new HashMap<>();
        Map<String, Object> result2 = new HashMap<>();

        flatJsonNode(jsonNode1, result1, "", 0);
        flatJsonNode(jsonNode2, result2, "", 0);

        return mapper.writeValueAsString(findDifferences(result1, result2));
    }

    private static void flatJsonNode(JsonNode jsonNode, Map<String, Object> result, String parent, int index) {
        try {
            if (jsonNode.isArray()) {
                for (int i = 0; i < jsonNode.size(); i++) {
                    flatJsonNode(jsonNode.get(i), result, parent, i);
                }
            } else if (jsonNode.isObject()) {

                jsonNode.fields().forEachRemaining(entry -> {
                    if (entry.getValue().isObject()) {
                        flatJsonNode(entry.getValue(), result, entry.getKey(), index);
                    } else if (entry.getValue().isArray()) {
                        for (int i = 0; i < entry.getValue().size(); i++) {
                            flatJsonNode(entry.getValue().get(i), result, entry.getKey(), i);
                        }
                    } else {
                        putValue(entry, parent, index, result);
                    }

                    putValue(entry, parent, index, result);
                });
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
    }

    private static Map<String, Object> findDifferences(Map<String, Object> result1, Map<String, Object> result2) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : result1.entrySet()) {

            if (entry.getValue() instanceof ArrayNode || entry.getValue() instanceof ObjectNode) {
                continue;
            }

            if (!result2.containsKey(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                Object value = result2.get(entry.getKey());
                if (!value.equals(entry.getValue())) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Map.Entry<String, Object> entry : result2.entrySet()) {
            if (!result1.containsKey(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    private static void putValue(Map.Entry<String, JsonNode> entry, String parent, int index, Map<String, Object> result) {
        if (!parent.isEmpty() && index != 0) {
            result.put(STR."\{parent}_\{index}_\{entry.getKey()}", entry.getValue());
        } else if (!parent.isEmpty()) {
            result.put(STR."\{parent}_\{entry.getKey()}", entry.getValue());
        } else if (index != 0) {
            result.put(STR."\{index}_\{entry.getKey()}", entry.getValue());
        } else {
            result.put(STR."\{entry.getKey()}", entry.getValue());
        }
    }
}
