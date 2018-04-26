package net.jmhossler.roastd.drinktask;

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
import net.jmhossler.roastd.data.drink.FirebaseRTDrinkRepository;
import net.jmhossler.roastd.data.review.FirebaseRTReviewRepository;
import net.jmhossler.roastd.data.shop.FirebaseRTShopRepository;
import net.jmhossler.roastd.data.user.FirebaseRTUserRepository;

import java.text.DecimalFormat;
import java.util.List;

public class DrinkActivity extends AppCompatActivity implements DrinkContract.View {

  private DrinkContract.Presenter mDrinkPresenter;
  private TextView mName;
  private TextView mDescription;
  private TextView mType;
  private TextView mPrice;
  private RatingBar mRatingBar;
  private ImageView mImageView;

  private static final String itemKey = "DRINK_ID";

  public static Intent createIntent(String drinkId, Context context) {
    Intent intent = new Intent(context, DrinkActivity.class);
    intent.putExtra(itemKey, drinkId);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drink);

    String drinkId = getIntent().getStringExtra(itemKey);

    mName = (TextView) findViewById(R.id.name);
    mDescription = (TextView) findViewById(R.id.description);
    mType = (TextView) findViewById(R.id.type);
    mPrice = (TextView) findViewById(R.id.price);
    mRatingBar = findViewById(R.id.rating_bar);
    mImageView = findViewById(R.id.drink_background);

    DrinkPresenter presenter = new DrinkPresenter(this, drinkId, FirebaseRTUserRepository.getsInstance(),
      FirebaseRTDrinkRepository.getInstance(), FirebaseRTShopRepository.getInstance(),
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
  public void displayType(String type) {
    mType.setText("Brew: " + type);
  }

  @Override
  public void displayPrice(Double price) {
    mPrice.setText("Price: $" + String.format("%.2f", price));
  }

  @Override
  public void displayReview(List<String> reviewIds) {

  }

  @Override
  public void displayImage(String imageUrl) {
    Glide.with(this).load(imageUrl).thumbnail(0.5f).into(mImageView);
  }

  @Override
  public void setPresenter(DrinkContract.Presenter presenter) {
    mDrinkPresenter = presenter;
  }

  @Override
  public void displayRating(int score) {
    mRatingBar.setRating(score);
  }
}
