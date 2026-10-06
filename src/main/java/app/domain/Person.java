package app.domain;

public class Person {
    protected Integer id;
    protected String name;
    protected String lastName;
    protected String email;
    protected String phone;
    protected String password;
    protected String state;
    private String rol;

    // constructor vacio

    public Person(){}

    public Person(int id){this.id = id;}

    public Person(Integer id , String name, String lastName , String email , String password, String state){
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.state = state;
        this.rol= rol;
    }


    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // agrego metodos;

    public void create(){}
    public void selectAll(){}
    public void selectById(int id){}
    public void update(){}
    public void deleteById(int id){}
    public void selectByRol(){}
}
