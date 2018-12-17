package tavant.twms.web.xls.writer;

public class Person {
    String firstName;
    String lastName;
    Integer age;

    public Person(String firstName, String lastName, Integer age) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
    public Integer getAge() {
        return age;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
}