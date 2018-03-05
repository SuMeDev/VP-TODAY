package vortex.vp_today;

/**
 * @author Simon Dräger
 * @version 5.3.18
 */

public enum VPKind {
    VERTRETUNG("Vertretung"),
    ENTFALL("Entfall"),
    EIGENVARBEITEN("Eigenv. Arbeiten");

    private final String name;

    VPKind(String strEquiv) {
        name = strEquiv;
    }

    public String getName() {
        return name;
    }
}