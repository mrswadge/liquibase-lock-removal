package com.traceisys.liquibase.lock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.naming.InitialContext;
import javax.sql.DataSource;

@ManagedBean
@RequestScoped
public class LockRemover {
	private String message = "To remove the lock press one of the buttons!";
	
	public String getMessage() {
		return message;
	}

	public void setMessage( String message ) {
		this.message = message;
	}

	public void zero() {
		String sql = "update databasechangeloglock set locked = 0";
		updateDatabase( sql );
	}
	
	public void delete() {
		String sql = "delete from databasechangeloglock";
		updateDatabase( sql );
	}

	private void updateDatabase( String sql ) {
		Date now = new Date();
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup( "jdbc/EJBDataSource" );
			try ( Connection conn = ds.getConnection() ) {
				PreparedStatement stmt = conn.prepareStatement( sql );
				int updated = stmt.executeUpdate();
				this.message = String.format( "%tF %tR Updated %d locks ", now, now, updated );
				conn.commit();
			} catch ( Exception ex ) {
				throw ex;
			}
		} catch ( Exception e ) {
			this.message = String.format( "%tF %tR - failed to update locks - message was: %s", now, now, e.getMessage() );
			e.printStackTrace();
		}
	}
}
