package tw.commonground.backend.service.internal.profile.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter
public class ListToStringConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return attribute == null ? null : String.join(",", attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String columnValue) {
        if (columnValue == null || columnValue.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(columnValue.split(","));
    }
}
