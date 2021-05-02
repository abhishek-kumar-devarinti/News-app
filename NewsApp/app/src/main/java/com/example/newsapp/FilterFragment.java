package com.example.newsapp;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    List<Bitmap> emptyList = new ArrayList<>();

    public FilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
        Button filterButton = rootView.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> filterNames = new ArrayList<>();
                ChipGroup filterChipGroup = rootView.findViewById(R.id.filter_chip_group);
                List<Integer> ids = filterChipGroup.getCheckedChipIds();
                for (Integer id : ids){
                    Chip chip = rootView.findViewById(id);
                    String filterText = chip.getText().toString().trim();
                    filterNames.add(filterText);
                }

                NewsViewModel.filters.setValue(setFilterIdsWithNames(filterNames));

            }
        });
        return rootView;
    }

    private List<Integer> setFilterIdsWithNames(List<String> filterNames) {
        List<Integer> filterNameIds = new ArrayList<>();
        for(String filterName : filterNames){
            switch (filterName){
                case "Business":
                    filterNameIds.add(0);
                    break;
                case "Politics":
                    filterNameIds.add(1);
                    break;
                case "Education":
                    filterNameIds.add(2);
                    break;
                case "Fashion":
                    filterNameIds.add(3);
                    break;
                case "Film":
                    filterNameIds.add(4);
                    break;
                case "Sport":
                    filterNameIds.add(5);
                    break;
                case "World":
                    filterNameIds.add(6);
                    break;
                case "Job Advice":
                    filterNameIds.add(7);
                    break;
                case "Weather":
                    filterNameIds.add(8);
                    break;
                case "Environment":
                    filterNameIds.add(9);
                    break;
            }
        }
        return filterNameIds;
    }
}