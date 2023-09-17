package com.example.openeyes.view.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.openeyes.R;
import com.example.openeyes.databinding.FragmentBottomSheetRulesBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetRulesFragment extends BottomSheetDialogFragment {

    private FragmentBottomSheetRulesBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_rules, container, false);

        String termsOfUse =
                "1. Introduction\n"
                + "By using this application, you agree to be bound by the following terms and conditions.\n\n"

                + "2. User Responsibilities\n"
                + "You are responsible for providing accurate information and for the content you upload.\n\n"

                + "3. Content Guidelines\n"
                + "You may not upload content that violates our guidelines, including hate speech or copyrighted materials.\n\n"

                + "4. Privacy Policy\n"
                + "Please refer to our Privacy Policy for information on data collection and usage.\n\n"

                + "5. User Conduct\n"
                + "Users are expected to behave respectfully and not engage in harmful or illegal activities.\n\n"

                + "6. Reporting Defects\n"
                + "You can report defects with text, images, and audio. Users can vote on defect importance.\n\n"

                + "7. Moderation and Enforcement\n"
                + "We may moderate and remove content that violates these terms. Violations may result in account suspension.\n\n"

                + "8. Liability and Disclaimers\n"
                + "We are not liable for issues arising from app usage. User-generated content does not represent our views.\n\n"

                + "9. Changes to Terms\n"
                + "We may update these terms; users will be notified of changes.\n\n"

                + "10. Termination\n"
                + "We can terminate accounts for violations of these terms.\n\n"

                + "11. Governing Law and Jurisdiction\n"
                + "Disputes will be resolved in the [Specify Jurisdiction].\n\n"

                + "12. Contact Information\n"
                + "For questions, contact us at [Your Contact Information].\n\n";

        binding.txtReadingPsalmsSheet.setText(termsOfUse);

        return binding.getRoot();
    }
}