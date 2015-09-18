package haramara.cicese.beepoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by diseno on 9/18/15. for BeePoll
 */
public class fragment1 extends Fragment {
    private ViewGroup root;
    private static final String TAG = "Fragment 1";
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = (ViewGroup) inflater.inflate(R.layout.fragment1, null);
        TextView tv = (TextView) root.findViewById(R.id.FragTV);
        tv.setText("Fragment 1");
        setHasOptionsMenu(true);
        return root;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_encuestas, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_sync:
                // do s.th.
                Log.i(TAG, "Syncking");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
