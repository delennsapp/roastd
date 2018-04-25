package net.jmhossler.roastd.searchtask;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import net.jmhossler.roastd.R;
import net.jmhossler.roastd.data.searchableItem.FirebaseRTSearchableItemRepository;
import net.jmhossler.roastd.data.user.FirebaseRTUserRepository;
import net.jmhossler.roastd.util.ActivityUtils;
import net.jmhossler.roastd.viewtask.SearchableItemListFragment;

/**
 * Activity to handle searching
 */
public class SearchActivity extends AppCompatActivity {

  private SearchPresenter mSearchPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    FragmentManager fm = getSupportFragmentManager();

    SearchFragment searchFragment =
      (SearchFragment) fm.findFragmentById(R.id.searchFrame);
    if(searchFragment == null) {
      searchFragment = SearchFragment.newInstance();
      ActivityUtils.addFragmentToActivity(fm, searchFragment, R.id.searchFrame);
    }

    SearchableItemListFragment silf =
      (SearchableItemListFragment) fm.findFragmentById(R.id.itemFrame);
    if(silf == null) {
      silf = new SearchableItemListFragment();
      ActivityUtils.addFragmentToActivity(fm, silf, R.id.itemFrame);
    }

    mSearchPresenter = new SearchPresenter(searchFragment, FirebaseAuth.getInstance(),
      FirebaseRTSearchableItemRepository.getInstance(), silf, FirebaseRTUserRepository.getsInstance());
  }


}
