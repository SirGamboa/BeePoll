package haramara.cicese.beepoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by diseno on 9/17/15. for BeePoll
 */
public class Home  extends Fragment {
    private ViewGroup root;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = (ViewGroup) inflater.inflate(R.layout.home, null);
        TextView tv = (TextView) root.findViewById(R.id.HomeTV);
        tv.setText("HOME");
        return root;
    }
}
