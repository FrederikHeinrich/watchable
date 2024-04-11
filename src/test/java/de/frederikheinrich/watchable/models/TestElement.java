package de.frederikheinrich.watchable.models;

import de.frederikheinrich.watchable.Watchable;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import static de.frederikheinrich.watchable.Watchable.watch;

public class TestElement {

    @BsonId
    ObjectId id;
    Watchable<String> name;
    Watchable<Integer> count;
    Watchable<Double> level;
    Watchable<Float> view;

    public TestElement() {
        name = watch("unset");
        count = watch(-1);
        level = watch(0.0);
        view = watch(0f);
    }

    public TestElement(String name, int count) {
        this.id = new ObjectId();
        this.name = watch(name);
        this.name.on((oldValue, newValue) -> {
            System.out.println("Changed " + oldValue + " to " + newValue);
        });
        this.name = watch(name);
        this.count = watch(count);
        this.level = watch(0.0d);
        this.view = watch(0.0f);
    }

    public Watchable<String> name() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getCount() {
        return count.get();
    }

    public Double getLevel() {
        return level.get();
    }

    public Float getView() {
        return view.get();
    }
}
