package org.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.tools.json.JsonComparer;

public class Main {
    public static void main(String[] args) {
        try {

            String one = """
                   [{"address": {"city": "Kushtia"}}, {"address": {"city": "Kushtia"}}]
                   """;

            String two = """
                    [{"address": {"city": "Kushtia"}}, {"address": {"city": "Kushtia"}}]
                    """;

            String compare = JsonComparer.compare(one, two);

            System.out.println(compare);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}