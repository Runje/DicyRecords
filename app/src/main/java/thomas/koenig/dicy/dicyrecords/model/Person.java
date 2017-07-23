package thomas.koenig.dicy.dicyrecords.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Thomas on 15.10.2016.
 */
public class Person extends RealmObject {
    public static final String FIELD_NAME = "name";

    public Person() {
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @PrimaryKey
    private String id;

    /**
     * Name of the person.
     */
    private String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
