package net.jmhossler.roastd.maintask;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import net.jmhossler.roastd.R;
import net.jmhossler.roastd.logintask.LoginActivity;
import net.jmhossler.roastd.profiletask.ProfileActivity;
import net.jmhossler.roastd.searchtask.SearchActivity;


/**
 * Main UI for the Main Screen. Users can navigate between locations in the application
 */
public class MainFragment extends Fragment implements MainContract.View {

  private static final int RC_SIGN_OUT = 9002;
  private static final int RC_SIGN_IN = 9001;

  private Button mSearchButton;
  private Button mProfileButton;

  private MainContract.Presenter mPresenter;

  public static MainFragment newInstance() { return new MainFragment(); }

  public MainFragment() {
    // Required empty public constructor
  }

  @Override
  public void setPresenter(@NonNull MainContract.Presenter presenter) {
    mPresenter = presenter;
  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.start();
  }

  @Nullable
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public boolean needToLogin() {
    return GoogleSignIn.getLastSignedInAccount(getActivity()) == null;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.main_fragment, container, false);

    mSearchButton = (Button) root.findViewById(R.id.start_search_activity_button);
    mSearchButton.setOnClickListener(v -> startActivity(new Intent(getContext(), SearchActivity.class)));

    mProfileButton = (Button) root.findViewById(R.id.start_profile_activity_button);
    mProfileButton.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), ProfileActivity.class), RC_SIGN_OUT));

    return root;
  }


  public void startLogin() {
    startActivityForResult(new Intent(getContext(), LoginActivity.class), RC_SIGN_IN);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    mPresenter.result(requestCode, resultCode);
    super.onActivityResult(requestCode, resultCode, data);

  }

  public void finish() {
    getActivity().finish();
  }
}
