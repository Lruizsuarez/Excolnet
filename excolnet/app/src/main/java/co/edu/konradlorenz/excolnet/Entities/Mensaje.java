package co.edu.konradlorenz.excolnet.Entities;

public class Mensaje {

    private String SenderUID;
    private String DestinyUUID;
    private String senderDisplayName;
    private String senderImage;
    private String senderTime;
    private String message;
    private String photoUrl;
    private String message_type;

    public Mensaje() {
    }

    private Mensaje(final Builder builder) {
        this.SenderUID = builder.SenderUID;
        this.DestinyUUID = builder.DestinyUUID;
        this.senderDisplayName = builder.senderDisplayName;
        this.senderImage = builder.senderImage;
        this.senderTime = builder.senderTime;
        this.message = builder.message;
        this.photoUrl = builder.photoUrl;
        this.message_type = builder.message_type;
    }

    public String getSenderUID() {
        return SenderUID;
    }

    public String getDestinyUUID() {
        return DestinyUUID;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public String getSenderTime() {
        return senderTime;
    }

    public String getMessage() {
        return message;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getMessage_type() {
        return message_type;
    }

    public static class Builder {
        private String SenderUID;
        private String DestinyUUID;
        private String senderDisplayName;
        private String senderImage;
        private String senderTime;
        private String message;
        private String photoUrl;
        private String message_type;

        public Builder senderUID(final String senderUID) {
            SenderUID = senderUID;
            return this;
        }

        public Builder destinyUUID(final String destinyUUID) {
            DestinyUUID = destinyUUID;
            return this;
        }

        public Builder senderDisplayName(final String senderDisplayName) {
            this.senderDisplayName = senderDisplayName;
            return this;
        }

        public Builder senderImage(final String senderImage) {
            this.senderImage = senderImage;
            return this;
        }

        public Builder senderTime(final String senderTime) {
            this.senderTime = senderTime;
            return this;
        }

        public Builder message(final String message) {
            this.message = message;
            return this;
        }

        public Builder photoUrl(final String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public Builder message_type(final String message_type) {
            this.message_type = message_type;
            return this;
        }

        public Mensaje create(){
            return new Mensaje(this);
        }
    }
}
