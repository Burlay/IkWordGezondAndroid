package me.rasing.mijngewicht;

import android.provider.BaseColumns;

public class Metingen implements BaseColumns {
    public static final String TABLE_NAME = "metingen";
    public static final String COLUMN_NAME_GEWICHT = "gewicht";
    public static final String COLUMN_NAME_DATUM = "datum";
    public static final String COLUMN_NAME_LAST_SYNCED = "last_synced";
    public static final String COLUMN_NAME_GUID = "guid";
    
    // Voorkom dat iemand deze class instantierd.
    public Metingen() {}
}