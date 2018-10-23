package com.kid.william.finddoctor.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kid.william.finddoctor.Model.entidad.UserInfo;
import com.kid.william.finddoctor.R;
import com.kid.william.finddoctor.utilidades.Common;


public class MainFragment extends Fragment {

    private DatabaseReference mUser;
    private static final String TAG = MainFragment.class.getName();
    private String key_Uid;
    private Query qUsuario;

    public static Fragment createInstance(String valor) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(Common.TAG_KEY_UID, valor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key_Uid = getArguments().getString(Common.TAG_KEY_UID);
        mUser = FirebaseDatabase.getInstance().getReference(Common.TABLE_USER_INFO);
        qUsuario = mUser.orderByKey().equalTo(key_Uid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_main, container, false);
        final TextView nombre = view.findViewById(R.id.txtName_user);
        final TextView apellido = view.findViewById(R.id.txtLastName_user);
        final TextView celular = view.findViewById(R.id.txtPhone_user);

        qUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserInfo user = snapshot.getValue(UserInfo.class);
                    nombre.setText(user.getName());
                    apellido.setText(user.getLastName());
                    celular.setText(String.valueOf(user.getPhone()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG,databaseError.getMessage());
            }
        });

        return view;
    }

}
