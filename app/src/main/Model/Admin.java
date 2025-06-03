package main.Model;

public class Admin {
    private int ID;
    private String username;
    private String password;

    public Admin(int ID, String username, String password) {
        this.ID = ID;
        this.username = username;
        this.password = password;
    }

    // Getters dan Setters  
    public int getID() {return ID;}
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public void setID(int ID) {this.ID = ID;}
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}

    public boolean Login(String Username,int Password) {
        if (this.username.equals(Username) && this.password.equals(Password)) {
            return true;
        } else {
            return false;
        }
    }
}
