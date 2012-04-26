import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleClient {
	Connection con;
	
	public OracleClient(String address, String login, String pass) throws SQLException, ClassNotFoundException {
		Class.forName("oracle.jdbc.OracleDriver");
		DriverManager.setLoginTimeout(60000);
		con = DriverManager.getConnection(address, login, pass);
	}
	public void close() throws SQLException{
		con.close();
	}
	
	private void execute(String request) throws SQLException {
		ResultSet rs = con.prepareStatement(request).executeQuery();
		int colsNb = rs.getMetaData().getColumnCount();
		while (rs.next()) {
			StringBuffer result = new StringBuffer(rs.getString(1));
			for (int i = 2; i <= colsNb; i++)
				result.append(";").append(rs.getString(i));
			System.out.println(result);
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 5 && args.length != 6){
			System.err.println("5 or 6 parameters are needed: host port sid login pass [request]");
			System.err.println("\tWith 5 parameters the requests are expected through system input one per line");
			System.exit(1);
		}
		String address = "jdbc:oracle:thin:@" + args[0] + ":" + args[1] + ":" + args[2];

		OracleClient client = new OracleClient(address, args[3], args[4]);
		if (args.length == 5) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while ((line = br.readLine()) != null) {
				client.execute(line);
			}
		} else {
			client.execute(args[5]);
		}
		client.close();
	}
}