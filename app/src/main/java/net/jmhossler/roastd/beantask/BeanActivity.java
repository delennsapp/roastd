package net.jmhossler.roastd.beantask;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import net.jmhossler.roastd.R;
import net.jmhossler.roastd.data.bean.FirebaseRTBeanRepository;
import net.jmhossler.roastd.data.review.FirebaseRTReviewRepository;
import net.jmhossler.roastd.data.shop.FirebaseRTShopRepository;
import net.jmhossler.roastd.data.user.FirebaseRTUserRepository;
import java.util.List;

public class BeanActivity extends AppCompatActivity implements BeanContract.View {

  private BeanContract.Presenter mBeanPresenter;
  private TextView mName;
  private TextView mDescription;
  private TextView mRoast;
  private TextView mOrigin;
  private RatingBar mRatingBar;
  private ImageView mImageView;

  private static final String itemKey = "BEAN_ID";

  public static Intent createIntent(String beanId, Context context) {
    Intent intent = new Intent(context, BeanActivity.class);
    intent.putExtra(itemKey, beanId);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bean);

    String beanId = getIntent().getStringExtra(itemKey);

    mName = (TextView) findViewById(R.id.name);
    mDescription = (TextView) findViewById(R.id.description);
    mRoast = (TextView) findViewById(R.id.roast);
    mOrigin = (TextView) findViewById(R.id.origin);
    mRatingBar = findViewById(R.id.rating_bar);
    mImageView = findViewById(R.id.bean_background);

    BeanPresenter presenter = new BeanPresenter(this, beanId, FirebaseRTUserRepository.getsInstance(),
      FirebaseRTBeanRepository.getInstance(), FirebaseRTShopRepository.getInstance(),
      FirebaseRTReviewRepository.getInstance(), FirebaseAuth.getInstance());

    presenter.start();

    mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
      @Override
      public void onRatingChanged(RatingBar ratingBar, float rating,
                                  boolean fromUser) {
        if (fromUser) {
          presenter.setNewRating(rating);
        }
      }
    });
  }

  @Override
  public void displayName(String name) {
    mName.setText(name);
  }

  @Override
  public void displayDescription(String description) {
    mDescription.setText(description);
  }

  @Override
  public void displayShop(String shopId) {

  }

  @Override
  public void displayRoast(String roast) {
    mRoast.setText("Roast: " + roast);
  }

  @Override
  public void displayOrigin(String origin) {
    mOrigin.setText("Origin: " + origin);
  }

  @Override
  public void displayReview(List<String> reviewIds) {

  }

  @Override
  public void displayImage(String imageUrl) {
    Glide.with(this).load(imageUrl).thumbnail(0.5f).into(mImageView);
  }

  @Override
  public void setPresenter(BeanContract.Presenter presenter) {
    mBeanPresenter = presenter;
  }

  @Override
  public void displayRating(int score) {
    mRatingBar.setRating(score);
  }
}
