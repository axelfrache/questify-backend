package me.axelfrache.questify.util;

import java.util.Base64;

/**
 * Utility class for image processing operations
 */
public class ImageUtils {

    private static final String BASE64_PREFIX = "data:image/";
    
    /**
     * Converts a Base64 encoded string to a byte array
     * 
     * @param base64Image Base64 encoded image string (can include data URI scheme prefix)
     * @return byte array of the image or null if input is null
     */
    public static byte[] base64ToBytes(String base64Image) {
        if (base64Image == null) {
            return null;
        }
        
        // Remove the data URI scheme prefix if present
        // Example: "data:image/jpeg;base64,/9j/4AAQSkZJRg..." -> "/9j/4AAQSkZJRg..."
        String base64Data = base64Image;
        if (base64Image.contains(",")) {
            base64Data = base64Image.substring(base64Image.indexOf(",") + 1);
        }
        
        return Base64.getDecoder().decode(base64Data);
    }
    
    /**
     * Converts a byte array to a Base64 encoded string
     * 
     * @param imageData byte array of the image
     * @param mimeType MIME type of the image (e.g., "image/jpeg", "image/png")
     * @return Base64 encoded string with data URI scheme prefix or null if input is null
     */
    public static String bytesToBase64(byte[] imageData, String mimeType) {
        if (imageData == null) {
            return null;
        }
        
        String base64 = Base64.getEncoder().encodeToString(imageData);
        return BASE64_PREFIX + mimeType + ";base64," + base64;
    }
    
    /**
     * Converts a byte array to a Base64 encoded string with default MIME type (image/jpeg)
     * 
     * @param imageData byte array of the image
     * @return Base64 encoded string with data URI scheme prefix or null if input is null
     */
    public static String bytesToBase64(byte[] imageData) {
        return bytesToBase64(imageData, "jpeg");
    }
    
    /**
     * Determines if a string is a valid Base64 encoded image
     * 
     * @param base64Image Base64 encoded image string
     * @return true if the string is a valid Base64 encoded image, false otherwise
     */
    public static boolean isValidBase64Image(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return false;
        }
        
        // Check if it has the data URI scheme prefix
        if (base64Image.startsWith(BASE64_PREFIX)) {
            // Check if it has the base64 indicator
            if (!base64Image.contains(";base64,")) {
                return false;
            }
            
            // Extract the Base64 data
            String base64Data = base64Image.substring(base64Image.indexOf(",") + 1);
            return isValidBase64(base64Data);
        }
        
        // If it doesn't have the prefix, check if it's a valid Base64 string
        return isValidBase64(base64Image);
    }
    
    /**
     * Determines if a string is a valid Base64 encoded string
     * 
     * @param base64 Base64 encoded string
     * @return true if the string is a valid Base64 encoded string, false otherwise
     */
    private static boolean isValidBase64(String base64) {
        try {
            Base64.getDecoder().decode(base64);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
