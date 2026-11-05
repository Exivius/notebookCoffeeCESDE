package app.domain;

public class User extends Person{

    private String City;

    public User(){super();}

    public User(String city) {
        City = city;
    }

    public User(int id, String city) {
        super(id);
        City = city;
    }

    public User(Integer id, String name, String lastName, String email, String phone, String password, String state, String rol, String city) {
        super(id, name, lastName, email, phone, password, state, rol);
        City = city;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }


}