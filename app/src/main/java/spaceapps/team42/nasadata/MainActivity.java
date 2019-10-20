package spaceapps.team42.nasadata;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SpaceOnClickListener {

    private static final int ASTEROID = 0;
    private static final int EPIC = 1;
    private static final int MARS_ROVER = 2;
    private static final int APOD = 3;

    private SpaceNavigationView spaceNavigationView;
    private FragmentTransaction fragmentTransaction;

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<Integer> backstack = new ArrayList<>();
    private String[] titles;
    private int[] icons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spaceNavigationView = findViewById(R.id.bottom_bar);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.setSpaceOnClickListener(this);
        spaceNavigationView.setSelected(true);

        addSpaces();
        // backstack.add(0);
        setFragment(new AsteroidNeoWs());
    }

    private void setFragment(Fragment fragment) {
        // TODO: 10/20/2019
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void addSpaces() {
        if (titles == null || titles.length == 0)
            titles = getResources().getStringArray(R.array.titles);
        if (icons == null || icons.length == 0)
            icons = getResources().getIntArray(R.array.icons);
        for (int i = 0; i < titles.length; i++) {
            fragments.add(getNewFragment(i));
            spaceNavigationView.addSpaceItem(new SpaceItem(titles[i].toUpperCase(), icons[i]));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    @Override
    public void onCentreButtonClick() {

    }

    @Override
    public void onItemClick(int itemIndex, String itemName) {
        if (backstack.size() == 0)
            backstack.add(0);
        if (backstack.get(backstack.size() - 1) != itemIndex)
            backstack.add(itemIndex);
        setFragment(getNewFragment(itemIndex));
    }

    private Fragment getNewFragment(int itemIndex) {
        switch (itemIndex) {
            case ASTEROID:
                return new AsteroidNeoWs();
            case EPIC:
                return new EPIC();
            case MARS_ROVER:
                return new Mars();
            case APOD:
                return new APOD();
        }
        return new AsteroidNeoWs();
    }

    @Override
    public void onItemReselected(int itemIndex, String itemName) {

    }

    @Override
    public void onBackPressed() {
        Log.d("MSL", "onBackPressed: " + backstack.size());
//        super.onBackPressed();
        backstack.remove(backstack.size() - 1);
        if (backstack.size() < 1) {
            finish();
        } else {
            spaceNavigationView.changeCurrentItem(backstack.get(backstack.size() - 1));
        }
    }
}
