package lk.ac.mrt.distributed.api.messages;

/**
 * @author Chathura Widanage
 */
public abstract class Message {
    public abstract String getSendableString();

    public static String getLengthAppenedMessage(String message){
        int length = message.length() + 5;
        String appened = "";
        if(length<1000){
            appened+="0";
        }else if(length<100){
            appened+="00";
        }else if(length<10){
            appened+="000";
        }
        appened+=length+" "+message;
        return appened;
    }
}
