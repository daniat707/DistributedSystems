package chat.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.push.Push;
import jakarta.faces.push.PushContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.*;

@Named
@ApplicationScoped
public class ChatAppBean implements Serializable {

    @Inject
    @Push(channel = "push")
    private PushContext push;

    private Set<String> recipients = new HashSet<>();

    @Inject
    Login login;
    private String name;

    private String message;

    private List<User> usersToDisplay = new ArrayList<>();

    // Grupos de usuarios
    private List<String> userGroups = new ArrayList<>(Arrays.asList("todos"));
    private String newGroupName; // New group name

    // Add user to recipients
    public void registerUser(){
        String loggedInUser = login.getName();
        // Associate user with 'todos' group
        login.setCurrentGroup("todos");
        if (loggedInUser != null && !recipients.contains(loggedInUser)) {
            recipients.add(loggedInUser);  // Add user to the list of recipients
            System.out.println("Registered user: " + loggedInUser);
        }
    }

    public String endSession() {

        // Eliminar el usuario al cerrar sesión
        recipients.remove(login.getName());

        // Invalidate the session
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate(); // This will destroy the session and invoke @PreDestroy on the SessionScoped bean
        }

        return "loggedOut.xhtml";
    }

    public void updateTable() {
        User newUser = new User();
        newUser.setUserName(login.getName() + ":   ");
        newUser.setMessage(message);
        newUser.setGroupName(login.getCurrentGroup());
        this.message = "";
        usersToDisplay.add(newUser);
        push.send("updateNotifications", (Collection<String>) recipients);
    }

    // Metodo para crear o unirse a un grupo
    public void joinOrCreateGroup() {
        if (newGroupName != null && !newGroupName.trim().isEmpty()) {
            if (!userGroups.contains(newGroupName)) {
                push.send("updateDropdown", recipients);
                userGroups.add(newGroupName);  // Crea el grupo si no existe
            }
            newGroupName = "";  // Resetea el campo de texto
        }
    }

    public List<String> getUserGroups(){
        return userGroups;
    }

    public void setUserGroups(List<String> userGroups){
        this.userGroups = userGroups;
    }

    public String getNewGroupName(){
        return newGroupName;
    }

    public void setNewGroupName(String newGroupName){
        this.newGroupName = newGroupName;
    }

    public List<User> getUsersToDisplay() {
        // Filter the users to display based on the selected group
        List<User> users = new ArrayList<>();
        for (User user : usersToDisplay){
            if (login.getCurrentGroup().equals(user.getGroupName())){
                users.add(user);
            }
        }
        return users;
    }

    public void setUsersToDisplay(List<User> usersToDisplay) {
        this.usersToDisplay = usersToDisplay;
    }

    public void setLoggedInUsers(List<User> usersToDisplay) {
        this.usersToDisplay = usersToDisplay;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        message = "  " + newMessage;
    }

    public String getName() {
        return login.getName();
    }

    public void printState(){
        System.out.println("User: " + login.getName());
        System.out.println("Current group: " + login.getCurrentGroup());
        System.out.println("Available groups: " + userGroups);
    }
}