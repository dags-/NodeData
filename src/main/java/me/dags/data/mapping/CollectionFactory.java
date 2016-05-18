package me.dags.data.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class CollectionFactory {

    static final CollectionFactory DEFAULT = new CollectionFactory();

    private final Map<Class<?>, Supplier<Map<Object, Object>>> mapSuppliers;
    private final Map<Class<?>, Supplier<Collection<Object>>> collectionSuppliers;

    private CollectionFactory() {
        Map<Class<?>, Supplier<Map<Object, Object>>> mapSuppliers = new HashMap<>();
        Map<Class<?>, Supplier<Collection<Object>>> collectionSuppliers = new HashMap<>();
        mapSuppliers.put(Map.class, () -> new HashMap<>());
        collectionSuppliers.put(List.class, () -> new ArrayList<>());
        collectionSuppliers.put(Set.class, () -> new HashSet<>());
        this.mapSuppliers = Collections.unmodifiableMap(mapSuppliers);
        this.collectionSuppliers = Collections.unmodifiableMap(collectionSuppliers);
    }

    private CollectionFactory(Builder builder) {
        mapSuppliers = Collections.unmodifiableMap(builder.mapSuppliers);
        collectionSuppliers = Collections.unmodifiableMap(builder.collectionSuppliers);
    }

    @SuppressWarnings("unchecked")
    Collection<Object> supplyCollection(Class<?> type) throws InstantiationException, IllegalAccessException {
        if (!type.isInterface()) {
            return (Collection<Object>) type.newInstance();
        }
        Supplier<Collection<Object>> supplier = collectionSuppliers.get(type);
        return supplier != null ? supplier.get() : null;
    }

    @SuppressWarnings("unchecked")
    Map<Object, Object> supplyMap(Class<?> type) throws InstantiationException, IllegalAccessException {
        if (!type.isInterface()) {
            return (Map<Object, Object>) type.newInstance();
        }
        Supplier<Map<Object, Object>> supplier = mapSuppliers.get(type);
        return supplier != null ? supplier.get() : null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Map<Class<?>, Supplier<Map<Object, Object>>> mapSuppliers = new HashMap<>();
        private final Map<Class<?>, Supplier<Collection<Object>>> collectionSuppliers = new HashMap<>();

        public Builder collection(Class<? extends Collection<?>> type, Supplier<Collection<Object>> supplier) {
            collectionSuppliers.put(type, supplier);
            return this;
        }

        public Builder map(Class<? extends Collection<?>> type, Supplier<Map<Object,Object>> supplier) {
            mapSuppliers.put(type, supplier);
            return this;
        }

        public CollectionFactory build() {
            return new CollectionFactory();
        }
    }
}
