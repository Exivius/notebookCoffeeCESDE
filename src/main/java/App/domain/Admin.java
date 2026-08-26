package app.domain;

public class Admin extends Person {
    private String area;

    public Admin() {
    }

    public Admin(Integer id, String name, String lastName, String email, String password, boolean state, String area) {
        super(id, name, lastName, email, password, state);
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public void create() {
        super.create();
    }

    @Override
    public void selectAll() {
        super.selectAll();
    }

    @Override
    public void selectById() {
        super.selectById();
    }

    @Override
    public void deleteById() {
        super.deleteById();
    }

    @Override
    public boolean selectState() {
        return super.selectState();
    }

    @Override
    public void selectByRol() {
        super.selectByRol();
    }
}
