package me.dags.data.mapping;

import me.dags.data.mapping.annotation.Mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MappedClass<T> {

    final List<MappedField> fields;
    final Class<T> type;

    MappedClass(Class<T> type) {
        List<MappedField> fields = new ArrayList<>();
        Class<?> c = type;
        do {
            for (Field field : c.getDeclaredFields()) {
                int modifier = field.getModifiers();
                if (Modifier.isStatic(modifier) || Modifier.isTransient(modifier)) {
                    continue;
                }
                fields.add(new MappedField(field));
                field.setAccessible(true);
            }
        } while (c != null && !(c = c.getSuperclass()).equals(Object.class));
        this.type = type;
        this.fields = Collections.unmodifiableList(fields);
    }

    static class MappedField {

        final String name;
        final String comment;
        final Field field;

        private MappedField(Field field) {
            Mapping mapping = field.getAnnotation(Mapping.class);
            this.name = mapping == null || mapping.name().isEmpty() ? field.getName() : mapping.name();
            this.comment = "";
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
