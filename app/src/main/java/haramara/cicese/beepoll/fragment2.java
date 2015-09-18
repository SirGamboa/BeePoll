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
public class fragment2 extends Fragment {
    private ViewGroup root;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = (ViewGroup) inflater.inflate(R.layout.fragment2, null);
        TextView tv = (TextView) root.findViewById(R.id.FragTV2);
        tv.setText("Fragment 2");
        return root;
    }
}
