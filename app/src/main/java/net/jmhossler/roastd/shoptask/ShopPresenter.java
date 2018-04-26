package net.jmhossler.roastd.shoptask;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import net.jmhossler.roastd.data.review.Review;
import net.jmhossler.roastd.data.review.ReviewDataSource;
import net.jmhossler.roastd.data.searchableItem.BaseDataSource;
import net.jmhossler.roastd.data.searchableItem.SearchableItem;
import net.jmhossler.roastd.data.searchableItem.SearchableItemDataSource;
import net.jmhossler.roastd.data.shop.Shop;
import net.jmhossler.roastd.data.shop.ShopDataSource;
import net.jmhossler.roastd.data.user.User;
import net.jmhossler.roastd.data.user.UserDataSource;
import net.jmhossler.roastd.viewtask.BaseSearchableItemPresenter;
import net.jmhossler.roastd.viewtask.SearchableItemListContract;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.support.constraint.Constraints.TAG;

public class ShopPresenter extends BaseSearchableItemPresenter implements ShopContract.Presenter {
  protected String mShopId;
  protected ShopContract.View mView;
  protected ShopDataSource mShopDataSource;
  protected ReviewDataSource mReviewDataSource;
  protected Shop mShop;


  public ShopPresenter(ShopContract.View view, SearchableItemListContract.View silf, String shopId, UserDataSource userDataSource, ShopDataSource shopDataSource,
                       SearchableItemDataSource searchableItemDataSource, ReviewDataSource reviewDataSource,
                       FirebaseAuth firebaseAuth) {
    super(silf, firebaseAuth, searchableItemDataSource, userDataSource);
    mShopId = shopId;
    mView = view;
    mShopDataSource = shopDataSource;
    mReviewDataSource = reviewDataSource;
    mView.setPresenter(this);
  }

  public void userLoaded() {
    if (mUser == null) {
      Log.d(TAG, "User " + mAuth.getUid() + " does not exist! This is bad!");
      mView.finish();
    }

    mShopDataSource.get(mShopId, new BaseDataSource.GetCallback() {
      @Override
      public void onLoaded(SearchableItem item) {
        mShop = (Shop) item;
        shopLoaded();
      }
      @Override
      public void onDataNotAvailable() { }
    });
  }

  public void shopLoaded() {
    setAddress(mShop.getAddress());
    setName(mShop.getName());
    setDescription(mShop.getDescription());
    setMapsUrl(mShop.getAddress());
    setCurrentRating(mShop);
    setImage(mShop.getImage());
    setConsumables(new ArrayList<String>(mShop.getItemUUIDs().keySet()));
  }

  @Override
  public void start() {
    super.start();
  }

  @Override
  public void setConsumables(List<String> ids) {
    mSearchableItemDataStore.getSearchableItems(ids, new SearchableItemDataSource.LoadSearchableItemsCallback() {
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
  public void setName(String name) {
    mView.displayName(name);
  }

  @Override
  public void setImage(String imageUrl) {
    if(imageUrl != null && !imageUrl.equals("")) {
      mView.displayImage(imageUrl);
    }
  }

  @Override
  public void setDescription(String description) {
    mView.displayDescription(description);
  }

  @Override
  public void setAddress(String address) {
    mView.displayAddress(address);
  }

  @Override
  public void setMapsUrl(String address) {
    mView.createMapsLink(address);
  }

  @Override
  public void setCurrentRating(SearchableItem item) {
    mReviewDataSource.getReviews(new ReviewDataSource.LoadReviewsCallback() {
      @Override
      public void onReviewsLoaded(List<Review> reviews) {
        Boolean reviewFound = false;
        for (Review r : reviews) {
          if (item.getReviewIds().get(r.getUuid()) != null && r.getUserUuid().equals(mUser.getUuid())) {
            reviewFound = true;
            mView.displayRating(r.getScore());
          }
        }
        // User hasn't reviewed this item yet
        if (!reviewFound) {
          // Set the empty rating
          mView.displayRating(0);
        }
      }
      @Override
      public void onDataNotAvailable() { Log.d(TAG, "No reviews found!"); }
    });
  }

  @Override
  public void setNewRating(float rating) {
    mUserDataStore.getUser(mAuth.getUid(), new UserDataSource.GetUserCallback() {
      @Override
      public void onUserLoaded(User user) {
        if (user == null) {
          Log.d(TAG, "User " + mAuth.getUid() + " does not exist! This is bad!");
          mView.finish();
        }

        mUser = user;

        mShopDataSource.get(mShopId, new BaseDataSource.GetCallback() {
          @Override
          public void onLoaded(SearchableItem item) {
            // Try to check if the user has already reviewed this item
            mReviewDataSource.getReviews(new ReviewDataSource.LoadReviewsCallback() {
              @Override
              public void onReviewsLoaded(List<Review> reviews) {
                Boolean reviewFound = false;
                for (Review r : reviews) {
                  if (item.getReviewIds().get(r.getUuid()) != null && r.getUserUuid().equals(mUser.getUuid())) {
                    reviewFound = true;
                    r.setScore((int) rating);
                    mReviewDataSource.saveReview(r);
                  }
                }
                // User hasn't reviewed this item yet
                if (!reviewFound) {
                  // Create a review and save it
                  String reviewID = UUID.randomUUID().toString();
                  mReviewDataSource.saveReview(new Review(reviewID, mUser.getUuid(), (int) rating));
                  item.addReviewId(reviewID);
                  mShopDataSource.saveShop((Shop) item);
                }
              }
              @Override
              public void onDataNotAvailable() { Log.d(TAG, "No reviews found!"); }
            });
          }
          @Override
          public void onDataNotAvailable() { Log.d(TAG, "Could not find shop of UUID: " + mShopId); }
        });
      }
      @Override
      public void onDataNotAvailable() { Log.d(TAG, "Error with Firebase Instance"); }
    });
  }
}
