package org.deepnic.locationlogger;

import java.util.ArrayList;
import java.util.List;

import org.deepnic.locationlogger.R;

import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends android.support.v4.app.FragmentActivity {

	private LocationClient locationClient;
	private LocationListener listener;
	private GoogleMap map;
	
	private List<Location> locus;
	private List<LatLng> latlngs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// for map
		SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
		map = fragment.getMap();

		// for Location Based System (LBS)
		listener = getLocationListener();
		
		locationClient = new LocationClient(this, getConnectionCallbacks(), getConnectionFailedListener());
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		ToggleButton recButton = (ToggleButton) menu.findItem(R.id.record).getActionView().findViewById(R.id.record_switch);
		recButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					locationClient.connect();
					locus = new ArrayList<Location>();
					latlngs = new ArrayList<LatLng>();
					map.clear();
				} else {
					locationClient.disconnect();
				}
			}
		});
		return true;
	}
	
	private LocationListener getLocationListener() {
		return new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
				
				locus.add(location);
				latlngs.add(latlng);

				if (map != null) {
					map.addPolyline(new PolylineOptions().addAll(latlngs));
//					map.addMarker(new MarkerOptions().position(latlng));
					map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
				}
			}
		};
	}

	private ConnectionCallbacks getConnectionCallbacks() {
		return new ConnectionCallbacks() {

			@Override
			public void onConnected(Bundle connectionHint) {
				Location curLocation = locationClient.getLastLocation();

				// for map
				LatLng latlng = new LatLng(curLocation.getLatitude(), curLocation.getLongitude());
				
				locus.add(curLocation);
				latlngs.add(latlng);
				if (map != null) {
					map.addPolyline(new PolylineOptions().addAll(latlngs));
//					map.addMarker(new MarkerOptions().position(latlng));
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
				}
				
				LocationRequest request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).setInterval(5000);
				locationClient.requestLocationUpdates(request, listener);
			}

			@Override
			public void onDisconnected() {
			}
		};
	}

	private OnConnectionFailedListener getConnectionFailedListener() {
		return new OnConnectionFailedListener() {

			@Override
			public void onConnectionFailed(ConnectionResult result) {
			}
		};
	}

}
