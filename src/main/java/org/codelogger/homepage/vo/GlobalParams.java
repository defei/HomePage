/**
 *
 */
package org.codelogger.homepage.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.codelogger.utils.ArrayUtils.isNotEmpty;
import static org.codelogger.utils.StringUtils.isBlank;

/**
 * 全局参数
 *
 * @author defei
 * @date 2015-2-3
 */
public class GlobalParams implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2980565642013490335L;

    /**
     * 手机操作系统 {ios,androId}
     */
    private String os;

    /**
     * 操作系统版本号
     */
    private String osVersion;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 客户端版本号
     */
    private String ver;

    /**
     * 手机型号
     */
    private String userAgent;

    /**
     * 网络类型{wifi,wan}
     */
    private String apn;

    /**
     * 签名
     */
    private String sign;

    /**
     * 当前登录用户Id(隔壁仓库号)
     */
    private Long userId;

    /**
     * 推广渠道标志
     */
    private String partner;

    /**
     * 推广子渠道标志
     */
    private String sub;

    /**
     * 定位纬度
     */
    private BigDecimal lat;
    /**
     * 定位经度
     */
    private BigDecimal lon;

    /**
     * 手机mac地址
     */
    private String mac;

    /**
     * 国际移动用户识别码
     */
    private String imsi;

    /**
     * 国际移动设备标识
     */
    private String imei;

    /**
     * 路由器地址
     */
    private String routerMac;

    /**
     * 基站信息
     */
    private String station;

    public static GlobalParams fromUrlParams(String urlGlobalParams) {

        GlobalParams globalParams = new GlobalParams();
        if (isBlank(urlGlobalParams)) {
            return globalParams;
        } else {
            String[] params = urlGlobalParams.split("&");
            if (isNotEmpty(params)) {
                for (String param : params) {
                    String[] paramKeyAndValue = param.split("=");
                    if (paramKeyAndValue.length == 2) {
                        Field declaredField = globalFields.get(paramKeyAndValue[0]);
                        if (declaredField != null) {
                            Type genericType = declaredField.getGenericType();
                            Object value = null;
                            try {
                                if (genericType == String.class) {
                                    value = paramKeyAndValue[1];
                                } else if (genericType == Integer.class
                                    || genericType == int.class) {
                                    value = Integer.valueOf(paramKeyAndValue[1]);
                                } else if (genericType == BigDecimal.class) {
                                    value = new BigDecimal(paramKeyAndValue[1]);
                                } else if (genericType == Long.class || genericType == long.class) {
                                    value = Long.valueOf(paramKeyAndValue[1]);
                                } else if (genericType == Boolean.class
                                    || genericType == boolean.class) {
                                    value = Boolean.valueOf(paramKeyAndValue[1]);
                                } else if (genericType == Byte.class || genericType == byte.class) {
                                    value = Byte.valueOf(paramKeyAndValue[1]);
                                } else if (genericType == Short.class
                                    || genericType == short.class) {
                                    value = Short.valueOf(paramKeyAndValue[1]);
                                }
                                if (value != null) {
                                    declaredField.set(globalParams, value);
                                }
                            } catch (IllegalAccessException e) {
                                logger.info("Can not set {} to {}.{}", value,
                                    GlobalParams.class.getClass().getName(),
                                    declaredField.getName(), e);
                            }
                        }
                    }
                }
            }
            return globalParams;
        }
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getRouterMac() {
        return routerMac;
    }

    public void setRouterMac(String routerMac) {
        this.routerMac = routerMac;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }


    private static final Map<String, Field> globalFields = newHashMap();

    static {

        Field[] declaredFields = GlobalParams.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (!Modifier.isFinal(declaredField.getModifiers())) {
                declaredField.setAccessible(true);
                globalFields.put(declaredField.getName(), declaredField);
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(GlobalParams.class);
}
