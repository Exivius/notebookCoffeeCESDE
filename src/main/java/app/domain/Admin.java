package app.domain;

public class Admin extends Person {
    private String area;

    public Admin() {
        super();
    }

    public Admin(String area) {
        this.area = area;
    }

    public Admin(int id, String area) {
        super(id);
        this.area = area;
    }

    public Admin(Integer id, String name, String lastName, String email, String phone, String password, String state, String rol, String area) {
        super(id, name, lastName, email, phone, password, state, rol);
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
    
}
