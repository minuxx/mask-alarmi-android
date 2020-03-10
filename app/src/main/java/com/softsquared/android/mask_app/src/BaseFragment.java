package com.softsquared.android.mask_app.src;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.Fragment;
import com.softsquared.android.mask_app.R;


public abstract class BaseFragment extends Fragment implements View.OnClickListener{
    public Context mContext;
    public ProgressDialog mProgressDialog;
    public AppCompatDialog progressDialog;

    public BaseFragment(Context context){
        this.mContext = context;
    }


    public void setContext(Context context) {
        this.mContext = context;
    }

    public void showCustomToast(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog(Activity activity) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }


    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public abstract void setComponentView(View v);
}

