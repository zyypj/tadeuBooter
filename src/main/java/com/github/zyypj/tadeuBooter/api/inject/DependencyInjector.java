package com.github.zyypj.tadeuBooter.api.inject;

import java.lang.reflect.Field;
import java.util.Map;

public class DependencyInjector {

    /**
     * Injeta dependências no objeto alvo, usando o container que mapeia tipos às suas instâncias.
     *
     * @param target O objeto em que as dependências serão injetadas.
     * @param dependencyContainer Mapa de dependências (classe ≥ instância).
     */
    public static void injectDependencies(Object target, Map<Class<?>, Object> dependencyContainer) {
        Class<?> clazz = target.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Object dependency = findDependency(field.getType(), dependencyContainer);
                    if (dependency != null) {
                        try {
                            field.setAccessible(true);
                            field.set(target, dependency);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Não foi possível injetar dependência no campo: " + field.getName(), e);
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private static Object findDependency(Class<?> type, Map<Class<?>, Object> dependencyContainer) {
        if (dependencyContainer.containsKey(type)) {
            return dependencyContainer.get(type);
        }
        for (Map.Entry<Class<?>, Object> entry : dependencyContainer.entrySet()) {
            if (type.isAssignableFrom(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}