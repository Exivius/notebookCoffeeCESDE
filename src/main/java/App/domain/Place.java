package App.domain;

public class Place {
    private Integer placeId;
    private String placeName;
    private String placeType;

    public Place(){}

    public Place(Integer placeId, String placeName, String placeType) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeType = placeType;
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

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
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
