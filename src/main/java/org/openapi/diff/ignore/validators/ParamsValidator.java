package org.openapi.diff.ignore.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import org.openapi.diff.ignore.models.validations.ValidationResult;
import org.openapi.diff.ignore.models.validations.enums.RequestSupport;
import org.openapi.diff.ignore.models.validations.enums.ValidationStatus;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class ParamsValidator implements Validator {


    private final ValidationResult result = new ValidationResult();
    private JsonNode params;

    @Override
    public boolean validate() {
        for (Iterator<Map.Entry<String, JsonNode>> it = params.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> paramsScope = it.next();
            if (!(paramsScope.getValue() instanceof ArrayNode) && !paramsScope.getValue().asText().equals("$")) {
                result.setMessage(String.format("the method \"%s\" not a valid object parameters method", paramsScope.getKey()));
                result.setValidationStatus(ValidationStatus.BAD_IGNORE_FILE);
                return false;
            }
        }

        return true;
    }
}
