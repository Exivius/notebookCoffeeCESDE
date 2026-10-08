package app.domain;

public class Place {
    private Integer placeId;
    private String placeName;

    public Place(){}

    public Place(Integer placeId, String placeName) {
        this.placeId = placeId;
        this.placeName = placeName;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    //metodos

    public void createPlace(){

    }

    public void selectAllPlaces(){

    }

    public void selectPlaceById(int id){

    }

    public void updatePlace(){

    }

    public void deletePlaceById(int id){

    }


}
