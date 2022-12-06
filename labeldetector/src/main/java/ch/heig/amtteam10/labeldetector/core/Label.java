package ch.heig.amtteam10.labeldetector.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A label represents a result of a image labellisation analysis
 *
 * @param name       The name of the label
 * @param confidence The confidence of the label (between 0 and 1)
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public record Label(String name, float confidence) {
    public static String toJSONArray(Label[] labels) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, labels);
        return out.toString();
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f%%)", name, confidence * 100);
    }

    public String toJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
