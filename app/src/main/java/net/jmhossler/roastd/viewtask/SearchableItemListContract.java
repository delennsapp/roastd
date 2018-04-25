package net.jmhossler.roastd.viewtask;

import net.jmhossler.roastd.BasePresenter;
import net.jmhossler.roastd.BaseView;

public interface SearchableItemListContract {

  interface View extends BaseView<Presenter> {
    void notifyDataSetChanged();
    void navigateToBean(String beanId);
    void navigateToDrink(String drinkId);
    void navigateToShop(String shopId);
    void showProgressBarHideList();
    void hideProgressBarShowList();
    void finish();
  }

  interface Presenter extends BasePresenter {
    void onListItemClicked(int position);
    void toggleFavorite(int position, Boolean isFavoriting);
    void bindViewAtPosition(int position, SearchableListItemView view);
    int itemCount();
    Boolean isFavorited(String UUID);
    void setFavorite(int position, Boolean isFavoriting);
  }

  interface SearchableListItemView {
    void setContent(String content);
    void setFavoriteState(Boolean state);
    void setIcon(byte [] icon);
  }
}
