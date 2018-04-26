package net.jmhossler.roastd.beantask;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import net.jmhossler.roastd.data.bean.Bean;
import net.jmhossler.roastd.data.bean.BeanDataSource;
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


public class BeanPresenter implements BeanContract.Presenter {
  private final BeanDataSource mBeanDataSource;
  private final ShopDataSource mShopDataSource;
  private final ReviewDataSource mReviewDataSource;
  private BeanContract.View mView;
  private final String mBeanId;
  private FirebaseAuth mAuth;
  private User mUser;
  private final UserDataSource mUserDataSource;

  public BeanPresenter(BeanContract.View view, String beanId, UserDataSource userDataSource,
                       BeanDataSource beanDataSource, ShopDataSource shopDataSource,
                       ReviewDataSource reviewDataSource, FirebaseAuth firebaseAuth) {
    mView = view;
    mBeanId = beanId;
    mBeanDataSource = beanDataSource;
    mShopDataSource = shopDataSource;
    mReviewDataSource = reviewDataSource;
    mUserDataSource = userDataSource;
    mAuth = firebaseAuth;
    mView.setPresenter(this);
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

        mBeanDataSource.get(mBeanId, new BaseDataSource.GetCallback() {
          @Override
          public void onLoaded(SearchableItem item) {
            setName(item);
            setDescription(item);
            setRoast(item);
            setOrigin(item);
            setCurrentRating(item);
            setImage(item);
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

  }

  @Override
  public void setName(SearchableItem item) {
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
  public void setRoast(SearchableItem item) {
    Bean bean = (Bean) item;
    mView.displayRoast(bean.getRoastType());
  }

  @Override
  public void setOrigin(SearchableItem item) {
    Bean bean = (Bean) item;
    mView.displayOrigin(bean.getOrigin());
  }

  @Override
  public void setImage(SearchableItem item) {
    Bean bean = (Bean) item;
    String imageUrl = item.getImage();
    if (imageUrl != null && !imageUrl.equals("")) {
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

        mBeanDataSource.get(mBeanId, new BaseDataSource.GetCallback() {
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
                  mBeanDataSource.saveBean((Bean) item);
                }
              }
              @Override
              public void onDataNotAvailable() { Log.d(TAG, "No reviews found!"); }
            });
          }
          @Override
          public void onDataNotAvailable() { Log.d(TAG, "Could not find bean of UUID: " + mBeanId); }
        });
      }
      @Override
      public void onDataNotAvailable() { Log.d(TAG, "Error with Firebase Instance"); }
    });
  }
}
