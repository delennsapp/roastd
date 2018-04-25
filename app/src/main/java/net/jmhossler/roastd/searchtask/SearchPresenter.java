package net.jmhossler.roastd.searchtask;



import android.support.annotation.NonNull;

import net.jmhossler.roastd.viewtask.BaseSearchableItemPresenter;
import net.jmhossler.roastd.viewtask.SearchableItemListFragment;

public class SearchPresenter extends BaseSearchableItemPresenter implements SearchContract.Presenter {

  private static final String TAG = "SearchActivity";
  @NonNull
  private final SearchContract.View mSearchView;

  public SearchPresenter(@NonNull SearchContract.View searchView, SearchableItemListFragment silfView) {
    super(silfView);
    mSearchView = searchView;
    mSearchView.setPresenter(this);
    mListView.setPresenter(this);
  }

  @Override
  public void start() {

  }

  public void search(String query) {

  }
}
