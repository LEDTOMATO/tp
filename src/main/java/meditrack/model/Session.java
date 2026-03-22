package meditrack.model;

/**
 * Represents the current user session in the application.
 */
public class Session {
    private Role currentRole;

    public void setRole(Role role) {
        this.currentRole = role;
    }

    public Role getRole() {
        return currentRole;
    }

    public void clear() {
        this.currentRole = null;
    }
}