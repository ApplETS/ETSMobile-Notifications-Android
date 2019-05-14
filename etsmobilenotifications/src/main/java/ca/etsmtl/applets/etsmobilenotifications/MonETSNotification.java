package ca.etsmtl.applets.etsmobilenotifications;

/**
 * Created by gnut3ll4 on 15/12/15.
 */
public class MonETSNotification {

    private int id;
    private int dossierId;
    private String notificationTexte;
    private String notificationDateCreation;
    private String notificationDateDebutAffichage;
    private String notificationApplicationNom;
    private String NotificationData;
    private String url;

    public MonETSNotification(int id, int dossierId, String notificationTexte,
                              String notificationDateCreation, String notificationDateDebutAffichage,
                              String notificationApplicationNom, String notificationData,
                              String url) {
        this.id = id;
        this.dossierId = dossierId;
        this.notificationTexte = notificationTexte;
        this.notificationDateCreation = notificationDateCreation;
        this.notificationDateDebutAffichage = notificationDateDebutAffichage;
        this.notificationApplicationNom = notificationApplicationNom;
        NotificationData = notificationData;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public int getDossierId() {
        return dossierId;
    }

    public String getNotificationTexte() {
        return notificationTexte;
    }

    public String getNotificationDateCreation() {
        return notificationDateCreation;
    }

    public String getNotificationDateDebutAffichage() {
        return notificationDateDebutAffichage;
    }

    public String getNotificationApplicationNom() {
        return notificationApplicationNom;
    }

    public String getNotificationData() {
        return NotificationData;
    }

    public String getUrl() {
        return url;
    }
}
