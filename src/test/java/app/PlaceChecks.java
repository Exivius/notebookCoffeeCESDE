package app;

import app.domain.Place;
import app.persistence.repository.PlaceRepositoryAdapter;
import app.service.PlaceServiceAdapter;

public class PlaceChecks {
    public static void main(String[] args) {
        PlaceServiceAdapter service = new PlaceServiceAdapter(new PlaceRepositoryAdapter());
        Place place = service.createPlace(null, " Bello ", " Punto de Entrega ");
        check(place.getPlaceId() != null, "Debe generar ID");
        check("Bello".equals(place.getPlaceName()), "Debe normalizar nombre");
        check("Punto de Entrega".equals(service.selectPlaceById(place.getPlaceId()).getPlaceType()),
                "Debe conservar tipo");
        service.updatePlace(place.getPlaceId(), "Centro");
        check("Punto de Entrega".equals(service.selectPlaceById(place.getPlaceId()).getPlaceType()),
                "Actualizar con la firma anterior debe conservar tipo");
        service.updatePlace(place.getPlaceId(), "Centro", "Punto de Venta");
        check("Punto de Venta".equals(service.selectPlaceById(place.getPlaceId()).getPlaceType()),
                "Debe actualizar tipo");
        check(service.updatePlace(999, "Otra", "Punto de Venta") == null, "No debe crear al actualizar");
        rejects(() -> service.createPlace(null, "  ", "Punto de Venta"));
        rejects(() -> service.createPlace(null, "Centro", ""));
        rejects(() -> service.selectPlaceById(0));
        service.deletePlaceById(place.getPlaceId());
        check(service.selectAllPlaces().isEmpty(), "Debe eliminar sede");
        System.out.println("PlaceChecks: OK");
    }

    private static void rejects(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Debía rechazar entrada inválida");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
