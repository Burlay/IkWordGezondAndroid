package me.rasing.mijngewicht.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NavigationAdapter extends ArrayAdapter<String> {

	public class MenuItemHolder {

		public TextView label;

	}

	private Context context;
	private int layout;
	private int textField;
	private String[] objects;

	public NavigationAdapter(Context context, int layout, int textField, String[] objects) {
		super(context, layout, textField, objects);
		this.context = context;
		this.layout = layout;
		this.textField = textField;
		this.objects = objects;
	}

	public View getView(final int position, View inView, ViewGroup parent) {
		MenuItemHolder holder = null;

		if (inView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inView = inflater.inflate(layout, null);

			holder = new MenuItemHolder();
			holder.label = (TextView) inView.findViewById(this.textField);

			inView.setTag(holder);
		} else {
			holder = (MenuItemHolder) inView.getTag();
		}

		holder.label.setText(this.objects[position]);

		return inView;
	}
}
