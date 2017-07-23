package thomas.koenig.dicy.dicyrecords.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thomas.koenig.dicy.dicyrecords.R;
import thomas.koenig.dicy.dicyrecords.model.Person;
import thomas.koenig.dicy.dicyrecords.model.Record;

/**
 * Created by Thomas on 15.10.2016.
 */

public class RecordListAdapter extends BaseAdapter {
    private List<Record> records;
    private DeleteRecordListener deleteRecordListern;

    public RecordListAdapter(Record[] records, DeleteRecordListener listener) {
        this.records = new ArrayList<>();
        for(Record record : records)
        {
            this.records.add(record);
        }
        this.deleteRecordListern = listener;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        Log.d("GameListAdapter", "GetView positon " + position);
        if (convertView == null)
        {

            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.record, null);
        }

        TextView persons = (TextView) convertView.findViewById(R.id.persons);
        TextView recordValue = (TextView) convertView.findViewById(R.id.recordValue);
        TextView date = (TextView) convertView.findViewById(R.id.textDate);
        TextView pos = (TextView) convertView.findViewById(R.id.textPos);

        final Record record = (Record) getItem(position);

        pos.setText(Integer.toString(position + 1) + ".");
        date.setText(record.getDate().toString(Record.dateFormat));
        final StringBuilder builder = new StringBuilder();

        for (Person person : record.getPersons()) {
            builder.append(person.getName());
            builder.append(", ");
        }

        builder.delete(builder.length() - 2, builder.length() - 1);
        persons.setText(builder.toString());

        recordValue.setText(Integer.toString(record.getValue()));

        // set Long click listener to delete record
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("GameListAdapter", "Deleting record: " + record);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(parent.getContext());
                dialogBuilder.setTitle(R.string.delete);
                dialogBuilder.setMessage(parent.getContext().getString(R.string.delete_entry) + record.toString());
                dialogBuilder.setNegativeButton(R.string.no, null);
                dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteRecordListern.deleteRecord(record);
                        notifyDataSetInvalidated();
                    }
                });

                dialogBuilder.show();
                return true;
            }
        });
        return convertView;
    }

    public void setRecords(Record[] records) {

        this.records.clear();
        notifyDataSetChanged();
        for(Record record : records)
        {
            this.records.add(record);
        }
    }

    public interface DeleteRecordListener {
        void deleteRecord(Record record);
    }

}
