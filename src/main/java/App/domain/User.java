package App.domain;

public class User extends Person{

    private String City;

    public User(){ super();}

    public User(Integer id, String name, String lastName, String email, String password, boolean state, String city) {
        super(id, name, lastName, email, password, state);
        City = city;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    @Override
    public void create(){ super.create(); }

    @Override
    public void update(){ super.update(); }

    @Override
    public void selectById(){
        super.selectById();
    }

    @Override
    public void selectAll(){
        super.selectAll();
    }

    @Override
    public void deleteById(){
        super.deleteById();
    }

    @Override
    public void selectByRol() {
        super.selectByRol();
    }
}