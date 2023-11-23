module com.build.fcproj1 {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.build.fcproj1 to javafx.fxml;
    exports com.build.fcproj1;
}