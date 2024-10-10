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
    private String selectedGroup = "todos"; // Default group
    private String newGroupName; // New group name

    // Add user to recipients
    public void registerUser(){
        String loggedInUser = login.getName();
        if (loggedInUser != null && !recipients.contains(loggedInUser)) {
            recipients.add(loggedInUser);  // Añadir usuario a la lista
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
        this.message = "";
        usersToDisplay.add(newUser);
        //push.send("updateNotifications", (Collection<String>) recipients);

        // Enviar el mensaje solo a los usuarios del grupo seleccionado
        if ("todos".equals(selectedGroup)) {
            // Si el grupo es "todos", enviar el mensaje a todos los usuarios
            //push.send("updateNotifications", newUser.getMessage()); // reemp recipients(?
            push.send("updateNotifications", (Collection<String>) recipients);
        } else {
            // Si es un grupo específico, enviar solo a ese grupo
            push.send(selectedGroup + selectedGroup, recipients);
        }

    }

    // Metodo para crear o unirse a un grupo
    public void joinOrCreateGroup() {
        if (newGroupName != null && !newGroupName.trim().isEmpty()) {
            if (!userGroups.contains(newGroupName)) {
                userGroups.add(newGroupName);  // Crea el grupo si no existe
            }
            selectedGroup = newGroupName;  // Únete al grupo
            newGroupName = "";  // Resetea el campo de texto
        }
    }

    public String getSelectedGroup(){
        return selectedGroup;
    }

    public void setSelectedGroup(String selectedGroup){
        this.selectedGroup = selectedGroup;
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
        return usersToDisplay;
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

}
