package net.jmhossler.roastd.beantask;

import net.jmhossler.roastd.BasePresenter;
import net.jmhossler.roastd.BaseView;
import net.jmhossler.roastd.data.searchableItem.SearchableItem;

import java.util.List;


public interface BeanContract {
  interface View extends BaseView<Presenter> {
    void displayName(String name);
    void displayDescription(String description);
    void displayShop(String shopId);
    void displayRoast(String roast);
    void displayOrigin(String origin);
    void displayReview(List<String> reviewIds);
    void displayImage(String imageUrl);
    void displayRating(int score);
    void finish();
  }

  interface Presenter extends BasePresenter {
    void setShopInfo();
    void setName(SearchableItem item);
    void setDescription(SearchableItem item);
    void setRoast(SearchableItem item);
    void setOrigin(SearchableItem item);
    void setCurrentRating(SearchableItem item);
    void setNewRating(float rating);
    void setImage(SearchableItem item);
  }
}
