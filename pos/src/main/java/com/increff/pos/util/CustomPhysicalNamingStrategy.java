package com.increff.pos.util;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

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
        StringBuilder snakeCaseName = new StringBuilder();
        for (int i = 0; i < camelCaseName.length(); i++) {
            char currentChar = camelCaseName.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                if (i > 0) {
                    snakeCaseName.append("_");
                }
                snakeCaseName.append(Character.toLowerCase(currentChar));
            } else {
                snakeCaseName.append(currentChar);
            }
        }
        return snakeCaseName.toString();
    }
}
