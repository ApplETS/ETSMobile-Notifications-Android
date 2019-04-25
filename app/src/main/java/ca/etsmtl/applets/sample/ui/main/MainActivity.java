package ca.etsmtl.applets.sample.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import ca.etsmtl.applets.sample.R;
import ca.etsmtl.applets.sample.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private Button loginButton;
    private TextView usernameTv;
    private TextView domainTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewModel = ViewModelProviders.of(this, new MainViewModelFactory(this))
                .get(MainViewModel.class);
        loginButton = findViewById(R.id.btn_login);
        usernameTv = findViewById(R.id.tv_username);
        domainTv = findViewById(R.id.tv_domain);
        subscribeUI();
    }


    private void subscribeUI() {
        mainViewModel.getMainState().observe(this, mainState -> {
            if (mainState == null) {
                return;
            }

            setViewVisible(loginButton, mainState.isLoginButtonVisible());
            setViewVisible(usernameTv, mainState.isUniversalCodeVisible());
            setViewVisible(domainTv, mainState.isDomainVisible());
            usernameTv.setText(mainState.getUniversalCodeText());
            domainTv.setText(mainState.getDomainText());
        });

        mainViewModel.getNavigateToLogin().observe(this, aVoid -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);

            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> mainViewModel.clickLoginButton());

        getLifecycle().addObserver(mainViewModel);
    }

    private void setViewVisible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

}