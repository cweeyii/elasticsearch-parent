package com.cweeyii.util;

/**
 * Created by wenyi on 17/5/10.
 * Email:caowenyi@meituan.com
 */
public class DistanceUtil {
    public static Double getDistance(double firstLat, double firstLng, double secondLat,
                                     double secondLng) {
        Double s = 0.0;
        Double firstlatRad = rad(firstLat);
        Double firstlngRad = rad(firstLng);
        Double secondlatRad = rad(secondLat);
        Double secondlngRad = rad(secondLng);

        Double latResult = firstlatRad - secondlatRad;
        Double lngResult = firstlngRad - secondlngRad;
        s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(latResult / 2), 2) + Math.cos(firstlatRad)
                * Math.cos(secondlatRad) * Math.pow(Math.sin(lngResult / 2), 2)));
        s = s * 6378137; // 地球半径 6378.137
        return s;

    }

    // 弧度转换
    private static double rad(Double location) {
        return location * Math.PI / 180.0;
    }
}
