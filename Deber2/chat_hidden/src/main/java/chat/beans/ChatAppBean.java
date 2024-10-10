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
    private Set<String> recipients = new HashSet<>();  //new ArrayList<>();

    @Inject
    Login login;
    private String name;

    private String message;

    // Lista de users a mostrar en el chat
    private List<User> usersToDisplay = new ArrayList<>();

    // Grupos de usuarios
    private List<String> userGroups = new ArrayList<>(Arrays.asList("todos"));  // Grupo "todos" por defecto
    private String selectedGroup = "todos";  // Grupo seleccionado
    private String newGroupName;  // Nombre del nuevo grupo

    //private ArrayList<String> recipients = new ArrayList<>(Arrays.asList("joha", "mateo"));  // Lista de receptores (puede expandirse)

    // Agregar el usuario logueado a la lista de recipients
    public void registerUser() {
        String loggedInUser = login.getName();
        if (loggedInUser != null && !recipients.contains(loggedInUser)) {
            recipients.add(loggedInUser);  // Añadir usuario a la lista
            System.out.println("Usuario registrado: " + loggedInUser);
        }
    }

    public String endSession() {
        // Eliminar el usuario al cerrar sesión
        recipients.remove(login.getName());

        // Invalidar la sesión
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate(); // Destruye la sesión e invoca @PreDestroy en el bean SessionScoped
        }

        return "loggedOut.xhtml";
    }

    public void updateTable() { //JOHA
        User newUser = new User();
        newUser.setUserName(login.getName() + ": ");
        newUser.setMessage(message);
        newUser.setGroup(selectedGroup);  // Asociar el grupo al mensaje
        this.message = "";
        usersToDisplay.add(newUser);

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

    //Getters y setters
    public List<User> getUsersToDisplay() { //JOHA
        if ("todos".equals(selectedGroup)) {
            return usersToDisplay;  // Mostrar todos los mensajes
        } else {
            // Filtrar los mensajes por el grupo seleccionado
            List<User> filteredUsers = new ArrayList<>();
            for (User user : usersToDisplay) {
                if (selectedGroup.equals(user.getGroup())) {
                    filteredUsers.add(user);
                }
            }
            return filteredUsers;
        }
    }

    public void setUsersToDisplay(List<User> usersToDisplay) {
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

    public List<String> getUserGroups() {
        return userGroups;
    }

    public String getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(String selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public void setNewGroupName(String newGroupName) {
        this.newGroupName = newGroupName;
    }
}