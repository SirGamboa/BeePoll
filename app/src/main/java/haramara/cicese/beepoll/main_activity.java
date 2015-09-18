package haramara.cicese.beepoll;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;


/**
 * Created by diseno on 9/14/15. for BeePoll
 */
public class main_activity  extends AppCompatActivity{
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private String[] fViews;
    private String drawerTitle;
    ActionBarDrawerToggle mDrawerToggle;
    String nHeader = "Rodolfo Robles";
    String eHeader= "rodosel@gmail.com";

    String TAG = "MainActivity";

    String TITLES[] = {"Encuestas","Borradores","Enviados","Bandeja de salida","Configuración"};
    int ICONS[] = {R.mipmap.ic_action_collection,
            R.mipmap.ic_action_discard,
            R.mipmap.ic_action_view_as_list,
            R.mipmap.ic_action_send_now,
            R.mipmap.ic_action_settings
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        fViews = getResources().getStringArray(R.array.navDrawerItems);

        setToolbar();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        mDrawerLayout.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navView);
        if(mNavigationView != null){
            // something
            setupDrawerContent(mNavigationView);
        }
        drawerTitle = getResources().getString(R.string.home_item);
        if(savedInstanceState == null ){
            //selec item
            selectItem(drawerTitle);
        }


    }
    private void setupDrawerContent(NavigationView nView) {
        //--
        TextView tvHeader =  (TextView) nView.findViewById(R.id.nameHeader);
        TextView tvMail =  (TextView) nView.findViewById(R.id.email);
        // metodo para cargar de DB nombre y correo. **
        tvHeader.setText(nHeader);
        tvMail.setText(eHeader);
        //--

        nView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                String title = menuItem.getTitle().toString();
                Log.i(TAG,""+menuItem.getItemId() );

                selectItem(title);
                return true;
            }
        });
    }

    private void selectItem(String title) {
        // Enviar título como arguemento del fragmento
//        Bundle args = new Bundle();
//        args.putString(PlaceHolderFragment.ARG_SECTION_TITLE, title);
        String frg = "haramara.cicese.beepoll."+title;
        if(title.contentEquals("Encuestas")){
            frg = "haramara.cicese.beepoll.fEncuestas";
        }

        Log.i(TAG,frg);

//        Fragment fragment = PlaceHolderFragment.newInstance(title);
//        fragment.setArguments(args);
//        Log.i(TAG, fragment.toString());


        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main_content, Fragment.instantiate(this, frg));
        tx.commit();

        mDrawerLayout.closeDrawers(); // Cerrar drawer

        setTitle(title); // Setear título actual
    }
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
//            ab.setHomeButtonEnabled(true);
            ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }


}
