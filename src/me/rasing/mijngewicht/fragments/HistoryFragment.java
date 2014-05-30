package me.rasing.mijngewicht.fragments;

import java.util.ArrayList;
import java.util.Iterator;

import me.rasing.mijngewicht.DbHelper;
import me.rasing.mijngewicht.Metingen;
import me.rasing.mijngewicht.MetingenAdapter;
import me.rasing.mijngewicht.R;
import me.rasing.mijngewicht.providers.GewichtProvider;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;

public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MultiChoiceModeListener {
	private MetingenAdapter mAdapter;
	private CursorLoader cursorLoader;
	private ListView listView;
	private LoaderManager loadermanager;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_history, container, false);
		
		loadermanager = getLoaderManager();
		loadermanager.initLoader(1, null, this);

		// The desired columns to be bound
		String[] columns = new String[] {
				Metingen.COLUMN_NAME_GEWICHT,
				Metingen.COLUMN_NAME_DATUM
		};

		int[] to = new int[] {
				R.id.gewicht,
				R.id.datum,
		};
		
		// create the adapter using the cursor pointing to the desired data
		// as well as the layout information
		mAdapter = new MetingenAdapter(
				getActivity(),
				R.layout.geschiedenis_list_item,
				null,
				columns,
				to,
				0);

		listView = (ListView) rootView.findViewById(R.id.geschiedenis);
		listView.setAdapter(mAdapter);
		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(this);
		
//		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int pos,
//					long id) {
//				Cursor c = (Cursor) parent.getItemAtPosition(pos);
//				
//				Intent intent = new Intent(getActivity(), MeetingInvoerenActivity.class);
//				intent.putExtra(MeetingInvoerenActivity.ID, c.getInt(c.getColumnIndex(Metingen._ID)));
//				startActivity(intent);
//			}
//
//		});
		
		listView.setEmptyView(rootView.findViewById(R.id.empty));
	   
		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				Metingen._ID,
				Metingen.COLUMN_NAME_GEWICHT,
				Metingen.COLUMN_NAME_DATUM
		};
		
		cursorLoader = new CursorLoader(
				getActivity(),
				GewichtProvider.METINGEN_URI,
				projection,
				null,
				null,
				Metingen.COLUMN_NAME_DATUM + " DESC");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if(mAdapter!=null && cursor!=null) {
			mAdapter.swapCursor(cursor); //swap the new cursor in.
		} else {
			Log.v("Hello World","OnLoadFinished: mAdapter is null");
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if(mAdapter!=null)
			mAdapter.swapCursor(null);
		else
			Log.v("Hello World","OnLoadFinished: mAdapter is null");
	}

	@Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // Respond to clicks on the actions in the CAB
        switch (item.getItemId()) {
            case R.id.actie_delete:
        		DbHelper mDbHelper = new DbHelper(getActivity().getBaseContext());
            	SQLiteDatabase db = mDbHelper.getWritableDatabase();
            	
            	ArrayList<Integer> selected_positions = mAdapter.getSelectedPositions();
            	//ArrayList<String> i = new ArrayList<String>();
            	ContentResolver mContentResolver = getActivity().getContentResolver();
            	
            	Iterator<Integer> iterator = selected_positions.iterator();
            	while (iterator.hasNext()) {
            		Integer pos = iterator.next();
	            	Cursor c = (Cursor) listView.getItemAtPosition(pos);
	            	int id = c.getInt(c.getColumnIndex("_id"));
	            	//i.add(pos.toString());
	            	// Issue SQL statement.
	            	Uri uri = Uri.withAppendedPath(GewichtProvider.METINGEN_URI, "/" + Integer.toString(id));
	            	mContentResolver.delete(uri, Metingen._ID + "="+Integer.toString(id), null);
	            	//db.delete(Metingen.TABLE_NAME, Metingen._ID + "="+Integer.toString(id), null);
            	}

            	db.close();
                
                mode.finish(); // Action picked, so close the CAB
                
                //mAdapter.requery();
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.cab_geschiedenis, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // Here you can make any necessary updates to the activity when
        // the CAB is removed. By default, selected items are deselected/unchecked.
    	mAdapter.clearSelection();
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // Here you can perform updates to the CAB due to
        // an invalidate() request
        return false;
    }

	@Override
    public void onItemCheckedStateChanged(ActionMode mode, int position,
                                          long id, boolean checked) {
    	mAdapter.toggleSelection(position);
    	mAdapter.notifyDataSetChanged();
    	
    	mode.setTitle(Integer.toString(mAdapter.count()) + " geselecteerd");
    }
}
