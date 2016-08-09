package tomerbu.edu.contactsproviderdemo.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {

        private String id;
        private String name;

        //Constructor:
        public Contact(String id, String name ) {
            this.id = id;
            this.name = name;
        }

        //Getters and Setters
        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }


        @Override
        public String toString() {
            return name;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    protected Contact(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}