package sep490.com.example.hrms_backend.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class GenderDeserializer extends JsonDeserializer<Gender> {

    @Override
    public Gender deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim().toUpperCase();
        switch (value) {
            case "NAM":
                return Gender.NAM;
            case "NỮ":
            case "NU":
            case "NỬ":
                return Gender.NỮ;
            default:
                throw new IllegalArgumentException("Giới tính không hợp lệ: " + p.getText());
        }
    }
}
