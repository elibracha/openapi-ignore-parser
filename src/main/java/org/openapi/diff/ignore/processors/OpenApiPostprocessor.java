package org.openapi.diff.ignore.processors;

import com.qdesrame.openapi.diff.model.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapi.diff.ignore.models.OpenApiIgnore;
import org.openapi.diff.ignore.models.ignore.HttpMethodIgnore;
import org.openapi.diff.ignore.models.ignore.OperationIgnore;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OpenApiPostprocessor {

    private ChangedOpenApi changedOpenApi;
    private OpenApiIgnore openApiIgnore;
    private List<ChangedOperation> changedOperationsToRemove;

    public OpenApiPostprocessor(ChangedOpenApi changedOpenApi, OpenApiIgnore openApiIgnore) {
        this.changedOpenApi = changedOpenApi;
        this.openApiIgnore = openApiIgnore;
        this.changedOperationsToRemove = new ArrayList<>();
    }

    public ChangedOpenApi process() {

        AntPathMatcher antPathMatcher = new AntPathMatcher();

        for (ChangedOperation changedOperation : this.changedOpenApi.getChangedOperations()) {


            boolean requestClear = false;
            boolean responseClear = false;
            boolean parametersClear = false;
            boolean securityClear = false;

            String pathUrl = changedOperation.getPathUrl();

            if (openApiIgnore.isValidIgnore() && openApiIgnore.getIgnore().getPaths() != null) {
                for (String path : openApiIgnore.getIgnore().getPaths().keySet()) {
                    boolean match = antPathMatcher.match(path, pathUrl);

                    if (match) {

                        HttpMethodIgnore pathOperationIgnore = openApiIgnore.getIgnore().getPaths().getPaths().get(path);

                        String httpMethod = changedOperation.getHttpMethod().name().toLowerCase();
                        OperationIgnore pathIgnore = pathOperationIgnore.checkIfIgnoreExist(httpMethod);

                        if (pathIgnore != null) {

                            if (pathIgnore.getParameters() != null) {
                                if (changedOperation.getParameters() != null) {

                                    if (changedOperation.getParameters().getChanged() != null) {
                                        List<ChangedParameter> changedParametersToRemove = new ArrayList<>();

                                        for (String ignoreParameter : pathIgnore.getParameters().getParameters()) {
                                            for (ChangedParameter changedParameter : changedOperation.getParameters().getChanged()) {
                                                if (ignoreParameter.equals(changedParameter.getName())) {
                                                    changedParametersToRemove.add(changedParameter);
                                                }
                                            }
                                        }

                                        changedOperation.getParameters().getChanged().removeAll(changedParametersToRemove);
                                    }

                                    if (changedOperation.getParameters().getIncreased() != null) {
                                        List<Parameter> increasedParametersToRemove = new ArrayList<>();

                                        for (String ignoreParameter : pathIgnore.getParameters().getParameters()) {
                                            for (Parameter increasedParameter : changedOperation.getParameters().getIncreased()) {
                                                if (ignoreParameter.equals(increasedParameter.getName())) {
                                                    increasedParametersToRemove.add(increasedParameter);
                                                }
                                            }
                                        }

                                        changedOperation.getParameters().getIncreased().removeAll(increasedParametersToRemove);
                                    }

                                    if (changedOperation.getParameters().getMissing() != null) {
                                        List<Parameter> missingParametersToRemove = new ArrayList<>();

                                        for (String ignoreParameter : pathIgnore.getParameters().getParameters()) {
                                            for (Parameter missingParameter : changedOperation.getParameters().getMissing()) {
                                                if (ignoreParameter.equals(missingParameter.getName())) {
                                                    missingParametersToRemove.add(missingParameter);
                                                }
                                            }
                                        }

                                        changedOperation.getParameters().getMissing().removeAll(missingParametersToRemove);
                                    }

                                    if (changedOperation.getParameters().getMissing().size() == 0 &&
                                            changedOperation.getParameters().getIncreased().size() == 0 &&
                                            changedOperation.getParameters().getChanged().size() == 0) {
                                        parametersClear = true;
                                    }

                                } else {
                                    parametersClear = true;
                                }
                            } else if (changedOperation.getParameters() == null) {
                                parametersClear = true;
                            }

                            if (pathIgnore.getRequest() != null) {
                                if (changedOperation.getRequestBody() != null) {

                                    if (changedOperation.getRequestBody().getContent().getMissing() != null) {
                                        for (String contentType : pathIgnore.getRequest().getRequest().getContent().getContentType()) {
                                            changedOperation.getRequestBody().getContent().getMissing().remove(contentType);
                                        }
                                    }

                                    if (changedOperation.getRequestBody().getContent().getIncreased() != null) {
                                        for (String contentType : pathIgnore.getRequestIgnore().getContentType()) {
                                            changedOperation.getRequestBody().getContent().getIncreased().remove(contentType);
                                        }
                                    }

                                    if (changedOperation.getRequestBody().getContent().getChanged() != null) {
                                        for (String contentType : pathIgnore.getRequestIgnore().getContentType()) {
                                            changedOperation.getRequestBody().getContent().getChanged().remove(contentType);
                                        }
                                    }

                                    if (changedOperation.getRequestBody().getContent().getChanged().size() == 0 &&
                                            changedOperation.getRequestBody().getContent().getIncreased().size() == 0 &&
                                            changedOperation.getRequestBody().getContent().getMissing().size() == 0) {
                                        requestClear = true;
                                    }

                                } else {
                                    requestClear = true;
                                }
                            } else if (changedOperation.getRequestBody() == null) {
                                requestClear = true;
                            }

                            if (pathIgnore.getResponseIgnore() != null) {
                                if (changedOperation.getApiResponses() != null) {
                                    if (changedOperation.getApiResponses().getMissing() != null) {
                                        for (String status : pathIgnore.getResponseIgnore().getStatus())
                                            changedOperation.getApiResponses().getMissing().remove(status);
                                    }

                                    if (changedOperation.getApiResponses().getChanged() != null) {
                                        for (String status : pathIgnore.getResponseIgnore().getStatus())
                                            changedOperation.getApiResponses().getChanged().remove(status);
                                    }

                                    if (changedOperation.getApiResponses().getIncreased() != null) {
                                        for (String status : pathIgnore.getResponseIgnore().getStatus())
                                            changedOperation.getApiResponses().getIncreased().remove(status);
                                    }

                                    if (changedOperation.getApiResponses().getIncreased().size() == 0 &&
                                            changedOperation.getApiResponses().getMissing().size() == 0 &&
                                            changedOperation.getApiResponses().getChanged().size() == 0) {
                                        responseClear = true;
                                    }
                                } else {
                                    responseClear = true;
                                }
                            } else if (changedOperation.getApiResponses() == null) {
                                responseClear = true;
                            }

                            if (pathIgnore.getSecurityIgnore() != null) {
                                if (changedOperation.getSecurityRequirements() != null) {
                                    if (changedOperation.getSecurityRequirements().getMissing() != null) {
                                        for (Map.Entry<String, SecurityOperationIgnore> entry : pathIgnore.getSecurityIgnore().getSecurities().entrySet()) {
                                            for (String ignoreReq : entry.getValue().getSecurities()) {
                                                List<SecurityRequirement> securityRequirementList = new ArrayList<>();

                                                for (SecurityRequirement securityRequirement : changedOperation.getSecurityRequirements().getMissing()) {
                                                    if (securityRequirement.containsKey(entry.getKey())) {
                                                        securityRequirement.get(entry.getKey()).removeIf(changeReq -> changeReq.equals(ignoreReq));

                                                        if (securityRequirement.get(entry.getKey()).size() == 0) {
                                                            securityRequirementList.add(securityRequirement);
                                                        }
                                                    }
                                                }

                                                changedOperation.getSecurityRequirements().getIncreased().removeAll(securityRequirementList);
                                            }
                                        }
                                    }

                                    if (changedOperation.getSecurityRequirements().getChanged() != null) {
                                        for (Map.Entry<String, SecurityOperationIgnore> entry : pathIgnore.getSecurityIgnore().getSecurities().entrySet()) {
                                            for (String ignoreReq : entry.getValue().getSecurities()) {
                                                List<ChangedSecurityRequirement> changedSecurityReqToRemove = new ArrayList<>();

                                                for (ChangedSecurityRequirement securityRequirement : changedOperation.getSecurityRequirements().getChanged()) {
                                                    List<ChangedSecurityScheme> changedSecuritySchemesToRemove = new ArrayList<>();

                                                    for (ChangedSecurityScheme changedSecurityScheme : securityRequirement.getChanged()) {
                                                        changedSecurityScheme.getChangedScopes().getMissing().removeIf(changedScope -> changedScope.equals(ignoreReq));
                                                        changedSecurityScheme.getChangedScopes().getIncreased().removeIf(changedScope -> changedScope.equals(ignoreReq));

                                                        if (changedSecurityScheme.getChangedScopes().getIncreased().size() == 0 &&
                                                                changedSecurityScheme.getChangedScopes().getMissing().size() == 0) {
                                                            changedSecuritySchemesToRemove.add(changedSecurityScheme);
                                                        }
                                                    }
                                                    securityRequirement.getChanged().removeAll(changedSecuritySchemesToRemove);

                                                    if ((securityRequirement.getChanged() == null ||
                                                            securityRequirement.getChanged().size() == 0) &&
                                                            (securityRequirement.getMissing() == null ||
                                                                    securityRequirement.getMissing().size() == 0) &&
                                                            (securityRequirement.getIncreased() == null ||
                                                                    securityRequirement.getIncreased().size() == 0)) {

                                                        changedSecurityReqToRemove.add(securityRequirement);
                                                    }
                                                }

                                                changedOperation.getSecurityRequirements().getChanged().removeAll(changedSecurityReqToRemove);
                                            }
                                        }
                                    }

                                    if (changedOperation.getSecurityRequirements().getIncreased() != null) {
                                        for (Map.Entry<String, SecurityOperationIgnore> entry : pathIgnore.getSecurityIgnore().getSecurities().entrySet()) {
                                            for (String ignoreReq : entry.getValue().getSecurities()) {
                                                List<SecurityRequirement> securityRequirementList = new ArrayList<>();

                                                for (SecurityRequirement securityRequirement : changedOperation.getSecurityRequirements().getIncreased()) {
                                                    if (securityRequirement.containsKey(entry.getKey())) {
                                                        securityRequirement.get(entry.getKey()).removeIf(changeReq -> changeReq.equals(ignoreReq));

                                                        if (securityRequirement.get(entry.getKey()).size() == 0) {
                                                            securityRequirementList.add(securityRequirement);
                                                        }
                                                    }
                                                }
                                                changedOperation.getSecurityRequirements().getIncreased().removeAll(securityRequirementList);
                                            }
                                        }
                                    }

                                    if ((changedOperation.getSecurityRequirements().getIncreased() == null ||
                                            changedOperation.getSecurityRequirements().getIncreased().size() == 0) &&
                                            (changedOperation.getSecurityRequirements().getMissing() == null ||
                                                    changedOperation.getSecurityRequirements().getMissing().size() == 0) &&
                                            (changedOperation.getSecurityRequirements().getChanged() == null ||
                                                    changedOperation.getSecurityRequirements().getChanged().size() == 0)) {
                                        securityClear = true;
                                    }
                                } else {
                                    securityClear = true;
                                }
                            } else if (changedOperation.getSecurityRequirements() == null) {
                                securityClear = true;
                            }

                            if (requestClear && responseClear && parametersClear && securityClear)
                                changedOperationsToRemove.add(changedOperation);
                        }
                    }
                }
            }
        }

        changedOpenApi.getChangedOperations().removeAll(changedOperationsToRemove);
        return changedOpenApi;
    }
}
