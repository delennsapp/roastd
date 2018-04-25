package net.jmhossler.roastd.favoritestask;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import net.jmhossler.roastd.R;
import net.jmhossler.roastd.data.searchableItem.FirebaseRTSearchableItemRepository;
import net.jmhossler.roastd.data.user.FirebaseRTUserRepository;
import net.jmhossler.roastd.viewtask.SearchableItemListFragment;
import net.jmhossler.roastd.util.ActivityUtils;

public class FavoritesActivity extends AppCompatActivity {

  private FavoritesPresenter mFavoritesPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_favorites);
    setupListFragment();
  }

  private void setupListFragment() {
    FragmentManager fm = getSupportFragmentManager();

    SearchableItemListFragment silf =
      (SearchableItemListFragment) fm.findFragmentById(R.id.favorites_list_fragment_container);

    if (silf == null) {
      silf = new SearchableItemListFragment();
      ActivityUtils.addFragmentToActivity(fm, silf, R.id.favorites_list_fragment_container);
    }

    mFavoritesPresenter = new FavoritesPresenter(silf, FirebaseAuth.getInstance(),
      FirebaseRTSearchableItemRepository.getInstance(), FirebaseRTUserRepository.getsInstance());
  }
}
