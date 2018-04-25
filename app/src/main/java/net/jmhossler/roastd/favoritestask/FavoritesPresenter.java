package net.jmhossler.roastd.favoritestask;

import net.jmhossler.roastd.data.bean.Bean;
import net.jmhossler.roastd.data.bean.BeanDataSource;
import net.jmhossler.roastd.data.bean.FirebaseRTBeanRepository;
import net.jmhossler.roastd.data.searchableItem.FirebaseRTSearchableItemRepository;
import net.jmhossler.roastd.data.searchableItem.SearchableItem;
import net.jmhossler.roastd.data.searchableItem.SearchableItemDataSource;
import net.jmhossler.roastd.viewtask.BaseSearchableItemPresenter;
import net.jmhossler.roastd.viewtask.SearchableItemListContract;

import java.util.List;
import java.util.stream.Collectors;

public class FavoritesPresenter extends BaseSearchableItemPresenter {

  public FavoritesPresenter(SearchableItemListContract.View v) {
    super(v);
    mListView.setPresenter(this);
  }

  @Override
  public void start() {
    super.start();

    // example change this to favorites using john's wrapper
    SearchableItemDataSource ds = FirebaseRTSearchableItemRepository.getInstance();
    BeanDataSource bs = FirebaseRTBeanRepository.getInstance();

    mListView.showProgressBarHideList();
    bs.getBeans(new BeanDataSource.LoadBeansCallback() {
      @Override
      public void onBeansLoaded(List<Bean> beans) {
        ds.getSearchableItems(beans.stream().map(b -> b.getUuid()).collect(Collectors.toList()), new SearchableItemDataSource.LoadSearchableItemsCallback() {
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

      @Override
      public void onDataNotAvailable() {

      }
    });
  }
}
