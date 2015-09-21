package haramara.cicese.beepoll;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by diseno on 9/18/15. for BeePoll
 */
public class fLogoff extends Fragment {
    private ViewGroup root;
    private Context cntx;
    private static final String TAG = "FragLogOff";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.encuestas_expandiblelistview, null);
        cntx = container.getContext();
        Log.i(TAG, "Entre LogOff");
        getActivity().setResult(44);
        getFragmentManager().popBackStack();
        getActivity().finish();
//        getActivity().onBackPressed();
        return root;
    }

}
