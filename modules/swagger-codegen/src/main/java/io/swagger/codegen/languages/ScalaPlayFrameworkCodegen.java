package io.swagger.codegen.languages;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.codegen.*;
import io.swagger.codegen.languages.features.BeanValidationFeatures;
import io.swagger.models.Model;
import io.swagger.models.Swagger;
import io.swagger.util.Json;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ScalaPlayFrameworkCodegen extends AbstractScalaCodegen implements BeanValidationFeatures, CodegenConfig {

    public static final String TITLE = "title";
    public static final String CONFIG_PACKAGE = "configPackage";
    public static final String BASE_PACKAGE = "basePackage";
    public static final String CONTROLLER_ONLY = "controllerOnly";
    public static final String USE_INTERFACES = "useInterfaces";
    public static final String HANDLE_EXCEPTIONS = "handleExceptions";
    public static final String WRAP_CALLS = "wrapCalls";
    public static final String USE_SWAGGER_UI = "useSwaggerUI";

    protected String title = "swagger-petstore";
    protected String configPackage = "io.swagger.configuration";
    protected String basePackage = "io.swagger";
    protected boolean controllerOnly = false;
    protected boolean useInterfaces = true;
    protected boolean useBeanValidation = true;
    protected boolean handleExceptions = true;
    protected boolean wrapCalls = true;
    protected boolean useSwaggerUI = true;

    public ScalaPlayFrameworkCodegen() {
        super();
        outputFolder = "generated-code/scalaPlayFramework";
        apiTestTemplateFiles.clear();
        embeddedTemplateDir = templateDir = "ScalaPlayFramework";
        apiPackage = "controllers";
        modelPackage = "apimodels";
        invokerPackage = "io.swagger.api";

        sourceFolder = "app";

        additionalProperties.put(CONFIG_PACKAGE, configPackage);
        additionalProperties.put(BASE_PACKAGE, basePackage);

        additionalProperties.put("jackson", "true");

        cliOptions.add(new CliOption(TITLE, "server title name or client service name"));
        cliOptions.add(new CliOption(CONFIG_PACKAGE, "configuration package for generated code"));
        cliOptions.add(new CliOption(BASE_PACKAGE, "base package for generated code"));

        //Custom options for this generator
        cliOptions.add(createBooleanCliWithDefault(CONTROLLER_ONLY, "Whether to generate only API interface stubs without the server files.", controllerOnly));
        cliOptions.add(createBooleanCliWithDefault(USE_BEANVALIDATION, "Use BeanValidation API annotations", useBeanValidation));
        cliOptions.add(createBooleanCliWithDefault(USE_INTERFACES, "Makes the controllerImp implements an interface to facilitate automatic completion when updating from version x to y of your spec", useInterfaces));
        cliOptions.add(createBooleanCliWithDefault(HANDLE_EXCEPTIONS, "Add a 'throw exception' to each controller function. Add also a custom error handler where you can put your custom logic", handleExceptions));
        cliOptions.add(createBooleanCliWithDefault(WRAP_CALLS, "Add a wrapper to each controller function to handle things like metrics, response modification, etc..", wrapCalls));
        cliOptions.add(createBooleanCliWithDefault(USE_SWAGGER_UI, "Add a route to /api which show your documentation in swagger-ui. Will also import needed dependencies", useSwaggerUI));

        modelTemplateFiles.put("model.mustache", ".scala");

        setReservedWordsLowerCase(
                Arrays.asList(
                        "abstract", "continue", "for", "new", "switch", "assert",
                        "default", "if", "package", "synchronized", "boolean", "do", "goto", "private",
                        "this", "break", "double", "implements", "protected", "throw", "byte", "else",
                        "import", "public", "throws", "case", "enum", "instanceof", "return", "transient",
                        "catch", "extends", "int", "short", "try", "char", "final", "interface", "static",
                        "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
                        "native", "super", "while", "type")
        );

        defaultIncludes = new HashSet<String>(
                Arrays.asList("double",
                        "Int",
                        "Long",
                        "Float",
                        "Double",
                        "char",
                        "float",
                        "String",
                        "boolean",
                        "Boolean",
                        "Double",
                        "Integer",
                        "Long",
                        "Float",
                        "List",
                        "Set",
                        "Map")
        );
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.SERVER;
    }

    @Override
    public String getName() {
        return "scala-play-framework";
    }

    @Override
    public String getHelp() {
        return "Generates a Scala Play Framework Server application.";
    }

    @Override
    public void processOpts() {
        super.processOpts();

        // clear model and api doc template as this codegen
        // does not support auto-generated markdown doc at the moment
        //TODO: add doc templates
        modelDocTemplateFiles.remove("model_doc.mustache");
        apiDocTemplateFiles.remove("api_doc.mustache");

        if (additionalProperties.containsKey(TITLE)) {
            this.setTitle((String) additionalProperties.get(TITLE));
        }

        if (additionalProperties.containsKey(CONFIG_PACKAGE)) {
            this.setConfigPackage((String) additionalProperties.get(CONFIG_PACKAGE));
        }

        if (additionalProperties.containsKey(BASE_PACKAGE)) {
            this.setBasePackage((String) additionalProperties.get(BASE_PACKAGE));
        }

        if (additionalProperties.containsKey(CONTROLLER_ONLY)) {
            this.setControllerOnly(convertPropertyToBoolean(CONTROLLER_ONLY));
        } else {
            writePropertyBack(CONTROLLER_ONLY, controllerOnly);
        }

        if (additionalProperties.containsKey(USE_BEANVALIDATION)) {
            this.setUseBeanValidation(convertPropertyToBoolean(USE_BEANVALIDATION));
        } else {
            writePropertyBack(USE_BEANVALIDATION, useBeanValidation);
        }

        if (additionalProperties.containsKey(USE_INTERFACES)) {
            this.setUseInterfaces(convertPropertyToBoolean(USE_INTERFACES));
        } else {
            writePropertyBack(USE_INTERFACES, useInterfaces);
        }

        if (additionalProperties.containsKey(HANDLE_EXCEPTIONS)) {
            this.setHandleExceptions(convertPropertyToBoolean(HANDLE_EXCEPTIONS));
        } else {
            writePropertyBack(HANDLE_EXCEPTIONS, handleExceptions);
        }

        if (additionalProperties.containsKey(WRAP_CALLS)) {
            this.setWrapCalls(convertPropertyToBoolean(WRAP_CALLS));
        } else {
            writePropertyBack(WRAP_CALLS, wrapCalls);
        }

        if (additionalProperties.containsKey(USE_SWAGGER_UI)) {
            this.setUseSwaggerUI(convertPropertyToBoolean(USE_SWAGGER_UI));
        } else {
            writePropertyBack(USE_SWAGGER_UI, useSwaggerUI);
        }

        //We don't use annotation anymore
        importMapping.remove("ApiModelProperty");
        importMapping.remove("ApiModel");

        //Root folder
        supportingFiles.add(new SupportingFile("README.mustache", "", "README"));
        supportingFiles.add(new SupportingFile("LICENSE.mustache", "", "LICENSE"));
        supportingFiles.add(new SupportingFile("build.mustache", "", "build.sbt"));

        //Project folder
        supportingFiles.add(new SupportingFile("buildproperties.mustache", "project", "build.properties"));
        supportingFiles.add(new SupportingFile("plugins.mustache", "project", "plugins.sbt"));

        //Conf folder
        supportingFiles.add(new SupportingFile("logback.mustache", "conf", "logback.xml"));
        supportingFiles.add(new SupportingFile("application.mustache", "conf", "application.conf"));
        supportingFiles.add(new SupportingFile("routes.mustache", "conf", "routes"));

        //App/Utils folder
        supportingFiles.add(new SupportingFile("JsonUtil.scala", "app/json", "JsonUtil.scala"));

        if(true) {
            //App/Controllers
            supportingFiles.add(new SupportingFile("swagger.mustache", "public", "swagger.json"));
            supportingFiles.add(new SupportingFile("apiDocController.mustache", "app/controllers", "ApiDocController.scala"));
        }

        //We remove the default api.mustache that is used
        apiTemplateFiles.remove("api.mustache");
        apiTemplateFiles.put("newApiController.mustache", "Controller.scala");
        if (true) {
            apiTemplateFiles.put("newApi.mustache", "ControllerImp.scala");
        }
        if (true) {
            apiTemplateFiles.put("newApiInterface.mustache", "ControllerImpInterface.scala");
        }

        additionalProperties.put("javaVersion", "1.8");
        additionalProperties.put("jdk8", "true");
        typeMapping.put("date", "LocalDate");
        typeMapping.put("DateTime", "OffsetDateTime");
        importMapping.put("LocalDate", "java.time.LocalDate");
        importMapping.put("OffsetDateTime", "java.time.OffsetDateTime");

        importMapping.put("InputStream", "java.io.InputStream");
        typeMapping.put("file", "MultipartFormData.FilePart[Files.TemporaryFile]");
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);

        //We don't use annotation anymore
        model.imports.remove("ApiModelProperty");
        model.imports.remove("ApiModel");
    }

    @Override
    public CodegenModel fromModel(String name, Model model, Map<String, Model> allDefinitions) {
        CodegenModel codegenModel = super.fromModel(name, model, allDefinitions);
        if(codegenModel.description != null) {
            codegenModel.imports.remove("ApiModel");
        }
        return codegenModel;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setConfigPackage(String configPackage) {
        this.configPackage = configPackage;
    }

    public void setBasePackage(String configPackage) {
        this.basePackage = configPackage;
    }

    public void setControllerOnly(boolean controllerOnly) { this.controllerOnly = controllerOnly; }

    public void setUseInterfaces(boolean useInterfaces) {
        this.useInterfaces = useInterfaces;
    }

    public void setUseBeanValidation(boolean useBeanValidation) {
        this.useBeanValidation = useBeanValidation;
    }

    public void setHandleExceptions(boolean handleExceptions) {
        this.handleExceptions = handleExceptions;
    }

    public void setWrapCalls(boolean wrapCalls) {
        this.wrapCalls = wrapCalls;
    }

    public void setUseSwaggerUI(boolean useSwaggerUI) {
        this.useSwaggerUI = useSwaggerUI;
    }

    private CliOption createBooleanCliWithDefault(String optionName, String description, boolean defaultValue) {
        CliOption defaultOption = CliOption.newBoolean(optionName, description);
        defaultOption.setDefault(Boolean.toString(defaultValue));
        return defaultOption;
    }

    @Override
    public Map<String, Object> postProcessSupportingFileData(Map<String, Object> objs) {
        Swagger swagger = (Swagger)objs.get("swagger");
        System.out.println("swagger" + swagger.toString());
        if(swagger != null) {
            try {
                objs.put("swagger-json", Json.pretty().writeValueAsString(swagger));
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return super.postProcessSupportingFileData(objs);
    }
}
