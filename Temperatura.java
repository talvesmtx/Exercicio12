import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Temperatura {
	private double valor;
	private int id;

	public Temperatura(int id) {
		setId(id);
	}

	public Temperatura(int id, double valor) {
		setId(id);
		setValor(valor);
	}

	public Temperatura() {
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String toString() {
		return String.format("#%d\n%.0fºC", this.getId(), this.getValor());
	}

	// DATABASE
	public void inserir(Connection conn) {
		String sqlInsert = "INSERT INTO TEMPERATURA(valor) VALUES (?)";
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			stm = conn.prepareStatement(sqlInsert);
			stm.setDouble(1, getValor());
			stm.execute();
			// pega o id gerado pelo MySQL
			String selecao = "select LAST_INSERT_ID()";
			// fecha o prepared statement para usar de novo
			stm.close();
			stm = conn.prepareStatement(selecao);
			rs = stm.executeQuery();
			if (rs.next()) {
				this.setId(rs.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				System.out.print(e1.getStackTrace());
			}
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stm != null) {
				try {
					stm.close();
				} catch (SQLException e1) {
					System.out.print(e1.getStackTrace());
				}
			}
		}
	}

	public void alterar(Connection con) throws SQLException {
		String sqlUpdate = "UPDATE TEMPERATURA SET Valor = ? WHERE id = ?";
		try (PreparedStatement stm = con.prepareStatement(sqlUpdate)) {
			stm.setDouble(1, getValor());
			stm.setInt(2, getId());
			stm.execute();
		}
	}

	public void excluir(Connection con) throws SQLException {
		String sqlDelete = "DELETE FROM TEMPERATURA WHERE id = ?";
		try (PreparedStatement stm = con.prepareStatement(sqlDelete)) {
			stm.setInt(1, getId());
			stm.execute();
		}
	}

	public void carregar(Connection con) throws SQLException {
		String sqlSelect = "SELECT valor FROM TEMPERATURA WHERE id = ?";
		try (PreparedStatement stm = con.prepareStatement(sqlSelect)) {
			stm.setInt(1, getId());
			try (ResultSet rs = stm.getResultSet()) {
				if (rs.next()) {
					this.setValor(rs.getDouble(1));
				}
			}
		}
	}
}