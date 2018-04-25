package net.jmhossler.roastd.maintask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import net.jmhossler.roastd.R;
import net.jmhossler.roastd.util.ActivityUtils;

public class MainActivity extends AppCompatActivity {

  private MainPresenter mMainPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
    if (mainFragment == null) {
      mainFragment = MainFragment.newInstance();
      ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
        mainFragment, R.id.mainFrame);
    }

    mMainPresenter = new MainPresenter(mainFragment);
  }
}
