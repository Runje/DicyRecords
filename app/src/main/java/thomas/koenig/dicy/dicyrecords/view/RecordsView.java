package thomas.koenig.dicy.dicyrecords.view;

import thomas.koenig.dicy.dicyrecords.model.Game;

/**
 * Created by Thomas on 15.10.2016.
 */

public interface RecordsView {
    void showStartScreen();
    void showPersonsScreen();
    void showRecordsFor(Game game);

}
