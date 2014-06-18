package net.sf.robocode.roborumble.netengine;

public class DownloadFailedException extends Throwable {

    private final String message;

    public DownloadFailedException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
