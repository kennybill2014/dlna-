package com.react.udpconnection;

/**
 * 参数构造器
 * Created by 82138 on 2016/6/21.
 */
public class ParameterConstructor {
    private String NOTIFY = "NOTIFY * HTTP/1.1";
    private String NT = "NT:";
    private String HOST = "HOST:";
    private String NTS = "NTS:";
    private String LOCATION = "LOCATION:";
    private String CACHE_CONTROL = "CACHE-CONTROL: max-age=";
    private String SERVER = "SERVER:";
    private String USN = "USN:";
    private String UUID = "uuid:";
    private String RequestBody = "";

    private String DATE = "DATE:";
    private String HTTP = "HTTP/1.1 ";
    private String EXT = "EXT:";
    private String ST = "ST:";


    private static ParameterConstructor parameterConstructor = null;

    public void setNT(String NT) {
        this.NT += NT;
    }

    public void setNTS(String NTS) {
        this.NTS += NTS;
    }

    public void setCACHE_CONTROL(String CACHE_CONTROL) {
        this.CACHE_CONTROL += CACHE_CONTROL;
    }

    public void setUUID(String UUID) {
        this.UUID += UUID;
    }

    public void setSERVER(String SERVER) {
        this.SERVER += SERVER;
    }

    public void setLOCATION(String LOCATION) {
        this.LOCATION += LOCATION;
    }

    public void setHOST(String HOST) {
        this.HOST += HOST;
    }

    public String getRequsetBody() {
        return RequestBody;
    }

    public void setDATE(String DATE) {
        this.DATE += DATE;
    }

    public void setHTTP(String HTTP) {
        this.HTTP += HTTP;
    }

    public void setEXT(String EXT) {
        this.EXT += EXT;
    }

    public void setST(String ST) {
        this.ST += ST;
    }

    public void joinInit() {
        RequestBody = this.NOTIFY + "\n" + this.NT + "\n" + this.HOST + "\n" +
                this.NTS  + "\n" + this.LOCATION + "\n" + this.CACHE_CONTROL + "\n" +
                this.SERVER + "\n" + this.USN + this.UUID + ":" + this.NT;
    }

    public void responseInit() {
        RequestBody = this.HTTP + "\n" + this.CACHE_CONTROL + "\n" + this.DATE + "\n" +
                this.EXT + "\n" + this.SERVER + "\n" + this.LOCATION + "\n" + this.ST + "\n" +
                this.USN + this.UUID;
    }

    public synchronized static ParameterConstructor getParameterConstructor() {
        if (parameterConstructor == null) {
            parameterConstructor = new ParameterConstructor();
        }
        return parameterConstructor;
    }

    public void clear() {
        NOTIFY = "NOTIFY * HTTP/1.1";
        NT = "NT:";
        HOST = "HOST:";
        NTS = "NTS:";
        LOCATION = "LOCATION:";
        CACHE_CONTROL = "CACHE-CONTROL: max-age=";
        SERVER = "SERVER:";
        USN = "USN:";
        UUID = "uuid:";

        DATE = "DATE:";
        HTTP = "HTTP/1.1 ";
        EXT = "EXT:";
        ST = "ST:";
        RequestBody = "";
    }

}
