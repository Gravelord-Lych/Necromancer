package lych.necromancer.util.tab;

import java.lang.reflect.Field;

public final class ReflectionUtils {
    private ReflectionUtils() {}

    public static void iterateFields(Class<?> src, Class<?> required, ReflectionConsumer<? super Field> consumer) {
        try {
            for (Field field : src.getFields()) {
                if (required.isAssignableFrom(field.getType())) {
                    consumer.accept(field);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public interface ReflectionConsumer<T> {
        void accept(T t) throws ReflectiveOperationException;
    }
}
