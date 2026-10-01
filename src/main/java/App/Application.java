package app;

import app.ui.CliAdminInterface;

public class Application {
    public static void main(String[] args) {
        CliAdminInterface adminInterface = new CliAdminInterface();
        adminInterface.applicationInit();
    }
}
