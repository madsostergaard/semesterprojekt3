import java.io.*;
import java.util.*;

class Conf {
		
   private String name;
   private String value;

   public Conf(String name, String value) {
      this.name = name;
      this.value = value;
   }

   public String getName() {
      return name;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

}

class Configs {
	
   private Vector<Conf> map = new Vector<Conf>();
	
   public int lines() {
      return map.size();
   }
   
   private int search(String n) {
      int l = lines();
      
      for (int i = 0; i < l; i++) {
         if ( map.get(i).getName().equals(n) )
            return i;
      }
      return -1;
   }
   
   public boolean add(String n, String v) {
      int i = search(n);
      if ( i < 0 ) {
         Conf c = new Conf(n,v);
         map.add(c);
         return true;
      }
      return false;
   }
   
   public void del(String n) {
      int i = search(n);
      if ( i > -1 )
         map.remove(i);
   }
   
   public boolean change(String n, String v) {
      int i = search(n);
      if ( i > -1 ) {
         map.get(i).setValue(v);
         return true;
      }
      return false;
   }
   public String value(String n) {
      int i = search(n);
      if ( i > -1 )
         return map.get(i).getValue();
      return null;
   }
   
   public Conf getConf(int i) {
      return map.get(i);
   }
   
   public boolean exists(String n) {
      return search(n) > -1;
   }
}

public class Config {

   private String filnavn = null;
   private BufferedReader inddata = null;
   private int error = 0;
   private Configs config = new Configs();
   private static Config conf = null;

   public Config(String filnavn) {
      this.filnavn = filnavn;
      try {
         inddata = new BufferedReader(new FileReader(filnavn));
      } catch (IOException e) {
         error = 1;
      }
      if ( error == 0 ) {
         getData();
         try {
            inddata.close();
         } catch (IOException e) {}
      }
   }

   private void getData() {
      String l = "";
      try {
         do {
            l = inddata.readLine();
            if ( l != null && l.length() > 0 && l.charAt(0) != ';' ) {
               StringTokenizer tt = new StringTokenizer(l,"=\n\r");
               if ( tt.hasMoreTokens() ) {
                  String n = tt.nextToken().trim();
                  if ( n != null ) {
                     if ( tt.hasMoreTokens() ) {
                        String v = tt.nextToken().trim();
                        if ( v != null ) config.add(n,v);
                     } else config.add(n,"");
                  }
               }
            }
         } while ( l != null );
      } catch (IOException e) {
         error = 2;
      }
   }
   
   public int getError() {
      return error;
   }
   
   public boolean confExists(String s) {
      return config.exists(s);
   }
   
   public String confValue(String s) {
      return config.value(s);
   }
   
   public void confChange(String n, String v) {
      config.change(n,v);
   }
   
   public void newConf(String n, String v) {
      config.add(n,v);
   }
   
   public void confRemove(String n) {
      config.del(n);
   }
   
   public void printConfig() {
      for (int i=0; i<config.lines(); i++) {
         System.out.println(config.getConf(i).getName()+" = "+config.getConf(i).getValue());
      }
   }
   
   public boolean save(String filnavn) {
      try {
         PrintWriter uddata = new PrintWriter(new FileWriter(filnavn));
         for (int i=0; i<config.lines(); i++)
            uddata.println(config.getConf(i).getName()+"="+config.getConf(i).getValue());
         uddata.close();
      } catch (IOException e) {
         return false;
      }
      return true;
   }
   
   public boolean save() {
      try {
         PrintWriter uddata = new PrintWriter(new FileWriter(filnavn));
         for (int i=0; i<config.lines(); i++)
            uddata.println(config.getConf(i).getName()+"="+config.getConf(i).getValue());
         uddata.close();
      } catch (IOException e) {
         return false;
      }
      return true;
   }
   
   private static void test(String name) {
      if ( conf.confExists(name) ) System.out.println(name+": "+conf.confValue(name));
      else System.out.println(name+": missing");
   }
   
   public static void main(String args[]) {
      
      conf = new Config("newtest.conf");
      if ( conf.getError() != 0 )
         System.out.println("Fejl: "+conf.getError());
      else {
      
         conf.newConf("ip","192.192.192.192");
         conf.newConf("navn", "en værdi");
         conf.newConf("hejsa","hello");
      
         conf.save();
         
      }
      test("ip");
      conf = new Config("tester.conf");
      conf.newConf("a", "b");
      conf.save();
      /*if ( conf.getError() != 0 )
         System.out.println("Fejl: "+conf.getError());
      else {
         conf.printConfig();
      
         System.out.println("---------------");
         test("ip");
         test("bad");
         test("empty");
      
         conf.confRemove("test2");
         conf.confChange("LangLinie","En del kortere");
      
         conf.save("test2.conf");
      }*/
      
   }
}

