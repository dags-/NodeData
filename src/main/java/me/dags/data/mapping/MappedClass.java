package me.dags.data.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.dags.data.mapping.annotation.Mapping;

public class MappedClass<T> {

    final List<FieldMapping> fields;
    final Class<T> type;

    MappedClass(Class<T> type) {
        List<FieldMapping> fields = new ArrayList<>();
        Class<?> c = type;
        do {
            for (Field field : c.getDeclaredFields()) {
                if (ignoreModifier(field.getModifiers())) {
                    continue;
                }
                fields.add(new FieldMapping(field));
                field.setAccessible(true);
            }
        } while (c != null && !(c = c.getSuperclass()).equals(Object.class));
        this.type = type;
        this.fields = Collections.unmodifiableList(fields);
    }

    private static boolean ignoreModifier(int mod) {
        return (Modifier.isStatic(mod) && Modifier.isFinal(mod)) || Modifier.isTransient(mod);
    }

    static class FieldMapping {

        final String name;
        final String comment;
        final Field field;

        private FieldMapping(Field field) {
            Mapping mapping = field.getAnnotation(Mapping.class);
            this.name = mapping == null || mapping.name().isEmpty() ? field.getName() : mapping.name();
            this.comment = mapping == null ? "" : mapping.comment();
            this.field = field;
        }

        Class<?> type() {
            return field.getType();
        }

        Type[] typeArgs() {
            Type type = field.getGenericType();
            return type instanceof ParameterizedType ? ((ParameterizedType) type).getActualTypeArguments() : new Type[0];
        }

        boolean hasComment() {
            return !comment.isEmpty();
        }
    }
}
