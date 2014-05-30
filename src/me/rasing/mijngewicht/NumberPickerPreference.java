package me.rasing.mijngewicht;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class NumberPickerPreference extends DialogPreference {
	
    private static final Float DEFAULT_VALUE = Float.valueOf(0);
	private EditText mEditText;
	private Float mCurrentValue;

	public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        setDialogLayoutResource(R.layout.numberpicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }
	
	@Override
    protected void onBindDialogView(View view) {
		Log.d("!!!DATA!!!", ">>> onBindDialogView " + this.mCurrentValue);
        super.onBindDialogView(view);
        
        this.mEditText = (EditText) view.findViewById(R.id.editText1);
        
        if (this.mCurrentValue != null) {
        	this.mEditText.setText(this.mCurrentValue.toString());
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
        	this.mCurrentValue = Float.parseFloat(this.mEditText.getText().toString());
            persistFloat(this.mCurrentValue);
        }
    }
    

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		if (restorePersistedValue) {
			// Restore existing state
			mCurrentValue = this.getPersistedFloat(DEFAULT_VALUE);
		} else {
			// Set default state from the XML attribute
			mCurrentValue = (Float) defaultValue;
			persistFloat(mCurrentValue);
		}
		Log.d("!!!DATA!!!", ">>> " + this.mCurrentValue);
	}
	
	public Float getLength() {
		return this.mCurrentValue;
	}
}
