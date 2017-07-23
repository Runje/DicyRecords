package thomas.koenig.dicy.dicyrecords.model;

/**
 * Created by Thomas on 16.10.2016.
 */

public class Utils {

    public static String[] personsToStrings(Person[] persons)
    {
        String[] items = new String[persons.length];
        for (int i = 0; i < persons.length; i++) {
            Person person = persons[i];
            items[i] = person.getName();
        }

        return items;
    }

    public static String[] gamesToStrings(Game[] games)
    {
        String[] items = new String[games.length];
        for (int i = 0; i < games.length; i++) {
            Game game = games[i];
            items[i] = game.getName();
        }

        return items;
    }
}
