package net.jmhossler.roastd.maintask;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import net.jmhossler.roastd.data.user.User;
import net.jmhossler.roastd.data.user.UserDataSource;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class MainPresenter implements MainContract.Presenter {
  private static final int RC_SIGN_IN = 9001;
  private static final int RC_SIGN_OUT = 9002;
  private static final int LOGGED_OUT = RESULT_OK;

  private final MainContract.View mMainView;
  private FirebaseAuth mAuth;
  private UserDataSource mUserDataSource;
  private Calendar mCalendar;

  public MainPresenter(MainContract.View mainView, FirebaseAuth auth,
                       UserDataSource userDataSource, Calendar calendar) {
    mMainView = mainView;
    mUserDataSource = userDataSource;
    mAuth = auth;
    mCalendar = calendar;
    mMainView.setPresenter(this);
  }

  @Override
  public void start() {
    mAuth.addAuthStateListener(firebaseAuth -> {
      if(mAuth.getCurrentUser() != null) {
        mMainView.enableButtons();
        setCurrentPhotoURL();
        setDisplayName();
        setFirstName();
      }
    });

    if (mMainView.needToLogin()) {
      mMainView.disableButtons();
      mMainView.startLogin();
    }

    setGreetingLabel();
  }

  @Override
  public void result(int requestCode, int resultCode) {
    if (requestCode == RC_SIGN_OUT && resultCode == LOGGED_OUT) {
      mMainView.startLogin();
    }
  }

  @Override
  public void setDisplayName() {
  mMainView.displayUserFullName(mAuth.getCurrentUser().getDisplayName());
  }

  @Override
  public void setFirstName() {
   mMainView.displayUserFirstName(mAuth.getCurrentUser().getDisplayName().split("\\s+")[0]);
  }

  @Override
  public void setCurrentPhotoURL() {
    mMainView.displayUserImage(mAuth.getCurrentUser().getPhotoUrl().toString());
  }

  @Override
  public void setGreetingLabel() {
    int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
    String greeting = mMainView.getMorningGreeting();

    if (hour > 17) {
      greeting = mMainView.getEveningGreeting();
    } else if (hour > 11) {
      greeting = mMainView.getAfternoonGreeting();
    }
    mMainView.displayGreetingLabel(greeting);
  }
}
