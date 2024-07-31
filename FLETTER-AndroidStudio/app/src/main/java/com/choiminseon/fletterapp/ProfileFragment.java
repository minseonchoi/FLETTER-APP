package com.choiminseon.fletterapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.api.UserApi;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.ProfileRes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView txtEmail = view.findViewById(R.id.txtEmail);
        TextView txtUserName = view.findViewById(R.id.txtName);
        TextView txtPhone = view.findViewById(R.id.txtPhone);

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        UserApi api = retrofit.create(UserApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");

        Call<ProfileRes> call = api.profile("Bearer " + token);
        call.enqueue(new Callback<ProfileRes>() {
            @Override
            public void onResponse(Call<ProfileRes> call, Response<ProfileRes> response) {

                if (response.isSuccessful()) {
                    ProfileRes profileRes = response.body();
                    if (profileRes != null) {
                        ProfileRes.Item profileResItem = profileRes.user.get(0);
                        txtEmail.setText(profileResItem.email);

                        SharedPreferences sp = getActivity().getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username", profileResItem.userName);
                        editor.commit();

                        txtUserName.setText(profileResItem.userName);
                        txtPhone.setText(profileResItem.phone);
                    } else {
                        Toast.makeText(getActivity(), "Empty response body", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "네트워크 에러", Toast.LENGTH_SHORT).show();
                    Log.i("PROFILE", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ProfileRes> call, Throwable throwable) {
                Toast.makeText(getActivity(), "네트워크 에러", Toast.LENGTH_SHORT).show();
                Log.i("PROFILE", throwable.toString());
            }
        });





        return view;
    }
}