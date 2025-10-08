package com.mediaghor.darkscreen.Helper;

import android.os.Build;

public class ManufacturerDetector {

    public static String getManufacturerName() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();

        if (manufacturer.contains("xiaomi")) {
            return "Xiaomi (MIUI)";
        } else if (manufacturer.contains("huawei") || manufacturer.contains("honor")) {
            return "Huawei (EMUI)";
        } else if (manufacturer.contains("oppo")) {
            return "Oppo (ColorOS)";
        } else if (manufacturer.contains("vivo")) {
            return "Vivo (Funtouch OS)";
        } else if (manufacturer.contains("samsung")) {
            return "Samsung (One UI)";
        } else if (manufacturer.contains("oneplus")) {
            return "OnePlus (OxygenOS)";
        } else if (manufacturer.contains("realme")) {
            return "Realme (Realme UI)";
        } else if (manufacturer.contains("motorola")) {
            return "Motorola";
        } else if (manufacturer.contains("google")) {
            return "Google (Pixel)";
        } else if (manufacturer.contains("sony")) {
            return "Sony";
        } else if (manufacturer.contains("lg")) {
            return "LG";
        } else if (manufacturer.contains("nokia")) {
            return "Nokia";
        } else {
            // Return capitalized manufacturer name for others
            return capitalizeFirstLetter(manufacturer);
        }
    }




    public static boolean isProblematicManufacturer() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        return manufacturer.contains("xiaomi") ||
                manufacturer.contains("huawei") ||
                manufacturer.contains("honor") ||
                manufacturer.contains("vivo") ||
                manufacturer.contains("realme") ||


                manufacturer.contains("oppo");

    }



    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    // Get raw manufacturer name without skin info
    public static String getRawManufacturer() {
        return Build.MANUFACTURER;
    }

    // Get device model
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    // Get device brand (sometimes different from manufacturer)
    public static String getDeviceBrand() {
        return Build.BRAND;
    }
}