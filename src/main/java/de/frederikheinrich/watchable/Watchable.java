package de.frederikheinrich.watchable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

@JsonAdapter(Watchable.WatchableTypeAdapter.class)
public class Watchable<T> {

    private volatile T value;
    transient ArrayList<Change<T>> listeners = new ArrayList<>();

    public Watchable(T value) {
        this.value = value;
    }

    public static <T> Watchable<T> of(T value) {
        return new Watchable<>(value);
    }

    public void watch(Change<T> listener) {
        listeners.add(listener);
    }

    public synchronized void set(T value) {
        final T old = this.value;
        this.value = value;
        listeners.forEach(listener -> listener.onChange(old, value));
    }

    public synchronized final T get() {
        return value;
    }

    public interface Change<T> {
        void onChange(T oldValue, T newValue);
    }

    static class WatchableTypeAdapter implements JsonSerializer<Watchable<?>>, JsonDeserializer<Watchable<?>> {
        @Override
        public JsonElement serialize(Watchable<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.value.toString());
        }

        @Override
        public Watchable<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String value = json.getAsString();
            return new Watchable<>(value);
        }
    }
}
