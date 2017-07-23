package thomas.koenig.dicy.dicyrecords.model;

import java.io.File;

import io.realm.RealmList;

/**
 * Created by Thomas on 15.10.2016.
 */

public interface RecordsDB {

    void addPerson(Person person);
    void addGame(Game game);
    void addRecord(Record record);

    Person[] getAllPersons();
    Game[] getAllGames();
    Record[] getAllRecords();

    void close();

    boolean backupDatabase(File file);
    boolean restoreDatabase(File file);

    Record[] getRecordsFrom(Game game);

    Game getGame(String name);

    boolean gameExists(String name);

    boolean nameExists(String name);

    void deleteRecord(Record record);

    void deletePerson(String name);

    void deleteGame(String name);
}
