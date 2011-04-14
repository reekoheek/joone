package com.xinixtech.joone.io;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joone.io.PatternTokenizer;
import org.joone.log.ILogger;
import org.joone.log.LoggerFactory;

public class MySqlTokenizer implements PatternTokenizer {

	private static final ILogger log = LoggerFactory.getLogger(MySqlTokenizer.class);

	private char decimalPoint = '.';

	private int lineno = 0;

	private int offset = -1;

	private int numTokens = 0;

	private List buffer;

	private String driverName = "com.mysql.jdbc.Driver";

	private String dbUrl;

	private String sqlQuery;

	private String dbUsername;

	private String dbPassword;

	private double[] tokensArray;

	private int maxResults = 0;

	public MySqlTokenizer(String dbUrl, String dbUsername, String dbPassword, String sqlQuery, int maxResults) {
//		log.info("create tokenizer");

		this.dbUrl = dbUrl;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.sqlQuery = sqlQuery;
		this.maxResults = maxResults;

		// try {
		// resetInput();
		// } catch (IOException e) {
		// }
	}

	public char getDecimalPoint() {
		return decimalPoint;
	}

	public int getLineno() {
		return lineno;
	}

	public int getNumTokens() throws IOException {
		return numTokens;
	}

	public double getTokenAt(int posiz) throws IOException {
//		log.info("lineno:" + lineno);
		if (getTokensArray() != null && getTokensArray().length > posiz) {
			return getTokensArray()[posiz];
		}
		return 0;
	}

	public double[] getTokensArray() {
		if (tokensArray == null && lineno >= 0) {
			try {
				fetchTokenArray();
			} catch (RuntimeException e) {
			}
		}
		return tokensArray;
	}

	private void fetchTokenArray() throws RuntimeException {
		int cursor = lineno;
		if (maxResults > 0) {
			cursor = lineno % maxResults;
		}
		double[] tokensArray = (double[]) getBuffer().get(cursor);
		this.tokensArray = tokensArray;
	}

	public void mark() throws IOException {
	}

	public boolean nextLine() throws IOException {
		this.lineno = getLineno() + 1;

		if (getBuffer().size() < 1) {
			return false;
		}
		try {
			fetchTokenArray();
		} catch (RuntimeException e) {
			return false;
		}
		return true;
	}

	private void setLineno(int lineno) {
		this.lineno = lineno;
		if (lineno >= 0) {
			fetchTokenArray();
		}
	}

	public void resetInput() throws IOException {
		buffer = null;
		offset = -1;
		setLineno(-1);
	}

	public void setDecimalPoint(char decimalPoint) {
		this.decimalPoint = decimalPoint;
	}

	public List getBuffer() {
		int expectedOffset = 0;
		if (maxResults > 0) {
			expectedOffset = (lineno / maxResults) * maxResults;
		}
		if (buffer == null || expectedOffset != offset) {
			String sql = sqlQuery;
			if (maxResults > 0) {
				sql += " limit " + expectedOffset + ", " + maxResults;
			}
			log.info(sql);

			List bufferedList = new ArrayList();

			// log.info("fetch new buffer offset:" + expectedOffset);
			offset = expectedOffset;

			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			if ((driverName != null) && (!"".equals(driverName))) {
				if ((dbUrl != null) && (!"".equals(dbUrl))) {
					if ((sqlQuery != null) && (!"".equals(sqlQuery))) {
						try {
							Class.forName(driverName);
							con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
							stmt = con.createStatement();
							rs = stmt.executeQuery(sql);

							while (rs.next()) {
								int columnCount = rs.getMetaData().getColumnCount();
								double[] data = new double[columnCount];
								for (int counter = 1; counter <= columnCount; counter++) {
									data[counter - 1] = rs.getDouble(counter);
								}
								bufferedList.add(data);
							}
						} catch (ClassNotFoundException ex) {
							log.error(
									"Could not find Database Driver Class while initializing the JDBCInputStream. Message is : "
											+ ex.getMessage(), ex); // LOG4J
						} catch (SQLException sqlex) {
							log.error("SQLException thrown while initializing the JDBCInputStream. Message is : "
									+ sqlex.getMessage(), sqlex); // LOG4J
						} finally {
							try {
								rs.close();
							} catch (Exception e1) {
							}
							try {
								stmt.close();
							} catch (Exception e1) {
							}
							try {
								con.close();
							} catch (Exception e1) {
							}
							this.buffer = /*Collections.synchronizedList(*/bufferedList/*)*/;
						}
					}
				}
			}
		}
		return buffer;
	}

}
