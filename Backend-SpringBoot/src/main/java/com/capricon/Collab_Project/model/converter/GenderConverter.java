package com.capricon.Collab_Project.model.converter;

import com.capricon.Collab_Project.model.enums.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GenderConverter implements AttributeConverter<Gender, String> {
    @Override
    public String convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return null;
        }
        return gender.name().toUpperCase();  // PostgreSQL enum values are case-sensitive
    }

    @Override
    public Gender convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return Gender.valueOf(s.toUpperCase());  // Assumes enum values match exactly
    }
}
