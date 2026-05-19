package com.corntrol.corntrol.domain.analysis.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class DoubleListConverter implements AttributeConverter<List<Double>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Double> attribute) {
        try {
            return attribute != null ? mapper.writeValueAsString(attribute) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Embedding 데이터를 JSON으로 변환하는 데 실패했습니다.", e);
        }
    }

    @Override
    public List<Double> convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? mapper.readValue(dbData, new TypeReference<>() {}) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 데이터를 Embedding List로 변환하는 데 실패했습니다.", e);
        }
    }
}