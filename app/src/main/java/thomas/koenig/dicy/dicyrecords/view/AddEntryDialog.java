package thomas.koenig.dicy.dicyrecords.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import thomas.koenig.dicy.dicyrecords.AndroidUtils;
import thomas.koenig.dicy.dicyrecords.R;

/**
 * Created by Thomas on 16.10.2016.
 */
public class AddEntryDialog {
    private Context context;
    private String[] items;
    private AddEntryDialogListener listener;
    private int title;

    public AddEntryDialog(Context context, String[] items, AddEntryDialogListener listener, int title) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.title = title;
    }

    public void show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View layout = LayoutInflater.from(context).inflate(R.layout.add_entry, null);
        ListView view = (ListView) layout.findViewById(R.id.itemsList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items);
        view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = items[position];
                Log.d("AddEntryDialog", "Deleting entry: " + item);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(parent.getContext());
                dialogBuilder.setTitle(R.string.delete);
                dialogBuilder.setMessage(parent.getContext().getString(R.string.delete_entry) + item);
                dialogBuilder.setNegativeButton(R.string.no, null);
                dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        listener.deleteName(item);

                    }
                });

                dialogBuilder.show();
                return true;
            }
        });
        view.setAdapter(arrayAdapter);


        builder.setTitle(title);
        builder.setView(layout);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                EditText gameText = (EditText) layout.findViewById(R.id.editText_Name);
                String gameName = gameText.getText().toString();
                if (listener.checkName(gameName))
                {
                    listener.onSuccess(gameName);
                }
                else
                {
                    listener.onFailure(gameName);
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        Dialog dialog = builder.create();
        dialog.show();

        //AndroidUtils.adaptDialogSize(dialog, 1, 0.8f);
    }

    interface AddEntryDialogListener {

        boolean checkName(String name);
        void onSuccess(String name);
        void onFailure(String name);
        void deleteName(String name);
    }
}
