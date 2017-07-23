package thomas.koenig.dicy.dicyrecords.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Thomas on 15.10.2016.
 */
public class Game extends RealmObject {
    public static final String FIELD_NAME = "name";

    public Game() {
    }

    @PrimaryKey

    private String id;
    /**
     * Name of the game
     */
    private String name;

    public Game(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Game{" +
                "name='" + name + '\'' +
                '}';
    }
}
