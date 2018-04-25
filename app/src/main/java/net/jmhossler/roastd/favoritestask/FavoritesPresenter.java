package net.jmhossler.roastd.favoritestask;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import net.jmhossler.roastd.data.searchableItem.SearchableItem;
import net.jmhossler.roastd.data.searchableItem.SearchableItemDataSource;
import net.jmhossler.roastd.data.user.User;
import net.jmhossler.roastd.data.user.UserDataSource;
import net.jmhossler.roastd.viewtask.BaseSearchableItemPresenter;
import net.jmhossler.roastd.viewtask.SearchableItemListContract;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class FavoritesPresenter extends BaseSearchableItemPresenter {

  public FavoritesPresenter(SearchableItemListContract.View v, FirebaseAuth firebaseAuth,
                            SearchableItemDataSource searchableItemRepository,
                            UserDataSource userRepository) {
    super(v, firebaseAuth, searchableItemRepository, userRepository);
  }

  @Override
  public void start() {
    super.start();
    getUserFavorites();
  }

  public void getUserFavorites() {
    mUserDataStore.getUser(mAuth.getUid(), new UserDataSource.GetUserCallback() {
      @Override
      public void onUserLoaded(User user) {
        if (user == null) {
          Log.d(TAG, "User " + mAuth.getUid() + " does not exist! This is bad!");
          mListView.finish();
        }
        mUser = user;
        retrieveFavorites();
      }

      @Override
      public void onDataNotAvailable() {
        Log.d(TAG, "Error with Firebase Instance????");
      }
    });
  }

  public void retrieveFavorites() {
    Map<String, Boolean> favoritesMap = mUser.getFavoriteUUIDs();

    if(favoritesMap == null) {
      favoritesMap = new HashMap<String, Boolean>();
    }
    List<String> favoritesList = new ArrayList<>(favoritesMap.keySet());

    mSearchableItemDataStore.getSearchableItems(favoritesList, new SearchableItemDataSource.LoadSearchableItemsCallback() {
      @Override
      public void onSearchableItemsLoaded(List<SearchableItem> items) {
        mItems = items;
        mListView.notifyDataSetChanged();
        mListView.hideProgressBarShowList();
      }

      @Override
      public void onDataNotAvailable() {
        // panic
      }
    });
  }
}
