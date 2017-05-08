package alexis.com.arqui;

import java.util.UUID;

public class Student {

    private String id;
    private String name;
    private String email;

    public Student(String name,String email){
        UUID id_random = UUID.randomUUID();
        this.setName(name);
        this.setEmail(email);
        this.setId(id_random.toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return this.id+"->"+this.name+"->"+this.email;
    }

}
