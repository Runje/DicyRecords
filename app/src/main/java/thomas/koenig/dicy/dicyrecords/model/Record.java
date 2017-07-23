package thomas.koenig.dicy.dicyrecords.model;

import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.util.Arrays;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Thomas on 15.10.2016.
 */

public class Record extends RealmObject {
    public static final String FIELD_VALUE = "value";
    public static String FIELD_GAME = "game";
    public static final String dateFormat = "dd.MM.YY";

    public String getId() {
        return id;
    }

    public static String FIELD_ID = "id";

    public Record() {
    }

    @PrimaryKey
    private String id;
    /*
        Value of the record (e.g. number of bats)
     */
    private int value;

    /**
     * Game of the record (e.g. Ogo)
     */
    private Game game;

    /**
     * Involved persons in the record.
     */
    private RealmList<Person> persons;

    /**
     * Date of the record in ms
     */
    private long date;

    public Record(String id, int value, Game game, Person[] persons, DateTime date) {
        this(id, value, game, new RealmList<Person>(persons), date);
    }

    public DateTime getDate() {
        return new DateTime(date);
    }

    public Record(String id, int value, Game game, RealmList<Person> persons, DateTime date) {
        this.id = id;
        this.value = value;
        this.persons = persons;
        this.game = game;
        this.date = date.getMillis();
    }

    public int getValue() {
        return value;
    }

    public RealmList<Person> getPersons() {
        return persons;
    }
}
