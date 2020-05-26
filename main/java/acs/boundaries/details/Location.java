package acs.boundaries.details;

public class Location {
	Double lat;
	Double lng;
	
	public Location() {
	}
	
	public Location(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat() {
		return lat;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}
	
	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "LocationBoundary [lat=" + lat + ", lng=" + lng + "]";
	}
	
	
}
