package com.tuituidan.openhub.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * IpUtils.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/11/4
 */
@Slf4j
@UtilityClass
public class IpUtils {

    private static String localIp = "";

    /**
     * 获取本机IP.
     *
     * @return IP
     */
    public static String getLocalIp() {
        if (StringUtils.hasText(localIp)) {
            return localIp;
        }
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                if (!isValidInterface(iface)) {
                    continue;
                }
                Enumeration<InetAddress> inetAddrs = iface.getInetAddresses();
                while (inetAddrs.hasMoreElements()) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (isValidAddress(inetAddr)) {
                        localIp = inetAddr.getHostAddress();
                        break;
                    }
                }
            }
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            Assert.notNull(jdkSuppliedAddress, "InetAddress.getLocalHost获取失败.");
            localIp = jdkSuppliedAddress.getHostAddress();
        } catch (Exception ex) {
            log.error("获取本机IP失败", ex);
        }
        return localIp;
    }

    /**
     * 过滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
     *
     * @param ni 网卡
     * @return boolean
     * @throws SocketException Socket异常
     */
    private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
        return !ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens"));
    }

    /**
     * 判断是否是IPv4，并且内网地址并过滤回环地址.
     *
     * @param address address
     * @return boolean
     */
    private static boolean isValidAddress(InetAddress address) {
        return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
    }
}
