package com.minux.mask_alarmi.src;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.minux.mask_alarmi.R;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    public ProgressDialog mProgressDialog;

    public void showCustomToast(final String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public void initViews(){}

    @Override
    public void onClick(View v) {

    }
}
