package haramara.cicese.beepoll;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by diseno on 9/23/15. for BeePoll
 */
public class fayuda extends Fragment{
    private View root;
    String TAG = "fAyuda";
    Context cntx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.ayuda_layout, null);
        cntx = container.getContext();
        Log.i(TAG,"ANtes de TouchListener");
        root.setOnTouchListener(new touchGesture());
        Log.i(TAG, "Despues de TouchListener");
        Log.i(TAG, "Entre onCreateView Ayuda");


        return root;
    }

    public void onTouchEvent(MotionEvent event){
        Log.i(TAG,"Action: "+event.getAction());
        Log.i(TAG,"ActionMasked: "+event.getActionMasked());

    }

    private class touchGesture implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.i(TAG,"TOUCHED"+motionEvent.getActionMasked());
            return false;
        }
    }
}