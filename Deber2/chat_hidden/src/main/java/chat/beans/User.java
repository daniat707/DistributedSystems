package chat.beans;

/**
 * @author Leonard
 */
public class User {

    private String userName;
    private String message;
    private String group;  // Añadir este atributo JOHA


    public User() {
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        this.message = newMessage;
    }

    public String getGroup() { //JOHA
        return group;
    }

    public void setGroup(String group) { //JOHA
        this.group = group;
    }

}