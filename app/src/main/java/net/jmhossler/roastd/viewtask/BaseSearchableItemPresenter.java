package net.jmhossler.roastd.viewtask;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import net.jmhossler.roastd.data.bean.Bean;
import net.jmhossler.roastd.data.drink.Drink;
import net.jmhossler.roastd.data.searchableItem.SearchableItem;
import net.jmhossler.roastd.data.searchableItem.SearchableItemDataSource;
import net.jmhossler.roastd.data.shop.Shop;
import net.jmhossler.roastd.data.user.User;
import net.jmhossler.roastd.data.user.UserDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class BaseSearchableItemPresenter implements SearchableItemListContract.Presenter {

  protected SearchableItemListContract.View mListView;
  protected List<SearchableItem> mItems;
  public UserDataSource mUserDataStore;
  public SearchableItemDataSource mSearchableItemDataStore;
  public FirebaseAuth mAuth;
  public User mUser;

  public BaseSearchableItemPresenter(SearchableItemListContract.View v, FirebaseAuth firebaseAuth,
                                     SearchableItemDataSource searchableItemRepository,
                                     UserDataSource userRepository) {
    mListView = v;
    mListView.setPresenter(this);
    mItems = new ArrayList<>();
    mAuth = firebaseAuth;
    mSearchableItemDataStore = searchableItemRepository;
    mUserDataStore = userRepository;
  }

  @Override
  public void onListItemClicked(int position) {
    SearchableItem i = mItems.get(position);
    if (i instanceof Drink) {
      mListView.navigateToDrink(i.getUuid());
    } else if (i instanceof Shop) {
      mListView.navigateToShop(i.getUuid());
    } else if (i instanceof Bean) {
      mListView.navigateToBean(i.getUuid());
    } else {
      // oh noes
    }
  }

  @Override
  public void toggleFavorite(int position, Boolean isFavoriting) {
    mUserDataStore.getUser(mAuth.getUid(), new UserDataSource.GetUserCallback() {
      @Override
      public void onUserLoaded(User user) {
        if (user == null) {
          Log.d(TAG, "User " + mAuth.getUid() + " does not exist! This is bad!");
          mListView.finish();
        }
        mUser = user;
        setFavorite(position, isFavoriting);
      }

      @Override
      public void onDataNotAvailable() {
        Log.d(TAG, "Error with Firebase Instance????");
      }
    });
  }

  @Override
  public void bindViewAtPosition(int position, SearchableItemListContract.SearchableListItemView view) {
    view.setContent(mItems.get(position).getName());
    view.setFavoriteState(isFavorited(mItems.get(position).getUuid()));
  }

  @Override
  public int itemCount() {
    return mItems.size();
  }

  @Override
  public void start() {

  }

  @Override
  public Boolean isFavorited(String UUID) {
    Map<String, Boolean> favoritesMap = mUser.getFavoriteUUIDs();
    if(favoritesMap == null) {
      return false;
    }
    List<String> favoritesList = new ArrayList<>(favoritesMap.keySet());
    return favoritesList.contains(UUID);
  }

  @Override
  public void setFavorite(int position, Boolean isFavoriting) {
    String favoriteUUID = mItems.get(position).getUuid();
    if (isFavoriting) {
      mUser.addFavoriteUUID(favoriteUUID);
    } else {
      mUser.removeFavoriteUUID(favoriteUUID);
    }
    mUserDataStore.saveUser(mUser);
  }
}
