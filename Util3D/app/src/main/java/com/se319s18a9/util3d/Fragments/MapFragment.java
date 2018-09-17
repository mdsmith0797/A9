package com.se319s18a9.util3d.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.se319s18a9.util3d.R;
import com.se319s18a9.util3d.backend.ConnectedPoint;
import com.se319s18a9.util3d.backend.CustomAsyncTask;
import com.se319s18a9.util3d.backend.Line;
import com.se319s18a9.util3d.backend.Map;
import com.se319s18a9.util3d.backend.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MapFragment extends Fragment implements View.OnClickListener {

    MapView mMapView;
    private GoogleMap googleMap;
    Map graph;
    CustomAsyncTask customUpload;
    CustomAsyncTask customDownload;
    LoadingDialogFragment loadingDialogFragment;
    String filename;
    HashMap<ConnectedPoint, Marker> pointToMarker = new HashMap<>();

    FloatingActionButton myLocationFab;

    TextView waterUtilityTextView;
    FloatingActionButton waterUtilityTypeFab;

    TextView gasUtilityTextView;
    FloatingActionButton gasUtilityTypeFab;

    TextView electricUtilityTextView;
    FloatingActionButton electricUtilityTypeFab;

    TextView sewageUtilityTextView;
    FloatingActionButton sewageUtilityTypeFab;

    FloatingActionButton utilityTypeFab;
    FloatingActionButton createNewLineFab;
    FloatingActionButton trackingFab;

    public enum UtilityType {
        WATER, GAS, ELECTRIC, SEWAGE
    }

    public UtilityType selectedUtility = UtilityType.WATER;

    boolean trackingEnabled = false;
    boolean utilitiesVisible = false;
    boolean createNewLineOnNextClick = false;

    ArrayList<Marker> markers = new ArrayList<>();

    /**
     * @param string Utility type in string form
     * @return UtilityType enumeration or null if string does not match recognized type
     */
    public UtilityType stringToUtilityType(String string) {
        switch(string) {
            case "Electric":
                return UtilityType.ELECTRIC;
            case "Gas":
                return UtilityType.GAS;
            case "Sewage":
                return UtilityType.SEWAGE;
            case "Water":
                return UtilityType.WATER;
        }

        return null;
    }

    /**
     * @param utilityType Utility type in enumeration form
     * @return UtilityType String representation or null if enum does not match recognized type
     */
    public String utilityTypeToString(UtilityType utilityType) {
        switch(utilityType){
            case ELECTRIC:
                return "Electric";
            case GAS:
                return "Gas";
            case SEWAGE:
                return "Sewage";
            case WATER:
                return "Water";
        }

        return null;
    }

    public int utilityTypeToColor(UtilityType utilityType) {
        int color;

        switch(utilityType) {
            case WATER:
                color = Color.BLUE;
                break;
            case GAS:
                color = Color.RED;
                break;
            case ELECTRIC:
                color = Color.YELLOW;
                break;
            case SEWAGE:
                color = Color.GREEN;
                break;
            default:
                color = Color.BLACK;
        }

        return color;
    }

    public MapFragment() {
        // Empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        graph = new Map();
        boolean loadFromFile;
        filename = null;

        if(getArguments() != null) {
            loadFromFile=getArguments().getBoolean("LoadMap");
            filename = getArguments().getString("ProjectName");
        } else{
            loadFromFile = false;
        }

        if(filename == null) {
            filename = "newfile";
        }

        if(loadFromFile) {
            customDownload = User.getInstance().readMapFromFirebaseStorage(filename, graph, this::loadDownloadedMap, getActivity());
            loadingDialogFragment = new LoadingDialogFragment();
            loadingDialogFragment.setCancelable(false);
            Bundle messageArgument = new Bundle();
            messageArgument.putString("message", "Loading Map");
            loadingDialogFragment.setArguments(messageArgument);
            loadingDialogFragment.show(getActivity().getFragmentManager(), null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // Set toolbar title
        final Bundle bundle = getArguments();
        getActivity().setTitle(bundle.getString("ProjectName") + ".json");

        // Initialize local variables
        mMapView = v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        myLocationFab = v.findViewById(R.id.fragment_map_fab_myLocation);
        myLocationFab.setOnClickListener(this);

        waterUtilityTextView = v.findViewById(R.id.fragment_map_textView_waterUtilityType);
        waterUtilityTextView.setVisibility(View.INVISIBLE);

        waterUtilityTypeFab = v.findViewById(R.id.fragment_map_fab_utilityType_water);
        waterUtilityTypeFab.setOnClickListener(this);
        waterUtilityTypeFab.setVisibility(FloatingActionButton.INVISIBLE);

        gasUtilityTextView = v.findViewById(R.id.fragment_map_textView_gasUtilityType);
        gasUtilityTextView.setVisibility(View.INVISIBLE);

        gasUtilityTypeFab= v.findViewById(R.id.fragment_map_fab_utilityType_gas);
        gasUtilityTypeFab.setOnClickListener(this);
        gasUtilityTypeFab.setVisibility(FloatingActionButton.INVISIBLE);

        electricUtilityTextView = v.findViewById(R.id.fragment_map_textView_electricUtilityType);
        electricUtilityTextView.setVisibility(View.INVISIBLE);

        electricUtilityTypeFab = v.findViewById(R.id.fragment_map_fab_utilityType_electric);
        electricUtilityTypeFab.setOnClickListener(this);
        electricUtilityTypeFab.setVisibility(FloatingActionButton.INVISIBLE);

        sewageUtilityTextView = v.findViewById(R.id.fragment_map_textView_sewageUtilityType);
        sewageUtilityTextView.setVisibility(View.INVISIBLE);

        sewageUtilityTypeFab = v.findViewById(R.id.fragment_map_fab_utilityType_sewage);
        sewageUtilityTypeFab.setOnClickListener(this);
        sewageUtilityTypeFab.setVisibility(FloatingActionButton.INVISIBLE);

        utilityTypeFab = v.findViewById(R.id.fragment_map_fab_utilityType);
        utilityTypeFab.setOnClickListener(this);
        setLineTypeButtonIcon(selectedUtility);

        createNewLineFab = v.findViewById(R.id.fragment_map_fab_createNewLine);
        createNewLineFab.setOnClickListener(this);

        trackingFab = v.findViewById(R.id.fragment_map_fab_tracking);
        trackingFab.setOnClickListener(this);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;

            initializeMapOnClickListener();
            renderFromScratch();

            // For showing a move to my location button
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.fragment_map_menu_togglemaptype:
                if(googleMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    Toast.makeText(getContext(), "Map Type: Satellite", Toast.LENGTH_SHORT).show();
                } else if(googleMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    Toast.makeText(getContext(), "Map Type: Terrain", Toast.LENGTH_SHORT).show();
                } else if(googleMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    Toast.makeText(getContext(), "Map Type: Hybrid", Toast.LENGTH_SHORT).show();
                } else if(googleMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    Toast.makeText(getContext(), "Map Type: Normal", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.fragment_map_menu_save:
                dialogSaveHelper(null, filename, false);
                return true;
            case R.id.fragment_map_menu_exit:
                saveWithDialog(true);
                return true;
            case R.id.fragment_map_menu_delete_point:
                deleteSelectedPoint();
                return true;
            case R.id.fragment_map_menu_move_point:
                //TODO: only show this menu option if point is selected. Check if point is selected
                if(graph.getSavedPoint()!=null) {
                    LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());

                    @SuppressLint("InflateParams")
                    final View dialogView = layoutInflater.inflate(R.layout.dialog_movepoint, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
                    alertDialogBuilder.setTitle("Move point");
                    alertDialogBuilder.setView(dialogView);
                    alertDialogBuilder
                            .setPositiveButton("Move",
                                    (dialog, id) -> {
                                    })
                            .setNegativeButton("Cancel",
                                    (dialog, id) -> dialog.cancel());

                    ((TextView) dialogView.findViewById(R.id.dialog_movePoint_textView_latitude)).setText(String.valueOf(graph.getSavedPoint().getLatitude()));
                    ((TextView) dialogView.findViewById(R.id.dialog_movePoint_textView_longitude)).setText(String.valueOf(graph.getSavedPoint().getLongitude()));

                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                        try {
                            double lat = Double.parseDouble(((EditText) dialogView.findViewById(R.id.dialog_movePoint_editText_latitude)).getText().toString());
                            double lon = Double.parseDouble(((EditText) dialogView.findViewById(R.id.dialog_movePoint_editText_longitude)).getText().toString());

                            if (lat > 90 || lat < -90 || lon < -180 || lon >= 180) {
                                throw new Exception();
                            }

                            moveSelectedPoint(lat, lon);
                            alertDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(v.getContext(), "Invalid entry", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_map_fab_myLocation:
                if(markers!=null&&markers.size()>0) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }

                    LatLngBounds bounds = builder.build();

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50); // padding = 50 px
                    googleMap.animateCamera(cu);
                } else{
                    Toast.makeText(getContext(), "File contains no points", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fragment_map_fab_utilityType_water:
                Toast.makeText(getContext(), "Water", Toast.LENGTH_SHORT).show();
                selectedUtility = UtilityType.WATER;
                changeSelectedLineColor();
                break;
            case R.id.fragment_map_fab_utilityType_gas:
                Toast.makeText(getContext(), "Gas", Toast.LENGTH_SHORT).show();
                selectedUtility = UtilityType.GAS;
                changeSelectedLineColor();
                break;
            case R.id.fragment_map_fab_utilityType_electric:
                Toast.makeText(getContext(), "Electric", Toast.LENGTH_SHORT).show();
                selectedUtility = UtilityType.ELECTRIC;
                changeSelectedLineColor();
                break;
            case R.id.fragment_map_fab_utilityType_sewage:
                Toast.makeText(getContext(), "Sewage", Toast.LENGTH_SHORT).show();
                selectedUtility = UtilityType.SEWAGE;
                changeSelectedLineColor();
                break;
            case R.id.fragment_map_fab_utilityType:
                if(!trackingEnabled && !utilitiesVisible) {
                    enableUtilities(true);
                } else if(utilitiesVisible) {
                    enableUtilities(false);
                }
                break;
            case R.id.fragment_map_fab_tracking:
                if(!trackingEnabled) {
                    trackingEnabled = true;

                    // Create a new line if there are currently no selected points
                    if(graph.getSavedPoint() == null) {
                        createNewLineOnNextClick = true;
                    }

                    utilityTypeFab.setEnabled(false);
                    enableUtilities(false);
                    trackingFab.setImageResource(android.R.drawable.ic_media_pause);
                    Toast.makeText(getContext(), "Tracking enabled", Toast.LENGTH_SHORT).show();
                } else {
                    trackingEnabled = false;
                    utilityTypeFab.setEnabled(true);
                    trackingFab.setImageResource(android.R.drawable.ic_media_play);
                    Toast.makeText(getContext(), "Tracking disabled", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.fragment_map_fab_createNewLine:
                createNewLineOnNextClick = true;
                clearSelectedMarkerHighlight();
                graph.setSavedPoint(null);

                if(trackingEnabled) {
                    trackingEnabled = false;
                    utilityTypeFab.setEnabled(true);
                    trackingFab.setImageResource(android.R.drawable.ic_media_play);
                }

                Toast.makeText(getContext(), "New line created", Toast.LENGTH_SHORT).show();
                break;
        }

        setLineTypeButtonIcon(selectedUtility);
    }

    private void setLineTypeButtonIcon(UtilityType utilityType){
        TextDrawable drawable = null;

        switch (utilityType) {
            case WATER:
                drawable = TextDrawable.builder()
                        .beginConfig()
                        .width(120)  // width in px
                        .height(120) // height in px
                        .endConfig().buildRound("W", Color.BLUE);
                break;
            case GAS:
                drawable = TextDrawable.builder()
                        .beginConfig()
                        .width(120)  // width in px
                        .height(120) // height in px
                        .endConfig().buildRound("G", Color.RED);
                break;
            case ELECTRIC:
                drawable = TextDrawable.builder()
                        .beginConfig()
                        .width(120)  // width in px
                        .height(120) // height in px
                        .endConfig().buildRound("E", Color.YELLOW);
                break;
            case SEWAGE:
                drawable = TextDrawable.builder()
                        .beginConfig()
                        .width(120)  // width in px
                        .height(120) // height in px
                        .endConfig().buildRound("S", Color.GREEN);
                break;
        }

        utilityTypeFab.setImageDrawable(drawable);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private class PointAndPolyline {
        private ConnectedPoint savedPoint;
        private Polyline savedPoly;

        PointAndPolyline(ConnectedPoint point, Polyline poly) {
            savedPoint = point;
            savedPoly = poly;
        }

        public ConnectedPoint getConnectedPoint() {
            return savedPoint;
        }

        public Polyline getSavedPoly() {
            return savedPoly;
        }
    }

    private void dragPolyLinesWithMarker(Marker marker) {
        ConnectedPoint point = ((PointAndPolyline) marker.getTag()).getConnectedPoint();
        point.move(marker.getPosition().latitude,marker.getPosition().longitude);
        ArrayList<LatLng> newPath = new ArrayList<>(2);
        newPath.add(marker.getPosition());
        newPath.add(null);

        if(!point.getParentPoint().isRoot()) {
            // Move polyline to parent
            newPath.set(1, point.getParentPoint().getLatLng());
            ((PointAndPolyline) marker.getTag()).getSavedPoly().setPoints(newPath);
        }

        if(point.hasChildren()) {
            for (ConnectedPoint child:point.getChildren()) {
                // Move polyline to child
                newPath.set(1,child.getLatLng());
                ((PointAndPolyline) pointToMarker.get(child).getTag()).getSavedPoly().setPoints(newPath);
            }
        }
    }

    private void moveSelectedPoint(double latitude, double longitude) {
        ConnectedPoint point = graph.getSavedPoint();

        if(point != null && !point.isRoot()) {
            // Move point
            point.move(latitude,longitude);
            Marker marker = pointToMarker.get(point);
            PointAndPolyline pointAndPolyline = ((PointAndPolyline) marker.getTag());
            marker.setPosition(new LatLng(latitude, longitude));
            ArrayList<LatLng> newPath = new ArrayList<>(2);
            newPath.add(point.getLatLng());
            newPath.add(null);

            if(!point.getParentPoint().isRoot()){
                // Move polyline to parent
                newPath.set(1, point.getParentPoint().getLatLng());
                pointAndPolyline.getSavedPoly().setPoints(newPath);
            }

            if(point.hasChildren()){
                for (ConnectedPoint child:point.getChildren()) {
                    // Move polyline to child
                    newPath.set(1,child.getLatLng());
                    ((PointAndPolyline) pointToMarker.get(child).getTag()).getSavedPoly().setPoints(newPath);
                }
            }
        }
    }

    private void deleteSelectedPoint(){
        ConnectedPoint point = graph.getSavedPoint();

        if(point != null && !point.isRoot()) {
            Marker marker = pointToMarker.get(point);

            markers.remove(marker);

            pointToMarker.remove(point);
            // Note: removing the point from the data structure does not modify the point itself,
            // only the children and parent points. The point itself is not deleted until it falls
            // out of scope when this method ends
            point.remove();

            if(!point.getParentPoint().isRoot()) {
                ((PointAndPolyline) marker.getTag()).getSavedPoly().remove();
            }

            if(point.hasChildren()) {
                ArrayList<LatLng> newPath = new ArrayList<>(2);
                newPath.add(null);
                newPath.add(null);

                if(point.getParentPoint().isRoot()) {
                    for (ConnectedPoint child:point.getChildren()) {
                        if(child.getParentPoint().isRoot()) {
                            newPath.set(0, child.getLatLng());
                            ((PointAndPolyline) pointToMarker.get(child).getTag()).getSavedPoly().remove();
                            break;
                        }
                    }
                } else {
                    newPath.set(0, point.getParentPoint().getLatLng());
                }

                for (ConnectedPoint child:point.getChildren()) {
                    if(!child.getParentPoint().isRoot()) {
                        newPath.set(1,child.getLatLng());
                        ((PointAndPolyline) pointToMarker.get(child).getTag()).getSavedPoly().setPoints(newPath);
                    }
                }
            } else if(point.getParentPoint().isRoot()){
                graph.getLines().remove(point.getParentLine());
            }

            marker.remove();
            graph.setSavedPoint(null);
        }
    }

    private void clearSelectedMarkerHighlight() {
        ConnectedPoint point = graph.getSavedPoint();

        if(point != null) {
            Marker oldMarker = pointToMarker.get(point);

            if (oldMarker != null) {
                oldMarker.setIcon(null);
                //Polyline tempPoly = ((PointAndPolyline) oldMarker.getTag()).getSavedPoly();
                //if(tempPoly!=null){
                //    tempPoly.setPattern(null);
                //}
            }
        }
    }

    private void setSelectedMarkerHighlightAndUpdateUtilityTypeFab() {
        ConnectedPoint point = graph.getSavedPoint();

        if(point != null) {
            selectedUtility = stringToUtilityType(point.getParentLine().getType());
            setLineTypeButtonIcon(selectedUtility);
            Marker newMarker = pointToMarker.get(point);
            //This if statement shouldn't really be necessary but could prevent program from
            //crashing if this method is called and the selected point is the null root point.
            // This should not ever actually happen.
            if (newMarker != null) {
                newMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                //Polyline tempPoly = ((PointAndPolyline) newMarker.getTag()).getSavedPoly();
                //if(tempPoly!=null){
                //    tempPoly.setPattern(Arrays.<PatternItem>asList(new Dash(30), new Gap(20)));
                //}
            }
        }
    }

    private void renderNewPointAndHighlight(ConnectedPoint point) {
        clearSelectedMarkerHighlight();
        renderNewPoint(point);
        setSelectedMarkerHighlightAndUpdateUtilityTypeFab();
    }

    private void changeSelectedLineColor() {
        ConnectedPoint selectedPoint = graph.getSavedPoint();

        if(selectedPoint != null) {
            Line line = selectedPoint.getParentLine();

            if (stringToUtilityType(line.getType()) != selectedUtility) {
                line.setType(utilityTypeToString(selectedUtility));
                ConnectedPoint point = line.getNullHeadPoint();
                reColorChildLines(point);
            }
        }
    }

    private void reColorChildLines(ConnectedPoint point) {
        if(!point.isRoot()&&!point.getParentPoint().isRoot()) {
            ((PointAndPolyline) pointToMarker.get(point).getTag()).getSavedPoly().setColor(utilityTypeToColor(selectedUtility));
        }

        if(point.hasChildren()) {
            for (ConnectedPoint child:point.getChildren()) {
                reColorChildLines(child);
            }
        }
    }

    private void renderNewPoint(ConnectedPoint point) {
        if(!point.isRoot()) {
            graph.setSavedPoint(point);

            MarkerOptions markerOptions = new MarkerOptions().position(point.getLatLng()).draggable(true);

            Marker marker = googleMap.addMarker(markerOptions);

            markers.add(marker);

            Polyline polyline = null;

            if(!point.getParentPoint().isRoot()) {
                PolylineOptions polylineOptions = new PolylineOptions().add(point.getParentPoint().getLatLng(), point.getLatLng());
                polylineOptions.color(utilityTypeToColor(stringToUtilityType(point.getParentLine().getType())));
                polyline = googleMap.addPolyline(polylineOptions);
            }

            marker.setTag(new PointAndPolyline(point, polyline));
            pointToMarker.put(point, marker);
        }

        if(point.hasChildren()) {
            for (ConnectedPoint child : point.getChildren()) {
                renderNewPoint(child);
            }
        }
    }

    private void renderFromScratch() {
        googleMap.clear();

        for (Line line : graph.getLines()) {
            renderNewPoint(line.getNullHeadPoint());
        }

        setSelectedMarkerHighlightAndUpdateUtilityTypeFab();
    }

    public void loadDownloadedMap() {
        try {
            if(!customDownload.isSuccessful()) {
                throw customDownload.getException();
            }

            LoadingDialogFragment temp = loadingDialogFragment;
            loadingDialogFragment = null;
            temp.dismiss();

            //This would be needed if the google map became ready before this step was reached
            //I believe this is technically possible but unlikely to happen
            if(googleMap != null) {
                renderFromScratch();
            }
        } catch(Exception e) {
            LoadingDialogFragment temp = loadingDialogFragment;
            loadingDialogFragment = null;
            temp.dismiss();
            Toast.makeText(getContext(), "Failed to load map. Encountered the following exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    public void saveCallback() {
        saveOrExit(false);
    }

    public void saveAndExitCallback() {
        saveOrExit(true);
    }

    private void saveOrExit(boolean saveAndExit) {
        LoadingDialogFragment temp = loadingDialogFragment;
        loadingDialogFragment = null;
        temp.dismiss();

        if(!customUpload.isSuccessful()) {
            Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_LONG).show();
        } else if(saveAndExit) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void dialogSaveHelper(AlertDialog dialog, String newFilename, boolean exitAfterSave) {
        filename = newFilename;

        if(dialog != null) {
            dialog.dismiss();
        }

        try{
            loadingDialogFragment = new LoadingDialogFragment();
            loadingDialogFragment.setCancelable(false);
            Bundle messageArgument = new Bundle();

            if(exitAfterSave) {
                messageArgument.putString("message", "Saving and exiting");
                customUpload = User.getInstance().writeMapToFirebaseStorage(filename, graph, this::saveAndExitCallback, getActivity());

            } else {
                messageArgument.putString("message", "Saving map");
                customUpload = User.getInstance().writeMapToFirebaseStorage(filename, graph, this::saveCallback, getActivity());
            }

            loadingDialogFragment.setArguments(messageArgument);
            loadingDialogFragment.show(getActivity().getFragmentManager(), null);
        } catch(Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void saveWithDialog(boolean exitAfterSave) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        final View dialogView = layoutInflater.inflate(R.layout.dialog_savemap, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());

        ((EditText) dialogView.findViewById(R.id.dialog_saveMap_editText_filename)).setText(filename);

        if(exitAfterSave){
            alertDialogBuilder.setTitle("Save and Exit");
            ((TextView) dialogView.findViewById(R.id.dialog_saveMap_textView_prompt)).setText("Would you like to save before exiting?");
        } else {
            alertDialogBuilder.setTitle("Save");
            ((TextView) dialogView.findViewById(R.id.dialog_saveMap_textView_prompt)).setText("Would you like to save?");
        }

        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder
                .setPositiveButton("Yes",
                        (dialog, id) -> dialogSaveHelper((AlertDialog) dialog, ((EditText) dialogView.findViewById(R.id.dialog_saveMap_editText_filename)).getText().toString(), exitAfterSave))
                .setNegativeButton("No",
                        (dialog, id) -> getActivity().getSupportFragmentManager().popBackStack());

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void initializeMapOnClickListener() {
        googleMap.setOnMapClickListener(point -> {
            if(trackingEnabled) {
                if(createNewLineOnNextClick){
                    graph.setSavedPoint(graph.addNewLine(utilityTypeToString(selectedUtility)).getNullHeadPoint());
                }

                renderNewPointAndHighlight(graph.getSavedPoint().addAChild(point.latitude, point.longitude));
                createNewLineOnNextClick = false;
            }
        });

        googleMap.setOnMarkerClickListener(marker -> {
            createNewLineOnNextClick = false;
            clearSelectedMarkerHighlight();
            ConnectedPoint temp = ((PointAndPolyline) marker.getTag()).getConnectedPoint();
            graph.setSavedPoint(temp);
            setSelectedMarkerHighlightAndUpdateUtilityTypeFab();
            selectedUtility = stringToUtilityType(temp.getParentLine().getType());
            setLineTypeButtonIcon(selectedUtility);
            return true;
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                createNewLineOnNextClick = false;
                if(trackingEnabled) {
                    trackingEnabled = false;
                    utilityTypeFab.setEnabled(true);
                    trackingFab.setImageResource(android.R.drawable.ic_media_play);
                }
                dragPolyLinesWithMarker(marker);
                clearSelectedMarkerHighlight();
                graph.setSavedPoint(((PointAndPolyline) marker.getTag()).getConnectedPoint());
                setSelectedMarkerHighlightAndUpdateUtilityTypeFab();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                dragPolyLinesWithMarker(marker);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                dragPolyLinesWithMarker(marker);
            }
        });
    }

    // Helper methods

    private void enableUtilities(boolean enable) {
        if(enable) {
            // set visible

            waterUtilityTypeFab.setVisibility(FloatingActionButton.VISIBLE);
            waterUtilityTextView.setVisibility(View.VISIBLE);

            gasUtilityTypeFab.setVisibility(FloatingActionButton.VISIBLE);
            gasUtilityTextView.setVisibility(View.VISIBLE);

            electricUtilityTypeFab.setVisibility(FloatingActionButton.VISIBLE);
            electricUtilityTextView.setVisibility(View.VISIBLE);

            sewageUtilityTypeFab.setVisibility(FloatingActionButton.VISIBLE);
            sewageUtilityTextView.setVisibility(View.VISIBLE);

            // set listeners

            waterUtilityTypeFab.setOnClickListener(this);
            gasUtilityTypeFab.setOnClickListener(this);
            electricUtilityTypeFab.setOnClickListener(this);
            sewageUtilityTypeFab.setOnClickListener(this);

            utilitiesVisible = true;
        } else {
            // set invisible

            waterUtilityTextView.setVisibility(View.INVISIBLE);
            waterUtilityTypeFab.setVisibility(FloatingActionButton.INVISIBLE);

            gasUtilityTextView.setVisibility(View.INVISIBLE);
            gasUtilityTypeFab.setVisibility(FloatingActionButton.INVISIBLE);

            electricUtilityTextView.setVisibility(View.INVISIBLE);
            electricUtilityTypeFab.setVisibility(FloatingActionButton.INVISIBLE);

            sewageUtilityTextView.setVisibility(View.INVISIBLE);
            sewageUtilityTypeFab.setVisibility(FloatingActionButton.INVISIBLE);

            // disconnect listeners

            waterUtilityTypeFab.setOnClickListener(null);
            gasUtilityTypeFab.setOnClickListener(null);
            electricUtilityTypeFab.setOnClickListener(null);
            sewageUtilityTypeFab.setOnClickListener(null);

            utilitiesVisible = false;
        }
    }
}