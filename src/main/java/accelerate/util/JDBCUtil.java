package accelerate.util;

import static accelerate.util.AccelerateConstants.COLON_CHAR;
import static accelerate.util.AppUtil.compare;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

/**
 * This class provides utility functions to create and get connections to
 * databases
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since September 11, 2009
 */
public final class JDBCUtil {

	/**
	 * hidden constructor
	 */
	private JDBCUtil() {
	}

	/**
	 * @param aJndiName
	 * @return {@link DataSource}
	 */
	public static DataSource getDataSource(String aJndiName) {
		JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		return dsLookup.getDataSource(aJndiName);
	}

	/**
	 * @param aDbType
	 * @param aServer
	 * @param aPort
	 * @param aServiceOrSid
	 * @param aDriverClassName
	 * @param aUsername
	 * @param aPassword
	 * @param aConnProps
	 * @return
	 */
	public static DataSource getDriverManagerDataSource(String aDbType, String aServer, int aPort, String aServiceOrSid,
			String aDriverClassName, String aUsername, String aPassword, Properties aConnProps) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(getConnectionString(aDbType, aServer, aPort, aServiceOrSid));
		dataSource.setDriverClassName(aDriverClassName);
		dataSource.setUsername(aUsername);
		dataSource.setPassword(aPassword);
		dataSource.setConnectionProperties(aConnProps);

		return dataSource;
	}

	/**
	 * @param aConnectionBean
	 * @return {@link Connection}
	 * @throws AccelerateException
	 */
	/**
	 * @param aDbType
	 * @param aServer
	 * @param aPort
	 * @param aServiceOrSid
	 * @return
	 */
	public static String getConnectionString(String aDbType, String aServer, int aPort, String aServiceOrSid) {
		StringBuilder connString = new StringBuilder();
		connString.append("jdbc:");
		connString.append(aDbType);
		connString.append(COLON_CHAR);

		if (compare(aDbType, "oracle")) {
			connString.append("thin:@");
		} else {
			connString.append("//");
		}

		connString.append(aServer);
		connString.append(COLON_CHAR);
		connString.append(aPort);

		if (compare(aDbType, "oracle")) {
			connString.append(COLON_CHAR);
		} else {
			connString.append("/");
		}

		connString.append(aServiceOrSid);
		return connString.toString();
	}
}