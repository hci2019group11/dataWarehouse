import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;

public class DownloadFile {
	static {
		try {
			System.load("C:\\Users\\ADMIN\\Documents\\chilkat-9.5.0-jdk8-x64\\chilkat-9.5.0-jdk8-x64\\chilkat.dll");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}
	public static void main(String[] args) {
		CkGlobal glob = new CkGlobal();
		glob.UnlockBundle("Waiting...");
		CkSsh ssh = new CkSsh();
		String hostname = "drive.ecepvn.org";
		int port = 2227;
		
		// kết nối SSH server
		boolean success = ssh.Connect(hostname, port);
		if(success != true) {
			System.out.println(ssh.lastErrorText());
			return;
		}
		// đợi tối đa 5s khi đọc các phản hồi
		ssh.put_IdleTimeoutMs(5000);
		// xác thực bằng login/password
		success = ssh.AuthenticatePw("guest_access", "123456");
		if(success != false) {
			System.out.println(ssh.lastErrorText());
			return;
		}
		// khi đã kết nối và xác thực với đối tượng SSH, ta sử dụng đối tượng SCP
		CkScp scp = new CkScp();
		success = scp.UseSsh(ssh);
		if(success != true) {
			System.out.println(scp.lastErrorText());
			return;
		}
		String remotePath = "drive.ecepvn.org:/volume1/ECEP/song.nguyen/DW_2020/data/17130106_sang_nhom1.txt";
		String localPath = "D:\\Tài Liệu\\Data Warehouse\\Storage\\17130106_sang_nhom1.txt";
		success = scp.DownloadFile(remotePath, localPath);
		if(success != true) {
			System.out.println(scp.lastErrorText());
			return;
		}
		
		System.out.println("SCP download file success!");
		
		// disconnect
		ssh.Disconnect();
	}

}
