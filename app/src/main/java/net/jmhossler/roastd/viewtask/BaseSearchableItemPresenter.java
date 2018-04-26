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
  protected UserDataSource mUserDataStore;
  protected SearchableItemDataSource mSearchableItemDataStore;
  protected FirebaseAuth mAuth;
  protected User mUser;

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
    if (isFavoriting) {
      mUser.addFavoriteUUID(mItems.get(position).getUuid());
    } else {
      mUser.removeFavoriteUUID(mItems.get(position).getUuid());
    }
    mUserDataStore.saveUser(mUser);
  }

  @Override
    public void bindViewAtPosition(int position, SearchableItemListContract.SearchableListItemView view) {
    SearchableItem si = mItems.get(position);
    view.setContent(si.getName());
    view.setFavoriteState(mUser.getFavoriteUUIDs().containsKey(si.getUuid()));
  }

  @Override
  public int itemCount() {
    return mItems.size();
  }

  public void userLoaded() {

  }

  @Override
  public void start() {
    mUserDataStore.getUser(mAuth.getUid(), new UserDataSource.GetUserCallback() {
      @Override
      public void onUserLoaded(User user) {
        mUser = user;
        userLoaded();
      }

      @Override
      public void onDataNotAvailable() {

      }
    });
  }
}
