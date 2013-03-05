package za.ac.sun.cs.intlola;


public class TestClient {

	public static void main(String argv[]) {
		/*String fname = "kselect3228.zip";
		byte[] fbytes = new byte[1024], sbytes = new byte[1024];
		OutputStream snd = null;
		FileInputStream fis = null;
		Socket sock = null;
		InputStream rcv = null;
		try {
			sock = new Socket("localhost", 9988);
			snd = sock.getOutputStream();
			snd.write("CONNECT".getBytes());
			rcv = sock.getInputStream();
			rcv.read(sbytes);
			System.out.println(new String(sbytes));
			snd.write(fname.getBytes());
			int count;
			fis = new FileInputStream(fname);
			while ((count = fis.read(fbytes)) >= 0) {
				snd.write(fbytes, 0, count);

			}
			snd.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				snd.close();
				rcv.close();
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}*/
	}
}
