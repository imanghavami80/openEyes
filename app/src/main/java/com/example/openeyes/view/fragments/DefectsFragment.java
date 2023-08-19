package com.example.openeyes.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.openeyes.R;
import com.example.openeyes.adapter.ReportedDefectAdapter;
import com.example.openeyes.databinding.FragmentDefectsBinding;
import com.example.openeyes.model.Defect;
import com.example.openeyes.view.activities.AddDefectActivity;

import java.util.ArrayList;

public class DefectsFragment extends Fragment implements View.OnClickListener {

    private FragmentDefectsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_defects, container, false);

        binding.fabAddDefect.setOnClickListener(this);

        ArrayList<Defect> items = new ArrayList<>();
        items.add(new Defect("cover/channel", "Mozartstrasse 40", "For about 10 days, the Hofer company has been storing the residual waste in front of the unoccupied house at Rizzistrasse 25, apparently with the intention of preventing other cars from parking, although there is no parking or stopping ban at this point.", 105, 2.5f));
        items.add(new Defect("cover/channel", "Mozartstrasse 40", "For about 10 days, the Hofer company has been storing the residual waste in front of the unoccupied house at Rizzistrasse 25, apparently with the intention of preventing other cars from parking, although there is no parking or stopping ban at this point.", 105, 2.5f));
        items.add(new Defect("cover/channel", "Mozartstrasse 40", "For about 10 days, the Hofer company has been storing the residual waste in front of the unoccupied house at Rizzistrasse 25, apparently with the intention of preventing other cars from parking, although there is no parking or stopping ban at this point.", 105, 2.5f));
        items.add(new Defect("cover/channel", "Mozartstrasse 40", "For about 10 days, the Hofer company has been storing the residual waste in front of the unoccupied house at Rizzistrasse 25, apparently with the intention of preventing other cars from parking, although there is no parking or stopping ban at this point.", 105, 2.5f));
        items.add(new Defect("cover/channel", "Mozartstrasse 40", "For about 10 days, the Hofer company has been storing the residual waste in front of the unoccupied house at Rizzistrasse 25, apparently with the intention of preventing other cars from parking, although there is no parking or stopping ban at this point.", 105, 2.5f));
        items.add(new Defect("cover/channel", "Mozartstrasse 40", "For about 10 days, the Hofer company has been storing the residual waste in front of the unoccupied house at Rizzistrasse 25, apparently with the intention of preventing other cars from parking, although there is no parking or stopping ban at this point.", 105, 2.5f));
        items.add(new Defect("cover/channel", "Mozartstrasse 40", "For about 10 days, the Hofer company has been storing the residual waste in front of the unoccupied house at Rizzistrasse 25, apparently with the intention of preventing other cars from parking, although there is no parking or stopping ban at this point.", 105, 2.5f));
        items.add(new Defect("cover/channel", "Mozartstrasse 40", "For about 10 days, the Hofer company has been storing the residual waste in front of the unoccupied house at Rizzistrasse 25, apparently with the intention of preventing other cars from parking, although there is no parking or stopping ban at this point.", 105, 2.5f));

        initDefectsRecycler(items);

        return binding.getRoot();

    }

    /****************************************************/

    private void initDefectsRecycler(ArrayList<Defect> reportedDefects) {
        ReportedDefectAdapter adapter = new ReportedDefectAdapter(requireContext(), reportedDefects);
        binding.recyclerDefects.setAdapter(adapter);
        binding.recyclerDefects.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

    }

    private void goToAddDefectActivity() {
        Intent intent = new Intent(requireContext(), AddDefectActivity.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.fabAddDefect.getId()) {
            goToAddDefectActivity();

        }
    }

}