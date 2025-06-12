package Controller;

/**
 * Simple test to verify JavaFX imports work properly
 */
public class JavaFXImportTest {
    
    public static void main(String[] args) {
        // Test if we can import basic JavaFX classes
        try {
            Class.forName("javafx.application.Application");
            Class.forName("javafx.scene.Scene");
            Class.forName("javafx.stage.Stage");
            Class.forName("javafx.fxml.FXML");
            Class.forName("javafx.scene.control.Button");
            
            System.out.println("✅ All JavaFX classes loaded successfully!");
            System.out.println("JavaFX runtime is available and working.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ JavaFX class not found: " + e.getMessage());
            System.err.println("JavaFX might not be properly configured.");
        }
    }
}
