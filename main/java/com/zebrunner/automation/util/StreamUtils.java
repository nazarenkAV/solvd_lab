package com.zebrunner.automation.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamUtils {

    public static <T, R> Stream<R> mapToStream(Collection<T> collection, Function<T, R> mapper) {
        return StreamUtils.notNullStream(collection)
                          .map(mapper);
    }

    public static <T, R> List<R> mapToList(Collection<T> collection, Function<T, R> mapper) {
        return StreamUtils.mapToStream(collection, mapper)
                          .collect(Collectors.toList());
    }

    public static <T> Optional<T> findFirst(Collection<T> collection, Predicate<T> filter) {
        return StreamUtils.notNullStream(collection)
                          .filter(filter)
                          .findFirst();
    }

    public static <T> Stream<T> filterToStream(Collection<T> collection, Predicate<? super T> filter) {
        return StreamUtils.notNullStream(collection)
                          .filter(filter);
    }

    private static <C> Stream<C> notNullStream(Collection<C> collection) {
        if (collection == null) {
            collection = List.of();
        }
        return collection.stream();
    }

}
