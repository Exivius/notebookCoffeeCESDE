package app.service.helpers;

import app.domain.enums.SelectStateEnum;

import app.service.validators.DataTypeValidator;

public class SetAdminState {
    public static String getAdminState(){
        while (true) {
            System.out.println("Seleccione 1. Activo 2. Inactivo 3. Bloqueado");
            int option = DataTypeValidator.validateInt("Estado:");
            switch (option){
                case 1: return SelectStateEnum.ACTIVE.getState();
                case 2: return SelectStateEnum.INACTIVE.getState();
                case 3: return SelectStateEnum.BLOCKED.getState();
                default: System.out.println("Opción no valida");
            }
        }
    }
}
