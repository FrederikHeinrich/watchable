package de.frederikheinrich.watchable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a watchable object that allows listeners to be notified of value changes.
 *
 * @param <T> the type of the value
 */
@JsonAdapter(Watchable.WatchableTypeAdapter.class)
public class Watchable<T> {

    /**
     * Represents a volatile variable that can be watched for value changes.
     *
     * @param <T> the type of the value
     */
    private volatile T value;
    /**
     * Represents a list of change listeners that are notified when a value changes.
     * This list is used in conjunction with the Watchable class to manage and notify listeners of value changes.
     *
     * @param <T> the type of the value
     */
    transient ArrayList<ChangeConsumer<T>> listeners = new ArrayList<>();

    /**
     * Represents a watchable object that allows listeners to be notified of value changes.
     *
     * @param <T> the type of the value
     */
    public Watchable(T value) {
        this.value = value;
    }


    /**
     * Represents a watchable object that allows listeners to be notified of value changes.
     *
     * @param <T> the type of the value
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
     * Adds a listener that will be notified when the value of the Watchable object changes.
     *
     * @param listener the listener to be added
     * @param <T>      the type of the value
     */
    public void watch(ChangeConsumer<T> listener) {
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
     * A custom Gson {@link TypeAdapter} for serializing and deserializing objects of type Watchable.
     * This adapter is used in conjunction with Gson to convert Watchable objects to and from JSON representations.
     * It serializes a Watchable object by converting its value to a JSON string.
     * It deserializes a JSON string to a Watchable object by creating a new Watchable object with the deserialized value.
     *
     * @param <T> the type parameter of the Watchable object
     */
    static class WatchableTypeAdapter implements JsonSerializer<Watchable<?>>, JsonDeserializer<Watchable<?>> {
        /**
         * Serializes a Watchable object by converting its value to a JSON string.
         *
         * @param src       the Watchable object to be serialized
         * @param typeOfSrc the type of the Watchable object
         * @param context   the serialization context
         * @return a JsonElement representing the serialized Watchable object
         */
        @Override
        public JsonElement serialize(Watchable<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.value.toString());
        }

        /**
         * Deserializes a JSON element to a Watchable object.
         *
         * @param json    The JSON element to be deserialized
         * @param typeOfT The type information of the Watchable object
         * @param context The context for deserialization
         * @return The deserialized Watchable object
         * @throws JsonParseException If the JSON element cannot be deserialized
         */
        @Override
        public Watchable<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String value = json.getAsString();
            return new Watchable<>(value);
        }
    }

    /**
     * Represents a consumer that handles changes in values.
     *
     * @param <T> the type of the values
     */
    public interface ChangeConsumer<T> {
        /**
         * This method is called when a change occurs in the specified values.
         *
         * @param oldValue the old value before the change
         * @param newValue the new value after the change
         */
        void onChange(T oldValue, T newValue);

        /**
         * This method returns a new ChangeConsumer that first calls the current ChangeConsumer and then calls the specified ChangeConsumer.
         *
         * @param after The ChangeConsumer to be called after the current ChangeConsumer
         * @return A new ChangeConsumer that calls both the current ChangeConsumer and the specified ChangeConsumer
         * @throws NullPointerException if the specified ChangeConsumer is null
         */
        default ChangeConsumer<T> andThen(ChangeConsumer<T> after) {
            Objects.requireNonNull(after);

            return (l, r) -> {
                onChange(l, r);
                after.onChange(l, r);
            };
        }
    }
}
