import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HelloWorld {
	
	public static void main(String[] args) throws Exception{
		System.out.println("fuk u");
		System.out.println("tell me stuff:");
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		bf.readLine();
		System.out.println("lol");
	}

}
