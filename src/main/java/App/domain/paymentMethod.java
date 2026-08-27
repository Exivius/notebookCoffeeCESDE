package App.domain;

public class paymentMethod {

    private Integer methodId;
    private String methodName;

    public paymentMethod() {
    }

    public paymentMethod(Integer methodId, String methodName) {
        this.methodId = methodId;
        this.methodName = methodName;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void createMethod() {
    }

    public void selectAllMethods() {
    }

    public void selectMethodtById() {
    }

    public void deleteMethodById() {
    }

    public void updateMethod() {
    }
}
