package net.jmhossler.roastd.drinktask;


import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import net.jmhossler.roastd.data.drink.Drink;
import net.jmhossler.roastd.data.drink.DrinkDataSource;
import net.jmhossler.roastd.data.review.Review;
import net.jmhossler.roastd.data.review.ReviewDataSource;
import net.jmhossler.roastd.data.searchableItem.BaseDataSource;
import net.jmhossler.roastd.data.searchableItem.SearchableItem;
import net.jmhossler.roastd.data.shop.ShopDataSource;
import net.jmhossler.roastd.data.user.User;
import net.jmhossler.roastd.data.user.UserDataSource;

import java.util.List;
import java.util.UUID;

import static android.support.constraint.Constraints.TAG;

public class DrinkPresenter implements DrinkContract.Presenter {
  private final DrinkDataSource mDrinkDataSource;
  private final ShopDataSource mShopDataSource;
  private final ReviewDataSource mReviewDataSource;
  private final UserDataSource mUserDataSource;
  private DrinkContract.View mView;
  private final String mDrinkId;
  private FirebaseAuth mAuth;
  private User mUser;

  public DrinkPresenter(DrinkContract.View view, String drinkId, UserDataSource userDataSource,
                        DrinkDataSource drinkDataSource, ShopDataSource shopDataSource,
                        ReviewDataSource reviewDataSource, FirebaseAuth firebaseAuth) {
    mView = view;
    mView.setPresenter(this);
    mDrinkId = drinkId;
    mDrinkDataSource = drinkDataSource;
    mShopDataSource = shopDataSource;
    mReviewDataSource = reviewDataSource;
    mUserDataSource = userDataSource;
    mAuth = firebaseAuth;
  }

  @Override
  public void start() {
    mUserDataSource.getUser(mAuth.getUid(), new UserDataSource.GetUserCallback() {
      @Override
      public void onUserLoaded(User user) {
        if (user == null) {
          Log.d(TAG, "User " + mAuth.getUid() + " does not exist! This is bad!");
          mView.finish();
        }

        mUser = user;

        mDrinkDataSource.get(mDrinkId, new BaseDataSource.GetCallback() {
          @Override
          public void onLoaded(SearchableItem item) {
            setName(item);
            setDescription(item);
            setType(item);
            setPrice(item);
            setCurrentRating(item);
          }
          @Override
          public void onDataNotAvailable() { }
        });
      }

      @Override
      public void onDataNotAvailable() { Log.d(TAG, "Error with Firebase Instance"); }
    });

  }

  @Override
  public void setShopInfo() {
    // I'm imagining this will start a list fragment with one item
  }

  @Override
  public void setName(SearchableItem item) {
    if (item == null) {
      Log.d(TAG, "Drink id " + mDrinkId + " does not exist");
    }
    String name = item.getName();
    if (name == null) {
      name = "uh oh";
    }
    mView.displayName(name);
  }

  @Override
  public void setDescription(SearchableItem item) {
    String description = item.getDescription();
    if (description == null) {
      description = "something bad";
    }
    mView.displayDescription(description);
  }

  @Override
  public void setType(SearchableItem item) {
    Drink drink = (Drink) item;
    mView.displayType(drink.getType());
  }

  @Override
  public void setPrice(SearchableItem item) {
    Drink drink = (Drink) item;
    mView.displayPrice(drink.getPrice());
  }

  @Override
  public void setImage(SearchableItem item) {
    Drink drink = (Drink) item;
    String imageUrl = item.getImage();
    if (imageUrl != null) {
      mView.displayImage(imageUrl);
    }
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
    mUserDataSource.getUser(mAuth.getUid(), new UserDataSource.GetUserCallback() {
      @Override
      public void onUserLoaded(User user) {
        if (user == null) {
          Log.d(TAG, "User " + mAuth.getUid() + " does not exist! This is bad!");
          mView.finish();
        }

        mUser = user;

        mDrinkDataSource.get(mDrinkId, new BaseDataSource.GetCallback() {
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
                  mDrinkDataSource.saveDrink((Drink) item);
                }
              }
              @Override
              public void onDataNotAvailable() { Log.d(TAG, "No reviews found!"); }
            });
          }
          @Override
          public void onDataNotAvailable() { Log.d(TAG, "Could not find drink of UUID: " + mDrinkId); }
        });
      }
      @Override
      public void onDataNotAvailable() { Log.d(TAG, "Error with Firebase Instance"); }
    });
  }

}
