package de.simonbrungs.teachingit.api.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.events.CreateAccountEvent;

public class AccountManager {
	public Account loginUser(String pUsername, String pPassword) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select id, activated from " + TeachingIt.getInstance().getConnection().getDatabase()
							+ ".`" + TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE user='"
							+ pUsername + "' AND password='" + pPassword + "' LIMIT 1");
			if (resultSet.next()) {
				Account account = new Account(resultSet.getInt("id"));
				if (resultSet.getByte("activated") == 1 && !account.isBanned()) {
					return account;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
	}

	public Account getUser(String pUsername) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select id from " + TeachingIt.getInstance().getConnection().getDatabase() + ".`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE user='"
							+ pUsername + "' LIMIT 1");
			if (resultSet.next()) {
				return new Account(resultSet.getInt("id"));
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
	}

	public Account getUser(int pUserID) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select id from `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE id='" + pUserID
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return new Account(pUserID);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
	}

	public Account createAccount(String pUserName, String pEmail, String pPassword, boolean pActive) {
		if (getUser(pUserName) != null)
			return null;
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		CreateAccountEvent createAccountEvent = new CreateAccountEvent(pUserName, pEmail, pActive);
		TeachingIt.getInstance().getEventExecuter().executeEvent(createAccountEvent);
		if (createAccountEvent.isCanceld()) {
			return null;
		}
		try {
			PreparedStatement preparedStatement = con.prepareStatement("insert into  `"
					+ TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
					+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` values (?, ?, ?, ?, ?, ?)");
			preparedStatement.setString(1, pUserName);
			preparedStatement.setString(2, pEmail);
			preparedStatement.setNull(3, 3);
			preparedStatement.setLong(4, System.currentTimeMillis() / 1000L);
			preparedStatement.setInt(5, 0);
			preparedStatement.setNull(3, 5);
			preparedStatement.setInt(6, 1);
			preparedStatement.executeUpdate();
			return getUser(pUserName);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
	}

	public void removeUser(int pID) {

	}

	public void removeUser(String pUserName) {

	}
}
