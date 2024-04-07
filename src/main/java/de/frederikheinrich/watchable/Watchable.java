package de.frederikheinrich.watchable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Represents a watchable object that allows listeners to be notified of value changes.
 *
 * @param <T> the type of the value
 */
@JsonAdapter(Watchable.WatchableTypeAdapter.class)
public class Watchable<T> {

    private volatile T value;
    transient ArrayList<Change<T>> listeners = new ArrayList<>();

    /**
     * Represents a watchable object that allows listeners to be notified of value changes.
     *
     * @param <T> the type of the value
     */
    public Watchable(T value) {
        this.value = value;
    }


    /**
     * Creates a new Watchable object without any initial value.
     * The value is set to null.
     */
    public Watchable() {
        this.value = null;
    }

    /**
     * Creates a new Watchable object with the given value.
     *
     * @param value the initial value of the Watchable
     * @param <T>   the type of the value
     * @return a new Watchable object with the given value
     */
    public static <T> Watchable<T> of(T value) {
        return new Watchable<>(value);
    }


    /**
     * Adds a listener to the Watchable object. The listener will be notified of value changes.
     *
     * @param listener the listener to be added
     * @param <T>      the type of the value
     */
    public void watch(Change<T> listener) {
        listeners.add(listener);
    }

    /**
     * Sets the value of the Watchable object and notifies all registered listeners of the change.
     *
     * @param value the new value to be set
     * @param <T>   the type of the value
     */
    public synchronized void set(T value) {
        final T old = this.value;
        this.value = value;
        listeners.forEach(listener -> listener.onChange(old, value));
    }

    /**
     * Retrieves the current value of the Watchable object.
     *
     * @param <T> the type of the value
     * @return the current value of the Watchable object
     */
    public synchronized final T get() {
        return value;
    }

    /**
     * Represents a change listener that is notified when a value changes.
     *
     * @param <T> the type of the value
     */
    public interface Change<T> {
        void onChange(T oldValue, T newValue);
    }

    /**
     * A custom Gson {@link TypeAdapter} for serializing and deserializing objects of type Watchable.
     * This adapter is used in conjunction with Gson to convert Watchable objects to and from JSON representations.
     * It serializes a Watchable object by converting its value to a JSON string.
     * It deserializes a JSON string to a Watchable object by creating a new Watchable object with the deserialized value.
     *
     * @param <T> the type parameter of the Watchable object
     */
    static class WatchableTypeAdapter implements JsonSerializer<Watchable<?>>, JsonDeserializer<Watchable<?>> {
        /**
         * Serializes the given Watchable object to a JSON element.
         *
         * @param src       The Watchable object to be serialized
         * @param typeOfSrc The type of the source object
         * @param context   The context for the serialization process
         * @return The serialized JSON element
         */
        @Override
        public JsonElement serialize(Watchable<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.value.toString());
        }

        /**
         * Deserializes a JSON element into a Watchable object.
         *
         * @param json    the JSON element to be deserialized
         * @param typeOfT the type of the object to be deserialized
         * @param context the context for deserialization
         * @return the deserialized Watchable object
         * @throws JsonParseException if the JSON element cannot be deserialized
         */
        @Override
        public Watchable<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String value = json.getAsString();
            return new Watchable<>(value);
        }
    }
}
