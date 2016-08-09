package tomerbu.edu.contactsproviderdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import tomerbu.edu.contactsproviderdemo.contacts.MyContactsProvider;
import tomerbu.edu.contactsproviderdemo.models.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

/**
 * An activity representing a list of Contacts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ContactDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ContactListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CONTACTS = 10;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (checkContactsPermission()) {
            readContacts();
        } else {
            requestContactsPermission();
        }

        View recyclerView = findViewById(R.id.contact_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.contact_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }


    private boolean checkContactsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                return true;
            } else {
                //need to request the permission
                return false;
            }
        } else {
            //We already have the permission:
            return true;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void requestContactsPermission() {
        requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, REQUEST_CODE_CONTACTS);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Get the result:
        if (requestCode == REQUEST_CODE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            } else {
                Snackbar.make(fab, "We need your contacts to display the list", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestContactsPermission();
                    }
                }).show();
            }
        }
    }

    //Method the requires permission:
    private void readContacts() {
        Uri contactsURI = ContactsContract.Contacts.CONTENT_URI;

        Cursor cursor = getContentResolver().query(contactsURI, null, null, null, null);

        if (cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Toast.makeText(ContactListActivity.this, name + " " + id, Toast.LENGTH_SHORT).show();

                String phone = getPhone(id);
            }
            while (cursor.moveToNext());
        }
    }

    private String getPhone(String id) {
        Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContentResolver().query(contentUri, null, "_ID = ?", new String[]{id}, null);
        if (cursor.moveToFirst()) {
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Toast.makeText(ContactListActivity.this, number, Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        MyContactsProvider provider = new MyContactsProvider();

        recyclerView.setAdapter(new ContactAdapter(provider.getContacts()));
    }

    public class ContactAdapter
            extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

        private final List<Contact> contactList;

        public ContactAdapter(List<Contact> contactList) {
            this.contactList = contactList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Contact c = contactList.get(position);


            holder.contact = c;
            holder.tvID.setText(c.getId());
            holder.tvContactName.setText(c.getName());

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        ContactDetailFragment fragment = ContactDetailFragment.newInstance(holder.contact);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.contact_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ContactDetailActivity.class);
                        intent.putExtra(ContactDetailFragment.ARG_CONTACT, holder.contact);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View layout;
            public final TextView tvID;
            public final TextView tvContactName;
            public Contact contact;

            public ViewHolder(View view) {
                super(view);
                layout = view;
                tvID = (TextView) view.findViewById(R.id.tvID);
                tvContactName = (TextView) view.findViewById(R.id.tvName);
            }

            @Override
            public String toString() {
                return tvContactName.getText().toString();
            }
        }
    }
}
