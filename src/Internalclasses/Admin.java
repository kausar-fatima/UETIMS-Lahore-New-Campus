package Internalclasses;

import java.util.ArrayList;
import java.util.List;

public class Admin {
   private String name;
   private String email;
   private String password;
   private String verificationCode;
   private boolean status;
   private List<String> admn = new ArrayList<>();
   
   public Admin(){}
   public void setName(String name) {
	   this.name = name;
   }
   public void setEmail(String email) {
	   this.email = email;
   }
   public void setPassword(String password) {
	   this.password = password;
   }
   public void setVerificationCode(String verificationCode) {
	   this.verificationCode = verificationCode;
   }
   public void setStatus(boolean status) {
	   this.status = status;
   }
   private String getName() {
	   return name;
   }
   private String getEmail() {
	   return email;
   }
   private String getPassword() {
	   return password;
   } 
   private String getVerificationCode() {
	   return verificationCode;
   }
   private boolean getStatus() {
	   return status;
   }
   public List<String> SignUp() {
	   admn= new ArrayList<>();
	   crudCommands<String> attribute1 = new AddCommand<>(getEmail());
	   admn = attribute1.execute(admn);
	   crudCommands<String> attribute2 = new AddCommand<>(getPassword());
	   admn = attribute2.execute(admn);
	   crudCommands<String> attribute3 = new AddCommand<>(getName());
	   admn = attribute3.execute(admn);
	      
	   crudCommands<String> attribute4 = new AddCommand<>(getVerificationCode());
	   admn = attribute4.execute(admn);
	   crudCommands<String> attribute5 = new AddCommand<>(String.valueOf(getStatus()));
	   admn = attribute5.execute(admn);
	   
	   return admn;
   }
   public void Login(String email,String password) {
	   if(admn.get(1).equalsIgnoreCase(email) && admn.get(2).equalsIgnoreCase(password)) {
		  // System.out.println("Allow to login");
	   }
   }
}
