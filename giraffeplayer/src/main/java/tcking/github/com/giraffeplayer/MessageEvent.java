package tcking.github.com.giraffeplayer;

/**
 * Created by 23578 on 2018/7/13.
 */

public class MessageEvent {
    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
