package tomerbu.edu.contactsproviderdemo.contacts;

import java.util.ArrayList;

import tomerbu.edu.contactsproviderdemo.models.Contact;

public class MyContactsProvider {

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    private ArrayList<Contact> contacts = new ArrayList<>();

    public MyContactsProvider() {
        for (int i = 0; i < 25; i++) {
            contacts.add(new Contact(i + " ", "Moshe" + i));
        }
    }



}
