package me.rasing.ikwordgezond;

import android.provider.BaseColumns;

public class Metingen implements BaseColumns {
    public static final String TABLE_NAME = "metingen";
    public static final String COLUMN_NAME_GEWICHT = "gewicht";
    public static final String COLUMN_NAME_WATER = "water";
    public static final String COLUMN_NAME_SPIERMASSA = "spiermassa";
    public static final String COLUMN_NAME_VET = "vet";
    
    // Voorkom dat iemand deze class instantierd.
    public Metingen() {}
}
