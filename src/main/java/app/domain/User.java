package app.domain;

public class User extends Person{

    private String City;

    public User(){ super();}

    public User(Integer id, String name, String lastName, String email, String password, String state, String city) {
        super(id, name, lastName, email, password, state);
        City = city;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }


}