package app.domain;

public class Admin extends Person {
    private String area;

    public Admin() {
        super();
    }

    public Admin(Integer id, String name, String lastName, String email, String password, String state, String area) {
        super(id, name, lastName, email, password, state);
        this.area = area;
    }

    public Admin(Integer id, String name, String lastName, String email, String password, String state) {
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
    
}
