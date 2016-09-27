package com.mojoteahouse.mojotea.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.adapter.MojoMenuItemAdapter;
import com.mojoteahouse.mojotea.data.MojoMenu;

import java.util.ArrayList;

public class MojoMenuFragment extends Fragment implements MojoMenuItemAdapter.MojoMenuItemClickListener {

    private static final String EXTRA_MOJO_MENU_LIST = "EXTRA_MOJO_MENU_LIST";

    private ArrayList<MojoMenu> mojoMenuList;
    private MojoMenuItemAdapter mojoMenuItemAdapter;
    private MojoMenuClickListener listener;

    public interface MojoMenuClickListener {

        void onMojoMenuClicked(MojoMenu mojoMenu);
    }

    public static MojoMenuFragment newInstance(ArrayList<MojoMenu> mojoMenuList) {
        MojoMenuFragment fragment = new MojoMenuFragment();
        fragment.setRetainInstance(true);
        Bundle data = new Bundle();
        data.putParcelableArrayList(EXTRA_MOJO_MENU_LIST, mojoMenuList);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (MojoMenuClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent activity must implement " + MojoMenuClickListener.class.getName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mojoMenuList = savedInstanceState.getParcelableArrayList(EXTRA_MOJO_MENU_LIST);
        } else {
            mojoMenuList = getArguments().getParcelableArrayList(EXTRA_MOJO_MENU_LIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mojo_menu, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mojo_menu_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mojoMenuItemAdapter = new MojoMenuItemAdapter(getActivity(), mojoMenuList, this);
        recyclerView.setAdapter(mojoMenuItemAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(EXTRA_MOJO_MENU_LIST, mojoMenuList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public void onItemClicked(MojoMenu mojoMenu) {
        listener.onMojoMenuClicked(mojoMenu);
    }

    public void updateMojoMenuList(ArrayList<MojoMenu> newMojoMenuList) {
        mojoMenuItemAdapter.updateMojoMenuList(newMojoMenuList);
    }
}
