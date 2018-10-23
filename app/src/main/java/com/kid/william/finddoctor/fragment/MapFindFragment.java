package com.kid.william.finddoctor.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kid.william.finddoctor.Model.entidad.DoctorInfo;
import com.kid.william.finddoctor.R;
import com.kid.william.finddoctor.activity.CallDoctor;
import com.kid.william.finddoctor.utilidades.Common;
import com.kid.william.finddoctor.utilidades.Constantes;


public class MapFindFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = MapFindFragment.class.getSimpleName();
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mlocationCallback;
    private LocationRequest mlocationRequest;
    private Location mlastLocation;
    private GeoFire geoDoctors;
    private Marker markerUser, markerPerson;
    private DatabaseReference mlocations;
    private String key_Uid;
    private Button btnFindDoctor;
    private int radiusDoctor = 3;
    private boolean isFoundDoctor = false;
    private String idDoctor = "";
    private int distanceLocation = 1;
    private static final int LIMIT_DISTANCE = 3;
    private DatabaseReference doctorsAvailable;

    public MapFindFragment() {
    }

    public static MapFindFragment createInstance(String valor) {
        MapFindFragment fragment = new MapFindFragment();
        Bundle args = new Bundle();
        args.putString(Common.TAG_KEY_UID, valor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //mlocations = FirebaseDatabase.getInstance().getReference(Common.TABLE_USER_LOCATION);
        //geoFire = new GeoFire(mlocations);
        key_Uid = getArguments().getString(Common.TAG_KEY_UID);
        setUpLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_find, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        btnFindDoctor = view.findViewById(R.id.btnFind_map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        btnFindDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDoctor();
            }
        });
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady");
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setOnInfoWindowClickListener(this);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(mlocationRequest, mlocationCallback, Looper.myLooper());
    }

    public void setUpLocation() {
        Log.i(TAG, "setUpLocation");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CALL_PHONE},
                    Constantes.MY_PERMISION_REQUEST_CODE);
        } else {
            buildLocationCallBack();
            createLocationRequest();
            displayLocation();
        }
    }

    private void buildLocationCallBack() {
        Log.i(TAG, "buildLocationCallBack");
        mlocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.i(TAG, "onLocationResult");
                mlastLocation = locationResult.getLocations().get(locationResult.getLocations().size() - 1);
                //displayLocation();
            }
        };
    }

    private void displayLocation() {
        Log.i(TAG, "displayLocation");

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Block");
            return;
        }

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mlastLocation = location;
                if (mlastLocation != null) {
                    doctorsAvailable = FirebaseDatabase.getInstance().getReference(Common.TABLE_DOCTOR_GEO);
                    doctorsAvailable.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i(TAG, "onDataChange");
                            loadAllAvailableDoctor();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //loadAllAvailableDoctor();
                } else {
                    Log.e(TAG, getString(R.string.map_location_notfound));
                }
            }
        });
    }

    private void loadAllAvailableDoctor() {
         Log.i(TAG,"loadAllAvailableDoctor");
         if(mMap != null){
             mMap.clear();
             markerUser = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(mlastLocation.getLatitude(),mlastLocation.getLongitude()))
                                    .title("You"));
             mMap.animateCamera(CameraUpdateFactory.
                                    newLatLngZoom(new LatLng(mlastLocation.getLatitude(),mlastLocation.getLongitude()), 15.0f));

        geoDoctors = new GeoFire(doctorsAvailable);
        GeoQuery geoQuery = geoDoctors.queryAtLocation(new GeoLocation(mlastLocation.getLatitude(),
                mlastLocation.getLongitude()),distanceLocation);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                FirebaseDatabase.getInstance().getReference(Common.TABLE_DOCTOR_INFO)
                        .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            DoctorInfo doctor = dataSnapshot.getValue(DoctorInfo.class);
                            if(markerPerson!=null)
                                markerPerson.remove();

                            markerPerson = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(location.latitude,location.longitude))
                                            .flat(true)
                                            .title(doctor.getName())
                                            .snippet(dataSnapshot.getKey())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                Log.i(TAG, "onGeoQueryReady");
                if(distanceLocation<=LIMIT_DISTANCE){
                    distanceLocation++;
                    loadAllAvailableDoctor();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

         }
    }

    private void createLocationRequest() {
        Log.i(TAG,"createLocationRequest");
        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(Constantes.UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(Constantes.FASTEST_INTERVAL);
        mlocationRequest.setSmallestDisplacement(Constantes.DISTANCE);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void findDoctor() {
        doctorsAvailable = FirebaseDatabase.getInstance().getReference(Common.TABLE_DOCTOR_GEO);
        geoDoctors = new GeoFire(doctorsAvailable);
        GeoQuery geoQuery = geoDoctors.queryAtLocation(new GeoLocation(mlastLocation.getLatitude(),
                mlastLocation.getLongitude()),radiusDoctor);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                    if(!isFoundDoctor){
                        isFoundDoctor = true;
                        idDoctor = key;
                        Snackbar.make(getView(),idDoctor,Snackbar.LENGTH_LONG).show();
                    }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                    if(!isFoundDoctor){
                        radiusDoctor++;
                        findDoctor();
                    }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(!marker.getTitle().equals("You")){
            Intent intent = new Intent(getActivity(),CallDoctor.class);
            //intent.putExtra("doctorID",marker.getSnippet().replaceAll("\\D+",""));
            intent.putExtra(Common.KEY_DOCTOR,marker.getSnippet());
            intent.putExtra(Common.LATITUD,mlastLocation.getLatitude());
            intent.putExtra(Common.LONGITUD,mlastLocation.getLongitude());
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        Log.i(TAG,"onStart");
        super.onStart();
        mapFragment.onStart();
    }

    @Override
    public void onStop() {
        Log.i(TAG,"onStop");
        super.onStop();
        mapFragment.onStop();
    }

    @Override
    public void onResume() {
        Log.i(TAG,"onResume");
        super.onResume();
        mapFragment.onResume();
    }


}
