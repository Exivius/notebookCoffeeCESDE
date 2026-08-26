package App.domain;

public class User extends Person{

    private Integer id;
    private String userType;

    public User(){ super();}

    public User(Integer id, String name, String email, String phone, String password, boolean state, String userType){
        super(id, name, email, phone,password, state);
        this.id = id;
        this.userType = userType;
    }

    public Integer getId() {return id;}
    public void setId(Integer id) { this.id = id; }

    public String getUserType() {return userType;}
    public void setUserType(String userType) { this.userType = userType; }

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



}