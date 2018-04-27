package eu.h2020.symbiote.crm.model.authorization;

/*
 * @author Matteo Pardi <m.pardi@nextworks.it>
*/
public class AuthorizationResult {
    private String message;
    private boolean validated;

    public AuthorizationResult(String message, boolean validated) {
        this.message = message;
        this.validated = validated;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isValidated() { return validated; }
    public void setValidated(boolean validated) { this.validated = validated; }
}
