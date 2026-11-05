package app.service.helpers;

import app.domain.Place;
import app.domain.enums.SelectSedeEnum;
import app.service.validators.DataTypeValidator;

public class SetSede {
    public static String getSede() {
        while (true) {
            System.out.println("Seleccione 1. Centro\n2. Bello\n3. Rionegro\n4. Bogotá");
            int option = DataTypeValidator.validateInt("Sede:");
            switch (option) {
                case 1: return SelectSedeEnum.CENTRO.name();
                case 2: return SelectSedeEnum.BELLO.name();
                case 3: return SelectSedeEnum.RIONEGRO.name();
                case 4: return SelectSedeEnum.BOGOTA.name();
                default: System.out.println("Opción no válida");
            }
        }
    }

    public static Place getPlace() {
        SelectSedeEnum[] places = SelectSedeEnum.values();
        System.out.println("Seleccione una sede:");
        for (int i = 0; i < places.length; i++) {
            System.out.println((i + 1) + ". " + places[i].getSede());
        }
        int option = DataTypeValidator.validateInt("Sede:");
        while (option < 1 || option > places.length) {
            System.out.println("Opción no válida.");
            option = DataTypeValidator.validateInt("Sede:");
        }
        SelectSedeEnum selectedPlace = places[option - 1];
        return new Place(option, selectedPlace.getSede());
    }

    public static Place getPlace(app.service.inputports.PlaceServiceInterface placeService) {
        java.util.List<Place> places = placeService.selectAllPlaces();
        if (places.isEmpty()) {
            System.out.println("No hay sedes registradas.");
            return null;
        }
        System.out.println("Seleccione una sede (0 para volver):");
        for (Place place : places) {
            System.out.println(place.getPlaceId() + ". " + place.getPlaceName()
                    + " | " + place.getPlaceType());
        }
        while (true) {
            int id = DataTypeValidator.validateInt("ID de la sede:");
            if (id == 0) return null;
            for (Place place : places) {
                if (place.getPlaceId().equals(id)) return place;
            }
            System.out.println("Seleccione una sede de la lista.");
        }
    }
}
