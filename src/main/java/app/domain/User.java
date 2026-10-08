package app.domain;

public class User extends Person{

    private String City;

    public User(){ super();}

    public User(Integer id, String name, String lastName, String email, String password, String state, String city) {
        super(id, name, lastName, email, password, "Activo");
        City = city;
        setRol("USUARIO");
    }

    public User(Integer id, String name, String lastName, String email, String password,
                String state, String city, boolean preserveState) {
        super(id, name, lastName, email, password, preserveState ? state : "Activo");
        City = city;
        setRol("USUARIO");
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }


}