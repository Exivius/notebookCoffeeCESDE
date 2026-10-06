package app.view;

import app.domain.Place;
import app.service.inputports.PlaceServiceInterface;
import app.service.validators.DataTypeValidator;
import java.util.List;

public class PlaceView {
    // La vista solicita el registro al servicio.
    private final PlaceServiceInterface placeService;

    // Recibe el servicio por el constructor.
    public PlaceView(PlaceServiceInterface placeService) {
        this.placeService = placeService;
    }

    public void createPlace() {
        // Solicita los datos por consola usando el validador existente.
        int id = DataTypeValidator.validateInt("Ingrese el ID de la sede:");
        String name = DataTypeValidator.validateString("Ingrese el nombre de la sede:");
        String type = DataTypeValidator.validateString("Ingrese el tipo de sede:");

        // Envía los datos al servicio para construir y guardar la sede.
        placeService.createPlace(id, name, type);
    }

    public void selectPlaceById() {
        // Pide el ID y consulta la sede mediante el servicio.
        int id = DataTypeValidator.validateInt("Ingrese el ID de la sede a consultar:");
        Place place = placeService.selectPlaceById(id);
        // Comprueba que exista antes de acceder a sus datos.
        if (place == null) {
            System.out.println("No existe una sede con ese ID.");
            return;
        }
        System.out.println(place.getPlaceId() + " | " + place.getPlaceName()
                + " | " + place.getPlaceType());
    }

    public void selectAllPlaces() {
        // Obtiene la lista y muestra cada sede en consola.
        List<Place> places = placeService.selectAllPlaces();
        if (places.isEmpty()) {
            System.out.println("No hay sedes registradas.");
            return;
        }
        for (Place place : places) {
            System.out.println(place.getPlaceId() + " | " + place.getPlaceName()
                    + " | " + place.getPlaceType());
        }
    }

    public void updatePlace() {
        int id = DataTypeValidator.validateInt("Ingrese el ID de la sede a actualizar:");
        // Verifica que exista antes de pedir los nuevos datos.
        if (placeService.selectPlaceById(id) == null) {
            System.out.println("No existe una sede con ese ID.");
            return;
        }
        String name = DataTypeValidator.validateString("Ingrese el nuevo nombre de la sede:");
        String type = DataTypeValidator.validateString("Ingrese el nuevo tipo de sede:");
        // Envía los nuevos datos al servicio para reemplazar la sede.
        Place place = placeService.updatePlace(id, name, type);
        if (place != null) {
            System.out.println("Sede actualizada correctamente.");
        } else {
            System.out.println("No existe una sede con ese ID.");
        }
    }

    public void deletePlaceById() {
        int id = DataTypeValidator.validateInt("Ingrese el ID de la sede a eliminar:");
        // Verifica que exista para no anunciar una eliminación que no ocurrió.
        if (placeService.selectPlaceById(id) == null) {
            System.out.println("No existe una sede con ese ID.");
            return;
        }
        placeService.deletePlaceById(id);
        System.out.println("Sede eliminada correctamente.");
    }
}
