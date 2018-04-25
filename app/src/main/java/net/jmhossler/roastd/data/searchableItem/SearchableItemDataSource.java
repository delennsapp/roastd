package net.jmhossler.roastd.data.searchableItem;

import java.util.List;

public interface SearchableItemDataSource extends BaseDataSource {

  interface LoadSearchableItemsCallback {
    void onSearchableItemsLoaded(List<SearchableItem> items);
    void onDataNotAvailable();
  }

  void getSearchableItems(List<String> ids, LoadSearchableItemsCallback callback);
  void getSearchableItemsByText(String query, LoadSearchableItemsCallback callback);
}
