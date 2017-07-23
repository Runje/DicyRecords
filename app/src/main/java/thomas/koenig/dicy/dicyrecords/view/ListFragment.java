package thomas.koenig.dicy.dicyrecords.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import thomas.koenig.dicy.dicyrecords.R;
import thomas.koenig.dicy.dicyrecords.model.RealmRecordsDB;
import thomas.koenig.dicy.dicyrecords.model.Record;

/**
 * Created by Thomas on 15.10.2016.
 */

public class ListFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private static final java.lang.String ARG_TITLE = "ARG_TITLE";

    private int mPage;
    private Record[] records;
    private String title;
    private RecordListAdapter adapter;
    private RealmRecordsDB db;

    public String getTitle() {
        return title;
    }

    public static ListFragment newInstance(String title, int page, Record[] records) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(ARG_TITLE, title);
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        fragment.setRecords(records);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        title = getArguments().getString(ARG_TITLE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview, container, false);
        ListView list = (ListView) view.findViewById(R.id.listview);
        update();
        adapter = new RecordListAdapter(records, new RecordListAdapter.DeleteRecordListener() {
            @Override
            public void deleteRecord(Record record) {
                db.deleteRecord(record);
                Record[] recordsFrom = db.getRecordsFrom(db.getGame(title));
                for(Record record1 : recordsFrom)
                {
                    Log.d("ListFragment", record1.toString());
                }

                adapter.setRecords(recordsFrom);
                ((Activity) getContext()).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        adapter.notifyDataSetChanged();
                    }
                });


            }
        });
        list.setAdapter(adapter);
        update();
        Log.d("ListFragment", "Set adapter " + title);
        return view;
    }

    public void setRecords(Record[] records) {
        this.records = records;
    }

    public Record[] getRecords() {
        return records;
    }

    public void update()
    {
        Log.d("ListFragment", "Updating " + title);
        RealmRecordsDB db = new RealmRecordsDB(getContext());
        records = db.getRecordsFrom(db.getGame(title));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }
}
