package chat.beans;

public class User {

    private String userName;
    private String message;
    private String groupName;

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

    public void setGroupName(String groupName){
        this.groupName = groupName;
    }

    public String getGroupName(){
        return groupName;
    }
}