package com.choiminseon.fletterapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.choiminseon.fletterapp.adapter.OrderPackageAdapter;
import com.choiminseon.fletterapp.api.FlowerApi;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.model.PackageType;
import com.choiminseon.fletterapp.model.PackageTypeList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopOrderFragment extends Fragment {

    RecyclerView recyclerView;
    OrderPackageAdapter adapter;
    ArrayList<PackageType> packageTypeArrayList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TopOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopOrderFragment newInstance(String param1, String param2) {
        TopOrderFragment fragment = new TopOrderFragment();
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
        View view = inflater.inflate(R.layout.fragment_top_order, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFlower);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getPackageType();






        return view;
    }

    private void getPackageType() {

        packageTypeArrayList.clear();

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        FlowerApi api = retrofit.create(FlowerApi.class);
        Call<PackageTypeList> call = api.getPackageList();

        call.enqueue(new Callback<PackageTypeList>() {
            @Override
            public void onResponse(Call<PackageTypeList> call, Response<PackageTypeList> response) {

                if (response.isSuccessful()) {
                    PackageTypeList packageTypeList = response.body();

                    packageTypeArrayList.addAll(packageTypeList.packages);
                    adapter = new OrderPackageAdapter(getActivity(), packageTypeArrayList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<PackageTypeList> call, Throwable throwable) {
                Toast.makeText(getActivity(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }


}