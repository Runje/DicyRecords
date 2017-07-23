package thomas.koenig.dicy.dicyrecords.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import thomas.koenig.dicy.dicyrecords.model.Game;
import thomas.koenig.dicy.dicyrecords.model.RecordsDB;

/**
 * Created by Thomas on 15.10.2016.
 */

public class ListFragmentPagerAdapter extends FragmentPagerAdapter {

    private final RecordsDB recordsDB;
    private final Game[] games;
    private Context context;

    public ListFragmentPagerAdapter(FragmentManager fm, Context context, RecordsDB recordsDB) {
        super(fm);
        this.context = context;
        this.recordsDB = recordsDB;
        games = recordsDB.getAllGames();
    }

    @Override
    public int getCount() {
        return games.length;
    }

    @Override
    public Fragment getItem(int position) {
        return ListFragment.newInstance(games[position].getName(), position + 1, recordsDB.getRecordsFrom(games[position]));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return games[position].getName();
    }

    public int getPositionOfGame(String gameName) {
        for (int i = 0; i < games.length; i++) {
            if (games[i].getName().equals(gameName))
            {
                return i;
            }
        }

        return 0;
    }
}
