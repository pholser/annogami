module com.pholser.spring.aliasfor.processor {
    requires java.compiler;
    provides javax.annotation.processing.Processor
        with com.pholser.spring.AliasForValidationProcessor;
}
