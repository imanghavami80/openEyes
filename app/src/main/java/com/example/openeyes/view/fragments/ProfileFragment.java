package com.example.openeyes.view.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.openeyes.R;
import com.example.openeyes.databinding.FragmentProfileBinding;
import com.example.openeyes.utility.MySharedPreferences;
import com.example.openeyes.view.activities.ContactUsActivity;
import com.example.openeyes.view.activities.SplashActivity;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private FragmentProfileBinding binding;
    private MySharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        binding.constLayoutLogoutProfile.setOnClickListener(this);
        binding.constLayoutContactUsProfile.setOnClickListener(this);

        sharedPreferences = new MySharedPreferences(requireContext());

        binding.txtProfileEmail.setText(Html.fromHtml("<u>Email:</u>" + " " + sharedPreferences.getUserEmail()));
        binding.txtProfileNickName.setText(Html.fromHtml("<u>NickName:</u>" + " " + sharedPreferences.getUserNickName()));

        return binding.getRoot();
    }

    /****************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.constLayoutLogoutProfile.getId()) {
            showLogoutDialog();

        } else if (view.getId() == binding.constLayoutContactUsProfile.getId()) {
            goToContactUsActivity();

        }

    }

    private void goToSplashActivity() {
        Intent intent = new Intent(requireContext(), SplashActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );
        requireActivity().finish();

    }

    private void goToContactUsActivity() {
        Intent intent = new Intent(requireContext(), ContactUsActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );

    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Do you want to logout?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            sharedPreferences.clearAllData();
            goToSplashActivity();

        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}