package haramara.cicese.beepoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by diseno on 9/18/15. for BeePoll
 */
public class fragament1 extends Fragment {
    private ViewGroup root;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = (ViewGroup) inflater.inflate(R.layout.fragment1, null);
        TextView tv = (TextView) root.findViewById(R.id.FragTV);
        tv.setText("Fragment 1");
        return root;
    }
}
