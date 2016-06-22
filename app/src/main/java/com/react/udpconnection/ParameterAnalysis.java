package com.react.udpconnection;

/**
 * 解析请求
 * Created by 82138 on 2016/6/21.
 */
public class ParameterAnalysis {

    private String RequestBody;
    private String[] RequestBodyEntity;
    private String SEARCH = "M-SEARCH * HTTP/1.1";
    private String HOST = "HOST:";
    private String MAN = "MAN:";
    private String MX = "MX:";
    private String ST = "ST:";

    public String getHOST() {
        return HOST;
    }

    public String getMAN() {
        return MAN;
    }

    public String getMX() {
        return MX;
    }

    public String getST() {
        return ST;
    }

    private static ParameterAnalysis parameterAnalysis = null;

    public void setRequestBody(String requestBody) {
        RequestBody = requestBody;
    }

    public void Parser() {
        RequestBodyEntity = RequestBody.split("\n");
        for (int i = 0; i < RequestBodyEntity.length; i++) {
            if (RequestBodyEntity[i].startsWith("HOST")) {
                this.HOST = RequestBodyEntity[i].substring(this.HOST.length() + 1, RequestBodyEntity[i].length() - 2);
            } else if (RequestBodyEntity[i].startsWith("MAN")) {
                this.MAN = RequestBodyEntity[i].substring(this.MAN.length() + 1, RequestBodyEntity[i].length() - 2);
            } else if (RequestBodyEntity[i].startsWith("MX")) {
                this.MX = RequestBodyEntity[i].substring(this.MX.length() + 1, RequestBodyEntity[i].length() - 2);
            } else if (RequestBodyEntity[i].startsWith("ST")) {
                this.ST = RequestBodyEntity[i].substring(this.ST.length() + 1, RequestBodyEntity[i].length() - 2);
            }
        }
    }

    public void clear() {
        HOST = "HOST:";
        MAN = "MAN:";
        MX = "MX:";
        ST = "ST:";
    }

    public synchronized static ParameterAnalysis getParameterAnalysis () {
        if (parameterAnalysis == null) {
            parameterAnalysis = new ParameterAnalysis();
        }
        return parameterAnalysis;
    }
}
