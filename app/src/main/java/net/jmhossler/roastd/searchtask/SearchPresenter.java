package net.jmhossler.roastd.searchtask;



import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import net.jmhossler.roastd.data.searchableItem.FirebaseRTSearchableItemRepository;
import net.jmhossler.roastd.data.searchableItem.SearchableItem;
import net.jmhossler.roastd.data.searchableItem.SearchableItemDataSource;
import net.jmhossler.roastd.data.user.UserDataSource;
import net.jmhossler.roastd.viewtask.BaseSearchableItemPresenter;
import net.jmhossler.roastd.viewtask.SearchableItemListFragment;

import java.util.List;

public class SearchPresenter extends BaseSearchableItemPresenter implements SearchContract.Presenter {

  private static final String TAG = "SearchActivity";
  @NonNull
  private final SearchContract.View mSearchView;
  private final FirebaseAuth mAuth;
  private final SearchableItemDataSource dataSource;

  public SearchPresenter(@NonNull SearchContract.View searchView,
                         FirebaseAuth instance, SearchableItemDataSource dataSources,
                         SearchableItemListFragment silfView, UserDataSource userDataSource) {
    super(silfView, instance, dataSources, userDataSource);
    mAuth = instance;
    dataSource = dataSources;
    mSearchView = searchView;
    mSearchView.setPresenter(this);
    mListView.setPresenter(this);
  }

  @Override
  public void start() {

  }

  public void search(String query) {
    dataSource.getSearchableItemsByText(query,
      new SearchableItemDataSource.LoadSearchableItemsCallback() {
      @Override
      public void onSearchableItemsLoaded(List<SearchableItem> items) {
        mItems = items;
        mListView.notifyDataSetChanged();
        mListView.hideProgressBarShowList();
      }

      @Override
      public void onDataNotAvailable() {

      }
    });
  }
}
