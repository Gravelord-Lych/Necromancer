package lych.necromancer.util;

import net.minecraft.core.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class NonNullListAccessor<E> extends NonNullList<E> {
    public NonNullListAccessor(List<E> list, @Nullable E defaultValue) {
        super(list, defaultValue);
    }

    public static <E> NonNullList<E> withDefault(@Nullable E defaultValue) {
        return new NonNullListAccessor<>(new ArrayList<>(), defaultValue);
    }

    public static <E> Collector<E, ?, NonNullList<E>> toNonNullList(@Nullable E defaultValue) {
        return Collectors.toCollection(() -> withDefault(defaultValue));
    }
}
