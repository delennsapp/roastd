package net.jmhossler.roastd.searchtask;


import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.util.Log;

import net.jmhossler.roastd.data.drink.Drink;
import net.jmhossler.roastd.data.drink.DrinkDataSource;
import net.jmhossler.roastd.data.drink.FirebaseRTDrinkRepository;
import net.jmhossler.roastd.viewtask.BaseSearchableItemPresenter;
import net.jmhossler.roastd.viewtask.SearchableItemListContract;
import net.jmhossler.roastd.viewtask.SearchableItemListFragment;

import java.util.List;

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
    DrinkDataSource ds = FirebaseRTDrinkRepository.getInstance();
    ds.getDrinks(new DrinkDataSource.LoadDrinksCallback() {
      @Override
      public void onDrinksLoaded(List<Drink> drinks) {
        mItems.addAll(drinks);
        mListView.notifyDataSetChanged();
      }

      @Override
      public void onDataNotAvailable() {

      }
    });
  }
}
