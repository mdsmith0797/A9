package com.se319s18a9.util3d.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.se319s18a9.util3d.backend.Project;
import com.se319s18a9.util3d.backend.User;
import com.se319s18a9.util3d.R;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    LinearLayout rootLayout;

    Button createProjectButton;
    Button openProjectButton;
    Button settingsButton;
    Button logoutButton;

    ListView listView;
    ArrayList<Project> projects;
    ListViewAdapter adapter;

    LoadingDialogFragment loadingDialogFragment;
    Exception[] loadProjectListException;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        projects = new ArrayList<>();

        loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setCancelable(false);

        Bundle messageArgument = new Bundle();
        messageArgument.putString("message", "Loading Projects");

        loadingDialogFragment.setArguments(messageArgument);
        loadProjectListException = User.getInstance().getMyPersonalProjects(projects, this::updateFileList);
        loadingDialogFragment.show(getActivity().getFragmentManager(), null);
    }

    public void updateFileList(){
        loadingDialogFragment.dismiss();
        adapter.notifyDataSetChanged();

        if(loadProjectListException[0] != null) {
            Toast.makeText(getContext(), loadProjectListException[0].getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Set toolbar title
        getActivity().setTitle(R.string.global_fragmentName_dashboard);

        // Initialize components and bind listeners
        rootLayout = v.findViewById(R.id.fragment_dashboard_linearLayout_root);

        createProjectButton = v.findViewById(R.id.fragment_dashboard_button_createProject);
        createProjectButton.setOnClickListener(this);

        openProjectButton = v.findViewById(R.id.fragment_dashboard_button_openProject);
        openProjectButton.setOnClickListener(this);

        settingsButton = v.findViewById(R.id.fragment_dashboard_button_settings);
        settingsButton.setOnClickListener(this);

        logoutButton = v.findViewById(R.id.fragment_dashboard_button_logout);
        logoutButton.setOnClickListener(this);

        listView = v.findViewById(R.id.fragment_dashboard_listview);

        // Create the Dashboard ListView adapter
        adapter = new ListViewAdapter(v.getContext(), projects);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Fragment mapFragment = new MapFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("LoadMap", true);
            bundle.putString("ProjectName", adapterView.getItemAtPosition(i).toString());
            mapFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frameLayout_root, mapFragment).addToBackStack(null).commit();
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        switch(v.getId()) {
            // ----- Create Project Button -----
            case R.id.fragment_dashboard_button_createProject:
                Fragment createProjectFragment = new CreateProjectFragment();
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_frameLayout_root, createProjectFragment);
                fragmentTransaction.addToBackStack("dashboardIdentifier").commit();
                break;
            // ----- Open Project Button -----
            case R.id.fragment_dashboard_button_openProject:
                getActivity().setTitle(R.string.global_fragmentName_openProject);
                Fragment openProjectFragment = new OpenProjectFragment();
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_frameLayout_root, openProjectFragment);
                fragmentTransaction.addToBackStack("dashboardIdentifier").commit();
                break;
            // ----- Settings Button -----
            case R.id.fragment_dashboard_button_settings:
                Fragment settingsFragment = new SettingsFragment();
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_frameLayout_root, settingsFragment);
                fragmentTransaction.addToBackStack(null).commit();
                break;
            // ----- Logout Button -----
            case R.id.fragment_dashboard_button_logout:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
                alertDialogBuilder.setMessage(getString(R.string.s_fragment_dashboard_alertMessage_logOutPrompt));

                // Bind actions to "Cancel" and "Reset Password" dialog buttons.
                alertDialogBuilder
                        .setPositiveButton("Log Out",
                                (dialog, id) -> {
                                    User.getInstance().signOut();
                                    Toast.makeText(getContext(), R.string.s_fragment_dashboard_alertMessage_loggedOut, Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                })
                        .setNegativeButton("Cancel",
                                (dialog, id) -> dialog.cancel());

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                break;
        }
    }

    public class ListViewAdapter extends BaseAdapter {
        Context context;
        ArrayList<Project> list;

        public ListViewAdapter(Context context, ArrayList<Project> list){
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            if(list.size()>3){
                return  3;
            }

            return list.size();
        }

        @Override
        public Project getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(this.context).inflate(R.layout.fragment_openproject_nameview, viewGroup, false);

            TextView textViewItemName = view.findViewById(R.id.fragment_openproject_nameview);
            textViewItemName.setText(list.get(i).name);

            return view;
        }
    }
}
