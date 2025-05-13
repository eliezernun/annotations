package com.embracon.anotations.aspect;

import com.embracon.anotations.anotation.Anonymize;
import com.embracon.anotations.service.AnonymizationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
@RequiredArgsConstructor
@Aspect
@Component
public class AnonymizationAspect {

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object anonymizeParams(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(Anonymize.class)) {
                Anonymize anonymizeAnnotation = parameters[i].getAnnotation(Anonymize.class);
                Class<?> type = anonymizeAnnotation.type();

                // Codifica a entrada com base no tipo
                args[i] = AnonymizationService.anonymize(args[i], type);
            } else if (parameters[i].getType().getPackageName().startsWith("com.")) {
                // Anonimiza propriedades dentro de objetos
                Field[] fields = parameters[i].getType().getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Anonymize.class)) {
                        field.setAccessible(true);
                        Anonymize anonymize = field.getAnnotation(Anonymize.class);
                        Class<?> type = anonymize.type();
                        Object originalValue = field.get(args[i]);
                        Object anonymizedValue = AnonymizationService.anonymize(originalValue, type);
                        field.set(args[i], anonymizedValue);
                    }
                }
            }
        }

        return joinPoint.proceed(args);
    }

}
