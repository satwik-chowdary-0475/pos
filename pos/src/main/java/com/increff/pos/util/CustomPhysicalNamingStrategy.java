package com.increff.pos.util;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        if (name == null) {
            return null;
        }
        String snakeCaseName = convertCamelToSnakeCase(name.getText());
        return Identifier.toIdentifier(snakeCaseName);
    }

    private String convertCamelToSnakeCase(String camelCaseName) {
        return Arrays.stream(camelCaseName.split("(?=[A-Z])"))
                .map(String::toLowerCase)
                .collect(Collectors.joining("_"));
    }

}
