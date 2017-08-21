package com.example.schuddinck.firebasedemo.Utilities;

import android.widget.EditText;

/**
 * Created by SCHUDDINCK on 08-Jul-17.
 */

public abstract class Tools
{
    public static void makeTextFieldsEmpty(EditText... txfs)
    {
        for(EditText txf : txfs)
        {
            txf.setText("");
        }
    }
}
