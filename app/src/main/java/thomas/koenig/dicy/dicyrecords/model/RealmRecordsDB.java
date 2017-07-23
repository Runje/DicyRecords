package thomas.koenig.dicy.dicyrecords.model;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static thomas.koenig.dicy.dicyrecords.R.layout.record;

/**
 * Created by Thomas on 15.10.2016.
 */
public class RealmRecordsDB implements RecordsDB {

    private Context context;
    private static Realm realmInstance;
    public RealmRecordsDB(Context context) {

        Realm.init(context);


        /**
         * DEVELOPMENT CODE TO HAVE A CLEAN DATABASE
         */
        /*realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });*/
        this.context = context;
    }

    private Realm getRealm()
    {
        if (realmInstance == null || realmInstance.isClosed()) {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realmInstance = Realm.getInstance(config);
        }


        return realmInstance;
    }

    @Override
    public Game getGame(String name) {
        return getRealm().where(Game.class).equalTo(Game.FIELD_NAME, name).findFirst();
    }

    @Override
    public boolean gameExists(String name) {
        return getRealm().where(Game.class).equalTo(Game.FIELD_NAME, name).count() > 0;
    }

    @Override
    public boolean nameExists(String name) {
        return getRealm().where(Person.class).equalTo(Game.FIELD_NAME, name).count() > 0;
    }

    @Override
    public void deleteRecord(final Record record) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Record.class).equalTo(Record.FIELD_ID, record.getId()).findAll().deleteAllFromRealm();
            }
        });
    }

    @Override
    public void deletePerson(final String name) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Person.class).equalTo(Person.FIELD_NAME, name).findAll().deleteAllFromRealm();
            }
        });
    }

    @Override
    public void deleteGame(final String name) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Game.class).equalTo(Game.FIELD_NAME, name).findAll().deleteAllFromRealm();
            }
        });
    }

    @Override
    public void addPerson(final Person person) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(person);
            }
        });
    }

    @Override
    public void addGame(final Game game) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(game);
            }
        });
    }

    @Override
    public void addRecord(final Record record) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(record);
            }
        });
    }

    @Override
    public Person[] getAllPersons() {
        RealmResults<Person> persons = getRealm().where(Person.class).findAll();
        return (Person[]) persons.toArray();
    }

    @Override
    public Game[] getAllGames() {
        RealmResults<Game> games = getRealm().where(Game.class).findAll();
        return (Game[]) games.toArray();
    }

    @Override
    public Record[] getAllRecords() {
        RealmResults<Record> records = getRealm().where(Record.class).findAll();
        return (Record[]) records.toArray();
    }

    @Override
    public boolean backupDatabase(File file) {

        file.mkdirs();
        // if backup file already exists, delete it
        file.delete();

        // copy current realm to backup file
        getRealm().writeCopyTo(file);

        //realm.close();
        return true;
    }

    @Override
    public Record[] getRecordsFrom(Game game) {
        RealmResults<Record> records = getRealm().where(Record.class).equalTo("game.name", game.getName()).findAll().sort(Record.FIELD_VALUE, Sort.DESCENDING);
        return (Record[]) records.toArray();
    }

    @Override
    public boolean restoreDatabase(File restoreFile) {

        /*Realm.deleteRealm(new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build());*/
        try {
            File file = new File(getRealm().getPath());
            getRealm().close();

            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(restoreFile);

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();

            Realm.init(context);
            getRealm();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void close() {
        getRealm().close();
    }
}
