package com.se319s18a9.util3d.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.se319s18a9.util3d.backend.Project;
import com.se319s18a9.util3d.backend.User;
import com.se319s18a9.util3d.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;

public class OpenProjectFragment  extends Fragment implements View.OnClickListener {

    ListView listView;
    ArrayList<Project> projects;
    ListViewAdapter adapter;
    LoadingDialogFragment loadingDialogFragment;
    Exception[] loadProjectListException;
    int sort;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        if(loadProjectListException[0]!=null) {
            Toast.makeText(getContext(), loadProjectListException[0].getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View v = inflater.inflate(R.layout.fragment_openproject, container, false);

        listView = v.findViewById(R.id.fragment_openproject_listview);

        adapter = new ListViewAdapter(v.getContext(), projects, 0);
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
    public void onClick(View view) {

    }

    public class ListViewAdapter extends BaseAdapter {
        Context context;
        ArrayList<Project> list;
        int simpleViewID= R.layout.fragment_openproject_nameview;
        int detailViewID = R.layout.fragment_openproject_detailview;
        int currentView;

        public ListViewAdapter(Context context, ArrayList<Project> list, int view){
            this.context = context;
            this.list = list;
            setView(view);
        }

        public void toggleDetail(){
            if(currentView == detailViewID){
                currentView = simpleViewID;
            } else {
                currentView = detailViewID;
            }
        }

        public void setView(int view){
            switch(view){
                case 0: currentView = simpleViewID; break;
                case 1: currentView = detailViewID; break;
                default: currentView = simpleViewID; break;
            }
        }

        @Override
        public int getCount() {
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
            view = LayoutInflater.from(this.context).inflate(currentView, viewGroup, false);

            if(currentView == detailViewID) {
                TextView itemName = view.findViewById(R.id.fragment_openproject_detailname);
                LinearLayout itemUtilities = view.findViewById(R.id.fragment_openproject_detailutilities);
                LinearLayout waterUtility = view.findViewById(R.id.fragment_openproject_detailutilities_water);
                LinearLayout gasUtility = view.findViewById(R.id.fragment_openproject_detailutilities_gas);
                LinearLayout electricUtility = view.findViewById(R.id.fragment_openproject_detailutilities_electric);
                LinearLayout sewageUtility = view.findViewById(R.id.fragment_openproject_detailutilities_sewage);
                TextView itemCreated = view.findViewById(R.id.fragment_openproject_detailcreated);
                TextView itemModified = view.findViewById(R.id.fragment_openproject_detailmodified);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                itemName.setText(list.get(i).name);
                itemCreated.setText(dateFormat.format(list.get(i).created));
                itemModified.setText(dateFormat.format(list.get(i).modified));
            } else if(currentView == simpleViewID) {
                // get the TextView for item name and item description
                TextView textViewItemName = view.findViewById(R.id.fragment_openproject_nameview);
                textViewItemName.setText(list.get(i).name);
            }

            return view;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_openproject_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.fragment_openproject_changeview:
                adapter.toggleDetail();
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Change View", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.fragment_openproject_sort:
                changeSort();
                return true;
            default:
                return false;
        }
    }

    public void changeSort() {
        Project[] arr;

        switch (sort) {
            case 0:
                // Created sort
                arr = projects.toArray(new Project[projects.size()]);
                createdSort(arr, 0, arr.length-1);
                Toast.makeText(this.getContext(), "Sort by Date Created", Toast.LENGTH_SHORT).show();
                sort = 1;
                break;
            case 1:
                // By modify
                arr = projects.toArray(new Project[projects.size()]);
                modifiedSort(arr, 0, arr.length-1);
                Toast.makeText(this.getContext(), "Sort By Date Modified", Toast.LENGTH_SHORT).show();
                sort = 2;
                break;
            case 2:
                // By alphabetical
                arr = projects.toArray(new Project[projects.size()]);
                stringSort(arr, 0, arr.length-1);
                Toast.makeText(this.getContext(), "Sort Alphabetically", Toast.LENGTH_SHORT).show();
                sort = 0;
                break;
        }

        adapter = new ListViewAdapter(this.getContext(), projects, 0);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    void modifiedSort(Project arr[], int low, int high) {
        if (low < high) {
            int pi = modifiedPartition(arr, low, high);
            modifiedSort(arr, low, pi-1);
            modifiedSort(arr, pi+1, high);
        }

        this.projects = new ArrayList<>(Arrays.asList(arr));
    }

    int modifiedPartition(Project arr[], int low, int high) {
        Project pivot = arr[high];
        int i = (low - 1); // index of smaller element

        for (int j = low; j < high; j++) {
            if (arr[j].modified.before(pivot.modified)) {
                i++;
                Project temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // swap arr[i + 1] and arr[high] (or pivot)
        Project temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    void createdSort(Project arr[], int low, int high) {
        if (low < high) {
            int pi = createdPartition(arr, low, high);
            createdSort(arr, low, pi - 1);
            createdSort(arr, pi + 1, high);
        }

        this.projects = new ArrayList<>(Arrays.asList(arr));
    }

    int createdPartition(Project arr[], int low, int high) {
        Project pivot = arr[high];
        int i = (low - 1); // index of smaller element

        for (int j = low; j < high; j++) {
            if (arr[j].created.before(pivot.modified)) {
                i++;
                Project temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // swap arr[i + 1] and arr[high] (or pivot)
        Project temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    void stringSort(Project arr[], int low, int high) {
        if (low < high) {
            int pi = stringPartition(arr, low, high);

            stringSort(arr, low, pi - 1);
            stringSort(arr, pi + 1, high);
        }

        this.projects = new ArrayList<>(Arrays.asList(arr));
    }

    int stringPartition(Project arr[], int low, int high) {
        Project pivot = arr[high];
        int i = (low - 1); // index of smaller element

        for (int j = low; j < high; j++) {
            // If current element is smaller than or equal to pivot
            if (arr[j].toString().compareTo(pivot.toString()) < 0) {
                i++;

                // swap arr[i] and arr[j]
                Project temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // swap arr[i + 1] and arr[high] (or pivot)
        Project temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i+1;
    }
}
