package com.example.lab_ass_app.ui.main.admin.privacy;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lab_ass_app.R;

public class AdminPrivacyFragment extends Fragment {

    private AdminPrivacyViewModel mViewModel;

    public static AdminPrivacyFragment newInstance() {
        return new AdminPrivacyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_privacy, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AdminPrivacyViewModel.class);
        // TODO: Use the ViewModel
    }

}