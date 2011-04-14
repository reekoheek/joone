package com.xinixtech.joone.io;

import org.joone.exception.JooneRuntimeException;
import org.joone.io.PatternTokenizer;
import org.joone.io.StreamInputSynapse;
import org.joone.log.ILogger;
import org.joone.log.LoggerFactory;

public class MyInputSynapse extends StreamInputSynapse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4777913602056882338L;

	private static final ILogger log = LoggerFactory.getLogger(MyInputSynapse.class);

	private String dbUrl = "jdbc:mysql://localhost/test";
	private String dbUsername = "root";
	private String dbPassword = "";
	private String sqlQuery = "";	

	private int maxResults = 0;

	public MyInputSynapse() {
		super();
		this.setAdvancedColumnSelector("1");
		this.setBuffered(true);
	}

	protected void initInputStream() throws JooneRuntimeException {
//		log.info("init input stream");
		PatternTokenizer tokens = new MySqlTokenizer(dbUrl, dbUsername, dbPassword, sqlQuery, maxResults);
		super.setTokens(tokens);
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		if (!this.dbUrl.equals(dbUrl)) {
			this.dbUrl = dbUrl;
            this.setTokens(null);
        }
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		if (!this.dbUsername.equals(dbUsername)) {
			this.dbUsername = dbUsername;
            this.setTokens(null);
        }
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		if (!this.dbPassword.equals(dbPassword)) {
			this.dbPassword = dbPassword;
            this.setTokens(null);
        }
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		if (!this.sqlQuery.equals(sqlQuery)) {
			this.sqlQuery = sqlQuery;
            this.setTokens(null);
        }
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		if (this.maxResults != maxResults) {
			this.maxResults = maxResults;
            this.setTokens(null);
        }
		
	}
}
