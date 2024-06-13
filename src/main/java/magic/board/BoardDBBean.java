package magic.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import magic.member.MemberBean;
import magic.member.MemberDBBean;

public class BoardDBBean {
	private static BoardDBBean instance = new BoardDBBean();
	
	public static BoardDBBean getInstance() {
		return instance;
	}
	
	public Connection getConnection() throws Exception {
		return ((DataSource)(new InitialContext().lookup("java:comp/env/jdbc/mysql"))).getConnection();
	}
	
	public int insertBoard(BoardBean board) {
		Connection conn=null;
		PreparedStatement pstmt=null;
		int re=-1;
		String sql="insert into boardt(b_name,b_email,b_title,b_content,b_pwd,b_date,b_ip)"
				+ " values(?,?,?,?,?,?,?)";
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, board.getB_name());
			pstmt.setString(2, board.getB_email());
			pstmt.setString(3, board.getB_title());
			pstmt.setString(4, board.getB_content());
			pstmt.setString(5, board.getB_pwd());
			pstmt.setTimestamp(6, board.getB_date());
			pstmt.setString(7, board.getB_ip());
//			INSERT 문은 executeUpdate 메소드 호출
			re = pstmt.executeUpdate();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(pstmt != null){ pstmt.close(); }
				if(conn != null){ conn.close(); }
			}catch(Exception se){
				se.printStackTrace();
			}
		}
		
		return re;
	}
	
	public ArrayList<BoardBean> listBoard() {

		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select b_id,b_name,b_email,b_title,b_content,b_pwd,b_date,b_hit,b_ip from boardt";
		ArrayList<BoardBean> boardList=new ArrayList<>();
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				BoardBean board = new BoardBean();
				
				board.setB_id(rs.getInt(1));
				board.setB_name(rs.getString(2));
				board.setB_email(rs.getString(3));
				board.setB_title(rs.getString(4));
				board.setB_content(rs.getString(5));
				board.setB_pwd(rs.getString(6));
				board.setB_date(rs.getTimestamp(7));
				board.setB_hit(rs.getInt(8));
				board.setB_ip(rs.getString(9));
//				여기까지가 글목록 화면의 한개의 행을 가져와서 저장
				
//				행의 데이터를 ArrayList에 저장
				boardList.add(board);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(rs != null){ rs.close(); }
				if(pstmt != null){ pstmt.close(); }
				if(conn != null){ conn.close(); }
			}catch(Exception se){
				se.printStackTrace();
			}
		}
		
		return boardList;
	
	}
//	public BoardBean getBoard(int bid) {
	public BoardBean getBoard(int bid, boolean hitadd) {


		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
//		String sql="select b_id,b_name,b_email,b_title,b_content,b_pwd,b_date,b_hit,b_ip from boardt where b_id=?";
		String sql="";
		BoardBean board=null;
		
		try {
			conn = getConnection();
			
			if (hitadd) {
				sql="update boardt set b_hit=b_hit+1 where b_id=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, bid);
				pstmt.executeUpdate();
			}
			
			sql="select b_id,b_name,b_email,b_title,b_content,b_pwd,b_date,b_hit,b_ip from boardt where b_id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bid);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				board = new BoardBean();
				
				board.setB_id(rs.getInt(1));
				board.setB_name(rs.getString(2));
				board.setB_email(rs.getString(3));
				board.setB_title(rs.getString(4));
				board.setB_content(rs.getString(5));
				board.setB_pwd(rs.getString(6));
				board.setB_date(rs.getTimestamp(7));
				board.setB_hit(rs.getInt(8));
				board.setB_ip(rs.getString(9));
//				여기까지가 글목록 화면의 한개의 행을 가져와서 저장
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(rs != null){ rs.close(); }
				if(pstmt != null){ pstmt.close(); }
				if(conn != null){ conn.close(); }
			}catch(Exception se){
				se.printStackTrace();
			}
		}
		
		return board;
	
	}
	public int deleteBoard(int b_id, String b_pwd) {


		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="";
		String pwd="";
		int re=-1;
		
		try {
			conn = getConnection();
			sql="select b_pwd from boardt where b_id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, b_id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {//비밀번호가 있으면 참
				pwd = rs.getString(1);//쿼리결과에서 b_pwd 컬럼 데이터
				if (pwd.equals(b_pwd)) {//비밀번호가 일치
					sql="delete from boardt where b_id=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, b_id);
					re = pstmt.executeUpdate();
					
				}else {//비밀번호가 불일치
					re=0;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(rs != null){ rs.close(); }
				if(pstmt != null){ pstmt.close(); }
				if(conn != null){ conn.close(); }
			}catch(Exception se){
				se.printStackTrace();
			}
		}
		
		return re;
	
	
	}
	
	public int editBoard(BoardBean board) {

		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
//		String sql="select b_id,b_name,b_email,b_title,b_content,b_pwd,b_date,b_hit,b_ip from boardt where b_id=?";
		String sql="";
		String pwd="";
		int re=-1;
		
		try {
			conn = getConnection();
			sql="select b_pwd from boardt where b_id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, board.getB_id());
			rs = pstmt.executeQuery();

			
			if(rs.next()) {//비밀번호가 있으면 참
				pwd = rs.getString(1);//쿼리결과에서 b_pwd 컬럼 데이터
				if (pwd.equals(board.getB_pwd())) {//비밀번호가 일치
					sql="update boardt set b_name=?, b_email=?, b_title=?, b_content=? where b_id=?";
					pstmt = conn.prepareStatement(sql);
					
					System.out.println("@# getB_name()=>"+board.getB_name());
					System.out.println("@# getB_email()=>"+board.getB_email());
					System.out.println("@# getB_title()=>"+board.getB_title());
					System.out.println("@# getB_content()=>"+board.getB_content());
					System.out.println("@# getB_id()=>"+board.getB_id());
					
					pstmt.setString(1, board.getB_name());
					pstmt.setString(2, board.getB_email());
					pstmt.setString(3, board.getB_title());
					pstmt.setString(4, board.getB_content());
					pstmt.setInt(5, board.getB_id());
					re = pstmt.executeUpdate();
					
				}else {//비밀번호가 불일치
					re=0;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(rs != null){ rs.close(); }
				if(pstmt != null){ pstmt.close(); }
				if(conn != null){ conn.close(); }
			}catch(Exception se){
				se.printStackTrace();
			}
		}
		
		return re;
	
	
	}
}


















